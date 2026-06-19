package client.gui;

import client.logic.ScreenSwitch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the Role Selection screen. Allows the user to select their
 * role as either a Traveler or a Park Employee.
 */
public class RoleSelectionController {

	@FXML
	private Button btnTraveler;

	@FXML
	private Button btnEmployee;

	/**
	 * Initializes the controller. Removes the default focus from the buttons upon
	 * screen load.
	 */
	@FXML
	public void initialize() {
		Platform.runLater(() -> btnTraveler.getParent().requestFocus());
	}

	/**
	 * Handles the Traveler button click action. Navigates the user to the Traveler
	 * Entry menu. * @param event The action event triggered by clicking the button.
	 */
	@FXML
	void clickTraveler(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
	}

	/**
	 * Handles the Employee button click action. Navigates the user to the Employee
	 * Login screen. * @param event The action event triggered by clicking the
	 * button.
	 */
	@FXML
	void clickEmployee(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/EmployeeLogin.fxml", "Employee Login");
	}
}