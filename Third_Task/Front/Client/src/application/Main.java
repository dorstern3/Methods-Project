package application;

import client.logic.ScreenSwitch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Main entry point for the GoNature client application.
 */
public class Main extends Application {
	
	/**
	 * Sets up the primary stage and opens the connection screen.
	 * * @param primaryStage the primary stage for this application
	 */
	@Override
	public void start(Stage primaryStage) {
		ScreenSwitch.setStage(primaryStage);
		ScreenSwitch.switchScreen("/application/Connection.fxml", "Connect to Server");
		
		primaryStage.setOnCloseRequest(event ->{
			Platform.exit();
			System.exit(0);
		});
	}
	
	/**
	 * Main method to launch the application.
	 * * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}