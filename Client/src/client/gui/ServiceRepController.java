package client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import client.logic.ServiceRepLogic;
import client.logic.ScreenSwitch; // Required for screen navigation after logout
import common.Message;
import common.MessageType;

/**
 * Controller class for the Service Representative Panel.
 * Manages the user interface for registering new Family Subscribers, 
 * Single Subscribers, and Group Guides into the GoNature system.
 * Implements centralized input validation to maintain clean and DRY (Don't Repeat Yourself) code.
 */
public class ServiceRepController {

    @FXML 
    private VBox mainContainer;
    
    private TextField famFname, famLname, famId, famPhone, famEmail, famMembers;
    private TextField sFname, sLname, sId, sPhone, sEmail;
    private TextField gFname, gLname, gId, gPhone, gEmail;
    private ServiceRepLogic logic;

    /**
     * Initializes the user interface panel.
     * Automatically called after the FXML file is loaded.
     * Constructs the tabs, layout forms, and header components including the logout button.
     */
    @FXML
    public void initialize() {
        logic = new ServiceRepLogic();
        
        mainContainer.setSpacing(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_LEFT);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab familyTab = new Tab("Family Subscription");
        Tab singleTab = new Tab("Single Subscription");
        Tab groupTab = new Tab("Group Guide");

        // ---------------------------------------------------------------------
        // Tab 1: Family Subscription Setup
        // ---------------------------------------------------------------------
        VBox familyVBox = new VBox(10);
        familyVBox.setPadding(new Insets(10));
        GridPane familyGrid = new GridPane();
        familyGrid.setVgap(10);
        familyGrid.setHgap(10);

        famFname = new TextField();
        famLname = new TextField();
        famId = new TextField();
        famPhone = new TextField();
        famEmail = new TextField();
        famMembers = new TextField();

        familyGrid.add(new Label("First Name:"), 0, 0);
        familyGrid.add(famFname, 1, 0);
        familyGrid.add(new Label("Last Name:"), 0, 1);
        familyGrid.add(famLname, 1, 1);
        familyGrid.add(new Label("ID Number:"), 0, 2);
        familyGrid.add(famId, 1, 2);
        familyGrid.add(new Label("Mobile Number:"), 0, 3);
        familyGrid.add(famPhone, 1, 3);
        familyGrid.add(new Label("Email:"), 0, 4);
        familyGrid.add(famEmail, 1, 4);
        familyGrid.add(new Label("Family Members Amount:"), 0, 5);
        familyGrid.add(famMembers, 1, 5);

        Button familySubmitBtn = new Button("Register to System");
        familySubmitBtn.setOnAction(e -> handleFamilyRegister());

        familyVBox.getChildren().addAll(familyGrid, familySubmitBtn);
        familyTab.setContent(familyVBox);

        // ---------------------------------------------------------------------
        // Tab 2: Single Subscription Setup
        // ---------------------------------------------------------------------
        VBox singleVBox = new VBox(10);
        singleVBox.setPadding(new Insets(10));
        GridPane singleGrid = new GridPane();
        singleGrid.setVgap(10);
        singleGrid.setHgap(10);

        sFname = new TextField();
        sLname = new TextField();
        sId = new TextField();
        sPhone = new TextField();
        sEmail = new TextField();

        singleGrid.add(new Label("First Name:"), 0, 0);
        singleGrid.add(sFname, 1, 0);
        singleGrid.add(new Label("Last Name:"), 0, 1);
        singleGrid.add(sLname, 1, 1);
        singleGrid.add(new Label("ID Number:"), 0, 2);
        singleGrid.add(sId, 1, 2);
        singleGrid.add(new Label("Mobile Number:"), 0, 3);
        singleGrid.add(sPhone, 1, 3);
        singleGrid.add(new Label("Email:"), 0, 4);
        singleGrid.add(sEmail, 1, 4);

        Button singleSubmitBtn = new Button("Register to System");
        singleSubmitBtn.setOnAction(e -> handleSingleRegister());
        
        singleVBox.getChildren().addAll(singleGrid, singleSubmitBtn);
        singleTab.setContent(singleVBox);

        // ---------------------------------------------------------------------
        // Tab 3: Group Guide Setup
        // ---------------------------------------------------------------------
        VBox groupVBox = new VBox(10);
        groupVBox.setPadding(new Insets(10));
        GridPane groupGrid = new GridPane();
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        gFname = new TextField();
        gLname = new TextField();
        gId = new TextField(); 
        gPhone = new TextField();
        gEmail = new TextField();

        groupGrid.add(new Label("First Name:"), 0, 0);
        groupGrid.add(gFname, 1, 0);
        groupGrid.add(new Label("Last Name:"), 0, 1);
        groupGrid.add(gLname, 1, 1);
        groupGrid.add(new Label("ID Number:"), 0, 2);
        groupGrid.add(gId, 1, 2);
        groupGrid.add(new Label("Mobile Number:"), 0, 3);
        groupGrid.add(gPhone, 1, 3);
        groupGrid.add(new Label("Email:"), 0, 4);
        groupGrid.add(gEmail, 1, 4);

        Button groupSubmitBtn = new Button("Register to System");
        groupSubmitBtn.setOnAction(e -> handleGuideRegister());

        groupVBox.getChildren().addAll(groupGrid, groupSubmitBtn);
        groupTab.setContent(groupVBox);

        tabPane.getTabs().addAll(familyTab, singleTab, groupTab);
        
        // Add the configured tabs to the main layout
        mainContainer.getChildren().addAll(tabPane);
    }

    /**
     * Displays UI popup notifications on the screen for success or error feedback.
     * Ensures execution runs safely on the main JavaFX Application Thread.
     *
     * @param type    The specific AlertType configuration (e.g., ERROR, INFORMATION).
     * @param title   The title of the alert window.
     * @param content The main body message text content.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Helper method to validate the 5 common input fields shared across all registration forms.
     * Prevents code duplication following the DRY (Don't Repeat Yourself) principle.
     *
     * @param fname     First Name string.
     * @param lname     Last Name string.
     * @param idText    ID Number string.
     * @param phoneText Mobile Number string.
     * @param emailText Email address string.
     * @return true if all inputs meet the structural formatting requirements, false otherwise.
     */
    private boolean isCommonInputValid(String fname, String lname, String idText, String phoneText, String emailText) {
        // 1. Check for empty fields
        if (fname.isEmpty() || lname.isEmpty() || idText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
            return false;
        }

        // 2. Validate names contain ENGLISH letters and spaces only
        if (!fname.matches("[a-zA-Z\\s]+") || !lname.matches("[a-zA-Z\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name", "First and Last names must contain only English letters!");
            return false;
        }

        // 3. Validate ID formatting constraints (exactly 5 digits)
        if (!idText.matches("\\d{5}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be 5 digits long (numbers only)!");
            return false;
        }

        // 4. Validate mobile identification (must contain only numbers)
        if (!phoneText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile Number must contain digits only!");
            return false;
        }
        
        // 5. Validate email structure (must contain an '@' symbol)
        if (!emailText.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter an email containing an '@' symbol.");
            return false;
        }

        return true;
    }
    
    /**
     * Extracts input fields, executes centralized validation logic, 
     * verifies family size specifications, and dispatches the request to the logic layer.
     */
    private void handleFamilyRegister() {
        String fname = famFname.getText().trim();
        String lname = famLname.getText().trim();
        String idText = famId.getText().trim();
        String phoneText = famPhone.getText().trim();
        String emailText = famEmail.getText().trim();
        String membersText = famMembers.getText().trim();

        // 1. Invoke centralized validation helper
        if (!isCommonInputValid(fname, lname, idText, phoneText, emailText)) {
            return;
        }

        // 2. Validate family-specific input parameters
        if (membersText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill Family Members Amount!");
            return;
        }

        int parsedMembers;
        try {
            parsedMembers = Integer.parseInt(membersText);
            if (parsedMembers <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Members Amount", "Family Members Amount must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Members Amount", "Family Members Amount must be a valid number!");
            return;
        }

        // 3. Dispatch the registration command message to the logic layer
        Message response = logic.requestFamilyRegistration(
            Integer.parseInt(idText), fname, lname, emailText, phoneText, parsedMembers
        );

        // 4. Handle server response
        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            int subNum = (int) response.getData();
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Family Subscription Registered!\nSub Number: " + subNum);
            
            // Clear fields upon successful registration
            famFname.clear(); famLname.clear(); famId.clear(); famPhone.clear(); famEmail.clear(); famMembers.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected family subscription registration.");
        }
    }
    
    /**
     * Extracts input fields, executes centralized validation logic,
     * and dispatches a single subscription request to the logic layer.
     */
    private void handleSingleRegister() {
        String fname = sFname.getText().trim();
        String lname = sLname.getText().trim();
        String idText = sId.getText().trim();
        String phoneText = sPhone.getText().trim();
        String emailText = sEmail.getText().trim();

        // 1. Invoke centralized validation helper
        if (!isCommonInputValid(fname, lname, idText, phoneText, emailText)) {
            return;
        }

        // 2. Dispatch the registration command message to the logic layer
        Message response = logic.requestSingleRegistration(
            Integer.parseInt(idText), fname, lname, emailText, phoneText
        );

        // 3. Handle server response
        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            int subNum = (int) response.getData();
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Single Subscription Registered!\nSub Number: " + subNum);
            
            // Clear fields upon successful registration
            sFname.clear(); sLname.clear(); sId.clear(); sPhone.clear(); sEmail.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected single subscription registration.");
        }
    }
    
    /**
     * Extracts input fields, executes centralized validation logic,
     * and dispatches a certified group guide request package to the logic layer.
     */
    private void handleGuideRegister() {
        String fname = gFname.getText().trim();
        String lname = gLname.getText().trim();
        String idText = gId.getText().trim();
        String phoneText = gPhone.getText().trim();
        String emailText = gEmail.getText().trim();

        // 1. Invoke centralized validation helper
        if (!isCommonInputValid(fname, lname, idText, phoneText, emailText)) {
            return;
        }

        // 2. Dispatch the registration command message to the logic layer (includes the Guide ID)
        Message response = logic.requestGuideRegistration(
            Integer.parseInt(idText), fname, lname, emailText, phoneText
        );

        // 3. Handle server response
        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Group Guide Registered Successfully!");
            
            // Clear fields upon successful registration
            gFname.clear(); gLname.clear(); gId.clear(); gPhone.clear(); gEmail.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected guide registration.");
        }
    }
    
    /**
     * Logs the service representative out of the active user session 
     * and redirects the window back to the main login panel screen view.
     */
    @FXML
    public void logoutbtn() {
        client.logic.CurUser.logout();
        ScreenSwitch.switchScreen("/client/gui/LoginScreen.fxml", "GoNature Login");
    }
}