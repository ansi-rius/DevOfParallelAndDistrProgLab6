package Messages;

public class DeleteServerMessage {
    private String server;

    public DeleteServerMessage(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }
}
