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
            if (msg instanceof String) {
                ArrayList<Order> orders = getOrdersBySubscriberId((String) msg);
                client.sendToClient(orders.isEmpty() ? "NOT_FOUND" : orders);
            } 
            else if (msg instanceof Order) {
                Order ord = (Order) msg;
                boolean success = updateOrderDetails(ord.getOrderNumber(), ord.getOrderDate(), ord.getNumberOfVisitors());
                client.sendToClient(success ? "UPDATE_SUCCESS" : "UPDATE_FAILED");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private ArrayList<Order> getOrdersBySubscriberId(String subId) {
        ArrayList<Order> ordersList = new ArrayList<>();
        String query = "SELECT * FROM `order` WHERE subscriber_id = ?";
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ordersList.add(new Order(rs.getInt("order_number"), rs.getDate("order_date"), rs.getInt("number_of_visitors"),
                        rs.getInt("confirmation_code"), rs.getInt("subscriber_id"), rs.getDate("date_of_placing_order")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ordersList;
    }

    private boolean updateOrderDetails(int orderNum, java.util.Date newDate, int visitors) {
        String query = "UPDATE `order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
            pstmt.setInt(2, visitors);
            pstmt.setInt(3, orderNum);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress().replace("/", "");
        String hostName = client.getInetAddress().getHostName();
        
        // חילוץ פורט ייחודי באמת לכל חיבור
        String info = client.toString();
        String port = info.substring(info.lastIndexOf(" ") + 1).replaceAll("[^0-9]", "");

        client.setInfo("ip", ip);
        client.setInfo("host", hostName);
        client.setInfo("port", port);

        System.out.println("Client connected! IP: " + ip + " Port: " + port);
        if (gui != null) {
            gui.updateClientDetails(ip, hostName, "Connected", port);
        }
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        handleDisconnection(client);
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        handleDisconnection(client);
    }

    private void handleDisconnection(ConnectionToClient client) {
        String ip = (String) client.getInfo("ip");
        String host = (String) client.getInfo("host");
        String port = (String) client.getInfo("port");

        if (ip != null && port != null) {
            System.out.println("Client disconnected! IP: " + ip + " Port: " + port);
            if (gui != null) {
                gui.updateClientDetails(ip, host, "Disconnected", port);
            }
        }
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }
}