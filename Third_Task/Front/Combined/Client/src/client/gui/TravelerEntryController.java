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
	 * screen. * @param event The action event triggered by clicking the button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
	}

	/**
	 * Handles the Manage Order button click. Validates input and proceeds to the
	 * manage order form if identification is successful. * @param event The action
	 * event triggered by clicking the button.
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

		client.logic.TravelerLogic logic = new client.logic.TravelerLogic();
		String loginResult = logic.loginTraveler(travelerId);

		if (!loginResult.equals("SUCCESS")) {
			System.out.println(loginResult);
			lblError.setText(loginResult); // מציג את השגיאה על המסך
			return;
		}

		client.gui.ManageOrderController.currentTravelerId = travelerId;
		ScreenSwitch.switchScreen("/client/gui/ManageOrderForm.fxml", "Manage Order");
	}

	/**
	 * Handles the New Order button click. Validates input and proceeds to the new
	 * order form based on traveler type. * @param event The action event triggered
	 * by clicking the button.
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

		client.logic.TravelerLogic logic = new client.logic.TravelerLogic();
		String dbResult = logic.identifyTraveler(travelerId);

		if (dbResult != null) {
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
		} else {
			lblError.setText("Error identifying traveler. Try again.");
		}
	}

	/**
	 * Handles the Exit Park button click. Validates identification and proceeds to
	 * the exit park screen. * @param event The action event triggered by clicking
	 * the button.
	 */
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
}