package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import java.sql.*;
import java.util.ArrayList;

// Imports for our common classes
import common.Order;
import common.Message;
import common.MessageType;

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
            // 1. Old Prototype Logic 
            if (msg instanceof String) {
                ArrayList<Order> orders = getOrdersBySubscriberId((String) msg);
                client.sendToClient(orders.isEmpty() ? "NOT_FOUND" : orders);
            } 
            else if (msg instanceof Order) {
                Order ord = (Order) msg;
                boolean success = updateOrderDetails(ord.getOrderNumber(), ord.getOrderDate(), ord.getNumberOfVisitors());
                client.sendToClient(success ? "UPDATE_SUCCESS" : "UPDATE_FAILED");
            }
            
            // 2. NEW LOGIC: Switch Statement for Entrance Control
            else if (msg instanceof Message) {
                Message message = (Message) msg;
                
                switch (message.getType()) {
                    case GET_FULL_PRICE:
                        handleGetFullPrice(message, client);
                        break;
                    case CHECK_PROMOTIONS:
                        handleCheckPromotions(message, client);
                        break;
                    case VALIDATE_ORDER:
                        handleValidateOrder(message, client);
                        break;
                    case CHECK_CAPACITY:
                        handleCheckCapacity(message, client);
                        break;
                    case VERIFY_GUIDE:
                        handleVerifyGuide(message, client);
                        break;
                    case CONFIRM_PAYMENT:
                        handleConfirmPayment(message, client);
                        break;
                    case VERIFY_SUBSCRIBER:
                        handleVerifySubscriber(message, client);
                        break;
                    case EXIT_PARK:
                        handleExitPark(message, client);
                        break;
                    default:
                        System.out.println("Server: Unknown message type received.");
                        break;
                        
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    // =========================================================================
    // --- PRIVATE HELPER METHODS 
    // =========================================================================

    private void handleExitPark(Message message, ConnectionToClient client) {
        // This string can now be either an order number (e.g., "123") or a QR code (e.g., "QR888")
        String inputStr = (String) message.getData(); 
        boolean success = false;

        try {
            Connection conn = DBconnection.getConnection();

            // Step 1: Fetch the order by either ID or QR code. 
            // Important: We ask the DB to return the actual order_number as well!
            String selectQuery = "SELECT order_number, number_of_visitors, park_name FROM gonature_db_new.`Order` " +
                                 "WHERE (order_number = ? OR QR_code = ?) AND status = 'Entered' AND exit_time IS NULL";
            PreparedStatement psSelect = conn.prepareStatement(selectQuery);
            psSelect.setString(1, inputStr); // Check if it matches an order number
            psSelect.setString(2, inputStr); // Check if it matches a QR code
            ResultSet rs = psSelect.executeQuery();

            if (rs.next()) {
                // Extract the actual order ID, even if the client scanned a QR code
                String actualOrderNumber = rs.getString("order_number"); 
                int visitorsAmount = rs.getInt("number_of_visitors");
                String parkName = rs.getString("park_name");

                // Step 2: Update the exit time (using the actual order ID!)
                String updateOrder = "UPDATE gonature_db_new.`Order` SET exit_time = CURTIME() WHERE order_number = ?";
                PreparedStatement psUpdateOrder = conn.prepareStatement(updateOrder);
                psUpdateOrder.setString(1, actualOrderNumber);
                psUpdateOrder.executeUpdate();
                psUpdateOrder.close();

                // Step 3: Free up space in the park's current occupancy
                String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy - ? WHERE park_name = ?";
                PreparedStatement psUpdatePark = conn.prepareStatement(updatePark);
                psUpdatePark.setInt(1, visitorsAmount);
                psUpdatePark.setString(2, parkName);
                psUpdatePark.executeUpdate();
                psUpdatePark.close();

                success = true;
                System.out.println("Server: Exit registered for Order " + actualOrderNumber + 
                                   " (Input: " + inputStr + "). Freed " + visitorsAmount + " spots in " + parkName);
            } else {
                System.out.println("Server: Exit failed. Could not find entered order matching the number or QR: " + inputStr);
            }
            
            rs.close();
            psSelect.close();

        } catch (Exception e) {
            System.err.println("Server: Database error during exit registration.");
            e.printStackTrace();
        }

        // Step 4: Send the response back to the client
        try { 
            client.sendToClient(new Message(MessageType.EXIT_PARK_RESPONSE, success)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

	private void handleGetFullPrice(Message message, ConnectionToClient client) {
        String requestedPark = (String) message.getData();
        double fullPrice = 50.0; // Default fallback price
        
        try {
            String query = "SELECT full_price FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, requestedPark);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Success: Found the price in the database
                fullPrice = rs.getDouble("full_price");
                System.out.println("Server: Fetched full price (" + fullPrice + ") for park: " + requestedPark);
            } else {
                // Edge Case 1: Park not found in the database
                System.out.println("Server: WARNING - Park '" + requestedPark + "' not found. Using Fallback price: " + fullPrice);
            }
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            // Edge Case 2: Database connection error or query failure
            System.err.println("Server: ERROR - Database error during fetchFullPrice. Using Fallback price: " + fullPrice);
            e.printStackTrace();
        }
        
        // Send the result (either DB price or fallback) back to the client
        try { 
            client.sendToClient(new Message(MessageType.GET_FULL_PRICE_RESPONSE, fullPrice)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void handleCheckPromotions(Message message, ConnectionToClient client) {
        String parkForPromo = (String) message.getData();
        double discount = 0.0; // Default no discount
        
        try {
            String query = "SELECT additonal_discount FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, parkForPromo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                discount = rs.getDouble("additonal_discount");
                System.out.println("Server: Found discount of " + discount + " for park: " + parkForPromo);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Database error during checkActivePromotions.");
            e.printStackTrace();
        }
        
        try { client.sendToClient(new Message(MessageType.CHECK_PROMOTIONS_RESPONSE, discount)); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleValidateOrder(Message message, ConnectionToClient client) {
        String inputIdStr = (String) message.getData();
        
        // Using an ArrayList to pack multiple details (Amount and Type)
        java.util.ArrayList<Object> orderDetails = new java.util.ArrayList<>(); 
        
        try {
            // Fetching BOTH number of visitors and visitor type! ---
            String query = "SELECT number_of_visitors, type_of_visitor FROM gonature_db_new.`Order` " +
                           "WHERE (order_number = ? OR QR_code = ?) " +
                           "AND status = 'Confirmed' " +
                           "AND order_date = CURDATE() " +
                           "AND ABS(TIMESTAMPDIFF(MINUTE, CURTIME(), entry_time)) <= 60 " +
                           "AND exit_time IS NULL";
                           
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, inputIdStr); 
            pstmt.setString(2, inputIdStr); 
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Extracting both values from the database
                int visitorsAmount = rs.getInt("number_of_visitors"); 
                String visitorType = rs.getString("type_of_visitor");
                
                // Packing them into the list
                orderDetails.add(visitorsAmount); // Index 0
                orderDetails.add(visitorType);    // Index 1
                
                System.out.println("Server: Valid entry found! Visitors: " + visitorsAmount + ", Type: " + visitorType);
            } else {
                System.out.println("Server: Entry denied for Order number/QR: " + inputIdStr);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Database error during order validation.");
            e.printStackTrace();
        }
        
        // Send the list (will be empty if validation failed, or size 2 if successful)
        try { client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, orderDetails)); } 
        catch (Exception e) { e.printStackTrace(); }
    }

    private void handleCheckCapacity(Message message, ConnectionToClient client) {
        // 1. Extract the list from the message
        java.util.ArrayList<Object> dataList = (java.util.ArrayList<Object>) message.getData();
        
        // 2. Safely unpack the data from the list
        int requestedAmount = (int) dataList.get(0);
        String parkName = (String) dataList.get(1); // Now using the dynamic park name!
        
        boolean hasSpace = false;
        
        try {
            String query = "SELECT max_capacity, casual_gap, current_occupancy FROM gonature_db_new.Parks WHERE park_name = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, parkName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int maxCapacity = rs.getInt("max_capacity");
                int casualGap = rs.getInt("casual_gap");
                int currentOccupancy = rs.getInt("current_occupancy");
                
                int allowedCapacity = maxCapacity - casualGap;
                
                if ((currentOccupancy + requestedAmount) <= allowedCapacity) {
                    hasSpace = true;
                    System.out.println("Server: Space available for " + requestedAmount + " in " + parkName);
                } else {
                    System.out.println("Server: Park " + parkName + " is full for casual visitors.");
                }
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.err.println("Server: Database error during capacity check.");
            e.printStackTrace();
        }
        
        try { 
            client.sendToClient(new Message(MessageType.CHECK_CAPACITY_RESPONSE, hasSpace)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void handleVerifyGuide(Message message, ConnectionToClient client) {
        String guideIdStr = (String) message.getData();
        boolean isCertified = false;
        
        try {
            int guideId = Integer.parseInt(guideIdStr);
            String query = "SELECT * FROM gonature_db_new.Guide WHERE guide_id = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, guideId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                isCertified = true; 
                System.out.println("Server: Guide " + guideId + " verified successfully.");
            } else {
                System.out.println("Server: Guide verification failed for ID: " + guideId);
            }
            rs.close();
            pstmt.close();
        } catch (NumberFormatException e) {
            System.err.println("Server: Invalid Guide ID format.");
        } catch (Exception e) {
            System.err.println("Server: Database error during guide verification.");
        }
        
        try { client.sendToClient(new Message(MessageType.VERIFY_GUIDE_RESPONSE, isCertified)); } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleVerifySubscriber(Message message, ConnectionToClient client) {
        String subIdStr = (String) message.getData();
        boolean isSubValid = false;
        
        try {
            int subId = Integer.parseInt(subIdStr);
            String query = "SELECT * FROM gonature_db_new.Subscriber WHERE sub_number = ?";
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, subId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                isSubValid = true; 
                System.out.println("Server: Subscriber " + subId + " verified successfully.");
            } else {
                System.out.println("Server: Subscriber verification failed for ID: " + subId);
            }
            rs.close();
            pstmt.close();
        } catch (NumberFormatException e) {
            System.err.println("Server: Invalid Subscriber ID format.");
        } catch (Exception e) {
            System.err.println("Server: Database error during subscriber verification.");
            e.printStackTrace();
        }
        
        try { client.sendToClient(new Message(MessageType.VERIFY_SUBSCRIBER_RESPONSE, isSubValid)); } 
        catch (Exception e) { e.printStackTrace(); }
    }

    private void handleConfirmPayment(Message message, ConnectionToClient client) {
        java.util.ArrayList<Object> paymentData = (java.util.ArrayList<Object>) message.getData();
        
        int amountToAdd = (int) paymentData.get(0);
        
        Object orderObj = paymentData.get(1);
        String orderToUpdate = (orderObj == null) ? null : String.valueOf(orderObj);
        
        String parkToUpdate = (String) paymentData.get(2);
        String visitorType = (String) paymentData.get(3); 
        boolean updateSuccess = false;

        try {
            Connection conn = DBconnection.getConnection();
            
            // 1. Update Occupancy in the Parks table
            String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy + ? WHERE park_name = ?";
            PreparedStatement psPark = conn.prepareStatement(updatePark);
            psPark.setInt(1, amountToAdd);
            psPark.setString(2, parkToUpdate);
            psPark.executeUpdate();
            psPark.close();

            // 2. Handle the Order table (Update existing OR Insert new)
            if (orderToUpdate != null && !orderToUpdate.isEmpty() && !orderToUpdate.equals("null")) {
                
                // Fix 2: Smart query separation to prevent MySQL Strict Mode crashes 
                // (MySQL throws an error if we search for a string with letters in an INT column)
                String updateOrder;
                if (orderToUpdate.startsWith("QR")) {
                    // Search only in the string column (QR_code)
                    updateOrder = "UPDATE gonature_db_new.`Order` SET status = 'Entered' WHERE QR_code = ?";
                } else {
                    // Search only in the integer column (order_number)
                    updateOrder = "UPDATE gonature_db_new.`Order` SET status = 'Entered' WHERE order_number = ?";
                }
                
                PreparedStatement psOrder = conn.prepareStatement(updateOrder);
                psOrder.setString(1, orderToUpdate);
                psOrder.executeUpdate();
                psOrder.close();
                
                System.out.println("Server: Pre-booked entry confirmed for ID/QR " + orderToUpdate + ". Status set to 'Entered'.");
                
            } else {
                // SCENARIO B: Casual Visitor (Insert New Order Row)
                String insertOrder = "INSERT INTO gonature_db_new.`Order` " +
                                     "(order_date, number_of_visitors, date_of_placing_order, entry_time, status, type_of_visitor, park_name) " +
                                     "VALUES (CURDATE(), ?, CURDATE(), CURTIME(), 'Entered', ?, ?)";
                
                PreparedStatement psInsert = conn.prepareStatement(insertOrder);
                psInsert.setInt(1, amountToAdd);
                psInsert.setString(2, visitorType);
                psInsert.setString(3, parkToUpdate); 
                
                psInsert.executeUpdate();
                psInsert.close();
                System.out.println("Server: Casual entry. Inserted new record for " + amountToAdd + " " + visitorType + "s in " + parkToUpdate + ".");
            }
            
            updateSuccess = true;
        } catch (Exception e) {
            System.err.println("Server: Error during payment confirmation and database update.");
            // This will print the exact reason for the crash if it happens again
            e.printStackTrace(); 
        }
        
        try { 
            client.sendToClient(new Message(MessageType.CONFIRM_PAYMENT_RESPONSE, updateSuccess)); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        
    }

    // =========================================================================
    // --- ORIGINAL HELPER & CONNECTION METHODS ---
    // =========================================================================

    // Retrieving a list of orders from the database for a specific subscriber
    private ArrayList<Order> getOrdersBySubscriberId(String subId) {
        ArrayList<Order> ordersList = new ArrayList<>();
        String query = "SELECT * FROM `Order` WHERE subscriber_id = ?";
        
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

    // Update existing booking details (date and number of visitors) in the database
    private boolean updateOrderDetails(int orderNum, java.util.Date newDate, int visitors) {
        String query = "UPDATE `Order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
            pstmt.setInt(2, visitors);
            pstmt.setInt(3, orderNum);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Action performed as soon as a new customer connects: saving their details and updating the GUI
    @Override
    protected void clientConnected(ConnectionToClient client) {
        String ip = client.getInetAddress().getHostAddress().replace("/", "");
        String hostName = client.getInetAddress().getHostName();
        
        // Truly unique port extraction for each connection
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

    // Handling client disconnection when it disconnects in an orderly manner
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        handleDisconnection(client);
    }

    // Handling client disconnection in the event of an error or forced disconnection
    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        handleDisconnection(client);
    }

    // Printing to the terminal and updating the graphical interface to remove the customer from the list
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

    // Notification as soon as the server starts listening for connections on the specified port
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }
}