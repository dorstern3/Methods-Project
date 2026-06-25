package application;

import client.ClientUI;
import client.logic.ClientController;
import client.logic.ScreenSwitch;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the server connection screen.
 */
public class ConnectionController {
	/** TextField for user input of the server's IP address. */
	@FXML private TextField ipField;
	/** TextField for user input of the server's port number. */
	@FXML private TextField portField;
	/** Label to display the current connection status to the user. */
	@FXML private Label status;
	
	/**
	 * Connects to the server using the entered IP and port,
	 * then switches to the Role Selection screen.
	 */
	public void connectToServer() {
		try {
			String ip = ipField.getText();
			int port = Integer.parseInt(portField.getText());
			ClientUI.clientChat = new ClientController(ip, port);
			status.setText("Trying to connect...");
			ClientUI.clientChat.openConnection();
			status.setText("Connected!");
			
			ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
			
		} catch(Exception e) {
			System.out.println("Error: Connection to server failed!");
			status.setText("Failed to connect!"); 
		}
	}
}
