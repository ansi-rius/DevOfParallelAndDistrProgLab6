import Messages.DeleteServerMessage;
import Messages.GetRandomServerMessage;
import Messages.ListOfServersMessage;
import Messages.ReturnRandomServerMessage;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class StorageActor extends AbstractActor {
    //а. создаем актор хранилище конфигурации.
    //Он принимает две команды —
    //--список серверов (который отправит zookeeper watcher)
    //--запрос на получение случайного сервера

    //-------------

    //Разрабатываем akka http сервер который при получении запроса либо
    //отправляет его на случайный сервер, уменьшая счетчик на 1 Либо
    //осуществляет get для данного url и возвращает.
    private List<String> storage;
    private Random randomServer;
    private static Logger log = Logger.getLogger(StorageActor.class.getName());

    public StorageActor() {
        this.storage = new ArrayList<>();
        this.randomServer = new Random();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                //--список серверов (который отправит zookeeper watcher)
                .match(ListOfServersMessage.class, this::receiveListOfServers)
                //--запрос на получение случайного сервера
                .match(GetRandomServerMessage.class, this::receiveGetRandomServerMessage)
                .match(DeleteServerMessage.class, this::receiveDeleteMessage)
                .build();
    }

    private void receiveListOfServers(ListOfServersMessage msg) {
        log.info("List of servers: " + msg.getServersList());
        this.storage.clear();
        this.storage.addAll(msg.getServersList());
    }

    private void receiveGetRandomServerMessage(GetRandomServerMessage msg) {
        getSender().tell(
                new ReturnRandomServerMessage(storage.get(randomServer.nextInt(storage.size()))),
                        ActorRef.noSender()
        );

    }

    private void receiveDeleteMessage(DeleteServerMessage msg) {
        this.storage.remove(msg.getServer());
    }


}
