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
			ClientUI.clientChat = new ClientController("localhost", 5555); 										// Set a port
			Parent root = FXMLLoader.load(getClass().getResource("/client/gui/Order.fxml"));					// Get order scene
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/client/gui/OrderStyles.css").toExternalForm());	// Load the css page
			primaryStage.setScene(scene);																		// Set the starting page to scene
			primaryStage.sizeToScene();
			// Set the minimum size of the Screen
			primaryStage.setMinHeight(600);
			primaryStage.setMinWidth(800);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
