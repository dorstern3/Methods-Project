package client.gui;

import java.util.ArrayList;
import common.Message;
import common.MessageType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the Alternative Dates selection form. Displays available time
 * slots and manages order updates.
 */
public class AlternativeDatesFormController {

	public static ArrayList<Object> originalOrderDetails;

	@FXML
	private Button btnBook;
	@FXML
	private Button btnCancel;
	@FXML
	private TableView<AvailableSlot> tblAlternativeDates;
	@FXML
	private TableColumn<AvailableSlot, String> colDate;
	@FXML
	private TableColumn<AvailableSlot, String> colTime;

	private ObservableList<AvailableSlot> availableSlotsList = FXCollections.observableArrayList();

	/**
	 * Initializes the table and fetches available dates from the server.
	 */
	@FXML
	public void initialize() {
		colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		tblAlternativeDates.setItems(availableSlotsList);

		if (originalOrderDetails != null) {
			Message msg = new Message(MessageType.GET_ALTERNATIVE_DATES, originalOrderDetails);
			Message reply = (Message) client.ClientUI.clientChat.accept(msg);

			if (reply != null && reply.getMessageType() == MessageType.GET_ALTERNATIVE_DATES_RESULT) {
				ArrayList<String> dates = (ArrayList<String>) reply.getMessageData();
				for (String dateTime : dates) {
					String[] parts = dateTime.split(" ");
					if (parts.length == 2) {
						availableSlotsList.add(new AvailableSlot(parts[0], parts[1]));
					}
				}
			}
		}
	}

	/**
	 * Handles the booking action for the selected slot.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickBook(ActionEvent event) {
		AvailableSlot selectedSlot = tblAlternativeDates.getSelectionModel().getSelectedItem();

		if (selectedSlot == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(null);
			alert.setContentText("Please select an alternative date and time from the table.");
			alert.showAndWait();
			return;
		}

		originalOrderDetails.set(1, selectedSlot.getDate());
		originalOrderDetails.set(2, selectedSlot.getTime());

		Message saveMsg = new Message(MessageType.SAVE_NEW_ORDER, originalOrderDetails);
		Message saveReply = (Message) client.ClientUI.clientChat.accept(saveMsg);

		if (saveReply != null && saveReply.getMessageType() == MessageType.SAVE_SUCCESS) {
			String generatedQR = (String) saveReply.getMessageData();
			String orderNumber = generatedQR.substring(3);
			String email = (String) originalOrderDetails.get(4);
			String phone = (String) originalOrderDetails.get(5);

			Alert simAlert = new Alert(Alert.AlertType.INFORMATION);
			simAlert.setTitle("Simulation");
			simAlert.setHeaderText("Simulation: SMS & Email Sent");
			simAlert.setContentText("To Email: " + email + "\nTo Phone: " + phone
					+ "\n\nYour alternative order has been saved and is pending confirmation.\nA reminder will be sent 24 hours before your visit."
					+ "\nOrder Number: " + orderNumber + "\nYour Entrance QR Code is: " + generatedQR);
			simAlert.showAndWait();

			originalOrderDetails = null;
			ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Saving Failed");
			alert.setContentText("There was an error saving your order to the database.");
			alert.showAndWait();
		}
	}

	/**
	 * Cancels the current selection and returns to the booking form.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickCancel(ActionEvent event) {
		originalOrderDetails = null;
		ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
	}
}