package client.gui;

import java.util.ArrayList;

import client.logic.ScreenSwitch;
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
import common.Order;

/**
 * Controller for the Alternative Dates selection form. Displays available
 * alternative time slots and manages order updates for travelers when their
 * originally requested time is fully booked.
 */
public class AlternativeDatesFormController {

	public static Order originalOrderDetails;

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
	 * Initializes the controller class. This method is automatically called after
	 * the fxml file has been loaded. It sets up the table columns and fetches the
	 * alternative available dates from the server.
	 */
	@FXML
	public void initialize() {
		colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		tblAlternativeDates.setItems(availableSlotsList);

		if (originalOrderDetails != null) {
			Message msg = new Message(MessageType.GET_ALTERNATIVE_DATES, originalOrderDetails);
			Message reply = (Message) client.ClientUI.clientChat.accept(msg);

			if (reply != null && reply.getType() == MessageType.GET_ALTERNATIVE_DATES_RESULT) {
				@SuppressWarnings("unchecked")
				ArrayList<String> dates = (ArrayList<String>) reply.getData();
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
	 * Handles the book action for a selected alternative slot. Updates the original
	 * order details with the new selected date and time, saves it to the database,
	 * and displays a simulation of an SMS/Email confirmation. 
	 * @param event The
	 * action event triggered by clicking the book button.
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

		originalOrderDetails.setOrderDate(selectedSlot.getDate());
		originalOrderDetails.setEntryTime(selectedSlot.getTime());

		client.logic.OrderLogic logic = new client.logic.OrderLogic();
		String generatedQR = logic.saveNewOrder(originalOrderDetails);

		if (generatedQR != null) {
			String orderNumber = generatedQR.substring(3);
			String email = originalOrderDetails.getEmail();
			String phone = originalOrderDetails.getPhoneNumber();

			Alert simAlert = new Alert(Alert.AlertType.INFORMATION);
			simAlert.setTitle("Simulation");
			simAlert.setHeaderText("Simulation: SMS & Email Sent");
			simAlert.setContentText("To Email: " + email + "\nTo Phone: " + phone
					+ "\n\nYour alternative order has been Booked.\nA reminder will be sent 24 hours before your visit."
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
	 * Cancels the current selection process and returns the user to the 
	 * Waitlist/Alternative Dates selection screen.
	 * @param event The action event triggered by clicking the cancel button.
	 */
	@FXML
	void clickCancel(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/WaitListForm.fxml", "Waiting List");
	}
}