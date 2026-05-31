package client.gui;

import client.logic.EntranceLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EntranceController {

    // --- Pre-booked Order Elements (Supports both Order ID and QR Code) ---
    @FXML
    private TextField orderIdInput; // This field now accepts either Order ID or QR Code string

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
            visitorTypeCombo.getItems().addAll("Regular/Family", "Subscriber", "Group");
            
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
                    } else if ("Group".equals(selected)) {
                        casualIdInput.setDisable(false); // Enable for guides/groups
                        casualIdInput.setPromptText("Enter Guide ID (Required)");
                    } else {
                        casualIdInput.setDisable(true);  // Disable for others
                        casualIdInput.clear();           // Clear any old input
                        casualIdInput.setPromptText("ID not required");
                    }
                });
            }
        }
    }

    // --- Handler for Tab 1: Pre-booked Orders (Supports Order ID / QR Code) ---
    @FXML
    public void onCheckOrderClicked(ActionEvent event) {
        String inputId = orderIdInput.getText().trim();

        if (inputId.isEmpty()) {
            showMessage("Please enter an Order ID or scan/enter a QR Code.", "red");
            return;
        }

        boolean isValid = entranceLogic.validateOrder(inputId);

        if (isValid) {
            showMessage("Order/QR verified successfully! Valid for today.", "green");
            double calculatedPrice = entranceLogic.calculatePrice("Regular", 1, true);
            showInvoice(String.format("%.2f NIS", calculatedPrice));
        } else {
            hideInvoice();
            showMessage("Error: Invalid Order ID or QR Code, or not scheduled for today.", "red");
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

        // --- ID Validation strictly for Subscribers and Groups ---
        if ("Subscriber".equals(type)) {
            String subId = casualIdInput.getText().trim();
            if (subId.isEmpty()) {
                showMessage("Subscriber ID is strictly required to apply the discount.", "red");
                return;
            }
        } else if ("Group".equals(type)) {
            String guideId = casualIdInput.getText().trim();
            if (guideId.isEmpty()) {
                showMessage("Guide ID is strictly required for group entry.", "red");
                return;
            }
            
            // Verify if the guide is actually certified
            boolean isCertifiedGuide = entranceLogic.verifyGuide(guideId);
            if (!isCertifiedGuide) {
                hideInvoice();
                showMessage("Verification Failed: Invalid or unrecognized Guide ID.", "red");
                return;
            }
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