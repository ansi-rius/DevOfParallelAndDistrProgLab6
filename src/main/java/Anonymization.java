import akka.actor.ActorRef;
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
}
