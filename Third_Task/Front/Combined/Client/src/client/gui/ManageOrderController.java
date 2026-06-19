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
 * Controller for the Manage Order screen. Handles searching, confirming, and
 * canceling existing visitor orders.
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
	 * Initializes the controller. Sets the initial state of action buttons to
	 * disabled.
	 */
	@FXML
	public void initialize() {
		btnConfirm.setDisable(true);
		btnCancel.setDisable(true);
		orderLogic = new OrderLogic();
	}

	/**
	 * Handles the search action for an order number. Fetches order details and
	 * updates the UI accordingly. * @param event The action event triggered by the
	 * search button.
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
				btnCancel.setDisable(false);
			} else if (status.equals("Confirmed")) {
				btnConfirm.setDisable(true);
				btnCancel.setDisable(false);
			} else {
				btnConfirm.setDisable(true);
				btnCancel.setDisable(true);
			}
		} else {
			showAlert("Not Found / Unauthorized", "Order Not Found or Access Denied",
					"No order matches this number and your ID.");
			clearLabels();
		}
	}

	/**
	 * Handles the order confirmation process. * @param event The action event
	 * triggered by the confirm button.
	 */
	@FXML
	void clickConfirmOrder(ActionEvent event) {
		int orderNumber = Integer.parseInt(txtOrderNumber.getText());
		String currentStatus = lblStatus.getText();
		String statusToSend = "";

		if (currentStatus.equals("Pending confirmation")) {
			statusToSend = "Confirmed";
		} else if (currentStatus.equals("Waiting list unconfirmed")) {
			statusToSend = "Pending confirmation";
		}

		Object[] result = orderLogic.updateOrderStatus(orderNumber, statusToSend);
		boolean isUpdated = (boolean) result[0];

		if (isUpdated) {
			showAlert("Success", "Order Updated", "Your order has been successfully updated!");
			lblStatus.setText(statusToSend);
			btnConfirm.setDisable(true);
		} else {
			showAlert("Error", "Update Failed", "Could not update the order. Please try again.");
		}
	}

	/**
	 * Handles the order cancellation process. Notifies the traveler and checks the
	 * waiting list for subsequent updates. * @param event The action event
	 * triggered by the cancel button.
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
	 * Handles the back navigation action. Logs out the current traveler before
	 * returning to the main menu. * @param event The action event triggered by the
	 * back button.
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

	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

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