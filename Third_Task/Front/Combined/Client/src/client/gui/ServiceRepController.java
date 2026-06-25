package client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.ArrayList;

import client.logic.CurUser;
import client.logic.ServiceRepLogic;
import common.Message;
import common.MessageType;
import common.Subscriber;
import common.Workers;

/**
 * Controller class for the Service Representative Panel.
 * Manages the user interface for registering new Family Subscribers, 
 * Single Subscribers, and Group Guides into the GoNature system.
 */
public class ServiceRepController {

    @FXML 
    private VBox mainContainer;
    @FXML private Button logoutBtn;
    private TextField famFname, famLname, famId, famPhone, famEmail, famMembers;
    private TextField sFname, sLname, sId, sPhone, sEmail;
    private TextField gFname, gLname, gId, gPhone, gEmail;
    private ServiceRepLogic logic;

    /**
     * Initializes the user interface panel.
     * Automatically called after the FXML file is loaded.
     * Constructs the tabs and form fields for the registration processes.
     */
    @FXML
    public void initialize() {
        logic = new ServiceRepLogic();
        
        mainContainer.setSpacing(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Service Representative Panel");
        
        Label myInfo = new Label(CurUser.getMyInfo());

        myInfo.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
        myInfo.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab familyTab = new Tab("Family Subscription");
        Tab singleTab = new Tab("Single Subscription");
        Tab groupTab = new Tab("Group Guide");
        
        Tab infoTab = new Tab("My Info");
        infoTab.setContent(myInfo);
        
        // ---------------------------------------------------------------------
        // General Info Tab: Workers and Subscribers
        // ---------------------------------------------------------------------
        Tab totalInfoTab = new Tab("Workers/Subscribers info");
        
        ComboBox<String> viewSelector = new ComboBox<>();
        viewSelector.getItems().addAll("Subscribers", "Workers");
        viewSelector.setValue("Subscribers"); 

        TableView<Subscriber> subscriberTable = new TableView<>(); 
        TableColumn<Subscriber, String> subFname  = createColumn("First name", "fname", 120);
        TableColumn<Subscriber, String> subLname  = createColumn("Last name", "lname", 120);
        TableColumn<Subscriber, String> subEmail  = createColumn("Email", "email", 160); 
        TableColumn<Subscriber, String> subPhone  = createColumn("Phone number", "phone", 120);
        TableColumn<Subscriber, String> subCard   = createColumn("Credit card", "card", 150);
        TableColumn<Subscriber, Integer> subFamily = createColumn("Family members", "familyMembers", 120);
        TableColumn<Subscriber, Integer> subNum    = createColumn("Subscriber number", "subNum", 130);
        subscriberTable.getColumns().addAll(subFname, subLname, subEmail, subPhone, subCard, subFamily, subNum);

        TableView<Workers> workersTable = new TableView<>();
        TableColumn<Workers, String> workFname = createColumn("First name", "firstName", 120);
        TableColumn<Workers, String> workLname = createColumn("Last name", "lastName", 120);
        TableColumn<Workers, String> workEmail = createColumn("Email", "email", 160);
        TableColumn<Workers, String> workRole  = createColumn("Role", "role", 120);
        TableColumn<Workers, String> workPark  = createColumn("Park Name", "parkName", 120);
        workersTable.getColumns().addAll(workFname, workLname, workEmail, workRole, workPark);

        StackPane tableContainer = new StackPane();
        tableContainer.getChildren().add(subscriberTable); 
        VBox.setVgrow(tableContainer, Priority.ALWAYS); 

        viewSelector.setOnAction(e -> {
            tableContainer.getChildren().clear();
            if ("Subscribers".equals(viewSelector.getValue())) {
                tableContainer.getChildren().add(subscriberTable);
                ArrayList<Subscriber> subs = logic.loadSubscribers();
                subscriberTable.getItems().setAll(subs);
            } else {
                tableContainer.getChildren().add(workersTable);
                ArrayList<Workers> workers = logic.loadWorkers();
                workersTable.getItems().setAll(workers);
            }
        });

        VBox totalInfoVBox = new VBox(15);
        totalInfoVBox.setPadding(new Insets(15));
        totalInfoVBox.getChildren().addAll(new Label("Select View Type:"), viewSelector, tableContainer);
        totalInfoTab.setContent(totalInfoVBox);

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
        title.getStyleClass().add("screen-title");
        myInfo.getStyleClass().add("worker-info-box");
        tabPane.getStyleClass().add("tab-pane");
        logoutBtn.getStyleClass().add("btn-secondary");
        
        // Styling Form Cards (GridPanes)
        familyGrid.getStyleClass().add("form-card");
        singleGrid.getStyleClass().add("form-card");
        groupGrid.getStyleClass().add("form-card");

        // Registration Form Buttons
        familySubmitBtn.getStyleClass().add("btn-primary");
        singleSubmitBtn.getStyleClass().add("btn-primary");
        groupSubmitBtn.getStyleClass().add("btn-primary");

        // Tables Styling
        subscriberTable.getStyleClass().add("table-view");
        workersTable.getStyleClass().add("table-view");
        tabPane.getTabs().addAll(familyTab, singleTab, groupTab, totalInfoTab, infoTab); 
        mainContainer.getChildren().addAll(title, tabPane);
    }

    /**
     * Displays structural popup notifications on the screen for success or error feedback.
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
     * Validates input fields and delegates the Family Subscription registration request to the logic layer.
     */
    private void handleFamilyRegister() {
        String firstName = famFname.getText().trim();
        String lastName = famLname.getText().trim();
        String idText = famId.getText().trim();
        String phoneText = famPhone.getText().trim();
        String emailText = famEmail.getText().trim();
        String membersText = famMembers.getText().trim();

        // 1. Validation: Reject completely empty parameters
        if (firstName.isEmpty() || lastName.isEmpty() || idText.isEmpty() || 
            phoneText.isEmpty() || emailText.isEmpty() || membersText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Family Subscription!");
            return;
        }

        // 2. Validation: Names must contain English alphabetic characters only
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name Fields", "First name and last name must contain English letters only!");
            return;
        }

        // 3. Validation: Identity specifications configuration (Exactly 5 digits long)
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        // 4. Validation: Structural pattern checks for email fields
        if (!emailText.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email Structure", "Email address must contain a valid '@' symbol.");
            return;
        }

        // 5. Validation: Mobile number must contain digits only (Any length allowed)
        if (!phoneText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile number must contain numeric digits only!");
            return;
        }

        // 6. Validation: Bound the dynamic family headcount sizes securely between 1 and 15 maximum
        if (!membersText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Format", "Family headcount must be a positive numeric value.");
            return;
        }

        int parsedMembers = Integer.parseInt(membersText);
        if (parsedMembers < 1 || parsedMembers > 15) {
            showAlert(Alert.AlertType.ERROR, "Quota Boundary Broken", "Number of family members must be constrained strictly between 1 and 15 maximum.");
            return; 
        }

        // 7. Data Pipeline Dispatching Phase
        Message response = logic.requestFamilyRegistration(
            Integer.parseInt(idText), firstName, lastName, emailText, phoneText, parsedMembers
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            int subNum = (int) response.getData();
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Family Subscription Registered!\nSub Number: " + subNum);
            famFname.clear(); famLname.clear(); famId.clear(); famPhone.clear(); famEmail.clear(); famMembers.clear();
        } else {
            // Check if the server provided a specific error reason string due to database constraint violation
            String errorReason = (response != null && response.getData() != null) ? (String) response.getData() : "";
            
            if ("DUPLICATE_ID".equals(errorReason)) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                    "Operation Aborted: This National ID Number is already registered to an active subscriber in the database!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                    "Server Error: Internal database constraint failure or network drop encountered.");
            }
        }
    }
    
    /**
     * Validates input fields and delegates the Single Subscription registration request to the logic layer.
     */
    private void handleSingleRegister() {
        String firstName = sFname.getText().trim();
        String lastName = sLname.getText().trim();
        String idText = sId.getText().trim();
        String phoneText = sPhone.getText().trim();
        String emailText = sEmail.getText().trim();

        // 1. Validation: Block empty variables
        if (firstName.isEmpty() || lastName.isEmpty() || idText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Single Subscription!");
            return;
        }

        // 2. Validation: Names must contain English alphabetic characters only
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name Fields", "First name and last name must contain English letters only!");
            return;
        }

        // 3. Validation: Identity configurations (Exactly 5 digits long)
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        // 4. Validation: Structural email checks
        if (!emailText.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email Structure", "Email address must contain a valid '@' symbol.");
            return;
        }

        // 5. Validation: Mobile number must contain digits only (Any length allowed)
        if (!phoneText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile number must contain numeric digits only!");
            return;
        }

        // 6. Data Pipeline Transmission Phase
        Message response = logic.requestSingleRegistration(
            Integer.parseInt(idText), firstName, lastName, emailText, phoneText
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Group Guide Registered Successfully!");
            gFname.clear(); gLname.clear(); gId.clear(); gPhone.clear(); gEmail.clear();
        } else {
            // Check if the server provided a specific error reason string due to database constraint violation
            String errorReason = (response != null && response.getData() != null) ? (String) response.getData() : "";
            
            if ("DUPLICATE_ID".equals(errorReason)) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                    "Operation Aborted: This Guide ID Number is already registered to an active group guide in the database!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", 
                    "Server Error: Internal database constraint failure or network drop encountered.");
            }
        }
    }
    
    /**
     * Validates input fields and delegates the Group Guide registration request to the logic layer.
     */
    private void handleGuideRegister() {
        String firstName = gFname.getText().trim();
        String lastName = gLname.getText().trim();
        String idText = gId.getText().trim();
        String phoneText = gPhone.getText().trim();
        String emailText = gEmail.getText().trim();

        // 1. Validation: Block incomplete arrays
        if (firstName.isEmpty() || lastName.isEmpty() || idText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields inside Group Guide!");
            return;
        }

        // 2. Validation: Names must contain English alphabetic characters only
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Name Fields", "First name and last name must contain English letters only!");
            return;
        }

        // 3. Validation: Structural sequence verification (Exactly 5 digits long)
        if (idText.length() != 5 || !idText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID Number", "ID Number must be exactly 5 digits long!");
            return;
        }

        // 4. Validation: Email criteria matching
        if (!emailText.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email Structure", "Email address must contain a valid '@' symbol.");
            return;
        }

        // 5. Validation: Mobile number must contain digits only (Any length allowed)
        if (!phoneText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Mobile Number", "Mobile number must contain numeric digits only!");
            return;
        }

        // 6. Data Pipeline Transmission Phase
        Message response = logic.requestGuideRegistration(
            Integer.parseInt(idText), firstName, lastName, emailText, phoneText
        );

        if (response != null && response.getType() == MessageType.REGISTRATION_SUCCESS) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Group Guide Registered Successfully!");
            gFname.clear(); gLname.clear(); gId.clear(); gPhone.clear(); gEmail.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Server rejected guide registration.");
        }
    }

    @FXML
    public void logoutbtn() {
        client.logic.CurUser.logout();
    }
    
    private <S,T> TableColumn<S, T> createColumn(String title, String propertyName, double prefWidth) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setPrefWidth(prefWidth);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        return column;
    }
}