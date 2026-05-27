package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class NewOrderFormController {

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
	private TextField txtId;

	@FXML
	private TextField txtVisitors;

	@FXML
	void clickBookVisit(ActionEvent event) {
		StringBuilder errorMessages = new StringBuilder();

		if (comboPark.getValue() == null || dateVisit.getValue() == null || comboTime.getValue() == null
				|| txtVisitors.getText().isEmpty() || txtEmail.getText().isEmpty() || txtId.getText().isEmpty()) {

			errorMessages.append("- Please fill in all the required fields.\n");
		}

		if (!txtVisitors.getText().isEmpty()) {
			try {
				int visitors = Integer.parseInt(txtVisitors.getText());
				if (visitors <= 0) {
					errorMessages.append("- Number of visitors must be a positive number.\n");
				} else if (visitors > 15) {
					errorMessages.append("- A group order can have a maximum of 15 visitors.\n");
				}
			} catch (NumberFormatException e) {
				errorMessages.append("- Visitors must be a valid number.\n");
			}
		}

		if (!txtEmail.getText().isEmpty() && !txtEmail.getText().contains("@")) {
			errorMessages.append("- Please enter a valid email address.\n");
		}

		if (!txtId.getText().isEmpty() && !txtId.getText().matches("\\d+")) {
			errorMessages.append("- Invalid ID. ID must contain only numbers.\n");
		}

		if (dateVisit.getValue() != null && comboTime.getValue() != null) {
			java.time.LocalDate selectedDate = dateVisit.getValue();
			java.time.LocalTime selectedTime = java.time.LocalTime.parse(comboTime.getValue()); // ממיר את הטקסט לשעה
			java.time.LocalDate today = java.time.LocalDate.now();
			java.time.LocalTime now = java.time.LocalTime.now();

			if (selectedDate.isBefore(today) || (selectedDate.isEqual(today) && selectedTime.isBefore(now))) {
				errorMessages.append("- Visit date and time must be in the future.\n");
			}
		}

		if (errorMessages.length() > 0) {
			showAlert("Validation Error", "Please correct the following errors:", errorMessages.toString());
			return;
		}

		System.out.println("--- New Booking Attempt (Validated Successfully!) ---");
		System.out.println("Park: " + comboPark.getValue());
		System.out.println("Date: " + dateVisit.getValue());
		System.out.println("Time: " + comboTime.getValue());
		System.out.println("Visitors: " + txtVisitors.getText());
		System.out.println("Email: " + txtEmail.getText());
		System.out.println("ID: " + txtId.getText());
	}

	private void showAlert(String title, String header, String content) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@FXML
	public void initialize() {
		comboPark.getItems().addAll("Carmel Park", "Hermon Park", "Dan Park");
		comboTime.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
	}

	@FXML
	void clickDashboard(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
	}

}
