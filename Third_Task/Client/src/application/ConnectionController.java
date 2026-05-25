package application;

import client.ClientUI;
import client.gui.ScreenSwitch;
import client.logic.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectionController {
	
	@FXML private TextField ipField;
	@FXML private TextField portField;
	@FXML private Label status;
	
	public void connectToServer() {
		try {
			// Open connection to the server
			String ip = ipField.getText();
			int port = Integer.parseInt(portField.getText());
			ClientUI.clientChat = new ClientController(ip, port);
			status.setText("Trying to connect...");
			ClientUI.clientChat.openConnection();
			status.setText("Connected!");
			
			ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
			
		} catch(Exception e) {
			System.out.println("Error: Connection to server failed!");
			status.setText("Failed to connect!"); 
		}
	}
}
