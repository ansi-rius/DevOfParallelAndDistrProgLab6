
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class AkkaHttpServer {
    //создаем с помощью api route в акка http сервер который принимает два
    //параметра, и если счетчик не равен 0, то сначала получает новый урл сервера
    //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
    //query параметрами (url, counter) но счетчиком на 1 меньше. Либо осуществляет
    //запрос по url из параметра
    private ActorSystem system;
    private ActorRef storageActor;
    private CompletionStage<ServerBinding> binding;
    private String host;
    private int port;
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 3000;
    private Logger log = Logger.getLogger(AkkaHttpServer.class.getName());



    public AkkaHttpServer(String host, int port) {
        this.storageActor = system.actorOf(Props.create(StorageActor.class), "Storage");
        this.host = host;
        this.port = port;
        this.system = ActorSystem.create("routes");

    }

    public void start() throws IOException{
        final ZooKeeper zoo = new ZooKeeper(connectString, sessionTimeout, watcher -> log.info(watcher.toString()));
        //a. Инициализация http сервера в akka
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                ServerRoutes.createRoute(system)
                        .flow(system, materializer);

        //метода которому передаем Http, ActorSystem и ActorMaterializer>;
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8086),
                materializer
        );
        System.out.println("Server started!");
    }

    public void close() {
        //закрываем
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());

    }


}
