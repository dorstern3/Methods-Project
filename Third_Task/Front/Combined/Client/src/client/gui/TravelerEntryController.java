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
	 * Handles the action when the "Back" button is clicked. Returns the user to the
	 * initial role selection screen.
	 * 
	 * @param event The action event triggered by clicking the back button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
	}

	/**
	 * Handles the action when the "Manage Order" button is clicked. Validates the
	 * traveler ID input and authenticates with the server. If successful, navigates
	 * the user to the manage order form.
	 * 
	 * @param event The action event triggered by clicking the manage order button.
	 */
	@FXML
	void clickManageOrder(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();
		OrderLogic orderLogic = new OrderLogic();
		
		
	    if (!orderLogic.checkManageableOrderExists(travelerId)) {
	        lblError.setText("Error: Only travelers with an active upcoming order can access Manage Order.");
	        return;
	    }
		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number first!");
			return;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("Input must contain only numbers!");
			return;
		}

		int length = travelerId.length();
		if (length != 4 && length != 5) {
			lblError.setText("Subscriber number must be exactly 4 digits, ID must be exactly 5 digits.");
			return;
		}

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
	 * 
	 * @param event The action event triggered by clicking the new order button.
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
			lblError.setText("Input must contain only numbers!");
			return;
		}
		int length = travelerId.length();
		if (length != 4 && length != 5) {
			lblError.setText("Subscriber number must be exactly 4 digits, ID must be exactly 5 digits.");
			return;
		}
		System.out.println("Frontend validation passed for ID: " + travelerId);

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
	 * Handles the action when the "Exit Park" button is clicked. Validates the
	 * traveler ID and navigates the user to the visitor exit screen.
	 * 
	 * @param event The action event triggered by clicking the exit park button.
	 */
	@FXML
	public void onExitParkClicked(ActionEvent event) {
		lblError.setText("");
		String travelerId = txtTravelerId.getText();
		OrderLogic orderLogic = new OrderLogic();
		
		// FIXED: Call the current inside-park validator
	    if (!orderLogic.checkActiveCheckInExists(travelerId)) {
	        lblError.setText("Error: Only travelers currently inside the park can access Exit Park.");
	        return;
	    }
		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number first!");
			return;
		}

		if (!travelerId.matches("\\d+")) {
			lblError.setText("Input must contain only numbers!");
			return;
		}
		int length = travelerId.length();
		if (length != 4 && length != 5) {
			lblError.setText("Subscriber number must be exactly 4 digits, ID must be exactly 5 digits.");
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

	
	/**
     * Handles the click event for the "Edit/View Subscriber Details" button.
     * Verifies that the entered subscriber number is valid and exists in the database
     * before navigating the traveler to the subscriber profile editor screen.
     * * @param event The ActionEvent triggered by clicking the edit/view details button.
     */
    @FXML
    public void onEditSubscriberDetailsClicked(ActionEvent event) {
        // Clear any previous error messages on the screen
        lblError.setText("");
        
        // Fetch the input from the traveler text field
        String travelerId = txtTravelerId.getText().trim();

        // 1. Frontend Structural Validations
        if (travelerId.isEmpty()) {
            lblError.setText("Please enter your Subscriber Number to proceed.");
            return;
        }

        if (!travelerId.matches("\\d+")) {
            lblError.setText("Input must contain only numbers!");
            return;
        }

        // Ensure it follows the subscriber number format (exactly 4 digits)
        if (travelerId.length() != 4) {
            lblError.setText("Error: Subscriber number must be exactly 4 digits.");
            return;
        }

        // 2. Database Backend Validation via the EntranceLogic layer
        client.logic.EntranceLogic entranceLogic = new client.logic.EntranceLogic();
        ArrayList<Object> result = entranceLogic.verifySubscriber(travelerId);
        
        // CRITICAL BLOCK: If the result is null, or index 0 returns false, the subscriber does NOT exist!
        if (result == null || result.isEmpty() || !(boolean) result.get(0)) {
            lblError.setText("Access Denied: Subscriber Number not found in the system.");
            return; // Stops execution immediately - will NOT switch screens!
        }

        // 3. Authenticated Context Saving: Pass the valid subscriber ID to the editor window
        client.gui.SubscriberEditorController.currentSubNumber = travelerId;

        // 4. Secure Screen Navigation: Switch only when identity is fully confirmed
        ScreenSwitch.switchScreen("/client/gui/SubscriberEditor.fxml", "Edit Subscriber Profile");
    }
}