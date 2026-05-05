package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateOrderTable {
    
    // הגדרות החיבור (זהות לקובץ הקודם)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gonature_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Aa123456";

    public static void main(String[] args) {
        
        // נתונים לדוגמה שנעדכן (במציאות אלו יגיעו מה-Client)
        int orderToUpdate = 1;          // מספר ההזמנה שרוצים לשנות
        int newVisitorsCount = 10;      // ערך חדש למספר מבקרים
        String newDate = "2026-12-25";  // ערך חדש לתאריך

        System.out.println("Connecting to database to perform UPDATE...");

        // שאילתת ה-SQL עם סימני שאלה כ-Placeholders
        String sql = "UPDATE `Order` SET number_of_visitors = ?, order_date = ? WHERE order_number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // השמת הערכים לתוך סימני השאלה לפי הסדר
            pstmt.setInt(1, newVisitorsCount); // סימן שאלה ראשון
            pstmt.setString(2, newDate);       // סימן שאלה שני
            pstmt.setInt(3, orderToUpdate);    // סימן שאלה שלישי (ה-WHERE)

            // ביצוע העדכון בפועל
            int rowsAffected = pstmt.executeUpdate();

            System.out.println("--------------------------------------------------");
            if (rowsAffected > 0) {
                System.out.println("SUCCESS: Order #" + orderToUpdate + " was updated!");
                System.out.println("Rows affected: " + rowsAffected);
            } else {
                System.out.println("WARNING: No order found with number " + orderToUpdate);
            }
            System.out.println("--------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("ERROR: Update failed.");
            e.printStackTrace();
        }
    }
}