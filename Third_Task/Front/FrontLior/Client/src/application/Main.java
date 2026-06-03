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
					
					
		/*try {
			// Open connection to the server
			String ip = "localhost";
			int port = 5555;
			
			List<String> args = getParameters().getRaw();
			if (!args.isEmpty()) {
				ip = args.get(0);
			}
			ClientUI.clientChat = new ClientController(ip, port); 
			ClientUI.clientChat.openConnection(); 
			ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
			
			
			// Load the Dashboard page
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/gui/Dashboard.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			//String css = getClass().getResource("/client/gui/OrderStyles.css").toExternalForm();
			//scene.getStylesheets().add(css);
			
			primaryStage.setTitle("Dashboard");
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(800);
			primaryStage.show();
			
			// Close task while exiting the application with the exit button
			primaryStage.setOnCloseRequest(event ->{
				Platform.exit();
				System.exit(0);
			});
			
		} catch(Exception e) {
			System.out.println("Error: Connection to server failed!");
			e.printStackTrace();
			System.exit(0); 
		}*/
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}