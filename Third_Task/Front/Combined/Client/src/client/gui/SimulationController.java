package client.gui;

import client.logic.ScreenSwitch;
import client.logic.SimulationLogic;
import common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import java.util.ArrayList;

/**
 * Controller for the Simulation Dashboard screen. Handles the execution and
 * presentation of time-based system simulations, such as sending automated
 * reminders and processing timeouts for unconfirmed orders.
 */
public class SimulationController {

	private SimulationLogic simLogic = new SimulationLogic();

	/**
	 * Button to trigger the cleanup of expired waiting list entries for today.
	 */
	@FXML
	private Button btnCleanWaitingList;

	/**
	 * Displays a custom alert dialog with a scrollable text area. This is
	 * particularly useful for displaying long simulation logs or messages that
	 * exceed the standard alert window size.
	 * 
	 * @param title   The title of the alert window.
	 * 
	 * @param header  The header text of the alert.
	 * @param content The detailed message content to be displayed in the scrollable
	 *                area.
	 */
	private void showScrollableAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);

		TextArea textArea = new TextArea(content);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 0);

		alert.getDialogPane().setContent(expContent);
		alert.setResizable(true);
		alert.getDialogPane().setPrefSize(400, 300);

		alert.showAndWait();
	}

	/**
	 * Handles the action to simulate sending reminders for the next day's orders.
	 * Fetches pending reminders from the logic layer and displays the simulated
	 * SMS/Email notifications to the user.
	 * 
	 * @param event The action event triggered by clicking the "Send Reminders"
	 *              button.
	 */
	@FXML
	void handleSendReminders(ActionEvent event) {
		ArrayList<Order> orders = simLogic.getPendingReminders();

		if (orders.isEmpty()) {
			showAlert("Simulation", "No orders scheduled for tomorrow require a reminder.");
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (Order o : orders) {
			sb.append("Title: Simulation\n");
			sb.append("To Email: ").append(o.getEmail()).append(" / Phone: ").append(o.getPhoneNumber()).append("\n");
			sb.append("Message: Dear ").append(o.getVisitorType())
					.append(", friendly reminder for your visit tomorrow at ").append(o.getParkName()).append(".\n");
			sb.append(
					"Please confirm your arrival within 2 hours. Unconfirmed orders will be automatically canceled.\n");
			sb.append("--------------------------------------------------\n");
		}

		showScrollableAlert("Simulation", "Day Before Reminders", sb.toString());
	}

	/**
	 * Handles the action to simulate waitlist confirmation timeouts. Simulates a
	 * scenario where 1 hour has passed, canceling unconfirmed waitlist orders and
	 * notifying the next eligible travelers in line.
	 * 
	 * @param event The action event triggered by clicking the "Waitlist Timeout"
	 *              button.
	 */
	@FXML
	void handleWaitlistTimeout(ActionEvent event) {
		ArrayList<String> messages = simLogic.simulateWaitlistTimeout();

		if (messages.isEmpty()) {
			showAlert("Simulation", "No waitlist timeouts found.");
		} else {
			StringBuilder sb = new StringBuilder();
			for (String msg : messages) {
				sb.append(msg).append("\n--------------------------------------------------\n");
			}
			showScrollableAlert("Simulation", "Waitlist Timeouts", sb.toString());
		}
	}

	/**
	 * Handles the action to simulate pending confirmation timeouts. Simulates a
	 * scenario where 2 hours have passed, automatically canceling orders that were
	 * not confirmed in time.
	 * 
	 * @param event The action event triggered by clicking the "Confirmation
	 *              Timeout" button.
	 */
	@FXML
	void handleConfirmationTimeout(ActionEvent event) {
		ArrayList<String> messages = simLogic.simulateConfirmationTimeout();

		if (messages.isEmpty()) {
			showAlert("Simulation", "No confirmation timeouts found.");
		} else {
			StringBuilder sb = new StringBuilder();
			for (String msg : messages) {
				sb.append(msg).append("\n--------------------------------------------------\n");
			}
			showScrollableAlert("Simulation", "Confirmation Timeouts", sb.toString());
		}
	}

	/**
	 * Handles the action when the "Clean Waiting List for Today" button is clicked.
	 * Requests the server to clean up any expired waiting list entries for the
	 * current day.
	 * 
	 * @param event The action event triggered by the button click.
	 */
	@FXML
	void clickCleanWaitingList(ActionEvent event) {
		System.out.println("Simulation Triggered: Requesting server to clean expired waiting lists...");

		int canceledCount = simLogic.cleanWaitingListForToday();

		if (canceledCount >= 0) {
			showScrollableAlert("Simulation Successful", "Waiting List Cleanup Completed",
					"Successfully scanned the database.\nCanceled " + canceledCount
							+ " expired waiting list entries for today.");
		} else {
			showAlert("Simulation Error", "An error occurred while communicating with the server.");
		}
	}

	/**
	 * Navigates back to the main role selection screen.
	 * 
	 * @param event The action event triggered by clicking the "Back" button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "GoNature - Main");
	}

	/**
	 * Displays a standard simple information alert dialog.
	 * 
	 * @param title   The title of the alert window.
	 * 
	 * @param content The message content to display.
	 */
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}