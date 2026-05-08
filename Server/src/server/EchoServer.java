package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import java.sql.*;
import java.util.ArrayList;
import common.Order;

public class EchoServer extends AbstractServer {
    
    private ServerGUI gui;

    public EchoServer(int port, ServerGUI gui) {
        super(port);
        this.gui = gui;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received from client: " + msg);
        
        try {
            // מקרה 1: עדי שולח String עם ה-ID ומצפה לקבל מערך של אובייקטי Order
            if (msg instanceof String) {
                ArrayList<Order> orders = getOrdersBySubscriberId((String) msg);
                
                if (orders.isEmpty()) {
                    client.sendToClient("NOT_FOUND");
                } else {
                    client.sendToClient(orders); // שולח לעדי ישירות את המערך כאובייקט
                }
            } 
            // מקרה 2: עדי שולח אובייקט Order לעדכון
            else if (msg instanceof Order) {
                Order ord = (Order) msg;
                boolean success = updateOrderDetails(
                    ord.getOrderNumber(),
                    ord.getOrderDate(),
                    ord.getNumberOfVisitors()
                );
                client.sendToClient(success ? "UPDATE_SUCCESS" : "UPDATE_FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * שולף את כל ההזמנות של מנוי מסוים ומחזיר אותן כרשימת אובייקטים
     */
    private ArrayList<Order> getOrdersBySubscriberId(String subId) {
        ArrayList<Order> ordersList = new ArrayList<>();
        // שם הטבלה עם גרש הפוך בגלל שהיא מילה שמורה
        String query = "SELECT * FROM `order` WHERE subscriber_id = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, subId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_number"),
                        rs.getDate("order_date"),
                        rs.getInt("number_of_visitors"),
                        rs.getInt("confirmation_code"),
                        rs.getInt("subscriber_id"),
                        rs.getDate("date_of_placing_order")
                    );
                    ordersList.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordersList;
    }

    private boolean updateOrderDetails(int orderNum, java.util.Date newDate, int visitors) {
        String query = "UPDATE `order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
            pstmt.setInt(2, visitors);
            pstmt.setInt(3, orderNum);
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- מתודות ניהול חיבורים ו-GUI ---

    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress();
        String hostName = client.getInetAddress().getHostName();
        System.out.println("> Client connected: " + ip);
        if (gui != null) {
            gui.updateClientDetails(ip, hostName, "Connected");
        }
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("> Client disconnected.");
        if (gui != null) {
            gui.updateClientDetails("---", "---", "Not Connected");
        }
    }

    /**
     * מטפל במקרה של ניתוק לא צפוי (כמו סגירת ה-Eclipse של הלקוח)
     */
    @Override
    protected void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println("> Client connection lost (Exception).");
        if (gui != null) {
            gui.updateClientDetails("---", "---", "Not Connected");
        }
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }
}