package client.gui;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import common.Message;
import common.MessageType;
import client.ClientUI;

/**
 * Controller for the Traveler Entry screen. Handles traveler identification
 * locally before proceeding to order creation or management.
 */
public class TravelerEntryController {

	@FXML
	private Button btnBack;

	@FXML
	private Button btnManageOrder;

	@FXML
	private Button btnNewOrder;

	@FXML
	private TextField txtTravelerId;

	@FXML
	private TextField txtOrderId;

	@FXML
	private Label lblError;

	/**
	 * Handles the Back button click. Returns the user to the main role selection
	 * screen.
	 *
	 * @param event the action event triggered by clicking the Back button
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
	}

	/**
	 * Handles the Manage Order button click. Validates the order number input
	 * locally before switching to the manage order screen.
	 *
	 * @param event the action event triggered by clicking the Manage Order button
	 */
	@FXML
	void clickManageOrder(ActionEvent event) {
		lblError.setText(""); // מחיקת שגיאות קודמות
		String orderId = txtOrderId.getText();

		// ולידציה: בדיקה שהשדה לא ריק
		if (orderId == null || orderId.trim().isEmpty()) {
			lblError.setText("Please enter Order Number!");
			return;
		}

		// ולידציה: בדיקה שהוזנו רק ספרות
		if (!orderId.matches("\\d+")) {
			lblError.setText("Order Number must contain only numbers!");
			return;
		}

		System.out.println("Frontend validation passed for Order Number: " + orderId);
		// מעבר זמני למסך טיפול בהזמנה עד שנחבר את הבק-אנד
		ScreenSwitch.switchScreen("/client/gui/Order.fxml", "Manage Order");
	}

	/**
	 * Handles the New Order button click. Validates the ID input locally before
	 * switching to the new order screen.
	 *
	 * @param event the action event triggered by clicking the New Order button
	 */
	@FXML
	void clickNewOrder(ActionEvent event) {
		lblError.setText(""); // מחיקת שגיאות קודמות
		String travelerId = txtTravelerId.getText();

		// ולידציה: בדיקה שהשדה לא ריק
		if (travelerId == null || travelerId.trim().isEmpty()) {
			lblError.setText("Please enter ID or Subscriber Number!");
			return;
		}

		// ולידציה: בדיקה שהוזנו רק ספרות
		if (!travelerId.matches("\\d+")) {
			lblError.setText("ID must contain only numbers!");
			return;
		}

		System.out.println("Frontend validation passed for ID: " + travelerId);
		// מעבר זמני למסך הזמנה חדשה עד שנחבר את הבק-אנד
		// ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "New Order");
		Message messageToServer = new Message(MessageType.IDENTIFY_TRAVELER, travelerId);
		// 1. שליחת המעטפה לשרת ושמירת התשובה שחוזרת ממנו לתוך משתנה
		Object response = ClientUI.clientChat.accept(messageToServer);

		// 2. פתיחת המעטפה שחזרה ובדיקה מה יש בפנים
		if (response instanceof Message) {
			Message responseMsg = (Message) response;

			// מוודאים שזו אכן התשובה לפקודת זיהוי המטייל
			if (responseMsg.getMessageType() == MessageType.IDENTIFY_TRAVELER_RESPONSE) {

				// שולפים את התשובה האמיתית מהדאטה-בייס (String)
				String dbResult = (String) responseMsg.getMessageData();
				System.out.println("The server returned: " + dbResult);

				// 3. שמירת הנתונים במשתנים של המסך הבא כדי שיידע "מי נכנס"
				client.gui.NewOrderFormController.currentTravelerInfo = dbResult;
				client.gui.NewOrderFormController.currentTravelerId = travelerId;

				if (dbResult.startsWith("Subscriber:")) {
				    System.out.println("Moving to Order Screen as a Subscriber...");
				} else if (dbResult.startsWith("Guide:")) {
				    System.out.println("Moving to Order Screen as a Guide...");
				} else {
				    System.out.println("Moving to Order Screen as a Regular Traveler...");
				}

				// 4. העברה למסך ההזמנה האחיד לכולם
				ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "New Order");
			}
		}
	}
}