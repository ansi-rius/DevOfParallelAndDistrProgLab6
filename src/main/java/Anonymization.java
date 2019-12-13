import Messages.GetRandomServerMessage;
import Messages.ReturnRandomServerMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import org.asynchttpclient.Response;
import scala.compat.java8.FutureConverters;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class Anonymization extends AllDirectives {
    private ActorRef storage;
    private AsyncHttpClient http;
    private ZooKeeper zoo;
    private static Logger log = Logger.getLogger(Anonymization.class.getName());

    public Anonymization(ActorRef storage, AsyncHttpClient http, ZooKeeper zoo) {
        this.storage = storage;
        this.http = http;
        this.zoo = zoo;
    }

    //д. Cтроим дерево route и пишем обработчики запросов

    public Route createRoute(ActorSystem system) {
        //создаем с помощью api route в акка http сервер который принимает два
        //параметра,
        return route(
                get(() ->
                        parameter("url", url ->
                            parameter("count", c -> {
                                int count = Integer.parseInt(c);
                                return count == 0 ?
                                        completeWithFuture(urlRequest(url, system)) //если 0, то запрос по url из параметра
                                        :
                                        completeWithFuture(requestWithLowerCount(url, count-1)); //если счетчик не равен 0, то сначала получает новый урл сервера
                                //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
                                //query параметрами (url, counter) но счетчиком на 1 меньше.

                                    }
                            )
                        )
                )
        );

    }

    /*

    Минимальный пример
        Route route = get(
        () -> complete("Received GET") ).orElse(
        () -> complete("Received something else") )

     */

    private static CompletionStage<HttpResponse> urlRequest(String url, ActorSystem system) {
        log.info("Request "+url);
        return Http.get(system).singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> requestWithLowerCount(String url, int count, ActorSystem system) {
        return Patterns.ask(storage, new GetRandomServerMessage(), Duration.ofSeconds(3))
                .thenApply(obj -> ((ReturnRandomServerMessage)obj).getServer())
                .thenCompose(msg -> urlRequest(getUri(msg), system)
                        .query()
        ())
    }

    public static Uri getUri(String adr) {
        return Uri.create("http://"+adr);
    }

}
