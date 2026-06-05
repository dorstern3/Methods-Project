package backend;

import java.sql.Connection;
import java.sql.DriverManager;

public class ServerTestMain {

    private static final int PORT = 5555; 

    public static void main(String[] args) {
        try {
            
            String url = "jdbc:mysql://localhost:3306/gonature_db_new?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "Aa123456"; 
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection dbConnection = DriverManager.getConnection(url, username, password);
            System.out.println("📬 Server DB Connection successful!");

           
            GoNatureServer server = new GoNatureServer(PORT, dbConnection);
            server.listen(); 
            System.out.println("🚀 GoNature Server is running on port " + PORT + " and waiting for clients...");

        } catch (Exception e) {
            System.out.println("❌ Failed to start GoNature Server!");
            e.printStackTrace();
        }
    }
}