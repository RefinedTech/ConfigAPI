public class DatabaseConnection {

    private final int port;
    private final String url;
    private final String user;
    private final String pass;

    public DatabaseConnection(int port, String url, String user, String pass) {
        this.port = port;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }
}
