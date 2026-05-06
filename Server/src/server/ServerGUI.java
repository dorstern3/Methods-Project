package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private EchoServer server;
    
    // רכיבי התצוגה שביקשת[cite: 11]
    private Label ipLabel = new Label("IP: ---");
    private Label hostLabel = new Label("Host: ---");
    private Label statusLabel = new Label("Status: Not Connected");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature Server Monitor");

        // עיצוב בסיסי ונקי
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
            new Label("CONNECTED CLIENT DETAILS:"),
            ipLabel, 
            hostLabel, 
            statusLabel
        );

        Scene scene = new Scene(root, 350, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        // הפעלת השרת על פורט 5555 וחיבורו ל-GUI הזה
        server = new EchoServer(5555, this);
        try {
            server.listen();
        } catch (Exception e) {
            statusLabel.setText("Status: Error starting server");
        }
    }

    /**
     * פונקציה שהשרת קורא לה כדי לעדכן את הממשק[cite: 11, 13]
     */
 // בתוך מחלקת ServerGUI
    public void updateClientDetails(String ip, String host, String status) {
        javafx.application.Platform.runLater(() -> {
            // תחליף את שמות ה-Labels כאן בשמות שהגדרת אצלך
            ipLabel.setText("IP Address: " + ip);
            hostLabel.setText("Host Name: " + host);
            statusLabel.setText("Status: " + status);
        });
    }
}