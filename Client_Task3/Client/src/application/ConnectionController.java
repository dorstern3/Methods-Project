package application;

import client.ClientUI;
import client.gui.ScreenSwitch;
import client.logic.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the connection screen. Handles the configuration and
 * initiation of the connection to the server.
 */
public class ConnectionController {

	@FXML
	private TextField ipField;
	@FXML
	private TextField portField;
	@FXML
	private Label status;

	/**
	 * Attempts to connect to the server using the provided IP address and port. On
	 * success, switches to the Role Selection screen.
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

		} catch (Exception e) {
			System.out.println("Error: Connection to server failed!");
			status.setText("Failed to connect!");
		}
	}
}