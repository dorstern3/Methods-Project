package server;

import java.io.*;
import ocsf.server.*;
import db.DBconnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import common.CancellationReportRow;
import common.Message;
import common.MessageType;
import common.OccupancyReportRow;
import common.Order;
import common.TotalVisitorsReportRow;

// Logic from the OCSF folder
public class EchoServer extends AbstractServer {
    
    private ServerGUI gui;

    public EchoServer(int port, ServerGUI gui) {
        super(port);
        this.gui = gui;
    }

    // method that is invoked whenever a message is received from a client and routes it to the appropriate action
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received from client: " + msg);
        try {
        	// If the message is a string, the server assumes it is a search for orders by subscription ID
            if (msg instanceof String) {
                ArrayList<Order> orders = getOrdersBySubscriberId((String) msg);
                client.sendToClient(orders.isEmpty() ? "NOT_FOUND" : orders);
            } 
            // If the message is an order object, the server updates the order details in the database
            else if (msg instanceof Order) {
                Order ord = (Order) msg;
                boolean success = updateOrderDetails(ord.getOrderNumber(), ord.getOrderDate(), ord.getNumberOfVisitors());
                client.sendToClient(success ? "UPDATE_SUCCESS" : "UPDATE_FAILED");
            }
            else if (msg instanceof Message) {
            	Message message = (Message) msg;
            	switch(message.getType()) {
	            	case GET_PARKS:{
	            		ArrayList<String> parks = getParksFromDB();
	            		client.sendToClient(new Message(MessageType.GET_PARKS_RESPONSE, parks));
	                    break;
	            	}
	            	case GET_TOTAL_VISITOR_REPORT: {
	            		String[] params = (String[]) message.getData();
	                    ArrayList<TotalVisitorsReportRow> reportData = getTotalVisitorsReportFromDB(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_TOTAL_VISITOR_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_OCCUPANCY_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    ArrayList<OccupancyReportRow> reportData = getOccupancyReportFromDB(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_OCCUPANCY_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_VISITOR_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    Map<String, Double[]> reportData = getVisitorsReport(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_VISITOR_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                
	                case GET_CANCELLATION_REPORT: {
	                	String[] params = (String[]) message.getData();
	                    ArrayList<CancellationReportRow> reportData = getCancellationReport(params[0], params[1], params[2]);
	                    client.sendToClient(new Message(MessageType.GET_CANCELLATION_REPORT_RESPONSE, reportData));
	                    break;
	                }
	                case GET_PARKS_CANCELLATION_REPORT:{
	                	String[] params = (String[]) message.getData();
	                	ArrayList<CancellationReportRow> reportData = getParksCancellationReport(params[0],params[1]);
	                	client.sendToClient(new Message(MessageType.GET_PARKS_CANCELLATION_REPORT_RESPONSE, reportData));
		                break;
	                }
	                default:
	                	System.out.println("Server: Unknown message type received.");
	                    break;
	            }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Retrieving a list of orders from the database for a specific subscriber
    private ArrayList<Order> getOrdersBySubscriberId(String subId) {
        ArrayList<Order> ordersList = new ArrayList<>();
        String query = "SELECT * FROM `order` WHERE subscriber_id = ?";
        
        //Singleton connection: note that the connection remains open and is not closed at the end of the operation
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
        String query = "UPDATE `order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
            pstmt.setInt(2, visitors);
            pstmt.setInt(3, orderNum);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
 
    /* ----------------------------------------------------------------------------------------------------  */   

    /**
     * Retrieves the daily total visitors report for a specific park, 
     * segmented by regular/subscriber visitors and organized groups.
     */
    private ArrayList<TotalVisitorsReportRow> getTotalVisitorsReportFromDB(String parkName, String startDate, String endDate) {
        ArrayList<TotalVisitorsReportRow> reportList = new ArrayList<>();
        
        String query = "SELECT o.order_date, " +
                       "       SUM(CASE WHEN o.type_of_visitor IN ('Regular', 'Subscriber') THEN o.number_of_visitors ELSE 0 END) AS regular_sum, " +
                       "       SUM(CASE WHEN o.type_of_visitor = 'Group' THEN o.number_of_visitors ELSE 0 END) AS group_sum " +
                       "FROM `Order` o " +
                       "WHERE o.status = 'Entered' " +
                       "  AND o.park_name = ? " +
                       "  AND o.order_date BETWEEN ? AND ? " + 
                       "GROUP BY o.order_date " +
                       "ORDER BY o.order_date ASC;";

        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, parkName);
            ps.setString(2, startDate); 
            ps.setString(3, endDate);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getString("order_date");
                    int regular = rs.getInt("regular_sum");
                    int group = rs.getInt("group_sum");
                    
                    reportList.add(new TotalVisitorsReportRow(date, regular, group));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
        return reportList;
    }
    
    /**
     * Generates an occupancy report listing days when the park was not full.
     * Uses a recursive to dynamically generate all dates in the range, 
     * ensuring days with 0 visitors are included.
     */
    private ArrayList<OccupancyReportRow> getOccupancyReportFromDB(String parkName, String startDate, String endDate){
    	ArrayList<OccupancyReportRow> reportList = new ArrayList<>();
    	
    	String query = "WITH RECURSIVE DateRange AS ( " +
                "    SELECT ? AS visit_date " +
                "    UNION ALL " +
                "    SELECT DATE_ADD(visit_date, INTERVAL 1 DAY) " +
                "    FROM DateRange " +
                "    WHERE visit_date < ? " +
                ") " +
                "SELECT " +
                "    dr.visit_date, " +
                "    IFNULL(SUM(o.number_of_visitors), 0) AS total_daily_visitors, " +
                "    IFNULL(ROUND(((SUM(o.number_of_visitors) / p.max_capacity) * 100), 1), 0.0) AS capacity_precentage " +
                "FROM DateRange dr " +
                "LEFT JOIN `order` o ON o.order_date = dr.visit_date " +
                "                   AND o.park_name = ? " +
                "                   AND o.status = 'Entered' " +
                "LEFT JOIN Parks p ON p.park_name = ? " +
                "GROUP BY dr.visit_date, p.max_capacity " +
                "HAVING total_daily_visitors < IFNULL(p.max_capacity, 999999) " +
                "ORDER BY dr.visit_date ASC;";
    	
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
               
    			ps.setString(1, startDate);
    			ps.setString(2, endDate);
    			ps.setString(3, parkName);
    			ps.setString(4, parkName);
    		 
               
               try (ResultSet rs = ps.executeQuery()) {
                   while (rs.next()) {
                       String date = rs.getString("visit_date");
                       int totalDailyVisitors = rs.getInt("total_daily_visitors");
                       float capacityPrecentage = rs.getFloat("capacity_precentage");
                       
                       reportList.add(new OccupancyReportRow(date,  totalDailyVisitors, capacityPrecentage));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
    	return reportList;
    }
    
    /**
     * Retrieves the average stay duration (in hours) for a specific park.
     * Data is categorized by entry hour slots, distinguishing singles from groups.
     */
    private Map<String ,Double[]> getVisitorsReport(String parkName, String startDate, String endDate){
    	
    	Map<String,Double[]> data = new LinkedHashMap<>();
    	
    	String query = "SELECT " +
                "    hours.slot AS time_slot, " +
                "    ROUND(IFNULL(AVG(CASE WHEN o.type_of_visitor IN ('Regular', 'Subscriber') " +
                "                          THEN TIME_TO_SEC(TIMEDIFF(o.exit_time, o.entry_time)) / 3600 END), 0)) AS avg_singles, " +
                "    ROUND(IFNULL(AVG(CASE WHEN o.type_of_visitor = 'Group' " +
                "                          THEN TIME_TO_SEC(TIMEDIFF(o.exit_time, o.entry_time)) / 3600 END), 0)) AS avg_groups " +
                "FROM ( " +
                "    SELECT '08:00' AS slot UNION SELECT '09:00' UNION SELECT '10:00' UNION " +
                "    SELECT '11:00' UNION SELECT '12:00' UNION SELECT '13:00' UNION " +
                "    SELECT '14:00' UNION SELECT '15:00' UNION SELECT '16:00' UNION " +
                "    SELECT '17:00' UNION SELECT '18:00' UNION SELECT '19:00' " +
                ") hours " +
                "LEFT JOIN `Order` o ON " +
                "    hours.slot = DATE_FORMAT(o.entry_time, '%H:00') " +
                "    AND o.park_name = ? " +  
                "    AND o.order_date BETWEEN ? AND ? " + 
                "    AND o.status = 'Entered' " +
                "    AND o.exit_time IS NOT NULL " +
                "GROUP BY hours.slot " +
                "ORDER BY hours.slot ASC;";
    	
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
               
               ps.setString(1, parkName);
               ps.setString(2, startDate); 
               ps.setString(3, endDate);
               
               try (ResultSet rs = ps.executeQuery()) {
                   while (rs.next()) {
                       String hour = rs.getString("time_slot");
                       double avgSingles = rs.getDouble("avg_singles");
                       double avgGroup = rs.getDouble("avg_groups");
                       
                       data.put(hour, new Double[]{avgSingles , avgGroup});
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
    	return data;
    }
    
    
    /**
     * Generates a cancellation and no-show report for a specific park, 
     * aggregated by days of the week (Sunday-Saturday).
     */
    private ArrayList<CancellationReportRow> getCancellationReport(String parkName ,String startDate ,String endDate) {
    	ArrayList<CancellationReportRow> reportList = new ArrayList<>();
    	String query = "SELECT " +
                "    days.day_name AS day_of_week, " +
                "    COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) AS canceled_count, " +
                "    COUNT(CASE WHEN o.status = 'Pending confirmation' AND TIMESTAMP(o.order_date, o.entry_time) < NOW() THEN 1 END) AS noshow_count, " +
                "    ROUND(IFNULL(COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) / COUNT(DISTINCT o.order_date), 0), 1) AS avg_canceled_per_day " +
                "FROM ( " +
                "    SELECT 'Sunday' AS day_name, 1 AS day_num UNION " +
                "    SELECT 'Monday', 2 UNION " +
                "    SELECT 'Tuesday', 3 UNION " +
                "    SELECT 'Wednesday', 4 UNION " +
                "    SELECT 'Thursday', 5 UNION " +
                "    SELECT 'Friday', 6 UNION " +
                "    SELECT 'Saturday', 7 " +
                ") days " +
                "LEFT JOIN `Order` o ON " +
                "    DAYNAME(o.order_date) = days.day_name " +
                "    AND (o.status = 'Canceled' OR (o.status = 'Pending confirmation' AND TIMESTAMP(o.order_date, o.entry_time) < NOW())) " +
                "    AND o.park_name = ? " +
                "    AND o.order_date BETWEEN ? AND ? " +
                "GROUP BY days.day_name, days.day_num " +
                "ORDER BY days.day_num ASC;";
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
               
               ps.setString(1, parkName);
               ps.setString(2, startDate); 
               ps.setString(3, endDate);
               
               try (ResultSet rs = ps.executeQuery()) {
                   while (rs.next()) {
                       String day = rs.getString("day_of_week");
                       int canceledCount = rs.getInt("canceled_count");
                       int noshowCount = rs.getInt("noshow_count");
                       float avgCanceledPerDay = rs.getFloat("avg_canceled_per_day");
                       
                       
                       reportList.add(new CancellationReportRow(day,  canceledCount, noshowCount,avgCanceledPerDay));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
    	return reportList;
    }
    
    /**
     * Generates a global cancellation report across ALL parks for the department manager.
     * Aggregates cancellations and no-shows per individual park.
     */
    private ArrayList<CancellationReportRow> getParksCancellationReport(String startDate ,String endDate) {
    	ArrayList<CancellationReportRow> reportList = new ArrayList<>();
    	String query ="SELECT p.park_name, "+
    				  "		COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) as total_canceled, "+
    				  "		COUNT(CASE WHEN o.status = 'Pending confirmation' AND TIMESTAMP(o.order_date, o.entry_time) < NOW() THEN 1 END) AS total_noshow, "+
    				  "		ROUND(IFNULL(COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) / COUNT(DISTINCT o.order_date), 0), 1) AS avg_canceled_per_day "+
    				  "FROM `Parks` p " + "LEFT JOIN `Order` o ON o.park_name = p.park_name " + "AND o.order_date BETWEEN ? AND ? " +
    				  "GROUP BY p.park_name "+
    				  "ORDER BY p.park_name ASC;";
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
               ps.setString(1, startDate); 
               ps.setString(2, endDate);
               
               try (ResultSet rs = ps.executeQuery()) {
                   while (rs.next()) {
                       String parkName = rs.getString("park_name");
                       int canceledCount = rs.getInt("total_canceled");
                       int noshowCount = rs.getInt("total_noshow");
                       float avgCanceledPerDay = rs.getFloat("avg_canceled_per_day");
                       
                       
                       reportList.add(new CancellationReportRow(parkName,  canceledCount, noshowCount,avgCanceledPerDay));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
    	return reportList;
    }
    
    /**
     * Fetches the names of all registered parks from the database.
     */
    private ArrayList<String> getParksFromDB(){
    	ArrayList<String> parks = new ArrayList<>();
    	String query = "SELECT park_name FROM parks";
    	try (Connection conn = DBconnection.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(query)) {
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
    
    
 /* ----------------------------------------------------------------------------------------------------  */   
    
    //Action performed as soon as a new customer connects: saving their details and updating the GUI
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

    //Handling client disconnection in the event of an error or forced disconnection
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