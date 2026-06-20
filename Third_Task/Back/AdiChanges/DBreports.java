package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import common.CancellationReportRow;
import common.OccupancyReportRow;
import common.TotalVisitorsReportRow;

public class DBreports {
	

    /**
     * Retrieves the daily total visitors report for a specific park, 
     * segmented by regular/subscriber visitors and organized groups.
     */
    public static ArrayList<TotalVisitorsReportRow> getTotalVisitorsReportFromDB(String parkName, String startDate, String endDate) {
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
    public static ArrayList<OccupancyReportRow> getOccupancyReportFromDB(String parkName, String startDate, String endDate){
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
    public static Map<String ,Double[]> getVisitorsReport(String parkName, String startDate, String endDate){
    	
    	Map<String,Double[]> data = new LinkedHashMap<>();
    	
    	String query = "SELECT " +
                "    hours.slot AS time_slot, " +
                "    IFNULL(AVG(CASE WHEN o.type_of_visitor IN ('Regular', 'Subscriber') " +
                "                          THEN TIME_TO_SEC(TIMEDIFF(o.exit_time, o.entry_time)) / 3600 END), 0) AS avg_singles, " +
                "    IFNULL(AVG(CASE WHEN o.type_of_visitor = 'Group' " +
                "                          THEN TIME_TO_SEC(TIMEDIFF(o.exit_time, o.entry_time)) / 3600 END), 0) AS avg_groups " +
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
    public static ArrayList<CancellationReportRow> getCancellationReport(String parkName ,String startDate ,String endDate) {
    	ArrayList<CancellationReportRow> reportList = new ArrayList<>();
    	String query = "SELECT " +
                "    days.day_name AS day_of_week, " +
                "    COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) AS canceled_count, " +
                "    COUNT(CASE WHEN (o.status = 'Pending confirmation' OR o.status = 'Confirmed') AND TIMESTAMP(o.order_date, o.entry_time) < NOW() THEN 1 END) AS noshow_count, " +
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
                "    AND (o.status = 'Canceled' OR ((o.status = 'Pending confirmation' OR o.status = 'Confirmed') AND TIMESTAMP(o.order_date, o.entry_time) < NOW())) " +
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
    public static ArrayList<CancellationReportRow> getParksCancellationReport(String startDate ,String endDate) {
    	ArrayList<CancellationReportRow> reportList = new ArrayList<>();
    	String query ="SELECT p.park_name, "+
    				  "		COUNT(CASE WHEN o.status = 'Canceled' THEN 1 END) as total_canceled, "+
    				  "		COUNT(CASE WHEN (o.status = 'Pending confirmation' OR o.status = 'Confirmed') AND TIMESTAMP(o.order_date, o.entry_time) < NOW() THEN 1 END) AS total_noshow, "+
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
}
