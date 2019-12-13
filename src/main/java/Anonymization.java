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

    public Route createRoute(ActorSystem system) {
        return route(
            get(() ->
                    parameter
        );
    }
}
