package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gonature_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Aa123456";

    // Static rank that will hold the connection of the individual server
    private static Connection conn = null;

    // Function that starts a connection
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                 
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Creating the connection
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println(">>> Database Connection Established");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Database Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }
}