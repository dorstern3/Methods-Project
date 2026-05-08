package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gonature_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Aa123456";

    // משתנה סטטי שיחזיק את החיבור היחיד של השרת
    private static Connection conn = null;

    /**
     * מחזירה את החיבור הקיים, או יוצרת חדש אם אין כזה.
     */
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                // טעינת הדרייבר
                Class.forName("com.mysql.cj.jdbc.Driver");
                // יצירת החיבור
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println(">>> Database Connection Established (Singleton).");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Database Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }
}