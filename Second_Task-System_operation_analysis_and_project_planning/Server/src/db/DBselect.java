package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBselect {

    public static void main(String[] args) {
        System.out.println("Starting DBselect...");

        // שאילתת השליפה
        String sql = "SELECT * FROM `Order` LIMIT 1000";

        // שימוש ב-try-with-resources כדי לסגור את המשאבים אוטומטית
        try {
            // קבלת החיבור מהקובץ השני
            Connection conn = DBconnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("Connection successful via DbConnection class!");
            System.out.println("Orders retrieved from MySQL:");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                System.out.print("Order Number: " + rs.getInt("order_number"));
                System.out.print(", Date: " + rs.getString("order_date"));
                System.out.println(", Visitors: " + rs.getInt("number_of_visitors"));
            }

            // סגירת החיבור בסיום
            conn.close();
            System.out.println("--------------------------------------------------");
            System.out.println("Database connection closed.");

        } catch (Exception e) {
            System.out.println("Error in DBselect:");
            e.printStackTrace();
        }
    }
}