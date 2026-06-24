package client.gui;

import client.logic.ExitLogic;
import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the Visitor Exit screen.
 * Allows visitors to independently register their departure from the park using their Order ID or QR code.
 */
public class ExitParkVisitorController {

    /**
     * Static reference to the currently logged-in traveler's ID.
     */
    public static String currentTravelerId;

    @FXML
    private TextField orderIdInput; // Text field matching the FXML fx:id exactly

    @FXML
    private Label statusLabel;      // Feedback label to display success or error messages

    private ExitLogic exitLogic;    // Reference to the logic layer

    /**
     * Initializes the controller class. Automatically called after the FXML file is loaded.
     * Instantiates the logic handler for processing exits.
     */
    @FXML
    public void initialize() {
        exitLogic = new ExitLogic();
    }

    /**
     * Handles the "Register Exit" button click event.
     * Validates the Order ID/QR code input format and delegates the exit request to the logic layer.
     * Displays appropriate success or error feedback to the user.
     * * @param event The ActionEvent triggered by clicking the "Register Exit" button.
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

        // 2. Input Validation: Ensure it contains only alphanumeric characters or hyphens
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

        // 3. Delegate the exit process to the logic layer (passing the specific traveler ID for security)
        boolean isSuccess = exitLogic.registerExit(orderIdStr, currentTravelerId); 
        
        // 4. Provide feedback to the visitor
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Have a safe trip.");
            
            // Clear the input field after a successful exit
            orderIdInput.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Exit rejected. Parameters do not match any active 'Entered' order.");
        }
    }
    
    /**
     * Navigates the visitor back to the main Traveler Entry screen.
     * Sends a logout request to the server to clear the active traveler session before switching screens.
     * * @param event The ActionEvent triggered by clicking the "Back" button.
     */
    @FXML
    public void onBackClicked(ActionEvent event) {
        Message msg = new Message(MessageType.TRAVELER_LOGOUT, currentTravelerId);
        Message response = (Message) client.ClientUI.clientChat.accept(msg);
        
        if (response != null && response.getType() == MessageType.LOGOUT_SUCCESS) {
            ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
        }
    }
}