import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;


public class Anonymization {
    private ActorRef storage;
    private AsyncHttpClient http;
    private ZooKeeper zoo;

    public Anonymization(ActorRef storage, AsyncHttpClient http, ZooKeeper zoo) {
        this.storage = storage;
        this.http = http;
        this.zoo = zoo;
    }

    //д. Cтроим дерево route и пишем обработчики запросов

    static Route createRoute(ActorRef RouteActor) {
        return
                route(
                        get(() ->
                                parameter("packageId", packageID -> {
                                    Future<Object> result = Patterns.ask(
                                            RouteActor,
                                            new TestResultRequestMessage(packageID),
                                            5000
                                    );
                                    return completeOKWithFuture(result, Jackson.marshaller());
                                })
                        )

                );
    }

    /*

    Минимальный пример
        Route route = get(
        () -> complete("Received GET") ).orElse(
        () -> complete("Received something else") )

     */
}
