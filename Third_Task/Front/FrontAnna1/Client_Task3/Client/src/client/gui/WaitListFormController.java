package client.gui;

import java.util.ArrayList;
import common.Message;
import common.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controller for the Waiting List screen. Handles user actions for checking
 * alternative dates or joining the waiting list.
 */
public class WaitListFormController {

	@FXML
	private Button btnAltDate;

	@FXML
	private Button btnCancel;

	@FXML
	private Button btnWaitList;

	/**
	 * Navigates to the Alternative Dates screen to offer the user other options.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickAltDate(ActionEvent event) {
		System.out.println("User chose: Alternative Date - Moving to Table Screen");
		AlternativeDatesFormController.originalOrderDetails = client.logic.OrderLogic.pendingOrderDetails;
		ScreenSwitch.switchScreen("/client/gui/AlternativeDatesForm.fxml", "Alternative Dates");
	}

	/**
	 * Cancels the current process and returns to the New Order form.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickCancel(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
	}

	/**
	 * Registers the pending order into the waiting list via the server.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void clickWaitList(ActionEvent event) {
		ArrayList<Object> orderData = client.logic.OrderLogic.pendingOrderDetails;

		if (orderData != null) {
			Message msg = new Message(MessageType.ENTER_WAITING_LIST, orderData);
			Message reply = (Message) client.ClientUI.clientChat.accept(msg);

			if (reply.getMessageType() == MessageType.ENTER_WAITING_LIST_RESULT) {
				boolean isSaved = (boolean) reply.getMessageData();

				if (isSaved) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Registration Successful");
					alert.setHeaderText(null);
					alert.setContentText(
							"You have been successfully added to the waiting list.\n We will notify you if a spot becomes available.");
					alert.showAndWait();

					client.logic.OrderLogic.pendingOrderDetails = null;
					ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
				} else {
					System.out.println("Error saving to waiting list in the database.");
				}
			}
		} else {
			System.out.println("No order details found to add to waiting list.");
		}
	}
}