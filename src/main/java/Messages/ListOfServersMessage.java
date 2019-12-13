package Messages;

import java.util.List;

public class ListOfServersMessage {
    private List<String> serversList;

    public ListOfServersMessage(List<String> serversList){
        this.serversList = serversList;
    }

    public List<String> getServersList() {
        return serversList;
    }
}
