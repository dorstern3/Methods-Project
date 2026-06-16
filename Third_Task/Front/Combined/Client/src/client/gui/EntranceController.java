package client.gui;

import client.logic.CurUser;
import client.logic.EntranceLogic;
import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controller class for the Park Entrance screen.
 * Handles validation and check-in processing for both pre-booked orders and casual drop-in visitors.
 */
public class EntranceController {

    @FXML
    private TextField orderIdInput; 

    @FXML
    private ComboBox<String> visitorTypeCombo;
    @FXML
    private TextField casualAmountInput;
    @FXML
    private TextField casualIdInput;

    @FXML
    private Label messageLabel;
    @FXML
    private VBox invoiceSection;
    @FXML
    private Label finalPriceLabel;
    
    // --- New field for real-time occupancy tracking ---
    @FXML
    private Label lblLiveCapacity;

    private EntranceLogic entranceLogic;
    
    // --- Memory variables for the current transaction ---
    private int currentTransactionAmount = 0;
    private String currentTransactionOrderId = null;
    private String currentVisitorType = null; 
    private String currentParkName = CurUser.getParkName(); 

    /**
     * Initializes the controller class. Automatically configures layout properties 
     * and sets dynamic prompt behaviors for the casual identification input fields.
     */
    @FXML
    public void initialize() {
        entranceLogic = new EntranceLogic();
        
        if (invoiceSection != null) {
            invoiceSection.setVisible(false);
        }

        if (visitorTypeCombo != null) {
            visitorTypeCombo.getItems().addAll("Regular", "Subscriber", "Group");
            
            if (casualIdInput != null) {
                // UNBLOCKED: Keep it enabled initially so it is accessible
                casualIdInput.setDisable(false); 
                casualIdInput.setPromptText("Select Visitor Type First");
                
                // Dynamically swap prompt instructions based on the selected classification
                visitorTypeCombo.setOnAction(event -> {
                    String selected = visitorTypeCombo.getValue();
                    
                    if (selected != null) {
                        casualIdInput.setDisable(false); // Always enabled for all types now!
                        
                        if ("Subscriber".equals(selected)) {
                            casualIdInput.setPromptText("Enter Subscriber Number (Required)");
                        } else if ("Group".equals(selected)) {
                            casualIdInput.setPromptText("Enter Guide ID (Required)");
                        } else {
                            // Prompt configuration updated for Regular casual visitors
                            casualIdInput.setPromptText("Enter Visitor ID (Required)");
                        }
                    }
                });
            }
        }
        
        // Fetch initial park capacity when screen loads
        updateLiveCapacity();
    }

    @FXML
    public void onCheckOrderClicked(ActionEvent event) {
        String inputId = orderIdInput.getText().trim();

        if (inputId.isEmpty()) {
            showMessage("Please enter an Order ID or scan/enter a QR Code.", "red");
            return;
        }

        Object[] orderDetails = entranceLogic.validateOrder(inputId);

        if (orderDetails != null) { 
            int visitorsInOrder = (int) orderDetails[0];
            String dynamicVisitorType = (String) orderDetails[1]; 
            
            currentTransactionOrderId = inputId;
            currentTransactionAmount = visitorsInOrder; 
            currentVisitorType = dynamicVisitorType; // Syncing memory
            
            showMessage("Order verified! Type identified as: " + dynamicVisitorType, "green");
            
            double calculatedPrice = entranceLogic.calculatePrice(dynamicVisitorType, currentTransactionAmount, true);
            showInvoice(String.format("%.2f NIS", calculatedPrice));
            
        } else {
            hideInvoice();
            showMessage("Error: Invalid Order ID or QR Code, or not scheduled for today.", "red");
        }
    }

    @FXML
    public void onCheckCasualClicked(ActionEvent event) {
        String type = visitorTypeCombo.getValue();
        String amountStr = casualAmountInput.getText().trim();

        if (type == null || amountStr.isEmpty()) {
            showMessage("Please select visitor type and amount.", "red");
            return;
        }

        // Enforcement validation checks during initial capacity valuation sequences
        if ("Subscriber".equals(type)) {
            String subId = casualIdInput.getText().trim();
            if (subId.isEmpty()) {
                showMessage("Subscriber ID is strictly required.", "red");
                return;
            }
            if (!entranceLogic.verifySubscriber(subId)) {
                hideInvoice();
                showMessage("Verification Failed: Subscriber ID not found.", "red");
                return;
            }
        } else if ("Group".equals(type)) {
            String guideId = casualIdInput.getText().trim();
            if (guideId.isEmpty()) {
                showMessage("Guide ID is strictly required.", "red");
                return;
            }
            if (!entranceLogic.verifyGuide(guideId)) {
                hideInvoice();
                showMessage("Verification Failed: Invalid Guide ID.", "red");
                return;
            }
        } else {
            // Check if Regular visitor provided an ID before validating availability
            String regularId = casualIdInput.getText().trim();
            if (regularId.isEmpty()) {
                showMessage("Visitor ID is required for departure verification routing.", "red");
                return;
            }
        }

        try {
            int amount = Integer.parseInt(amountStr);
            if (amount <= 0 || amount > 15) {
                hideInvoice();
                showMessage("Error: Amount must be between 1 and 15.", "red");
                return;
            }
            if (amount > 1 && "Regular".equals(type)) {
                hideInvoice();
                showMessage("Regular invite is limited to one person.", "red");
                return;
            }
            
            if (entranceLogic.checkCasualAvailability(amount, currentParkName)) {
                currentTransactionAmount = amount;
                currentTransactionOrderId = null; 
                currentVisitorType = type; // Saving casual visitor type
                
                showMessage("Space available! Proceed to payment.", "green");
                double calculatedPrice = entranceLogic.calculatePrice(type, amount, false);
                showInvoice(String.format("%.2f NIS", calculatedPrice));
            } else {
                hideInvoice();
                showMessage("Notice: The park is currently at maximum capacity.", "red");
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number of visitors.", "red");
        }
    }

    @FXML
    public void onConfirmPaymentClicked(ActionEvent event) {
        String visitorId = casualIdInput.getText().trim();
        
        if (currentTransactionOrderId == null || currentTransactionOrderId.isEmpty()) {
            if (visitorId.isEmpty()) {
                showMessage("Please enter a valid ID / Subscriber Number first!", "red");
                return; 
            }
        } else {
            visitorId = ""; 
        }

        String generatedOrderId = entranceLogic.confirmPayment(
            currentTransactionAmount, 
            currentTransactionOrderId, 
            currentParkName, 
            currentVisitorType,
            visitorId 
        );
        
        if (generatedOrderId != null) {
            if (currentTransactionOrderId == null || currentTransactionOrderId.isEmpty()) {
                showMessage("Payment registered. Casual Entry confirmed! Order ID: " + generatedOrderId, "green");
            } else {
                showMessage("Payment registered. Pre-booked Entry confirmed successfully!", "green");
            }
            
            hideInvoice();
            currentTransactionAmount = 0;
            currentTransactionOrderId = null;
            currentVisitorType = null; 
            
            casualAmountInput.clear();
            casualIdInput.clear();
            if (orderIdInput != null) {
                orderIdInput.clear();
            }
            if (visitorTypeCombo != null) {
                visitorTypeCombo.getSelectionModel().clearSelection();
            }
            
            // Auto-refresh the capacity after a successful entry
            updateLiveCapacity();
            
        } else {
            showMessage("System Error: Could not complete registration updates.", "red");
        }
    }

    /**
     * Handles the click event for the manual refresh button.
     */
    @FXML
    public void onRefreshCapacityClicked(ActionEvent event) {
        updateLiveCapacity();
    }

    /**
     * Dispatches a manual request query to the server context to grab real-time occupancy fields.
     */
    private void updateLiveCapacity() {
        if (lblLiveCapacity == null) {
            return;
        }
        
        try {
            String parkName = CurUser.getParkName();
            if (parkName == null || parkName.isEmpty()) {
                parkName = "Banias"; // Fallback
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
            System.err.println("Client Controller: Failed to fetch capacity updates.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onBackButtonClicked(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
    }

    private void showMessage(String text, String color) {
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    private void showInvoice(String priceText) {
        invoiceSection.setVisible(true);
        finalPriceLabel.setText("Total to pay: " + priceText);
    }

    private void hideInvoice() {
        invoiceSection.setVisible(false);
    }
}