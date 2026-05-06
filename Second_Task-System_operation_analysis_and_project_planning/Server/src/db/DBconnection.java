// DB Details
// username: root
// password: Aa123456


package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    // פרטי ההתחברות מרוכזים כאן פעם אחת בלבד
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gonature_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "Aa123456";

    /**
     * פונקציה סטטית שיוצרת ומחזירה חיבור לבסיס הנתונים
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // טעינת הדרייבר
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // יצירת והחזרת החיבור
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}