package db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateOrderTable {
    
	// Update Order
    public static void updateOrder(int orderToUpdate, int newVisitorsCount, String newDate) {
        String sql = "UPDATE `Order` SET number_of_visitors = ?, order_date = ? WHERE order_number = ?";

        try {
            // Using the systems permanent connection
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, newVisitorsCount);
            pstmt.setString(2, newDate);
            pstmt.setInt(3, orderToUpdate);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("SUCCESS: Order #" + orderToUpdate + " was updated!");
            }
            
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}