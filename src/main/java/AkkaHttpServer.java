
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
import org.apache.zookeeper.KeeperException;
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

    private static String connectString = "127.0.0.1:2181";
    private static int sessionTimeout = 3000;
    private static String serversPath = "/servers";

    private ActorSystem system;
    private ActorRef storage;
    private CompletionStage<ServerBinding> binding;
    private String host;
    private int port;
    private Logger log = Logger.getLogger(AkkaHttpServer.class.getName());
    private AsyncHttpClient asyncHttpClient = asyncHttpClient();
    ServersHandler serverHandle;
    ZooKeeper zoo;



    public AkkaHttpServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.system = ActorSystem.create("routes");

    }

    public void start() throws IOException, KeeperException, InterruptedException{
        zoo = new ZooKeeper(connectString, sessionTimeout, watcher -> log.info(watcher.toString()));
        //a. Инициализация http сервера в akka
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final AsyncHttpClient asyncHttpClient = asyncHttpClient();

        storage = system.actorOf(Props.create(StorageActor.class), "Storage");

        ServersHandler serverHandle = new ServersHandler(zoo, storage, serversPath);
        serverHandle.startServer("localhost"+port, host, port);

        final Anonymization anonymousServer = new Anonymization(storage, asyncHttpClient, zoo);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow =
                anonymousServer.createRoute(system)
                        .flow(system, materializer);

        //метода которому передаем Http, ActorSystem и ActorMaterializer>;
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer
        );
        System.out.println("Server started!");
    }

    public void close() throws IOException, InterruptedException,  KeeperException{
        asyncHttpClient.close();
        //serverHandle.removeAllWatches();
        zoo.close();
        //закрываем
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());

    }


}
