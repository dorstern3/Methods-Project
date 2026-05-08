package application;
	
import client.ClientUI;
import client.logic.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			ClientUI.clientChat = new ClientController("192.168.1.115", 5555); 
			System.out.println("Connecting to server...");
			ClientUI.clientChat.openConnection(); 
			
			System.out.println("Client connected to server successfully.");

			
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