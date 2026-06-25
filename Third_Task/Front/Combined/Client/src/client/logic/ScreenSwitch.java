package client.logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utility class responsible for managing navigation and screen switching across the application.
 * Caches the primary window stage reference and dynamically loads JavaFX FXML layouts.
 */
public class ScreenSwitch {
  
    /** The primary graphical stage window context of the application. */
    private static Stage mainStage;

    /**
     * Stores the application's primary stage reference globally to allow navigation.
     *
     * @param stage The primary Stage instance initialized during application startup.
     */
    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Dynamically loads a given FXML view and updates the main window scene and title properties.
     * Swaps the visible root container context while intercepting potential I/O file system resource failures.
     *
     * @param fxmlPath The explicit resource path mapping to the target FXML file layout.
     * @param title    The descriptive text string to display on the primary window title bar.
     */
    public static void switchScreen(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(ScreenSwitch.class.getResource(fxmlPath));
            mainStage.setScene(new Scene(root));
            mainStage.setTitle(title);
            mainStage.show();
            
        } catch (IOException e) {
            // Log fallback indicator when a designated FXML component resource goes missing
            System.out.println("Failed to load the page: " + fxmlPath);
            e.printStackTrace();
        }
    } 
}