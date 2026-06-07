package client.gui;

import client.logic.EntranceLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EntranceController {

    // --- Pre-booked Order Elements ---
    @FXML
    private TextField orderIdInput;

    // --- Casual Visitor Elements ---
    @FXML
    private ComboBox<String> visitorTypeCombo;
    @FXML
    private TextField casualAmountInput;
    @FXML
    private TextField casualIdInput;

    // --- Shared Elements ---
    @FXML
    private Label messageLabel;
    @FXML
    private VBox invoiceSection;
    @FXML
    private Label finalPriceLabel;

    private EntranceLogic entranceLogic;

    @FXML
    public void initialize() {
        entranceLogic = new EntranceLogic();
        
        // Hide invoice initially
        if (invoiceSection != null) {
            invoiceSection.setVisible(false);
        }

        // Initialize ComboBox for Casual Visitors
        if (visitorTypeCombo != null) {
            visitorTypeCombo.getItems().addAll("Regular", "Subscriber", "Group");
            
            // Disable the ID input field by default
            if (casualIdInput != null) {
                casualIdInput.setDisable(true);
                
                // --- Simple Action Event: Toggle ID field based on visitor type ---
                visitorTypeCombo.setOnAction(event -> {
                    // Extract the currently selected value
                    String selected = visitorTypeCombo.getValue();
                    
                    if ("Subscriber".equals(selected)) {
                        casualIdInput.setDisable(false); // Enable for subscribers
                        casualIdInput.setPromptText("Enter Subscriber ID (Required)");
                    } else {
                        casualIdInput.setDisable(true);  // Disable for others
                        casualIdInput.clear();           // Clear any old input
                        casualIdInput.setPromptText("ID not required");
                    }
                });
            }
        }
    }

    // --- Handler for Tab 1: Pre-booked Orders ---
    @FXML
    public void onCheckOrderClicked(ActionEvent event) {
        String inputId = orderIdInput.getText().trim();

        if (inputId.isEmpty()) {
            showMessage("Please enter an Order ID.", "red");
            return;
        }

        boolean isValid = entranceLogic.validateOrder(inputId);

        if (isValid) {
            showMessage("Order found! Valid for today.", "green");
            
            /* * MOCK IMPLEMENTATION:
             * Currently, we are manually passing "Regular", 1 visitor, pre-booked=true.
             * FUTURE DB INTEGRATION: 
             * You will pull the actual visitor type and amount from the Order object returned from the Database.
             */
            double calculatedPrice = entranceLogic.calculatePrice("Regular", 1, true);
            
            // Display the formatted price with 2 decimal points
            showInvoice(String.format("%.2f NIS", calculatedPrice));
            
        } else {
            hideInvoice();
            showMessage("Error: Order not found or not for today.", "red");
        }
    }

    // --- Handler for Tab 2: Casual Visitors ---
    @FXML
    public void onCheckCasualClicked(ActionEvent event) {
        String type = visitorTypeCombo.getValue();
        String amountStr = casualAmountInput.getText().trim();

        if (type == null || amountStr.isEmpty()) {
            showMessage("Please select visitor type and amount.", "red");
            return;
        }

        // --- Added: ID Validation strictly for Subscribers ---
        if ("Subscriber".equals(type)) {
            String subId = casualIdInput.getText().trim();
            if (subId.isEmpty()) {
                showMessage("Subscriber ID is strictly required to apply the discount.", "red");
                return;
            }
            /* * FUTURE DB INTEGRATION: 
             * Here you will verify if 'subId' actually exists in the Subscribers database table 
             * before proceeding to calculate the price.
             */
        }

        try {
            int amount = Integer.parseInt(amountStr);
            
            if (amount <= 0) {
                showMessage("Amount must be greater than 0.", "red");
                return;
            }
            
            // --- Business rule for group size limit ---
            if (type.equals("Group") && amount > 15) {
                hideInvoice();
                showMessage("An organized group cannot exceed 15 participants.", "red");
                return;
            }
            
            // Checking available space in the park via the server (Mock)
            boolean hasSpace = entranceLogic.checkCasualAvailability(amount);
            
            if (hasSpace) {
                showMessage("Space available! Proceed to payment.", "green");
                
                // Calculate the real price using our logic class 
                // Parameters: type (from ComboBox), amount (from input), isPreBooked = false
                double calculatedPrice = entranceLogic.calculatePrice(type, amount, false);
                
                // Display the formatted price with 2 decimal points
                showInvoice(String.format("%.2f NIS", calculatedPrice));
                
            } else {
                hideInvoice();
                showMessage("Notice: The park is currently at maximum capacity.", "red");
            }
            
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number of visitors.", "red");
        }
    }

    // --- Handler for Payment Confirmation ---
    @FXML
    public void onConfirmPaymentClicked(ActionEvent event) {
        entranceLogic.confirmPayment();
        showMessage("Payment registered. Entry confirmed! Capacity updated.", "green");
        hideInvoice();
        
        // Clear all inputs for the next customer
        orderIdInput.clear();
        casualAmountInput.clear();
        casualIdInput.clear();
        if (visitorTypeCombo != null) {
            visitorTypeCombo.getSelectionModel().clearSelection();
        }
    }
    
    @FXML
    public void onBackButtonClicked(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
    }

    // --- Helper Methods to keep code clean ---
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