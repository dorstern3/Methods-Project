package client.gui;

import client.logic.ScreenSwitch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the Role Selection screen. This is the initial entry point of
 * the client application, allowing the user to identify their role as either a
 * Traveler or a Park Employee.
 */
public class RoleSelectionController {

	@FXML
	private Button btnTraveler;

	@FXML
	private Button btnEmployee;

	/**
	 * Initializes the controller class. Removes the default focus from the buttons
	 * immediately upon screen load to provide a cleaner initial UI experience.
	 */
	@FXML
	public void initialize() {
		Platform.runLater(() -> btnTraveler.getParent().requestFocus());
	}

	/**
	 * Handles the action when the Traveler button is clicked. Navigates the user to
	 * the Traveler Entry menu where they can identify themselves. * @param event
	 * The action event triggered by clicking the Traveler button.
	 */
	@FXML
	void clickTraveler(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
	}

	/**
	 * Handles the action when the Employee button is clicked. Navigates the user to
	 * the Employee Login screen for authentication. * @param event The action event
	 * triggered by clicking the Employee button.
	 */
	@FXML
	void clickEmployee(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/EmployeeLogin.fxml", "Employee Login");
	}

	/**
	 * Opens the Simulation Dashboard screen. This is typically used for
	 * demonstrating or testing system background processes (like sending SMS/Email
	 * reminders or handling timeouts). * @param event The action event triggered by
	 * the corresponding UI element.
	 */
	@FXML
	void openSimulationDashboard(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/SimulationDashboard.fxml", "System Simulation");
	}
}