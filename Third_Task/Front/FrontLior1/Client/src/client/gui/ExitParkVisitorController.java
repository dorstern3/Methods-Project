package client.gui;

import client.logic.ExitLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the Visitor Exit screen.
 * Allows visitors to register their departure from the park using only their Order ID.
 */
public class ExitParkVisitorController {

    @FXML
    private TextField orderIdInput; // Text field matching the FXML fx:id exactly

    @FXML
    private Label statusLabel;      // Feedback label to display success or error messages

    private ExitLogic exitLogic;    // Reference to the logic layer

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        exitLogic = new ExitLogic();
    }

    /**
     * Handles the "Register Exit" button click event.
     * Validates the Order ID input and delegates to the logic layer.
     * * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void onExitClicked(ActionEvent event) {
        String orderIdStr = orderIdInput.getText().trim();

        // 1. Check if the input field is empty
        if (orderIdStr.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter your Order ID.");
            return;
        }

        // 2. Input Validation: Ensure it contains only digits using Regex
        if (!orderIdStr.matches("[a-zA-Z0-9\\-]+")) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid input! Please enter a valid ID or scan your QR code.");
            return;
        }
        
        // Ensure the ID is not unreasonably long (prevents overflow logic issues)
        if (orderIdStr.length() > 10) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid input! too long.");
            return;
        }

        // 3. Delegate the exit process to the logic layer (ONE parameter)
        boolean isSuccess = exitLogic.registerExit(orderIdStr);

        // 4. Provide feedback to the visitor
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Have a safe trip.");
            
            // Clear the input field after a successful exit
            orderIdInput.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Could not register exit. Check your Order ID or Qr code.");
        }
    }
    
    /**
     * Navigates the visitor back to the main dashboard screen.
     * * @param event The action event triggered by clicking the button.
     */
    @FXML
    public void onBackClicked(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
    }
}