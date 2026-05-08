package server;

import javafx.beans.property.SimpleStringProperty;

public class ConnectedClient {
    private SimpleStringProperty ip;
    private SimpleStringProperty host;
    private SimpleStringProperty status;
    private String port; 

    public ConnectedClient(String ip, String host, String status, String port) {
        this.ip = new SimpleStringProperty(ip);
        this.host = new SimpleStringProperty(host);
        this.status = new SimpleStringProperty(status);
        this.port = port;
    }

    public String getPort() { return port; }
    public String getIp() { return ip.get(); }
    public String getHost() { return host.get(); }
    public String getStatus() { return status.get(); }
    
    public void setStatus(String status) { this.status.set(status); }

    public SimpleStringProperty ipProperty() { return ip; }
    public SimpleStringProperty hostProperty() { return host; }
    public SimpleStringProperty statusProperty() { return status; }
}