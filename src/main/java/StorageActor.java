import akka.actor.AbstractActor;
import sun.rmi.runtime.Log;

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
    private Logger log = Logger.getLogger(StorageActor.class.getName());

    public StorageActor() {
        this.storage = new ArrayList<>();
        this.randomServer = new Random();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ListOfServersMessage.class, this::receiveListOfServers) //--список серверов (который отправит zookeeper watcher)
    }

    private void receiveListOfServers(ListOfServersMessage msg) {
        log.Info()
    }


}
