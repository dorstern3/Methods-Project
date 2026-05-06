package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import java.sql.*;

public class EchoServer extends AbstractServer {
    
    // רפרנס ישיר למחלקה של ה-GUI
    private ServerGUI gui;

    public EchoServer(int port, ServerGUI gui) {
        super(port);
        this.gui = gui;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received from client: " + msg);
        try {
            String subscriberId = msg.toString();
            String result = getSubscriberInfo(subscriberId);
            client.sendToClient(result); 
        } catch (Exception e) {
            try {
                client.sendToClient("Server Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getSubscriberInfo(String id) {
        String query = "SELECT first_name, last_name FROM gonature_db.subscriber WHERE subscriber_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "Found: " + rs.getString("first_name") + " " + rs.getString("last_name");
            } else {
                return "Subscriber ID " + id + " not found.";
            }
        } catch (Exception e) {
            return "DB Error: " + e.getMessage();
        }
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress();
        String hostName = client.getInetAddress().getHostName();

        // הדפסה ל-Console בדיוק כמו שביקשת
        System.out.println("> Client connected:");
        System.out.println("> IP Address: " + ip);
        System.out.println("> Host Name: " + hostName);
        System.out.println("> Status: Connected");

        // עדכון ה-GUI ישירות (נשתמש במתודה שנוסיף ב-ServerGUI)
        if (gui != null) {
            gui.updateClientDetails(ip, hostName, "Connected");
        }
    }
    
    
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        // הדפסה ל-Console של השרת
        System.out.println("> Client disconnected.");

        // עדכון ה-GUI חזרה למצב ריק
        if (gui != null) {
            gui.updateClientDetails("---", "---", "Not Connected");
        }
    }
    
    @Override
    protected void clientException(ConnectionToClient client, Throwable exception) {
        // ברגע שיש שגיאה בחיבור (כמו סגירה אלימה), אנחנו קוראים לניתוק המסודר
        clientDisconnected(client);
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }
}