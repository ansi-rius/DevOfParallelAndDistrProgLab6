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

    public Route createRoute(ActorRef RouteActor) {

        return route
                
    }

    /*

    Минимальный пример
        Route route = get(
        () -> complete("Received GET") ).orElse(
        () -> complete("Received something else") )

     */
}
