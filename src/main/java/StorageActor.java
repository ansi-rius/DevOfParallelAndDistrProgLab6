import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;

public class StorageActor extends AbstractActor {
    //а. создаем актор хранилище конфигурации.
    //Он принимает две команды —
    //--список серверов (который отправит zookeeper watcher)
    //--запрос на получение случайного сервера
    private List<String> storage;

    public StorageActor() {
        this.storage = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return null;
    }


}
