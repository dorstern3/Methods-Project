package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import common.Message;
import common.MessageType;

import java.util.ArrayList;

import client.ClientUI;
import client.logic.EntranceLogic;
import client.logic.OrderLogic;
import client.logic.ScreenSwitch;

/**
 * Controller for the Traveler Entry screen. Handles the initial identification
 * of travelers locally before directing them to either create a new order or
 * manage an existing order based on their input.
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
	
	@FXML
	private Button btnEditSubscriber;

	/**
	 * Shared helper method to handle structural validation of the input field.
	 * * @param travelerId The extracted raw string from the input text field.
	 * @param mustBeSubscriber Only true if the specific action strictly requires a 4-digit subscriber code.
	 * @return true if the structure is completely valid, false otherwise.
	 */
	private boolean validateInputStructure(String travelerId, boolean mustBeSubscriber) {
		if (travelerId == null || travelerId.trim().isEmpty()) {
			if (mustBeSubscriber) {
				lblError.setText("Please enter your Subscriber Number to proceed.");
			} else {
				lblError.setText("Please enter ID or Subscriber Number first!");
			}
			return false;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("Input must contain only numbers!");
			return false;
		}

		int length = travelerId.length();
		if (mustBeSubscriber) {
			if (length != 4) {
				lblError.setText("Error: Subscriber number must be exactly 4 digits.");
				return false;
			}
		} else {
			if (length != 4 && length != 5) {
				lblError.setText("Subscriber number must be exactly 4 digits, ID must be exactly 5 digits.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Handles the action when the "Back" button is clicked. Returns the user to the
	 * initial role selection screen.
	 * * @param event The action event triggered by clicking the back button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
	}

	/**
	 * Handles the action when the "Manage Order" button is clicked. Validates the
	 * traveler ID input and authenticates with the server. If successful, navigates
	 * the user to the manage order form.
	 * * @param event The action event triggered by clicking the manage order button.
	 */
	@FXML
	void clickManageOrder(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();
		OrderLogic orderLogic = new OrderLogic();
		
		// 1. Business Logic Check
		if (!orderLogic.checkOrderExists(travelerId)) {
			lblError.setText("Error: Only travelers with an active order can access Manage Order.");
			return;
		}
		
		// 2. Structural Input Check
		if (!validateInputStructure(travelerId, false)) {
			return;
		}

		// 3. Server Authentication
		client.logic.TravelerLogic logic = new client.logic.TravelerLogic();
		String loginResult = logic.loginTraveler(travelerId);

		if (!loginResult.equals("SUCCESS")) {
			System.out.println(loginResult);
			lblError.setText(loginResult);
			return;
		}

		client.gui.ManageOrderController.currentTravelerId = travelerId;
		ScreenSwitch.switchScreen("/client/gui/ManageOrderForm.fxml", "Manage Order");
	}

	/**
	 * Handles the action when the "New Order" button is clicked. Validates the
	 * traveler ID and queries the server to determine the specific traveler type
	 * (Regular, Guide, or Subscriber) before proceeding to the booking form.
	 * * @param event The action event triggered by clicking the new order button.
	 */
	@FXML
	void clickNewOrder(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();

		// 1. Structural Input Check
		if (!validateInputStructure(travelerId, false)) {
			return;
		}
		System.out.println("Frontend validation passed for ID: " + travelerId);

		// 2. Server Identification
		client.logic.TravelerLogic logic = new client.logic.TravelerLogic();
		String dbResult = logic.identifyTraveler(travelerId);

		if (dbResult != null) {
			if (dbResult.startsWith("ERROR:")) {
				lblError.setText(dbResult.replace("ERROR:", "").trim());
				return;
			}
			System.out.println("The server returned: " + dbResult);

			client.gui.NewOrderFormController.currentTravelerInfo = dbResult;
			client.gui.NewOrderFormController.currentTravelerId = travelerId;

			ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "New Order");
		} else {
			lblError.setText("Error identifying traveler. Try again.");
		}
	}

	/**
	 * Handles the action when the "Exit Park" button is clicked. Validates the
	 * traveler ID and navigates the user to the visitor exit screen.
	 * * @param event The action event triggered by clicking the exit park button.
	 */
	@FXML
	public void onExitParkClicked(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();
		OrderLogic orderLogic = new OrderLogic();
		
		// 1. Business Logic Check
		if (!orderLogic.checkActiveCheckInExists(travelerId)) {
			lblError.setText("Error: Only travelers currently inside the park can access Exit Park.");
			return;
		}
		
		// 2. Structural Input Check
		if (!validateInputStructure(travelerId, false)) {
			return;
		}

		// 3. Server Authentication
		client.logic.TravelerLogic logic = new client.logic.TravelerLogic();
		String loginResult = logic.loginTraveler(travelerId);

		if (!loginResult.equals("SUCCESS")) {
			System.out.println(loginResult);
			lblError.setText(loginResult);
			return;
		}

		client.gui.ExitParkVisitorController.currentTravelerId = travelerId;
		ScreenSwitch.switchScreen("/client/gui/ExitParkVisitor.fxml", "Visitor Exit");
	}

	/**
	 * Handles the click event for the "Edit/View Subscriber Details" button.
	 * Verifies that the entered subscriber number is valid and exists in the database
	 * before navigating the traveler to the subscriber profile editor screen.
	 * * @param event The ActionEvent triggered by clicking the edit/view details button.
	 */
	@FXML
	public void onEditSubscriberDetailsClicked(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText().trim();

		// 1. Structural Input Check (Strictly must be a 4-digit subscriber)
		if (!validateInputStructure(travelerId, true)) {
			return;
		}

		// 2. Database Backend Validation via the EntranceLogic layer
		client.logic.EntranceLogic entranceLogic = new client.logic.EntranceLogic();
		ArrayList<Object> result = entranceLogic.verifySubscriber(travelerId);
		
		if (result == null || result.isEmpty() || !(boolean) result.get(0)) {
			lblError.setText("Access Denied: Subscriber Number not found in the system.");
			return; 
		}

		client.gui.SubscriberEditorController.currentSubNumber = travelerId;
		ScreenSwitch.switchScreen("/client/gui/SubscriberEditor.fxml", "Edit Subscriber Profile");
	}
}