package client.gui;

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
	        mainStage.setScene(new Scene(root));
	        mainStage.setTitle(title);
	        mainStage.show();
	        
	    } catch (IOException e) {
	        System.out.println("Failed to load the page: " + fxmlPath);
	        e.printStackTrace();
	    }
    }
}