package client.gui;

import java.util.ArrayList;

import client.logic.OrderLogic;
import client.logic.ScreenSwitch;
import common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Manage Order screen. Handles the operations of searching,
 * confirming, and canceling existing visitor orders.
 */
public class ManageOrderController {

	@FXML
	private TextField txtOrderNumber;
	@FXML
	private Button btnSearchOrder;
	@FXML
	private Label lblParkName;
	@FXML
	private Label lblDate;
	@FXML
	private Label lblTime;
	@FXML
	private Label lblVisitors;
	@FXML
	private Label lblStatus;
	@FXML
	private Button btnConfirm;
	@FXML
	private Button btnCancel;
	@FXML
	private Button btnBack;

	private Order currentOrder;
	private OrderLogic orderLogic;

	public static String currentTravelerId = "";

	/**
	 * Initializes the controller. Sets the initial state of the action buttons
	 * (Confirm/Cancel) to disabled and instantiates the OrderLogic layer.
	 */
	@FXML
	public void initialize() {
		btnConfirm.setDisable(true);
		btnCancel.setDisable(true);
		orderLogic = new OrderLogic();
	}

	/**
	 * Handles the search action for a specific order number. Fetches the order
	 * details from the database and updates the UI accordingly. Enables or disables
	 * the action buttons based on the current order status. * @param event The
	 * action event triggered by clicking the Search button.
	 */
	@FXML
	void clickSearchOrder(ActionEvent event) {
		String orderNumStr = txtOrderNumber.getText();

		if (orderNumStr.isEmpty() || !orderNumStr.matches("\\d+")) {
			showAlert("Error", "Invalid Input", "Please enter a valid numeric Order Number.");
			return;
		}

		int orderNumber = Integer.parseInt(orderNumStr);

		currentOrder = orderLogic.fetchOrderDetails(orderNumber, currentTravelerId);

		if (currentOrder != null) {
			lblParkName.setText(currentOrder.getParkName());
			lblDate.setText(currentOrder.getOrderDate());
			lblTime.setText(currentOrder.getEntryTime());
			lblVisitors.setText(String.valueOf(currentOrder.getNumberOfVisitors()));
			lblStatus.setText(currentOrder.getStatus());

			String status = currentOrder.getStatus();

			if (status.equals("Pending confirmation") || status.equals("Waiting list unconfirmed")) {
				btnConfirm.setDisable(false);
			} else {
				btnConfirm.setDisable(true);
			}

			if (status.equals("Canceled") || status.equals("Entered")) {
				btnCancel.setDisable(true);
			} else {
				btnCancel.setDisable(false);
			}
		} else {
			showAlert("Not Found / Unauthorized", "Order Not Found or Access Denied",
					"No order matches this number and your ID.");
			clearLabels();
		}
	}


	/**
	 * Handles the order confirmation process. Updates the status of the current
	 * order from 'Pending confirmation' to 'Confirmed', or from 'Waiting list
	 * unconfirmed' to 'Booked'.
	 * 
	 * @param event The action event triggered by clicking the Confirm button.
	 */
	@FXML
	void clickConfirmOrder(ActionEvent event) {
		int orderNumber = Integer.parseInt(txtOrderNumber.getText());
		String currentStatus = lblStatus.getText();
		String statusToSend = "";

		if (currentStatus.equals("Pending confirmation")) {
			statusToSend = "Confirmed";
		} else if (currentStatus.equals("Waiting list unconfirmed")) {
			statusToSend = "Booked";
		}

		Object[] result = orderLogic.updateOrderStatus(orderNumber, statusToSend);
		boolean isUpdated = (boolean) result[0];

		if (isUpdated) {
			lblStatus.setText(statusToSend);
			btnConfirm.setDisable(true);

			if (currentStatus.equals("Waiting list unconfirmed")) {

				Alert simAlert = new Alert(Alert.AlertType.INFORMATION);
				simAlert.setTitle("Simulation");
				simAlert.setHeaderText("Simulation: SMS & Email Sent");
				simAlert.setContentText("To Email: " + currentOrder.getEmail() + "\n" + "To Phone: "
						+ currentOrder.getPhoneNumber() + "\n\n" + "Your waitlist order has been successfully Booked!\n"
						+ "Order Number: " + orderNumber + "\n" + "Your Entrance QR Code is: QR-" + orderNumber);
				simAlert.showAndWait();
			} else {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Order Updated",
						"Your order has been successfully confirmed!");
			}

		} else {
			showAlert("Error", "Update Failed", "Could not update the order. Please try again.");
		}
	}

	/**
	 * Handles the order cancellation process. Updates the status of the current
	 * order to 'Canceled'. If a spot becomes available, it triggers a simulated
	 * notification (SMS/Email) to the next traveler on the waiting list. * @param
	 * event The action event triggered by clicking the Cancel button.
	 */
	@FXML
	void clickCancelOrder(ActionEvent event) {
		int orderNumber = Integer.parseInt(txtOrderNumber.getText());

		Object[] result = orderLogic.updateOrderStatus(orderNumber, "Canceled");
		boolean isUpdated = (boolean) result[0];
		String waitingListMsg = (String) result[1];

		if (isUpdated) {
			Alert simAlert = new Alert(Alert.AlertType.INFORMATION);
			simAlert.setTitle("Simulation");
			simAlert.setHeaderText("SMS/Email sent to traveler");
			simAlert.setContentText("Your order #" + orderNumber + " has been successfully canceled.");
			simAlert.showAndWait();

			lblStatus.setText("Canceled");
			btnConfirm.setDisable(true);
			btnCancel.setDisable(true);

			if (waitingListMsg != null) {
				Alert waitlistAlert = new Alert(Alert.AlertType.INFORMATION);
				waitlistAlert.setTitle("Waiting List Simulation");
				waitlistAlert.setHeaderText("SMS/Email sent to the NEXT traveler");
				waitlistAlert.setContentText(waitingListMsg);
				waitlistAlert.showAndWait();
			}
		} else {
			showAlert("Error", "Update Failed", "Could not cancel the order. Please try again.");
		}
	}

	/**
	 * Handles the back navigation action. Logs out the current traveler from the
	 * system and returns to the Traveler main menu. * @param event The action event
	 * triggered by clicking the Back button.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		boolean success = orderLogic.logoutTraveler(currentTravelerId);

		if (success) {
			ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
		} else {
			System.out.println("Error: Could not logout traveler safely.");
		}
	}

	/**
	 * Displays a standard error alert dialog to the user. * @param title The title
	 * of the alert window.
	 * 
	 * @param header  The header text of the alert.
	 * @param content The main message content to display.
	 */
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Displays a customizable alert dialog to the user based on the specified alert
	 * type. * @param alertType The type of the alert (e.g., INFORMATION, ERROR,
	 * WARNING).
	 * 
	 * @param title   The title of the alert window.
	 * @param header  The header text of the alert.
	 * @param content The main message content to display.
	 */
	private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Clears all data from the display labels and disables the action buttons. Used
	 * when an order is not found or when resetting the view.
	 */
	private void clearLabels() {
		lblParkName.setText("");
		lblDate.setText("");
		lblTime.setText("");
		lblVisitors.setText("");
		lblStatus.setText("");
		btnConfirm.setDisable(true);
		btnCancel.setDisable(true);
	}
}