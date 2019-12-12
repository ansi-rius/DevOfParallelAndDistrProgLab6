
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;

import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpServer {
    //создаем с помощью api route в акка http сервер который принимает два
    //параметра, и если счетчик не равен 0, то сначала получает новый урл сервера
    //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
    //query параметрами (url, counter) но счетчиком на 1 меньше. Либо осуществляет
    //запрос по url из параметра
    private ActorSystem system = ActorSystem.create("routes");
    private ActorRef storageActor;


    public AkkaHttpServer() {
        this.storageActor = system.actorOf(Props.create(StorageActor.class), "Storage");
    }

    public void start() {
        //a. Инициализация http сервера в akka
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                instance.createRoute(system).flow(system, materializer);


    }











    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = test.createFlow(); //<вызов
    //метода которому передаем Http, ActorSystem и ActorMaterializer>;
    final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow,
            ConnectHttp.toHost("localhost", 8086),
            materializer
    );

    //System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
    //System.in.read();

    binding
            .thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());

}
