package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBselect {

    public static void main(String[] args) {
        String sql = "SELECT * FROM `Order` LIMIT 1000";

        try {
            // Getting the connection
            Connection conn = DBconnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.print("Order Number: " + rs.getInt("order_number"));
                System.out.println(", Visitors: " + rs.getInt("number_of_visitors"));
            }

            // Stay logged in on shutdown 
            stmt.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}