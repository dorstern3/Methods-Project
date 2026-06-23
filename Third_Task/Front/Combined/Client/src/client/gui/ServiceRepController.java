package client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import client.logic.ServiceRepLogic;
import common.Message;
import common.MessageType;

public class ServiceRepController{

    private Connection dbConnection;
    @FXML private VBox mainContainer;
    private TextField famFname, famLname, famId, famPhone, famEmail, famMembers;
    private TextField sFname, sLname, sId, sPhone, sEmail;
    private TextField gFname, gLname, gId, gPhone, gEmail;
    private ServiceRepLogic logic;

    // Initializes the user interface panel and establishes database connection
    public void initialize() {
        logic = new ServiceRepLogic();
        
    	mainContainer.setSpacing(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Service Representative Panel");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab familyTab = new Tab("Family Subscription");
        Tab singleTab = new Tab("Single Subscription");
        Tab groupTab = new Tab("Group Guide");

        // ---------------------------------------------------------------------
        // Tab 1: Family Subscription
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
        
        //handleFamilyRegister() 
        familySubmitBtn.setOnAction(e -> handleFamilyRegister());

        familyVBox.getChildren().addAll(familyGrid, familySubmitBtn);
        familyTab.setContent(familyVBox);

        // ---------------------------------------------------------------------
        // Tab 2: Single Subscription
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
        // Tab 3: Group Guide
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
        mainContainer.getChildren().addAll(title, tabPane);

    }

    // Displays structural popup notifications on the screen for success or error feedback
    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    private void handleFamilyRegister() {
       
        if (famFname.getText().trim().isEmpty() || famLname.getText().trim().isEmpty() || 
            famId.getText().trim().isEmpty() || famPhone.getText().trim().isEmpty() || 
            famEmail.getText().trim().isEmpty() || famMembers.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Family Subscription!");
            return;
        }

        String idText = famId.getText().trim();
        String phoneText = famPhone.getText().trim();
        String membersText = famMembers.getText().trim();

       
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        
        if (!phoneText.matches("[0-9\\-]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile Number must contain digits only!");
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

        
        Message response = logic.requestFamilyRegistration(
            Integer.parseInt(idText), famFname.getText().trim(), famLname.getText().trim(),
            famEmail.getText().trim(), phoneText, parsedMembers
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            int subNum = (int) response.getData();
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Family Subscription Registered!\nSub Number: " + subNum);
            famFname.clear(); famLname.clear(); famId.clear(); famPhone.clear(); famEmail.clear(); famMembers.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected family subscription registration.");
        }
    }
    
    private void handleSingleRegister() {
          
        if (sFname.getText().trim().isEmpty() || sLname.getText().trim().isEmpty() || 
            sId.getText().trim().isEmpty() || sPhone.getText().trim().isEmpty() || 
            sEmail.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Single Subscription!");
            return;
        }

        String idText = sId.getText().trim();
        String phoneText = sPhone.getText().trim();

       
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        
        if (!phoneText.matches("[0-9\\-]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile Number must contain digits only!");
            return;
        }

         
        Message response = logic.requestSingleRegistration(
            Integer.parseInt(idText), sFname.getText().trim(), sLname.getText().trim(),
            sEmail.getText().trim(), phoneText
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            int subNum = (int) response.getData();
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Single Subscription Registered!\nSub Number: " + subNum);
            sFname.clear(); sLname.clear(); sId.clear(); sPhone.clear(); sEmail.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected single subscription registration.");
        }
    }
    
    private void handleGuideRegister() {
        
        if (gFname.getText().trim().isEmpty() || gLname.getText().trim().isEmpty() || 
            gId.getText().trim().isEmpty() || gPhone.getText().trim().isEmpty() || 
            gEmail.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Group Guide!");
            return;
        }

        String idText = gId.getText().trim();
        String phoneText = gPhone.getText().trim();

        
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        
        if (!phoneText.matches("[0-9\\-]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile Number must contain digits only!");
            return;
        }

        
        Message response = logic.requestGuideRegistration(
            gFname.getText().trim(), gLname.getText().trim(), gEmail.getText().trim(), phoneText
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Group Guide Registered Successfully!");
            gFname.clear(); gLname.clear(); gId.clear(); gPhone.clear(); gEmail.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected guide registration.");
        }
    }
}