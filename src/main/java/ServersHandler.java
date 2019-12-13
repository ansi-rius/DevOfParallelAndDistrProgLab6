import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;

public class ServersHandler {
    //создаем с помощью api route в акка http сервер который принимает два
    //параметра, и если счетчик не равен 0, то сначала получает новый урл сервера
    //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
    //query параметрами (url, counter) но счетчиком на 1 меньше. Либо осуществляет
    //запрос по url из параметра

    //слайд 23

    private String serversPath;
    private ActorRef serversStorage;
    private ZooKeeper zoo;

    public ServersHandler(ZooKeeper zoo, ActorRef serversStorage, String serversPath) {
        this.zoo = zoo;
        this.serversPath = serversPath;
        this.serversStorage = serversStorage;
    }



}
