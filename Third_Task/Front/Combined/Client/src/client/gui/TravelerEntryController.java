package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import common.Message;
import common.MessageType;
import client.ClientUI;
import client.logic.ScreenSwitch;

/**
 * Controller for the Traveler Entry screen. Handles traveler identification
 * locally before proceeding to order creation or management.
 */
public class TravelerEntryController {

	@FXML
	private Button btnBack;

	@FXML
	private Button btnManageOrder;

	@FXML
	private Button btnNewOrder;

	@FXML
	private TextField txtTravelerId;

	@FXML
	private Label lblError;
	
	@FXML
	private Button btnEbtnExit;

	/**
	 * Handles the Back button click, returning the user to the role selection
	 * screen.
	 * 
	 * @param event The action event triggered by clicking the button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
	}

	/**
	 * Validates the traveler ID and navigates to the order management screen.
	 * 
	 * @param event The action event triggered by clicking the button.
	 */
	@FXML
	void clickManageOrder(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();

		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number first!");
			return;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("ID must contain only numbers!");
			return;
		}

		client.gui.ManageOrderController.currentTravelerId = travelerId;
		Message msg = new Message(MessageType.TRAVELER_LOGIN,travelerId);
		Message response = (Message) client.ClientUI.clientChat.accept(msg);
		if(response.getType() == MessageType.LOGIN_FAILED) {
			System.out.println((String) response.getData());
			return;
		}
		ScreenSwitch.switchScreen("/client/gui/ManageOrderForm.fxml", "Manage Order");
	}

	/**
	 * Validates the ID and queries the server to identify the traveler type, then
	 * navigates to the new order screen.
	 * 
	 * @param event The action event triggered by clicking the button.
	 */
	@FXML
	void clickNewOrder(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();

		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number!");
			return;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("ID must contain only numbers!");
			return;
		}

		System.out.println("Frontend validation passed for ID: " + travelerId);

		Message messageToServer = new Message(MessageType.IDENTIFY_TRAVELER, travelerId);
		Object response = ClientUI.clientChat.accept(messageToServer);

		if (response instanceof Message) {
			Message responseMsg = (Message) response;

			if (responseMsg.getType() == MessageType.IDENTIFY_TRAVELER_RESPONSE) {

				String dbResult = (String) responseMsg.getData();
				System.out.println("The server returned: " + dbResult);

				client.gui.NewOrderFormController.currentTravelerInfo = dbResult;
				client.gui.NewOrderFormController.currentTravelerId = travelerId;

				if (dbResult.startsWith("Subscriber:")) {
					System.out.println("Moving to Order Screen as a Subscriber...");
				} else if (dbResult.startsWith("Guide:")) {
					System.out.println("Moving to Order Screen as a Guide...");
				} else {
					System.out.println("Moving to Order Screen as a Regular Traveler...");
				}

				ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "New Order");
			}
		}
	}
	@FXML
	public void onExitParkClicked(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();

		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number first!");
			return;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("ID must contain only numbers!");
			return;
		}
		Message msg = new Message(MessageType.TRAVELER_LOGIN,travelerId);
		Message response = (Message) client.ClientUI.clientChat.accept(msg);
		if(response.getType() == MessageType.LOGIN_FAILED) {
			System.out.println((String) response.getData());
			lblError.setText((String) response.getData());
			return;
		}
		client.gui.ExitParkVisitorController.currentTravelerId = travelerId;
		
	    ScreenSwitch.switchScreen("/client/gui/ExitParkVisitor.fxml", "Visitor Exit");
	}
}