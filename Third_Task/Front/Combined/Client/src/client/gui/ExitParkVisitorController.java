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
 * Facilitates independent visitor exit registration via Order ID or QR code.
 */
public class ExitParkVisitorController {

	/** Static identifier for the currently active traveler session. */
    public static String currentTravelerId;

    @FXML
    private TextField orderIdInput; 
    
    @FXML
    private Label statusLabel;      

    private ExitLogic exitLogic;    
    
    /**
     * Initializes the controller and prepares the logic handler for exit processing.
     */
    @FXML
    public void initialize() {
        exitLogic = new ExitLogic();
    }

    /**
     * Validates input format and processes the visitor's exit registration.
     * * @param event the action event triggered by the exit button
     */
    @FXML
    public void onExitClicked(ActionEvent event) {
        String orderIdStr = orderIdInput.getText().trim();

        if (orderIdStr.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter your Order Number.");
            return;
        }

        if (!orderIdStr.matches("[a-zA-Z0-9\\-]+")) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid input! Please enter a valid ID or scan your QR code.");
            return;
        }
        
        if (orderIdStr.length() > 10) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid input! too long.");
            return;
        }

        boolean isSuccess = exitLogic.registerExit(orderIdStr, currentTravelerId); 
        
        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Have a safe trip.");
            
            orderIdInput.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Exit rejected. Parameters do not match any active 'Entered' order.");
        }
    }
    
    /**
     * Terminates the active traveler session and returns to the Traveler Entry screen.
     * * @param event the action event triggered by the back button
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