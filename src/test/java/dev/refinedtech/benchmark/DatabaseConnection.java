package dev.refinedtech.benchmark;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("dev.refinedtech.benchmark.DatabaseConnection{");
        sb.append("port=").append(port);
        sb.append(", url='").append(url).append('\'');
        sb.append(", user='").append(user).append('\'');
        sb.append(", pass='").append(pass).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
