package client.gui;

import java.util.ArrayList;

import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
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

	private ArrayList<Object> currentOrder;

	public static String currentTravelerId = "";

	/**
	 * Initializes the view by disabling action buttons until an order is found.
	 */
	@FXML
	public void initialize() {
		btnConfirm.setDisable(true);
		btnCancel.setDisable(true);
	}

	/**
	 * Searches for an order based on the provided order number and current traveler
	 * ID.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickSearchOrder(ActionEvent event) {
		String orderNumStr = txtOrderNumber.getText();

		if (orderNumStr.isEmpty() || !orderNumStr.matches("\\d+")) {
			showAlert("Error", "Invalid Input", "Please enter a valid numeric Order Number.");
			return;
		}

		int orderNumber = Integer.parseInt(orderNumStr);

		ArrayList<Object> searchParams = new ArrayList<>();
		searchParams.add(orderNumber);
		searchParams.add(currentTravelerId);

		Message msg = new Message(MessageType.FETCH_ORDER_DETAILS, searchParams);
		Message reply = (Message) client.ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.FETCH_ORDER_RESULT) {
			currentOrder = (ArrayList<Object>) reply.getData();

			if (currentOrder != null) {
				lblParkName.setText(currentOrder.get(1).toString());
				lblDate.setText(currentOrder.get(2).toString());
				lblTime.setText(currentOrder.get(3).toString());
				lblVisitors.setText(currentOrder.get(4).toString());
				lblStatus.setText(currentOrder.get(5).toString());

				String status = (String) currentOrder.get(5);

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
	}

	/**
	 * Confirms the selected order and updates its status in the database.
	 * 
	 * @param event The action event.
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

		ArrayList<Object> updateData = new ArrayList<>();
		updateData.add(orderNumber);
		updateData.add(statusToSend);

		Message msg = new Message(MessageType.UPDATE_ORDER_STATUS, updateData);
		Message reply = (Message) client.ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.UPDATE_ORDER_RESULT) {
			ArrayList<Object> result = (ArrayList<Object>) reply.getData();
			boolean isUpdated = (boolean) result.get(0);

			if (isUpdated) {
				showAlert("Success", "Order Updated", "Your order has been successfully updated!");
				lblStatus.setText(statusToSend);
				btnConfirm.setDisable(true);
			} else {
				showAlert("Error", "Update Failed", "Could not update the order. Please try again.");
			}
		}
	}

	/**
	 * Cancels the selected order and checks for the next eligible traveler in the
	 * waiting list.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickCancelOrder(ActionEvent event) {
		int orderNumber = Integer.parseInt(txtOrderNumber.getText());

		ArrayList<Object> updateData = new ArrayList<>();
		updateData.add(orderNumber);
		updateData.add("Canceled");

		Message msg = new Message(MessageType.UPDATE_ORDER_STATUS, updateData);
		Message reply = (Message) client.ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.UPDATE_ORDER_RESULT) {
			ArrayList<Object> result = (ArrayList<Object>) reply.getData();
			boolean isUpdated = (boolean) result.get(0);
			String waitingListMsg = (String) result.get(1);

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
	}

	/**
	 * Returns to the main Traveler Entry menu.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		Message msg = new Message(MessageType.TRAVELER_LOGOUT,currentTravelerId);
		Message response = (Message) client.ClientUI.clientChat.accept(msg);
	    if (response != null && response.getType() == MessageType.LOGOUT_SUCCESS) {
	        ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
	    }
	}

	/**
	 * Displays an error alert dialog.
	 * 
	 * @param title   The alert title.
	 * @param header  The alert header.
	 * @param content The alert message content.
	 */
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Clears the displayed order labels.
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