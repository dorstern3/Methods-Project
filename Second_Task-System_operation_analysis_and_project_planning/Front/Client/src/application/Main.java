package application;
	
import java.util.List;

import client.ClientUI;
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
		try {
			// Open connection to the server
			String ip = "192.168.1.115";
			int port = 5555;
			
			List<String> args = getParameters().getRaw();
			if (!args.isEmpty()) {
				ip = args.get(0);
			}
			ClientUI.clientChat = new ClientController(ip, port); 
			ClientUI.clientChat.openConnection(); 
			
			// Load the Order page
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/gui/Order.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			String css = getClass().getResource("/client/gui/OrderStyles.css").toExternalForm();
			scene.getStylesheets().add(css);
			
			primaryStage.setTitle("Order Management System");
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
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}