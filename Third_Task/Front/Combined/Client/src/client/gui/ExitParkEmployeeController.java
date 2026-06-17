package client.gui;

import client.logic.ExitLogic;
import client.logic.ScreenSwitch;
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
    private Label lblLiveCapacity;

    @FXML
    public void initialize() {
        // Instantiate the logic class
        exitLogic = new ExitLogic();
        
        // Fetch the capacity when the screen first loads
        updateLiveCapacity();
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
        boolean isSuccess = exitLogic.registerExit(inputId, null);

        if (isSuccess) {
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Exit registered successfully! Park capacity updated.");
            
            // Clear the input field for the next visitor
            visitorIdInput.clear();
            
            // Auto-refresh the capacity after a successful exit
            updateLiveCapacity();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("System Error: Could not register exit.");
        }
    }

    /**
     * Handles the click event for the manual refresh button.
     */
    @FXML
    public void onRefreshCapacityClicked(javafx.event.ActionEvent event) {
        updateLiveCapacity();
    }

    /**
     * Dispatches a manual request query to the server context to grab real-time occupancy fields.
     * Updates the text and color properties dynamically.
     */
    private void updateLiveCapacity() {
        // REMOVED the recursive call here that caused the StackOverflowError
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

    @FXML
    public void onBackButtonClicked(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
    }
}