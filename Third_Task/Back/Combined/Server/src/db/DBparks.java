package db;



import java.sql.Connection;

import java.sql.Date;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.sql.Time;

import java.time.LocalTime;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;



import common.Message;

import common.MessageType;
import common.Subscriber;
import common.Workers;
import ocsf.server.ConnectionToClient;



public class DBparks {

/**

* Fetches the names of all registered parks from the database.

*/

public static ArrayList<String> getParksFromDB() {

ArrayList<String> parks = new ArrayList<>();

String query = "SELECT park_name FROM parks";

try (Connection conn = DBconnection.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

try (ResultSet rs = ps.executeQuery()) {

while (rs.next()) {

parks.add(rs.getString("park_name"));

}

}

} catch (SQLException e) {

e.printStackTrace();

}

return parks;

}

/**

     * Fetches the dynamic real-time occupancy and maximum capacity configuration for a specific park.

     * @param message The message containing the target park name (String).

     * @param client  The connection thread representing the client making the request.

     */

public static void handleGetParkOccupancy(Message message, ConnectionToClient client) {

        String parkName = (String) message.getData();

        int[] capacityResults = new int[2]; // Index 0: current_occupancy, Index 1: max_capacity

        

        try {

            Connection conn = DBconnection.getConnection();

            String query = "SELECT current_occupancy, max_capacity FROM gonature_db_new.Parks WHERE park_name = ?";

            

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, parkName);

            ResultSet rs = ps.executeQuery();

            

            if (rs.next()) {

                capacityResults[0] = rs.getInt("current_occupancy");

                capacityResults[1] = rs.getInt("max_capacity");

            }

            

            rs.close();

            ps.close();

            

        } catch (Exception e) {

            System.err.println("Server: Error retrieving live park occupancy metadata.");

            e.printStackTrace();

        }

        

       

        try {

            client.sendToClient(new Message(MessageType.GET_PARK_OCCUPANCY_RESPONSE, capacityResults));

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


/**

* Processes a park exit request sent from either a park employee or a visitor gate.

* Maps identifications using the single 'id' column, enforces park boundaries for employees,

* and prevents double-exit errors using exit_time checks.

*

* @param message The message containing an Object array: 

* [0] OrderID/QRCode (String), 

* [1] TravelerID (String or null), 

* [2] CurrentWorkerParkName (String or null)

* @param client  The connection thread representing the specific client.

*/

public static void handleExitPark(Message message, ConnectionToClient client) {

        // Extract the data array received from the client

        Object[] data = (Object[]) message.getData();

        String inputStr = (String) data[0];    // Can be an Order Number or a scanned QR Code

        String travelerId = (String) data[1];  // Traveler ID/Subscriber Number, or null if performed by an employee

        String currentPark = (String) data[2]; // Employee's park name (will be null for visitors)

        

        boolean success = false;



        try {

            Connection conn = DBconnection.getConnection();

            String selectQuery;

            PreparedStatement psSelect;



            // Step 1: Formulate the query based on the user type

            if (travelerId == null) {

                // EMPLOYEE MODE: Enforce park alignment so employees cannot exit visitors from other parks

                selectQuery = "SELECT order_number, number_of_visitors, park_name FROM gonature_db_new.`Order` " +

                              "WHERE (order_number = ? OR QR_code = ?) AND status = 'Entered' " +

                              "AND park_name = ? AND exit_time IS NULL";

                psSelect = conn.prepareStatement(selectQuery);

                psSelect.setString(1, inputStr);

                psSelect.setString(2, inputStr);

                psSelect.setString(3, currentPark); 

            } else {

                // VISITOR MODE: Order ID + Traveler ID is strict enough authentication. 

                // We do NOT need to check the park name here. The DB will find the order wherever it is.

                selectQuery = "SELECT o.order_number, o.number_of_visitors, o.park_name " +

                              "FROM gonature_db_new.`Order` o " +

                              "LEFT JOIN gonature_db_new.Subscriber s ON o.id = s.id " +

                              "WHERE (o.order_number = ? OR o.QR_code = ?) " +

                              "AND (o.id = ? OR s.sub_number = ?) AND o.status = 'Entered' " +

                              "AND o.exit_time IS NULL"; // Removed AND o.park_name = ?

                psSelect = conn.prepareStatement(selectQuery);

                psSelect.setString(1, inputStr);   

                psSelect.setString(2, inputStr);   

                psSelect.setString(3, travelerId); 

                psSelect.setString(4, travelerId); 

            }

            

            ResultSet rs = psSelect.executeQuery();



            // Step 2: If a matching active record is found, proceed with the exit workflow

            if (rs.next()) {

                String actualOrderNumber = rs.getString("order_number"); 

                int visitorsAmount = rs.getInt("number_of_visitors");

                String parkName = rs.getString("park_name"); // Fetched directly from the order itself



                // Step 3: Update ONLY departure time (Status remains 'Entered' per system requirements)

                String updateOrder = "UPDATE gonature_db_new.`Order` SET exit_time = CURTIME() WHERE order_number = ?";

                try (PreparedStatement psUpdateOrder = conn.prepareStatement(updateOrder)) {

                    psUpdateOrder.setString(1, actualOrderNumber);

                    psUpdateOrder.executeUpdate();

                }



                // Step 4: Decrement the specific park's current occupancy

                String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy - ? WHERE park_name = ?";

                try (PreparedStatement psUpdatePark = conn.prepareStatement(updatePark)) {

                    psUpdatePark.setInt(1, visitorsAmount);

                    psUpdatePark.setString(2, parkName);

                    psUpdatePark.executeUpdate();

                }



                success = true;

                System.out.println("Server: Exit registered successfully for Order: " + actualOrderNumber + " at " + parkName);

            } else {

                System.out.println("Server: Exit rejected. Parameters do not match any active 'Entered' order, or user already exited.");

            }

            

            rs.close();

            psSelect.close();



        } catch (Exception e) {

            System.err.println("Server: Database error during exit registration execution.");

            e.printStackTrace();

        }



        // Step 5: Dispatch the evaluation response back to the client

        try { 

            client.sendToClient(new Message(MessageType.EXIT_PARK_RESPONSE, success)); 

        } catch (Exception e) { 

            System.err.println("Server: Critical error transmitting response to client.");

            e.printStackTrace(); 

        }

    }


    /**

     * Fetches the base full ticket price for a specified park from the database.

     * If the park is not found or a database error occurs, a fallback price of 50.0 is used.

     * * @param message The network message containing the requested park name (String).

     * @param client  The connection thread representing the client making the request.

     */

public static void handleGetFullPrice(Message message, ConnectionToClient client) {

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



    /**

     * Retrieves any active promotional discount values for a specified park.

     * * @param message The network message containing the target park name (String).

     * @param client  The connection thread representing the client making the request.

     */

public static void handleCheckPromotions(Message message, ConnectionToClient client) {

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



/**

* Validates a pre-booked order attempting to enter the park.

* Cross-references the provided input against the database, distinguishing between

* numeric Order IDs and alphanumeric QR Codes.

* 

* @param message The network message containing the Order ID or QR Code (String).

* @param client  The connection thread representing the client making the request.

*/

public static void handleValidateOrder(Message message, ConnectionToClient client) {

    String inputIdStr = (String) message.getData();

    

    try {

        String query;

        // Check if the input is purely numeric to safely handle Integer conversion

        boolean isNumeric = inputIdStr.matches("\\d+");

        

        if (isNumeric) {

            // If numeric, check both Order ID (INT) and QR Code (String)

            query = "SELECT number_of_visitors, type_of_visitor, order_date, entry_time, status " +

                    "FROM gonature_db_new.`Order` " +

                    "WHERE (order_number = ? OR QR_code = ?) AND exit_time IS NULL";

        } else {

            // If alphanumeric, check only QR Code (String)

            query = "SELECT number_of_visitors, type_of_visitor, order_date, entry_time, status " +

                    "FROM gonature_db_new.`Order` " +

                    "WHERE QR_code = ? AND exit_time IS NULL";

        }

        

        Connection conn = DBconnection.getConnection();

        PreparedStatement pstmt = conn.prepareStatement(query);

        

        if (isNumeric) {

            int parsedId = Integer.parseInt(inputIdStr);

            pstmt.setInt(1, parsedId);       // Used for order_number

            pstmt.setString(2, inputIdStr);  // Used for QR_code

        } else {

            pstmt.setString(1, inputIdStr);  // Used for QR_code only

        }

        

        ResultSet rs = pstmt.executeQuery();

        

        if (rs.next()) {

            String status = rs.getString("status");

            Date orderDate = rs.getDate("order_date");

            Time entryTime = rs.getTime("entry_time");

            

            // Error Check 1: Check if the order is scheduled for today

            java.time.LocalDate today = java.time.LocalDate.now();

            if (!orderDate.toLocalDate().equals(today)) {

                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "WRONG_DATE"));

                rs.close();

                pstmt.close();

                return;

            }



            // Error Check 2: Check time constraints (within 1 hour range)

            LocalTime now = LocalTime.now();

            LocalTime scheduledTime = entryTime.toLocalTime();

            long minutesDifference = ChronoUnit.MINUTES.between(scheduledTime, now);

            

            if (minutesDifference > 60) {

                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "TIME_PASSED"));

                rs.close();

                pstmt.close();

                return;

            } else if (minutesDifference < -60) {

                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "TOO_EARLY"));

                rs.close();

                pstmt.close();

                return;

            }



            // Error Check 3: Check if the order status is Confirmed

            if (!status.equals("Confirmed")) {

                client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "NOT_CONFIRMED"));

                rs.close();

                pstmt.close();

                return;

            }

            

            // Success: All conditions met

            ArrayList<Object> orderDetails = new ArrayList<>();

            orderDetails.add(rs.getInt("number_of_visitors")); 

            orderDetails.add(rs.getString("type_of_visitor"));   

            

            client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, orderDetails));

            

        } else {

            // Error Check 4: The order number or QR code does not exist

            client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "NOT_FOUND"));

        }

        

        rs.close();

        pstmt.close();

        

    } catch (NumberFormatException e) {

        try { client.sendToClient(new Message(MessageType.VALIDATE_ORDER_RESPONSE, "INVALID_FORMAT")); } catch (Exception ex) {}

    } catch (Exception e) {

        e.printStackTrace();

    }

}


    /**

     * Checks if a specified park has enough available capacity to admit a group of casual visitors.

     * Accounts for the maximum capacity limit minus the designated casual gap.

     * * @param message The network message containing an ArrayList with: [0] amount (int), [1] parkName (String).

     * @param client  The connection thread representing the client making the request.

     */

public static void handleCheckCapacity(Message message, ConnectionToClient client) {

        // 1. Extract the list from the message

        ArrayList<Object> dataList = (ArrayList<Object>) message.getData();

        

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



    /**

     * Verifies if a provided ID belongs to a certified group guide registered in the system.

     * * @param message The network message containing the Guide ID (String).

     * @param client  The connection thread representing the client making the request.

     */

public static void handleVerifyGuide(Message message, ConnectionToClient client) {

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



    /**

     * Verifies if a provided subscriber number exists and is valid in the system.

     * * @param message The network message containing the Subscriber ID/Number (String).

     * @param client  The connection thread representing the client making the request.

     */

/**
 * Verifies if a provided subscriber number exists and is valid in the system.
 * Retrieves the allowed family members limit to enforce quota rules.
 * * @param message The network message containing the Subscriber ID/Number (String).
 * @param client  The connection thread representing the client making the request.
 */
public static void handleVerifySubscriber(Message message, ConnectionToClient client) {
    String subIdStr = (String) message.getData();
    // Response will hold [Boolean (isValid), Integer (familyLimit)]
    ArrayList<Object> response = new ArrayList<>();
    
    try {
        int subId = Integer.parseInt(subIdStr);
        String query = "SELECT family_members FROM gonature_db_new.Subscriber WHERE sub_number = ?";
        Connection conn = DBconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, subId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            response.add(true); // Subscriber exists
            response.add(rs.getInt("family_members")); // Subscriber family limit
            System.out.println("Server: Subscriber " + subId + " verified successfully.");
        } else {
            response.add(false);
            response.add(0);
            System.out.println("Server: Subscriber verification failed for ID: " + subId);
        }
        rs.close();
        pstmt.close();
    } catch (NumberFormatException e) {
        System.err.println("Server: Invalid Subscriber ID format.");
        response.add(false);
        response.add(0);
    } catch (Exception e) {
        System.err.println("Server: Database error during subscriber verification.");
        e.printStackTrace();
        response.add(false);
        response.add(0);
    }
    
    try { 
        client.sendToClient(new Message(MessageType.VERIFY_SUBSCRIBER_RESPONSE, response)); 
    } catch (Exception e) { 
        e.printStackTrace(); 
    }
}



    /**

     * Processes transaction for visitor admissions.

     * Increments designated park capacities dynamically.

     * Evaluates casual vs pre-booked states.

     *

     * @param message The message containing the transaction details.

     * @param client  The specific client communication connection execution thread reference.

     */

public static void handleConfirmPayment(Message message, ConnectionToClient client) {

        // Disassemble the packaged transaction array structure mapped by the logic layer

        ArrayList<Object> paymentData = (ArrayList<Object>) message.getData();

        int amountToAdd = (int) paymentData.get(0);

        String orderToUpdate = (String) paymentData.get(1); 

        String parkToUpdate = (String) paymentData.get(2);

        String visitorType = (String) paymentData.get(3); 

        String visitorId = (String) paymentData.get(4); // Extracted traveler verification identification parameter

        

        // Resolve Subscriber Number to the actual personal ID for consistent database logging

        if ("Subscriber".equals(visitorType) && (orderToUpdate == null || orderToUpdate.isEmpty())) {
        	     		

            try {

                Connection conn = DBconnection.getConnection();

                String findIdQuery = "SELECT id FROM gonature_db_new.Subscriber WHERE sub_number = ?";

                PreparedStatement psFind = conn.prepareStatement(findIdQuery);

                psFind.setInt(1, Integer.parseInt(visitorId));

                ResultSet rsFind = psFind.executeQuery();

                

                if (rsFind.next()) {

                    visitorId = rsFind.getString("id"); // Translate subscriber number to real personal ID

                }

                rsFind.close();

                psFind.close();

            } catch (Exception e) {

                System.err.println("Server: Error translating subscriber number to ID.");

                e.printStackTrace();

            }

        }

        

        String resultOrderId = null;



        try {

            Connection conn = DBconnection.getConnection();

            

            // Step 1: Dynamically increment the specific park real-time occupancy monitoring schema values

            String updatePark = "UPDATE gonature_db_new.Parks SET current_occupancy = current_occupancy + ? WHERE park_name = ?";

            PreparedStatement psPark = conn.prepareStatement(updatePark);

            psPark.setInt(1, amountToAdd);

            psPark.setString(2, parkToUpdate);

            psPark.executeUpdate();

            psPark.close();



         // Step 2: Evaluate scenario properties to manage structural Order table data modifications

            if (orderToUpdate != null && !orderToUpdate.isEmpty()) {

                // Check if the input is purely numeric (Order ID) or contains letters (QR Code)

                String updateOrder;

                PreparedStatement psOrder;



                if (orderToUpdate.matches("\\d+")) {

                    // SCENARIO A1: Pre-booked Order via Numeric ID

                    int oId = Integer.parseInt(orderToUpdate);

                    updateOrder = "UPDATE gonature_db_new.`Order` SET status = 'Entered' WHERE order_number = ?";

                    psOrder = conn.prepareStatement(updateOrder);

                    psOrder.setInt(1, oId);

                } else {

                    // SCENARIO A2: Pre-booked Order via Alphanumeric QR Code

                    updateOrder = "UPDATE gonature_db_new.`Order` SET status = 'Entered' WHERE QR_code = ?";

                    psOrder = conn.prepareStatement(updateOrder);

                    psOrder.setString(1, orderToUpdate);

                }

                

                psOrder.executeUpdate();

                psOrder.close();

                

                // Return the existing order identifier back to the client

                resultOrderId = orderToUpdate;

                

            } else {

                // SCENARIO B: Casual Visitor (Insert New Order Row with a single ID column)

                // We store all identification types (Regular ID, Subscriber, Guide) in the same column

                String insertOrder = "INSERT INTO gonature_db_new.`Order` " +

                                     "(order_date, number_of_visitors, date_of_placing_order, entry_time, status, type_of_visitor, park_name, id) " +

                                     "VALUES (CURDATE(), ?, CURDATE(), CURTIME(), 'Entered', ?, ?, ?)";

                

                PreparedStatement psInsert = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);

                psInsert.setInt(1, amountToAdd);

                psInsert.setString(2, visitorType);

                psInsert.setString(3, parkToUpdate); 

                psInsert.setString(4, visitorId); // Stores Regular ID / Translated Subscriber ID / Guide ID dynamically

                

                psInsert.executeUpdate();

                

                // Extract generated auto-increment key

                ResultSet rsKeys = psInsert.getGeneratedKeys();

                if (rsKeys.next()) {

                    resultOrderId = String.valueOf(rsKeys.getInt(1));

                }

                rsKeys.close();

                psInsert.close();

                

                System.out.println("Server: Casual entry registered. Type: " + visitorType + ", ID: " + visitorId + " -> Assigned Order: " + resultOrderId);

            }

            

        } catch (Exception e) {

            System.err.println("Server: Error during payment confirmation and database transaction persistence routine.");

            e.printStackTrace();

            resultOrderId = null; // Enforce null state resolution configuration mapping outputs

        }

        

        // Step 5: Dispatch the data resolution tracking index string parameter configuration to the client

        try { 

            client.sendToClient(new Message(MessageType.CONFIRM_PAYMENT_RESPONSE, resultOrderId)); 

        } catch (Exception e) { 

            System.err.println("Server: Fatal exception transmitting structural confirmation payload.");

            e.printStackTrace(); 

        }

    }
	/**
  * Fetches all registered subscribers using explicit connect and release pool tracking.
  */
 public static void handleGetSubscribersList(Message message, ConnectionToClient client) {
     ArrayList<Subscriber> subscribersList = new ArrayList<>();
     String query = "SELECT sub_number, fname, lname, email, phone_number, credit_card_number, family_members FROM gonature_db_new.Subscriber";
     
     Connection conn = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     try {
         conn = DBconnection.getConnection(); 
         
         pstmt = conn.prepareStatement(query);
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             Subscriber sub = new Subscriber(
                 rs.getInt("sub_number"),
                 rs.getString("fname"),
                 rs.getString("lname"),
                 rs.getString("email"),
                 rs.getString("phone_number"),
                 rs.getString("credit_card_number"),
                 rs.getInt("family_members")
             );
             subscribersList.add(sub);
         }
         
     } catch (Exception e) {
         System.err.println("Server: Error fetching subscribers list.");
         e.printStackTrace();
     } finally {
         // Safely close DB resources and release the connection back to the pool
         if (rs != null) { try { rs.close(); } catch (Exception e) {} }
         if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
         if (conn != null) { 
             try { 
             	//DBconnection.release(conn);
             	conn.close();
                 //System.out.println("Server: Connection explicitly released back to pool.");
             } catch (Exception e) {} 
         }
     }
     
     // Dispatch the response outside the DB allocation block
     try {
         client.sendToClient(new Message(MessageType.GET_SUBSCRIBERS_LIST_RESPONSE, subscribersList));
     } catch (Exception e) {
         e.printStackTrace();
     }
 }
 
 /**
  * Fetches all system workers rows from the database using explicit pool management.
  * Maps database schema columns directly into common Workers entity models.
  * * @param message The received network tracking message package.
  * @param client  The specific communication thread execution reference for the response.
  */
 public static void handleGetWorkersList(Message message, ConnectionToClient client) {
     ArrayList<Workers> workersList = new ArrayList<>();
     String query = "SELECT fname, lname, email, role, park_name FROM gonature_db_new.Workers";
     
     Connection conn = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     try {
         // 1. Borrow an active connection from your DB pool helper configuration
         conn = DBconnection.getConnection();
         
         pstmt = conn.prepareStatement(query);
         rs = pstmt.executeQuery();
         
         // 2. Iterate through records and populate the collection payload
         while (rs.next()) {
             Workers worker = new Workers(
                 rs.getString("fname"),
                 rs.getString("lname"),
                 rs.getString("email"),
                 rs.getString("role"),
                 rs.getString("park_name")
             );
             workersList.add(worker);
         }
         System.out.println("Server: Retrieved " + workersList.size() + " worker rows from database schema.");
         
     } catch (Exception e) {
         System.err.println("Server: Database failure executing workers table metadata collection query.");
         e.printStackTrace();
     } finally {
         // 3. Clean up database tracking cursors and release resource connection back to pool
         if (rs != null) { try { rs.close(); } catch (Exception e) {} }
         if (pstmt != null) { try { pstmt.close(); } catch (Exception e) {} }
         if (conn != null) { 
             try { 
                 //DBconnection.release(conn);
                 conn.close();
                 //System.out.println("Server: Database connection successfully released back to memory pool allocation.");
             } catch (Exception e) {} 
         }
     }
     
     // 4. Transmit data package back to client outside of active pool connection blocks
     try {
         client.sendToClient(new Message(MessageType.GET_WORKERS_LIST_RESPONSE, workersList));
     } catch (Exception e) {
         System.err.println("Server: Critical error transmitting workers structural payload back to client connection.");
         e.printStackTrace();
     }
 }
 
 
}