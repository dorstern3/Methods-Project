package application;
	
import java.util.List;

import client.ClientUI;
import client.gui.ScreenSwitch;
import client.logic.ClientController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	
	
	@Override
	public void start(Stage primaryStage) {
		ScreenSwitch.setStage(primaryStage);
		ScreenSwitch.switchScreen("/application/Connection.fxml", "Connect to Server");
		
		// Close task while exiting the application with the exit button
		primaryStage.setOnCloseRequest(event ->{
			Platform.exit();
			System.exit(0);
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}