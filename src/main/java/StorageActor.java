import akka.actor.AbstractActor;

public class StorageActor extends AbstractActor {
    //а. создаем актор хранилище конфигурации.
    //Он принимает две команды —
    //--список серверов (который отправит zookeeper watcher)
    //--запрос на получение случайного сервера
    
    @Override
    public Receive createReceive() {
        return null;
    }


}
