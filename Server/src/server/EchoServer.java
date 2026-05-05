package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection; // שימוש במחלקת החיבור שלך[cite: 1]
import java.sql.*;

/**
 * השרת המרכזי עבור פרויקט GoNature.
 * יורש מ-AbstractServer ומממש את הלוגיקה של ה-DB והתצוגה ב-GUI.
 */
public class EchoServer extends AbstractServer {
    
    private ServerGUI gui; // רפרנס למסך כדי לעדכן אותו בחיבורים[cite: 9]

    public EchoServer(int port, ServerGUI gui) {
        super(port);
        this.gui = gui;
    }

    /**
     * מטפל בהודעות שמגיעות מהלקוח.
     * כרגע השרת מנסה לפרש כל הודעה כמספר מנוי ולחפש אותו ב-DB.
     */
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received from client: " + msg);
        
        try {
            // שליפת המידע מה-DB בהתאם למה שהלקוח שלח
            String subscriberId = msg.toString();
            String result = getSubscriberInfo(subscriberId);
            
            // שליחת התשובה חזרה ללקוח הספציפי
            client.sendToClient(result); 
        } catch (Exception e) {
            try {
                client.sendToClient("Server Error: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * פונקציית עזר המבצעת את השאילתה מול מסד הנתונים[cite: 1, 2].
     */
    private String getSubscriberInfo(String id) {
        // שימוש בשאילתה על טבלת ה-subscriber שראינו ב-Workbench
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

    /**
     * מתודה שמופעלת אוטומטית כשלקוח מתחבר.
     * מעדכנת את ה-Console ואת ה-GUI בפרטי הלקוח[cite: 11].
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress();
        String host = client.getInetAddress().getHostName();
        
        // הדפסה ל-Console לגיבוי
        System.out.println("> Client connected: " + client);
        System.out.println("> IP: " + ip);
        
        // עדכון ה"ריבוע" (ServerGUI) בפרטים שביקשת[cite: 11, 13]
        if (gui != null) {
            gui.updateClientInfo(ip, host, "Connected");
        }
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }
}