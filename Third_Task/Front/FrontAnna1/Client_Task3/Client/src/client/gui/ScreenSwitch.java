package client.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utility class for switching scenes within the application.
 */
public class ScreenSwitch {

	private static Stage mainStage;

	/**
	 * Sets the primary stage for the application.
	 * 
	 * @param stage The primary stage.
	 */
	public static void setStage(Stage stage) {
		mainStage = stage;
	}

	/**
	 * Loads and displays a new FXML screen.
	 * 
	 * @param fxmlPath The file path to the FXML resource.
	 * @param title    The title to set for the window.
	 */
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