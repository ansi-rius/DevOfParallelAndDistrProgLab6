import Messages.ListOfServersMessage;
import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServersHandler {
    //создаем с помощью api route в акка http сервер который принимает два
    //параметра, и если счетчик не равен 0, то сначала получает новый урл сервера
    //(от актора хранилища конфигурации) и делает запрос к нему с аналогичными
    //query параметрами (url, counter) но счетчиком на 1 меньше. Либо осуществляет
    //запрос по url из параметра

    //слайд 23
    private static Logger log = Logger.getLogger(ServersHandler.class.getName());
    private String serversPath;
    private ActorRef serversStorage;
    private ZooKeeper zoo;

    public ServersHandler(ZooKeeper zoo, ActorRef serversStorage, String serversPath) {
        this.zoo = zoo;
        this.serversPath = serversPath;
        this.serversStorage = serversStorage;
        watchChildrenCallback(null);
    }

    //ZooK)eeper zoo = new ZooK)eeper("1MB27.0.0.1MB:21MB81MB", 3000, this); zoo.create("/servers/s",
    //“data”.getBytes(),
    //ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.EPHEMERAL_SEQUENTIAL
    //);
    //List<String> servers = zoo.getChildren("/servers", this); for (String s : servers) {
    //byte[] data = zoo.getData("/servers/" + s, false, null);
    //System.out.println("server " + s + " data=" + new String(data)); }

    public void startServer(String host, int port) throws KeeperException, InterruptedException{
        String serverPath = zoo.create("/servers/"+host+":"+port,
                (host+":"+port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        log.info("Path connected "+serverPath);
    }

    public void removeAllWatches() throws InterruptedException, KeeperException{
        zoo.removeAllWatches(serversPath, Watcher.WatcherType.Any, true);
    }

    private void watchChildrenCallback(WatchedEvent event) {
        if (event != null) {
            log.info(event.toString());
        }
        try {
            saveServer(
                    zoo.getChildren(serversPath, this::watchChildrenCallback).stream()
                    .map(s -> serversPath+"/"+s)
                    .collect(Collectors.toList())
            );
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveServer(List<String> servers) {
        this.serversStorage.tell(new ListOfServersMessage(servers), ActorRef.noSender());
    }

}
