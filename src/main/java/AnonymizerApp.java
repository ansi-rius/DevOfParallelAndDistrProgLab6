import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class AnonymizerApp {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException{
        //Администратор запускает несколько серверов. В параметре командной строки
        //он указывает порт для каждого.
        if (args.length != 2) {
            System.out.println("Usage: AnonymizerApp <host> <post>");
            System.exit(-1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        AkkaHttpServer server = new AkkaHttpServer(host, port);
        server.start();
        System.in.read();






    }
}
