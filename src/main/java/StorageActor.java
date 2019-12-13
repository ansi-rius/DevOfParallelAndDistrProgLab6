import akka.actor.AbstractActor;

import java.util.List;

public class StorageActor extends AbstractActor {
    //а. создаем актор хранилище конфигурации.
    //Он принимает две команды —
    //--список серверов (который отправит zookeeper watcher)
    //--запрос на получение случайного сервера
    private List<String> storage;

    
    @Override
    public Receive createReceive() {
        return null;
    }


}
