package client.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import client.ClientUI;
import client.logic.OrderLogic;
import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Controller for the New Order Form screen. Handles booking creation and input
 * validation.
 */
public class NewOrderFormController {
	public static String currentTravelerInfo = "";
	public static String currentTravelerId = "";

	@FXML
	private Button btnBook;
	@FXML
	private ComboBox<String> comboPark;
	@FXML
	private ComboBox<String> comboTime;
	@FXML
	private DatePicker dateVisit;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtPhone;
	@FXML
	private TextField txtVisitors;
	@FXML
	private CheckBox cbGroupOrder;

	/**
	 * Initializes the controller, populates combo boxes, and configures UI
	 * visibility based on traveler type.
	 */
	@FXML
	public void initialize() {
		comboPark.getItems().addAll("Achziv", "Banias", "Caesarea", "Ein Gedi", "Masada");
		comboTime.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");

		txtVisitors.setText("1");

		System.out.println("New Order Screen loaded for: " + currentTravelerInfo);

		if (currentTravelerInfo.startsWith("Regular")) {
			txtVisitors.setEditable(false);
			txtVisitors.setDisable(true);
			cbGroupOrder.setVisible(false);

		} else if (currentTravelerInfo.startsWith("Guide")) {
			txtVisitors.setEditable(true);
			txtVisitors.setDisable(false);
			cbGroupOrder.setVisible(true);

		} else if (currentTravelerInfo.startsWith("Subscriber")) {
			txtVisitors.setEditable(true);
			txtVisitors.setDisable(false);
			cbGroupOrder.setVisible(false);
		}
	}

	/**
	 * Handles the booking process, validates inputs, and interacts with the server.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickBookVisit(ActionEvent event) {
		StringBuilder errorMessages = new StringBuilder();

		String park = comboPark.getValue();
		String dateStr = (dateVisit.getValue() != null) ? dateVisit.getValue().toString() : null;
		String time = comboTime.getValue();
		String visitorsStr = txtVisitors.getText();
		String email = txtEmail.getText();
		String phone = txtPhone.getText();

		if (park == null || dateStr == null || time == null || visitorsStr.isEmpty() || email.isEmpty()
				|| phone.isEmpty()) {
			errorMessages.append("- Please fill in all the required fields.\n");
		}

		String visitorType = "Regular";
		int finalVisitors = 0;

		if (!visitorsStr.isEmpty()) {
			if (visitorsStr.startsWith("0")) {
				errorMessages.append("- Number of visitors cannot start with a zero.\n");
			} else {
				try {
					finalVisitors = Integer.parseInt(visitorsStr);
					if (finalVisitors <= 0) {
						errorMessages.append("- Number of visitors must be a positive number.\n");
					} else {

						if (currentTravelerInfo.startsWith("Subscriber")) {
							visitorType = "Subscriber";
							String[] infoParts = currentTravelerInfo.split(":");
							if (infoParts.length > 2) {
								int maxFamilyMembers = Integer.parseInt(infoParts[2].trim());
								if (finalVisitors > maxFamilyMembers) {
									errorMessages.append("- Your subscription is limited to ").append(maxFamilyMembers)
											.append(" members.\n");
								}
							}
						}

						else if (currentTravelerInfo.startsWith("Guide")) {
							if (cbGroupOrder != null && cbGroupOrder.isSelected()) {
								visitorType = "Group";
								if (finalVisitors > 15) {
									errorMessages.append("- A group order can have a maximum of 15 visitors.\n");
								}
							} else {
								if (finalVisitors > 1) {
									errorMessages.append("- A regular non-group order is limited to 1 person.\n");
								}
							}
						}

						else if (currentTravelerInfo.startsWith("Regular") && finalVisitors > 1) {
							errorMessages.append("- A regular traveler can only book for 1 person.\n");
						}
					}
				} catch (NumberFormatException e) {
					errorMessages.append("- Visitors must be a valid number.\n");
				}
			}
		}

		if (!email.isEmpty() && !email.contains("@")) {
			errorMessages.append("- Please enter a valid email address.\n");
		}

		if (dateVisit.getValue() != null && time != null) {
			LocalDate selectedDate = dateVisit.getValue();
			LocalTime selectedTime = LocalTime.parse(time);
			LocalDate today = LocalDate.now();
			LocalTime now = LocalTime.now();

			if (selectedDate.isBefore(today) || (selectedDate.isEqual(today) && selectedTime.isBefore(now))) {
				errorMessages.append("- Visit date and time must be in the future.\n");
			}
		}

		if (errorMessages.length() > 0) {
			showAlert("Validation Error", "Please correct the following errors:", errorMessages.toString());
			return;
		}

		ArrayList<Object> orderData = new ArrayList<>();
		orderData.add(park);
		orderData.add(dateStr);
		orderData.add(time);
		orderData.add(finalVisitors);
		orderData.add(email);
		orderData.add(phone);
		orderData.add(currentTravelerId);
		orderData.add(visitorType);

		System.out.println("Sending to server to check availability: " + orderData);
		Message msg = new Message(MessageType.CHECK_AVAILABILITY, orderData);
		Message reply = (Message) ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.CHECK_AVAILABILITY_RESULT) {
			boolean isAvailable = (boolean) reply.getData();

			if (isAvailable) {
				System.out.println("THERE IS PLACE... Sending SAVE request");

				Message saveMsg = new Message(MessageType.SAVE_NEW_ORDER, orderData);
				Message saveReply = (Message) ClientUI.clientChat.accept(saveMsg);

				if (saveReply != null && saveReply.getType() == MessageType.SAVE_SUCCESS) {
					String generatedQR = (String) saveReply.getData();
					String orderNumber = generatedQR.substring(3);

					Alert simAlert = new Alert(Alert.AlertType.INFORMATION);
					simAlert.setTitle("Simulation");
					simAlert.setHeaderText("Simulation: SMS & Email Sent");
					simAlert.setContentText("To Email: " + email + "\nTo Phone: " + phone
							+ "\n\nYour order has been saved and is pending confirmation." + "\nOrder Number: "
							+ orderNumber + "\nYour Entrance QR Code is: " + generatedQR);
					simAlert.showAndWait();
					ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");

				} else {
					showAlert("Error", "Saving Failed", "There was an error saving your order to the database.");
				}

			} else {
				OrderLogic.pendingOrderDetails = orderData;
				ScreenSwitch.switchScreen("/client/gui/WaitListForm.fxml", "Waiting List");
			}
		}
	}

	/**
	 * Displays an error alert dialog.
	 * 
	 * @param title   The title of the alert.
	 * @param header  The header text.
	 * @param content The content text.
	 */
	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Handles the back button action.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
	}
}