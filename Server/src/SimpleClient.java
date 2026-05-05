
import java.io.*;
import ocsf.client.AbstractClient;

public class SimpleClient extends AbstractClient {

    public SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        // מדפיס את מה שהשרת החזיר (למשל: "Found: Dor")
        System.out.println("Server says: " + msg);
    }

    public static void main(String[] args) {
        try {
            SimpleClient client = new SimpleClient("localhost", 5555);
            client.openConnection(); // מתחבר לשרת
            
            // שולח מספר מנוי לבדיקה מול ה-DB
            client.sendToServer("12345"); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}