package client.gui;

import java.util.ArrayList;

import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
import common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controller for the Waiting List screen. Handles user actions when a park is
 * fully booked, allowing them to either check alternative available dates or
 * join the waiting list for the requested date.
 */
public class WaitListFormController {

	@FXML
	private Button btnAltDate;

	@FXML
	private Button btnCancel;

	@FXML
	private Button btnWaitList;

	/**
	 * Navigates the user to the Alternative Dates screen to explore other available
	 * time slots. Passes the pending order details to the next controller before
	 * switching screens. * @param event The action event triggered by clicking the
	 * "Alternative Dates" button.
	 */
	@FXML
	void clickAltDate(ActionEvent event) {
		System.out.println("User chose: Alternative Date - Moving to Table Screen");
		AlternativeDatesFormController.originalOrderDetails = client.logic.OrderLogic.pendingOrderDetails;
		ScreenSwitch.switchScreen("/client/gui/AlternativeDatesForm.fxml", "Alternative Dates");
	}

	/**
	 * Cancels the current booking process and returns the user to the New Order
	 * form. * @param event The action event triggered by clicking the "Cancel"
	 * button.
	 */
	@FXML
	void clickCancel(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
	}

	/**
	 * Registers the currently pending order into the park's waiting list via the
	 * server. Displays a success message upon completion and redirects to the main
	 * menu. * @param event The action event triggered by clicking the "Enter
	 * Waiting List" button.
	 */
	@FXML
	void clickWaitList(ActionEvent event) {
		Order pendingOrder = client.logic.OrderLogic.pendingOrderDetails;

		if (pendingOrder != null) {
			client.logic.OrderLogic logic = new client.logic.OrderLogic();
			boolean isSaved = logic.enterWaitingList(pendingOrder);

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
		} else {
			System.out.println("No order details found to add to waiting list.");
		}
	}
}