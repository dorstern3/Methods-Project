package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * Controller for the New Order Form screen. Handles the creation of a new
 * booking, including validation of input fields.
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
	private TextField txtVisitors;

	// הרכיב החדש שהוספנו לעיצוב עבור קבוצה מאורגנת
	@FXML
	private CheckBox cbGroupOrder;

	@FXML
	public void initialize() {
		// 1. אכלוס הרשימות מתוך הקוד המקורי שלך (הכנסתי את השמות האמיתיים מה-DB שלכם!)
		comboPark.getItems().addAll("Achziv", "Banias", "Caesarea", "Ein Gedi", "Masada");
		comboTime.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");

		// 2. ברירת המחדל לכולם: מציגים 1 בשדה כמות המבקרים
		txtVisitors.setText("1");

		// 3. קוראים את "תא הדואר" כדי לדעת מי המטייל שנכנס
		System.out.println("New Order Screen loaded for: " + currentTravelerInfo);

		if (currentTravelerInfo.startsWith("Regular")) {
			// --- מטייל רגיל ---
			txtVisitors.setEditable(false);
			txtVisitors.setDisable(true);
			cbGroupOrder.setVisible(false);

		} else if (currentTravelerInfo.startsWith("Guide")) {
			// --- מדריך ---
			txtVisitors.setEditable(true);
			txtVisitors.setDisable(false);
			cbGroupOrder.setVisible(true);

		} else if (currentTravelerInfo.startsWith("Subscriber")) {
			// --- מנוי ---
			txtVisitors.setEditable(true);
			txtVisitors.setDisable(false);
			cbGroupOrder.setVisible(false);
		}
	}

	@FXML
	void clickBookVisit(ActionEvent event) {
		StringBuilder errorMessages = new StringBuilder();

		// 1. בדיקת שדות חובה ריקים
		if (comboPark.getValue() == null || dateVisit.getValue() == null || comboTime.getValue() == null
				|| txtVisitors.getText().isEmpty() || txtEmail.getText().isEmpty()) {
			errorMessages.append("- Please fill in all the required fields.\n");
		}

		// 2. ולידציה לכמות מבקרים לפי סוג המטייל הספציפי (הקסם של Client-Server)
		if (!txtVisitors.getText().isEmpty()) {
			String visitorsText = txtVisitors.getText();

			// --- הבדיקה החדשה: חוסמים מספרים שמתחילים באפס ---
			if (visitorsText.startsWith("0")) {
				errorMessages.append("- Number of visitors cannot start with a zero.\n");
			} else {
				try {
					int visitors = Integer.parseInt(visitorsText);
					if (visitors <= 0) {
						errorMessages.append("- Number of visitors must be a positive number.\n");
					} else {

						// --- א. הגבלת מנוי משפחתי לפי הנתון מהדאטה-בייס ---
						if (currentTravelerInfo.startsWith("Subscriber")) {
							// השרת החזיר לנו למשל "Subscriber: Yossi: 5". נחתוך את המשפט כדי לחלץ את הספרה
							// 5
							String[] infoParts = currentTravelerInfo.split(":");
							int maxFamilyMembers = Integer.parseInt(infoParts[2].trim());

							if (visitors > maxFamilyMembers) {
								errorMessages.append("- Your subscription is limited to ").append(maxFamilyMembers).append(" members.\n");
							}
						}

						// --- ב. הגבלת מדריך קבוצות ---
						else if (currentTravelerInfo.startsWith("Guide")) {
							if (cbGroupOrder.isSelected()) {
								// אם הוא סימן V על קבוצה מאורגנת - מוגבל ל-15 משתתפים
								if (visitors > 15) {
									errorMessages.append("- A group order can have a maximum of 15 visitors.\n");
								}
							} else {
								// אם הוא לא סימן V, זו אינה קבוצה ולכן הוא מוגבל ל-1 כמו הזמנה רגילה
								if (visitors > 1) {
									errorMessages.append("- A regular non-group order is limited to 1 person.\n");
								}
							}
						}

						// --- ג. הגבלת מטייל מזדמן ---
						else if (currentTravelerInfo.startsWith("Regular") && visitors > 1) {
							errorMessages.append("- A regular traveler can only book for 1 person.\n");
						}
					}
				} catch (NumberFormatException e) {
					errorMessages.append("- Visitors must be a valid number.\n");
				}
			}
		}

		// 3. בדיקת תקינות אימייל (נשאר כמו שעשית)
		if (!txtEmail.getText().isEmpty() && !txtEmail.getText().contains("@")) {
			errorMessages.append("- Please enter a valid email address.\n");
		}

		// 4. בדיקת תאריך עתידי (נשאר כמו שעשית)
		if (dateVisit.getValue() != null && comboTime.getValue() != null) {
			java.time.LocalDate selectedDate = dateVisit.getValue();
			java.time.LocalTime selectedTime = java.time.LocalTime.parse(comboTime.getValue());
			java.time.LocalDate today = java.time.LocalDate.now();
			java.time.LocalTime now = java.time.LocalTime.now();

			if (selectedDate.isBefore(today) || (selectedDate.isEqual(today) && selectedTime.isBefore(now))) {
				errorMessages.append("- Visit date and time must be in the future.\n");
			}
		}

		// 5. עצירת הפעולה והצגת כל השגיאות למשתמש במרוכז
		if (errorMessages.length() > 0) {
			// אני מניח שיש לך פונקציית showAlert מוכנה במחלקה
			showAlert("Validation Error", "Please correct the following errors:", errorMessages.toString());
			return;
		}

		// 6. הכל תקין! מציגים נתונים (ובהמשך נשלח לשרת)
		System.out.println("--- New Booking Attempt (Validated Successfully!) ---");
		System.out.println("Traveler ID: " + currentTravelerId);
		System.out.println("Traveler Type: " + currentTravelerInfo);
		System.out.println("Park: " + comboPark.getValue());
		System.out.println("Date: " + dateVisit.getValue());
		System.out.println("Time: " + comboTime.getValue());
		System.out.println("Visitors: " + txtVisitors.getText());
		System.out.println("Email: " + txtEmail.getText());
		System.out.println("Is Group Order: " + cbGroupOrder.isSelected());

		// --- השלב הבא שלנו: לארוז את הנתונים האלו למעטפת Message ולשגר לשרת! ---
	}

	/**
	 * Shows an error alert dialog.
	 * 
	 * @param title   the title of the alert
	 * @param header  the header text
	 * @param content the content text
	 */
	private void showAlert(String title, String header, String content) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Handles the back button action. Navigates back to the Traveler Entry menu.
	 * 
	 * @param event the action event triggered by clicking the back button
	 */
	@FXML
	void clickBack(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
	}

}