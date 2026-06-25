package client.gui;

import client.logic.ExitLogic;
import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the Park Exit screen.
 * Manages visitor exit registration and monitors real-time park occupancy.
 */
public class ExitParkEmployeeController {
	
    @FXML
    private TextField visitorIdInput; // Text field for entering Visitor ID or Order ID

    @FXML
    private Label statusLabel;        // Feedback label for the park employee

    private ExitLogic exitLogic;      // Reference to the logic layer
    
    @FXML
    private Label lblLiveCapacity;

    /**
     * Initializes the controller, prepares logic handlers, and updates capacity.
     */
    @FXML
    public void initialize() {
        // Instantiate the logic class
        exitLogic = new ExitLogic();
        
        // Fetch the capacity when the screen first loads
        updateLiveCapacity();
    }

    /**
     * Validates input and processes the visitor's exit request.
     * @param event the action event triggered by the exit button
     */
    @FXML
    public void onRegisterExitClicked(ActionEvent event) {
        String inputId = visitorIdInput.getText().trim();

        if (inputId.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid Order ID or QR code.");
            return;
        }

        if (!inputId.matches("[a-zA-Z0-9\\-]+")) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid format! Input must contain letters, numbers, or dashes only.");
            return;
        }
        
        boolean isSuccess = exitLogic.registerExit(inputId, null);

        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Park capacity updated.");
            
            visitorIdInput.clear();
            
            updateLiveCapacity();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Exit rejected. Parameters do not match any active 'Entered' order.");
        }
    }

    /**
     * Manually triggers an update of the live park capacity display.
     * @param event the action event triggered by the refresh button
     */
    @FXML
    public void onRefreshCapacityClicked(javafx.event.ActionEvent event) {
        updateLiveCapacity();
    }

    /**
     * Fetches and displays current park capacity from the server on a background thread.
     */
    private void updateLiveCapacity() {
        if (lblLiveCapacity == null) {
            return;
        }
        
        try {
            String parkName = client.logic.CurUser.getParkName();
            if (parkName == null || parkName.isEmpty()) {
                parkName = "Banias"; 
            }

            common.Message request = new common.Message(common.MessageType.GET_PARK_OCCUPANCY, parkName);
            common.Message response = (common.Message) client.ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == common.MessageType.GET_PARK_OCCUPANCY_RESPONSE) {
                int[] capacityData = (int[]) response.getData();
                int current = capacityData[0];
                int max = capacityData[1];

                lblLiveCapacity.setText(String.format("Current Park Occupancy (%s): %d / %d", parkName, current, max));
                
                if (current >= max) {
                    lblLiveCapacity.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: red;");
                } else {
                    lblLiveCapacity.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2e7d32;");
                }
            }
        } catch (Exception e) {
            System.err.println("Client Controller: Failed to fetch capacity updates from server thread.");
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the Employee Dashboard.
     * @param event the action event triggered by the back button
     */
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
    }
}