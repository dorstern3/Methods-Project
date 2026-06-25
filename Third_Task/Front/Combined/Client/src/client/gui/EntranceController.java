package client.gui;

import java.util.ArrayList;

import client.logic.CurUser;

import client.logic.EntranceLogic;

import client.logic.ScreenSwitch;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;

import javafx.scene.control.Label;

import javafx.scene.control.TextField;

import javafx.scene.layout.VBox;

/**
 * Controller class for the Park Entrance screen. Manages processing park
 * entries, validating pre-booked orders and casual visitors, calculating
 * prices, and tracking real-time park occupancy.
 */
public class EntranceController {

	@FXML
	private TextField orderIdInput;

	@FXML
	private ComboBox<String> visitorTypeCombo;

	@FXML
	private TextField casualAmountInput;

	@FXML
	private TextField casualIdInput;

	@FXML
	private Label messageLabel;

	@FXML
	private VBox invoiceSection;

	@FXML
	private Label finalPriceLabel;

	@FXML
	private Label lblLiveCapacity;

	private EntranceLogic entranceLogic;

	private int currentTransactionAmount = 0;

	private String currentTransactionOrderId = null;

	private String currentVisitorType = null;

	private String currentParkName = CurUser.getParkName();

	/**
	 * Initializes the controller, configures UI components, and updates live
	 * capacity.
	 */
	@FXML
	public void initialize() {

		entranceLogic = new EntranceLogic();

		if (invoiceSection != null) {

			invoiceSection.setVisible(false);

		}

		if (visitorTypeCombo != null) {

			visitorTypeCombo.getItems().addAll("Regular", "Subscriber", "Group");

			if (casualIdInput != null) {
				casualIdInput.setDisable(false);
				casualIdInput.setPromptText("Select Visitor Type First");
				visitorTypeCombo.setOnAction(event -> {
					String selected = visitorTypeCombo.getValue();

					if (selected != null) {

						casualIdInput.setDisable(false); 

						if ("Subscriber".equals(selected)) {

							casualIdInput.setPromptText("Enter Subscriber Number (Required)");

						} else if ("Group".equals(selected)) {

							casualIdInput.setPromptText("Enter Guide ID (Required)");

						} else {
							casualIdInput.setPromptText("Enter Visitor ID (Required)");

						}

					}

				});

			}

		}
		updateLiveCapacity();

	}

	/**
	 * Validates a pre-booked order and displays the invoice if valid.
	 * @param event the action event triggered by the verify button
	 */
	@FXML

	public void onCheckOrderClicked(ActionEvent event) {

		resetTransactionState();

		hideInvoice();

		String inputId = orderIdInput.getText().trim();

		if (inputId.isEmpty()) {

			showMessage("Please enter an Order ID or scan/enter a QR Code.", "red");

			return;

		}

		Object result = entranceLogic.validateOrder(inputId);

		if (result instanceof Object[]) {

			Object[] orderDetails = (Object[]) result;

			int visitorsInOrder = (int) orderDetails[0];

			String dynamicVisitorType = (String) orderDetails[1];

			currentTransactionOrderId = inputId;

			currentTransactionAmount = visitorsInOrder;

			currentVisitorType = dynamicVisitorType;

			showMessage("Order verified! Type identified as: " + dynamicVisitorType, "green");

			double calculatedPrice = entranceLogic.calculatePrice(dynamicVisitorType, currentTransactionAmount, true);

			showInvoice(String.format("%.2f NIS", calculatedPrice));

		}

		else if (result instanceof String) {

			hideInvoice();

			String errorType = (String) result;

			switch (errorType) {

			case "NOT_FOUND":

				showMessage("Error: Invalid Order ID or QR Code.", "red");

				break;

			case "WRONG_DATE":

				showMessage("Error: This order is not scheduled for today.", "red");

				break;

			case "TIME_PASSED":

				showMessage("Error: More than an hour has passed since the scheduled entry time.", "red");

				break;

			case "TOO_EARLY":

				showMessage("Error: Early entrance is only permitted 1 hour before the booked time.", "red");

				break;

			case "NOT_CONFIRMED":

				showMessage("Error: This order has not been confirmed yet.", "red");

				break;

			case "INVALID_FORMAT":

				showMessage("Error: Please enter a valid numeric Order number", "red");

				break;

			default:

				showMessage("System Error: Could not validate the order.", "red");

				break;

			}

		}

	}

	/**
	 * Validates casual visitor details and capacity, then displays the invoice.
	 * @param event the action event triggered by the check button
	 */
	@FXML
	public void onCheckCasualClicked(ActionEvent event) {
		resetTransactionState();
		hideInvoice();
		String type = visitorTypeCombo.getValue();
		String amountStr = casualAmountInput.getText().trim();
		String casualId = casualIdInput.getText().trim();

		if (type == null || amountStr.isEmpty()) {
			showMessage("Please select visitor type and amount.", "red");
			return;
		}

		if (casualId.isEmpty()) {
			showMessage("Visitor ID / Subscriber Number is required.", "red");
			return;
		}

		if (!casualId.matches("[0-9]+")) {
			hideInvoice();
			showMessage("Error: ID must contain only numbers.", "red");
			return;
		}

		int amount;
		try {
			amount = Integer.parseInt(amountStr);
			if (amount <= 0 || amount > 15) {
				hideInvoice();
				showMessage("Error: Amount must be between 1 and 15.", "red");
				return;
			}
		} catch (NumberFormatException e) {
			showMessage("Please enter a valid number of visitors.", "red");
			return;
		}

		if (amount > 1 && "Regular".equals(type)) {
			hideInvoice();
			showMessage("Regular invite is limited to one person.", "red");
			return;
		}

		if ("Subscriber".equals(type)) {
			if (casualId.length() != 4) {
				hideInvoice();
				showMessage("Error: Subscriber Number must be exactly 4 digits.", "red");
				return;
			}

			ArrayList<Object> subData = entranceLogic.verifySubscriber(casualId);

			if (subData == null || !(boolean) subData.get(0)) {
				hideInvoice();
				showMessage("Verification Failed: Subscriber ID not found.", "red");
				return;
			}

			int familyLimit = (int) subData.get(1);
			if (amount > familyLimit) {
				hideInvoice();
				showMessage("Error: Subscriber plan allows only " + familyLimit + " members.", "red");
				return;
			}

		} else if ("Group".equals(type)) {
			if (casualId.length() != 5) {
				hideInvoice();
				showMessage("Error: Guide ID must be exactly 5 digits.", "red");
				return;
			}
			if (!entranceLogic.verifyGuide(casualId)) {
				hideInvoice();
				showMessage("Verification Failed: Invalid Guide ID.", "red");
				return;
			}
		} else {
			if (casualId.length() != 5) {
				hideInvoice();
				showMessage("Error: Visitor ID must be exactly 5 digits.", "red");
				return;
			}
		}

		if (entranceLogic.checkCasualAvailability(amount, currentParkName)) {
			currentTransactionAmount = amount;
			currentTransactionOrderId = null;
			currentVisitorType = type;

			showMessage("Space available! Proceed to payment.", "green");
			double calculatedPrice = entranceLogic.calculatePrice(type, amount, false);
			showInvoice(String.format("%.2f NIS", calculatedPrice));
		} else {
			hideInvoice();
			showMessage("Notice: The park is currently at maximum capacity.", "red");
		}
	}

	/**
	 * Finalizes the entry registration process.
	 * @param event the action event triggered by the confirm button
	 */
	@FXML

	public void onConfirmPaymentClicked(ActionEvent event) {

		String visitorId = casualIdInput.getText().trim();

		if (currentTransactionOrderId == null || currentTransactionOrderId.isEmpty()) {

			if (visitorId.isEmpty()) {

				showMessage("Please enter a valid ID / Subscriber Number first!", "red");

				return;

			}

		} else {

			visitorId = "";

		}

		String generatedOrderId = entranceLogic.confirmPayment(

				currentTransactionAmount,

				currentTransactionOrderId,

				currentParkName,

				currentVisitorType,

				visitorId

		);

		if (generatedOrderId != null) {

			if (currentTransactionOrderId == null || currentTransactionOrderId.isEmpty()) {

				String computedQrCode = "QR-" + generatedOrderId;

				showMessage("Payment registered. Casual Entry confirmed! Order ID: " + generatedOrderId + " | QR Code: "
						+ computedQrCode, "green");

			} else {

				showMessage("Payment registered. Pre-booked Entry confirmed successfully!", "green");

			}

			hideInvoice();

			resetTransactionState();

			casualAmountInput.clear();

			casualIdInput.clear();

			if (orderIdInput != null) {

				orderIdInput.clear();

			}

			if (visitorTypeCombo != null) {

				visitorTypeCombo.getSelectionModel().clearSelection();

			}


			updateLiveCapacity();

		} else {

			showMessage("System Error: Could not complete registration updates.", "red");

		}

	}

	/**
	 * Manually triggers an update of the live park capacity.
	 * @param event the action event triggered by the refresh button
	 */
	@FXML
	public void onRefreshCapacityClicked(ActionEvent event) {

		updateLiveCapacity();

	}

	/**
	 * 
	 * Sends a request to the server to fetch the current live occupancy and maximum
	 * capacity of the park. 
	 * Updates the UI labels dynamically 
	 */
	private void updateLiveCapacity() {
		if (lblLiveCapacity == null) {
			return;
		}

		// Creating a new background Thread to prevent UI freezing while waiting for the
		// server response

		new Thread(() -> {
			try {
				String parkName = CurUser.getParkName();
				if (parkName == null || parkName.isEmpty()) {
					parkName = "Banias"; // Fallback
				}
				common.Message request = new common.Message(common.MessageType.GET_PARK_OCCUPANCY, parkName);
				common.Message response = (common.Message) client.ClientUI.clientChat.accept(request);
				if (response != null && response.getType() == common.MessageType.GET_PARK_OCCUPANCY_RESPONSE) {
					int[] capacityData = (int[]) response.getData();
					int current = capacityData[0];
					int max = capacityData[1];
					final String finalParkName = parkName;
					javafx.application.Platform.runLater(() -> {
						lblLiveCapacity.setText(
								String.format("Current Park Occupancy (%s): %d / %d", finalParkName, current, max));
						if (current >= max) {
							lblLiveCapacity.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: red;");
						} else {
							lblLiveCapacity
									.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2e7d32;");
						}
					});
				}

			} catch (Exception e) {
				System.err.println("Client Controller: Failed to fetch capacity updates.");
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * 
	 * Navigates the user back to the Employee Dashboard screen.
	 * 
	 * * @param event The ActionEvent triggered by clicking the "Back" button.
	 * 
	 */
	@FXML
	public void onBackButtonClicked(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml", "Employee Dashboard");
	}

	/**
	 * 
	 * Displays a feedback message to the user in a specified color.
	 * 
	 * * @param text The text of the message to display.
	 * 
	 * @param color The CSS color string (e.g., "red", "green") for the text.
	 * 
	 */
	private void showMessage(String text, String color) {
		messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
		messageLabel.setText(text);
	}

	/**
	 * 
	 * Displays the invoice section with the calculated final price.
	 * 
	 * * @param priceText The formatted price string to display (e.g., "45.00 NIS").
	 * 
	 */
	private void showInvoice(String priceText) {
		invoiceSection.setVisible(true);
		finalPriceLabel.setText("Total to pay: " + priceText);
	}

	/**
	 * 
	 * Resets the current transaction memory variables to prevent state leakage
	 * 
	 * between different entry attempts.
	 * 
	 */
	private void resetTransactionState() {
		currentTransactionAmount = 0;
		currentTransactionOrderId = null;
		currentVisitorType = null;
	}
	
	/**
	 * 
	 * Hides the invoice and payment confirmation section from the screen.
	 * 
	 */
	private void hideInvoice() {
		invoiceSection.setVisible(false);
	}

}