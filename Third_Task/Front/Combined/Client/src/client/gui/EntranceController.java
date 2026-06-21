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
 * Manages the UI for processing park entries, validating both pre-booked orders 
 * and casual drop-in visitors, calculating prices, and tracking real-time park occupancy.
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
     * Initializes the controller class. 
     * Automatically called after the FXML file is loaded.
     * Configures combo boxes, sets dynamic prompt behaviors for the ID input fields 
     * based on visitor type, hides the invoice section, and fetches the initial live park capacity.
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

    /**
     * Handles the validation process for a pre-booked order when the "Verify Order" button is clicked.
     * Communicates with the logic layer to verify the order details, date, and entry time constraints.
     * If valid, it calculates the required payment and displays the invoice.
     * * @param event The ActionEvent triggered by clicking the verify button.
     */
    @FXML
    public void onCheckOrderClicked(ActionEvent event) {
    		resetTransactionState();
    		hideInvoice();
        String inputId = orderIdInput.getText().trim();

        if (inputId.isEmpty()) {
            showMessage("Please enter an Order ID or scan/enter a QR Code.", "red");
            return;
        }

        Object result = entranceLogic.validateOrder(inputId);

        // Check if the result is a successful array
        if (result instanceof Object[]) { 
            Object[] orderDetails = (Object[]) result;
            int visitorsInOrder = (int) orderDetails[0];
            String dynamicVisitorType = (String) orderDetails[1]; 
            
            currentTransactionOrderId = inputId;
            currentTransactionAmount = visitorsInOrder; 
            currentVisitorType = dynamicVisitorType; 
            
            showMessage("Order verified! Type identified as: " + dynamicVisitorType, "green");
            
            double calculatedPrice = entranceLogic.calculatePrice(dynamicVisitorType, currentTransactionAmount, true);
            showInvoice(String.format("%.2f NIS", calculatedPrice));
            
        } 
        // Check if the result is an error string from the server
        else if (result instanceof String) {
            hideInvoice();
            String errorType = (String) result;
            
            switch (errorType) {
                case "NOT_FOUND":
                    showMessage("Error: Invalid Order ID or QR Code.", "red");
                    break;
                case "WRONG_DATE":
                    showMessage("Error: This order is not scheduled for today.", "red");
                    break;
                case "TIME_PASSED":
                    showMessage("Error: More than an hour has passed since the scheduled entry time.", "red");
                    break;
                case "TOO_EARLY":
                    showMessage("Error: Early entrance is only permitted 1 hour before the booked time.", "red");
                    break;
                case "NOT_CONFIRMED":
                    showMessage("Error: This order has not been confirmed yet.", "red");
                    break;
                case "INVALID_FORMAT":
                    showMessage("Error: Please enter a valid numeric Order ID.", "red");
                    break;
                default:
                    showMessage("System Error: Could not validate the order.", "red");
                    break;
            }
        }
    }

    /**
     * Handles the validation and capacity check for casual (unplanned) visitors.
     * Verifies specific ID requirements (e.g., Guide ID, Subscriber ID) and checks 
     * if the park has enough available capacity for the requested amount of visitors.
     * If space is available, it calculates the price and displays the invoice.
     * * @param event The ActionEvent triggered by clicking the "Check Capacity & Price" button.
     */
    @FXML
    public void onCheckCasualClicked(ActionEvent event) {
    		resetTransactionState();
        hideInvoice();
        String type = visitorTypeCombo.getValue();
        String amountStr = casualAmountInput.getText().trim();
        String casualId = casualIdInput.getText().trim(); // Fetch the ID once here

        if (type == null || amountStr.isEmpty()) {
            showMessage("Please select visitor type and amount.", "red");
            return;
        }

        if (casualId.isEmpty()) {
            showMessage("Visitor ID / Subscriber Number is required.", "red");
            return;
        }

        // Basic validation: Ensure the ID contains only numbers
        if (!casualId.matches("[0-9]+")) {
            hideInvoice();
            showMessage("Error: ID must contain only numbers.", "red");
            return;
        }

        // Enforcement validation checks based on visitor type
        if ("Subscriber".equals(type)) {
            if (casualId.length() != 4) {
                hideInvoice();
                showMessage("Error: Subscriber Number must be exactly 4 digits.", "red");
                return;
            }
            if (!entranceLogic.verifySubscriber(casualId)) {
                hideInvoice();
                showMessage("Verification Failed: Subscriber ID not found.", "red");
                return;
            }
        } else if ("Group".equals(type)) {
            if (casualId.length() != 5) {
                hideInvoice();
                showMessage("Error: Guide ID must be exactly 5 digits.", "red");
                return;
            }
            if (!entranceLogic.verifyGuide(casualId)) {
                hideInvoice();
                showMessage("Verification Failed: Invalid Guide ID.", "red");
                return;
            }
        } else {
            // Regular visitor
            if (casualId.length() != 5) {
                hideInvoice();
                showMessage("Error: Visitor ID must be exactly 5 digits.", "red");
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

    /**
     * Finalizes the entry process after payment confirmation.
     * Sends the transaction details to the server to register the entry and update the database.
     * Upon success, it clears the form, hides the invoice, and refreshes the live park capacity.
     * * @param event The ActionEvent triggered by clicking the "Confirm Payment & Register Entry" button.
     */
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
            resetTransactionState();
            
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
     * Handles the click event for the manual refresh button to update park occupancy.
     * * @param event The ActionEvent triggered by clicking the refresh button.
     */
    @FXML
    public void onRefreshCapacityClicked(ActionEvent event) {
        updateLiveCapacity();
    }

    /**
     * Sends a request to the server to fetch the current live occupancy and maximum capacity of the park.
     * Runs the network request on a background thread to prevent UI freezing.
     * Updates the UI labels dynamically based on capacity limits using the JavaFX Application Thread.
     */
    private void updateLiveCapacity() {
        if (lblLiveCapacity == null) {
            return;
        }
        
        // Creating a new background Thread to prevent UI freezing while waiting for the server response
        new Thread(() -> {
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

                    final String finalParkName = parkName;

                    // Platform.runLater forces the UI update to run on the main JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        lblLiveCapacity.setText(String.format("Current Park Occupancy (%s): %d / %d", finalParkName, current, max));
                        
                        if (current >= max) {
                            lblLiveCapacity.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: red;");
                        } else {
                            lblLiveCapacity.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2e7d32;");
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Client Controller: Failed to fetch capacity updates.");
                e.printStackTrace();
            }
        }).start(); 
    }

    /**
     * Navigates the user back to the Employee Dashboard screen.
     * * @param event The ActionEvent triggered by clicking the "Back" button.
     */
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
    }

    /**
     * Displays a feedback message to the user in a specified color.
     * * @param text  The text of the message to display.
     * @param color The CSS color string (e.g., "red", "green") for the text.
     */
    private void showMessage(String text, String color) {
        messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        messageLabel.setText(text);
    }

    /**
     * Displays the invoice section with the calculated final price.
     * * @param priceText The formatted price string to display (e.g., "45.00 NIS").
     */
    private void showInvoice(String priceText) {
        invoiceSection.setVisible(true);
        finalPriceLabel.setText("Total to pay: " + priceText);
    }
    
    /**
     * Resets the current transaction memory variables to prevent state leakage
     * between different entry attempts.
     */
    private void resetTransactionState() {
        currentTransactionAmount = 0;
        currentTransactionOrderId = null;
        currentVisitorType = null;
    }

    /**
     * Hides the invoice and payment confirmation section from the screen.
     */
    private void hideInvoice() {
        invoiceSection.setVisible(false);
    }
}