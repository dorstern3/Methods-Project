package client.gui;

import client.logic.ExitLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ExitParkEmployeeController {

    @FXML
    private TextField visitorIdInput; // Text field for entering Visitor ID or Order ID

    @FXML
    private Label statusLabel;        // Feedback label for the park employee

    private ExitLogic exitLogic;      // Reference to the logic layer

    @FXML
    public void initialize() {
        // Instantiate the logic class
        exitLogic = new ExitLogic();
    }

    @FXML
    public void onRegisterExitClicked(ActionEvent event) {
        String inputId = visitorIdInput.getText().trim();

        // 1. Check if the field is empty
        if (inputId.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid Visitor ID or QR code.");
            return;
        }

        // 2. Input Validation
        if (!inputId.matches("[a-zA-Z0-9\\-]+")) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid Order ID or QR code.");
            return;
        }
        
        // 3. Ensure the ID is not unreasonably long 
        if (inputId.length() > 10) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid input! too long.");
            return;
        }

        // 4. Delegate the exit process to the logic layer if validation passes
        boolean isSuccess = exitLogic.registerExit(inputId);

        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Park capacity updated.");
            
            // Clear the input field for the next visitor
            visitorIdInput.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("System Error: Could not register exit.");
        }
    }
    
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
    }
}