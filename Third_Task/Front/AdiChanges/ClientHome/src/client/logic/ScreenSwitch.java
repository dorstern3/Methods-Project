package client.logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ScreenSwitch {
  
    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void switchScreen(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(ScreenSwitch.class.getResource(fxmlPath));
            String cssPath = ScreenSwitch.class.getResource("/application/AppStyle.css").toExternalForm();
            
            if (mainStage.getScene() != null) {
                mainStage.getScene().setRoot(root);
                if (!mainStage.getScene().getStylesheets().contains(cssPath)) {
                    mainStage.getScene().getStylesheets().add(cssPath);
                }
            } else {
                Scene scene = new Scene(root);
                scene.getStylesheets().add(cssPath);
                mainStage.setScene(scene);
            }
            
            mainStage.setTitle(title);
            mainStage.show();
            
        } catch (IOException e) {
            System.out.println("Failed to load the page: " + fxmlPath);
            e.printStackTrace();
        }
    }
}

// Updated