package client.gui;

import client.logic.ExitLogic;
import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the Park Exit screen.
 * Manages the UI for processing visitor exits and tracking real-time park capacity.
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
     * Initializes the controller class. Automatically called after the FXML file is loaded.
     * Instantiates the logic handler and fetches the initial live park capacity.
     */
    @FXML
    public void initialize() {
        // Instantiate the logic class
        exitLogic = new ExitLogic();
        
        // Fetch the capacity when the screen first loads
        updateLiveCapacity();
    }

    /**
     * Handles the validation and processing of a visitor's exit request.
     * Communicates with the logic layer to register the exit and update the database.
     * If successful, it clears the input field and refreshes the live park capacity.
     * * @param event The ActionEvent triggered by clicking the "Register Exit" button.
     */
    @FXML
    public void onRegisterExitClicked(ActionEvent event) {
        String inputId = visitorIdInput.getText().trim();

        // 1. Validate that the input field is not empty
        if (inputId.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please enter a valid Order ID or QR code.");
            return;
        }

     // 2. Validate input format: Allow alphanumeric characters and hyphens for QR Codes (e.g., QR-3525).
        if (!inputId.matches("[a-zA-Z0-9\\-]+")) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid format! Input must contain letters, numbers, or dashes only.");
            return;
        }
        
        // 3. Delegate the exit process to the logic layer
        // The logic layer will handle the specific lookup in the database 
        // whether the input is a numeric Order ID or an alphanumeric QR Code.
        boolean isSuccess = exitLogic.registerExit(inputId, null);

        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Park capacity updated.");
            
            // Clear input field for the next transaction
            visitorIdInput.clear();
            
            // Auto-refresh the live park capacity display after a successful exit
            updateLiveCapacity();
        } else {
            // Display error if the parameter does not match any active 'Entered' order in the system
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Exit rejected. Parameters do not match any active 'Entered' order.");
        }
    }

    /**
     * Handles the click event for the manual refresh button to update park occupancy.
     * * @param event The ActionEvent triggered by clicking the refresh button.
     */
    @FXML
    public void onRefreshCapacityClicked(javafx.event.ActionEvent event) {
        updateLiveCapacity();
    }

    /**
     * Sends a request to the server to fetch the current live occupancy and maximum capacity of the park.
     * Updates the UI label with the retrieved data, changing text color to red if the park is full.
     */
    private void updateLiveCapacity() {
        if (lblLiveCapacity == null) {
            return;
        }
        
        try {
            String parkName = client.logic.CurUser.getParkName();
            if (parkName == null || parkName.isEmpty()) {
                parkName = "Banias"; // Fallback default matching system parameters
            }

            common.Message request = new common.Message(common.MessageType.GET_PARK_OCCUPANCY, parkName);
            common.Message response = (common.Message) client.ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == common.MessageType.GET_PARK_OCCUPANCY_RESPONSE) {
                int[] capacityData = (int[]) response.getData();
                int current = capacityData[0];
                int max = capacityData[1];

                lblLiveCapacity.setText(String.format("Current Park Occupancy (%s): %d / %d", parkName, current, max));
                
                // Color alert styling depending on total utilization volume
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
     * Navigates the user back to the Employee Dashboard screen.
     * * @param event The ActionEvent triggered by clicking the "Back" button.
     */
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
    }
}