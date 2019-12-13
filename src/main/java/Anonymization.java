import Messages.GetRandomServerMessage;
import Messages.ReturnRandomServerMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import scala.Int;
import scala.compat.java8.FutureConverters;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class Anonymization extends AllDirectives {
    private static ActorRef storage;
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
                            parameter("count", c ->
                                handleGetWithUrlCount(url, Integer.parseInt(c))
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


    private Route handleGetWithUrlCount(String url, int count) {
        CompletionStage<Response> response = count == 0 ?
                urlRequest(http.prepareGet(url).build()) //если 0, то запрос по url из параметра
                :
                requestWithLowerCount(url, count-1); //если счетчик не равен 0, то сначала получает новый урл сервера
        //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
        //query параметрами (url, counter) но счетчиком на 1 меньше.
        return completeOKWithFutureString(response.thenApply(Response::getResponseBody));
    }

    private CompletionStage<Response> urlRequest(Request req) {
        log.info("Request "+req.getUri());
        return http.executeRequest(req).toCompletableFuture();
    }

    private CompletionStage<Response> requestWithLowerCount(String url, int count) {
        return Patterns.ask(storage, new GetRandomServerMessage(), Duration.ofSeconds(3))
                .thenApply(o -> ((ReturnRandomServerMessage)o).getServer())
                .thenCompose(msg ->
                    urlRequest(makeRequest(getServUrl(msg), url, ActorRef.noSender());
                    )
    }

    private Request makeRequest(String servUrl, String url, int count) {
        return http.prepareGet(servUrl)
                .addQueryParam("url", url)
                .addQueryParam("count", Integer.toString(count))
                .build();
    }

    private String getServUrl(String obj) {
        try {
            return new String(zoo.getData(obj, false, null));
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
