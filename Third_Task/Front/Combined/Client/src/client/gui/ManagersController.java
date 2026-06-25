package client.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import client.logic.ManagersLogic;
import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
import common.ParameterRequest;
import client.logic.CurUser;

public class ManagersController {

    // --- Field for occupancy tracking ---
    private Label lblLiveCapacity;
    private ComboBox<String> parkSelectorComboBox;
    
    @FXML private VBox mainContainer;
    private ManagersLogic logic;

    public void initialize() {
    	logic = new ManagersLogic();
        showMainDashboard();
    }

    // Initializes the main window, establishes database connection, and builds the primary dashboard
    private void showMainDashboard(){
        mainContainer.getChildren().clear();
    	
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        Label mainTitle = new Label("Managers Screen");
        mainTitle.getStyleClass().add("screen-title");
     
        Label myInfo = new Label(CurUser.getMyInfo());
        myInfo.getStyleClass().add("worker-info-box");
        myInfo.setPadding(new javafx.geometry.Insets(20, 25, 20, 25));
        myInfo.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        myInfo.setPrefWidth(420);
        
    	VBox infoContainer = new VBox(myInfo);
        infoContainer.setPadding(new Insets(20));
        infoContainer.setStyle("-fx-background-color: #ffffff;");
        
        Tab infoTab = new Tab("My Info");
        infoTab.setContent(infoContainer);
        
        // --- Create the capacity label and a manual refresh button ---
        lblLiveCapacity = new Label("Current Park Occupancy: Loading...");
        lblLiveCapacity.getStyleClass().add("bold-label");

        Button btnRefreshCapacity = new Button("🔄 Refresh");
        btnRefreshCapacity.getStyleClass().add("btn-secondary");
        btnRefreshCapacity.setOnAction(e -> updateLiveCapacity());

        
        parkSelectorComboBox = new ComboBox<>();
        parkSelectorComboBox.getStyleClass().add("combo-box");
        
        try {
            Message getParksRequest = new Message(MessageType.GET_PARKS, null);
            Message getParksResponse = (Message) client.ClientUI.clientChat.accept(getParksRequest);

            if (getParksResponse != null && getParksResponse.getType() == MessageType.GET_PARKS_RESPONSE) {
               
                java.util.ArrayList<String> dbParks = (java.util.ArrayList<String>) getParksResponse.getData();
                parkSelectorComboBox.getItems().addAll(dbParks);
            } else {
                System.err.println(" Failed to load parks from DB, falling back to defaults.");
                parkSelectorComboBox.getItems().addAll("Achziv", "Banias", "Caesarea", "Ein Gedi", "Masada");
            }
        } catch (Exception e) {
            System.err.println(" Exception while fetching parks list from server:");
            e.printStackTrace();
            
            parkSelectorComboBox.getItems().addAll("Achziv", "Banias", "Caesarea", "Ein Gedi", "Masada");
        }
        
       
        String initialPark = CurUser.getParkName();
        if (initialPark == null || initialPark.isEmpty()) {
            initialPark = "Caesarea"; 
        }
        parkSelectorComboBox.setValue(initialPark);
        
        
        parkSelectorComboBox.setOnAction(e -> updateLiveCapacity());

       
        HBox capacityContainer = new HBox(15);
        capacityContainer.setAlignment(Pos.CENTER);
        capacityContainer.getStyleClass().add("occupancy-banner");
        
        if ("Dept_manager".equals(CurUser.getRole())) {
        	Label selectParkLabel = new Label("Select Park:");
            capacityContainer.getChildren().addAll(new Label("Select Park:"), parkSelectorComboBox, lblLiveCapacity, btnRefreshCapacity);
            selectParkLabel.getStyleClass().add("bold-label");
        } else {
            capacityContainer.getChildren().addAll(lblLiveCapacity, btnRefreshCapacity);
        }

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab parkManagerTab = new Tab("Park Manager");
        VBox parkButtonsContainer = new VBox(15);
        parkButtonsContainer.setPadding(new Insets(20));
        parkButtonsContainer.setAlignment(Pos.CENTER);

        Button btnRequestParams = new Button("Request Parameter Change");
        Button btnProduceReports = new Button("Generate Reports");
        Button btnPromotions = new Button("Promotions");

        btnRequestParams.getStyleClass().add("btn-primary");
        btnProduceReports.getStyleClass().add("btn-secondary");
        btnPromotions.getStyleClass().add("btn-secondary");
        
        btnRequestParams.setMaxWidth(Double.MAX_VALUE);
        btnProduceReports.setMaxWidth(Double.MAX_VALUE);
        btnPromotions.setMaxWidth(Double.MAX_VALUE);

        parkButtonsContainer.getChildren().addAll(btnRequestParams, btnProduceReports, btnPromotions);
        parkManagerTab.setContent(parkButtonsContainer);

        Tab deptManagerTab = new Tab("Department Manager");
        VBox deptButtonsContainer = new VBox(15);
        deptButtonsContainer.setPadding(new Insets(20));
        deptButtonsContainer.setAlignment(Pos.CENTER);

        Button btnManageRequests = new Button("Manage Parameter Change Requests");
        Button btnDeptReports = new Button("Generate Reports");
        Button btnDeptPromotions = new Button("Promotions");

        btnManageRequests.getStyleClass().add("btn-primary");
        btnDeptReports.getStyleClass().add("btn-secondary");
        btnDeptPromotions.getStyleClass().add("btn-secondary");
        
        btnManageRequests.setMaxWidth(Double.MAX_VALUE);
        btnDeptReports.setMaxWidth(Double.MAX_VALUE);
        btnDeptPromotions.setMaxWidth(Double.MAX_VALUE);

        deptButtonsContainer.getChildren().addAll(btnManageRequests, btnDeptReports, btnDeptPromotions);
        deptManagerTab.setContent(deptButtonsContainer);

        // Filtering the display of tabs according to the role of the employee connected from the static department
        String userRole = client.logic.CurUser.getRole();
        
        if ("Park_manager".equals(userRole)) {
            tabPane.getTabs().addAll(parkManagerTab,infoTab); 
        } else if ("Dept_manager".equals(userRole)) {
            tabPane.getTabs().addAll(deptManagerTab,infoTab); 
        } else {
            tabPane.getTabs().addAll(parkManagerTab, deptManagerTab);
        }
          
        Button btnBackToLogin = new Button("Back");
        btnBackToLogin.getStyleClass().add("btn-nav-back");
        btnBackToLogin.setOnAction(e -> {
            ((javafx.scene.Node)e.getSource()).getScene().getWindow().hide();
            client.logic.CurUser.logout();
        });

       
        mainContainer.getChildren().addAll(mainTitle, capacityContainer, tabPane, btnBackToLogin);

        btnRequestParams.setOnAction(e -> switchToParkManagerRequestScreen());
        btnPromotions.setOnAction(e -> switchToSharedPromotionsScreen());
        btnProduceReports.setOnAction(e -> ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports"));
        
        btnManageRequests.setOnAction(e -> switchToDeptManagerApprovalScreen());
        btnDeptPromotions.setOnAction(e -> switchToSharedPromotionsScreen());
        btnDeptReports.setOnAction(e -> ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports"));
        
        // Fetch the initial capacity once when the screen loads
        updateLiveCapacity(); 
    }
    

    // Displays the interface for park managers to submit a parameter change request
    private void switchToParkManagerRequestScreen() {
        mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Submit Parameter Change Request");
        title.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        ComboBox<String> paramComboBox = new ComboBox<>();
        paramComboBox.getStyleClass().add("combo-box");
        paramComboBox.getItems().addAll(
                "Maximum Visitors Capacity",
                "Order Gap for Casual Visitors",
                "Estimated Stay Time (Default 4 hours)"
        );
        paramComboBox.setPromptText("Select parameter to change");

        TextField valTxtField = new TextField();
        valTxtField.getStyleClass().add("text-field");
        
        grid.add(new Label("Requested Parameter:"), 0, 0);
        grid.add(paramComboBox, 1, 0);

        grid.add(new Label("Requested New Value:"), 0, 1);
        grid.add(valTxtField, 1, 1);

        HBox actionButtons = new HBox(10);
        Button submitBtn = new Button("Send Request for Department Manager Approval");
        submitBtn.getStyleClass().add("btn-primary");
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> showMainDashboard());

        submitBtn.setOnAction(e -> {
            String selectedUIParam = paramComboBox.getValue();
            String newValueStr = valTxtField.getText();

            // 1. Validation: Ensure all form input fields are populated
            if (selectedUIParam == null || newValueStr == null || newValueStr.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all fields before submitting!");
                return;
            }

            // 2. Validation: Ensure the value contains digits only to prevent NumberFormatException
            if (!newValueStr.trim().matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "The requested new value must contain numbers/digits only!");
                return; 
            }

            String dbParamName = "";
            if (selectedUIParam.contains("Capacity")) dbParamName = "max_capacity";
            else if (selectedUIParam.contains("Gap")) dbParamName = "casual_gap";
            else if (selectedUIParam.contains("Stay")) dbParamName = "estimated_staying_time";

            String currentPark = CurUser.getParkName();
            int currentWorkerId = CurUser.getEmployeeId();

            if (currentPark == null || currentPark.isEmpty()) {
                currentPark = "Banias"; 
            }

            int parsedValue = Integer.parseInt(newValueStr.trim());

            // 3. Dynamic Business Rule Validation: Enforce 10% max capacity limit for Order Gap parameters
            if ("casual_gap".equals(dbParamName)) {
                try {
                    // Fetch real-time capacity parameters directly via the active connection messaging pipeline
                    Message capRequest = new Message(MessageType.GET_PARK_OCCUPANCY, currentPark);
                    Message capResponse = (Message) client.ClientUI.clientChat.accept(capRequest);

                    if (capResponse != null && capResponse.getType() == MessageType.GET_PARK_OCCUPANCY_RESPONSE) {
                        int[] capacityData = (int[]) capResponse.getData();
                        int maxCapacity = capacityData[1]; // Get max_capacity from index 1
                        
                        // Calculate the absolute 10% maximum boundary
                        int maxAllowedGap = (int) (maxCapacity * 0.10);

                        if (parsedValue > maxAllowedGap) {
                            showAlert(Alert.AlertType.ERROR, "Business Rule Violation", 
                                String.format("The requested Gap (%d) exceeds the allowed 10%% threshold of the park's max capacity (%d).\nMaximum allowed value is: %d", 
                                parsedValue, maxCapacity, maxAllowedGap));
                            return; // Stop execution to prevent dispatching invalid database updates
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Configuration Warning", "Could not verify park capacity limits from server. Request aborted.");
                        return;
                    }
                } catch (Exception ex) {
                    System.err.println("Client Error: Failure in verifying dynamic parameter cap thresholds.");
                    ex.printStackTrace();
                    return;
                }
            }

            // 4. Transmission Pipeline: Dispatch the request parameters to the managers logic layer
            Message response = logic.sendParameterRequest(currentPark, currentWorkerId, dbParamName, 500, parsedValue);
            if (response != null && response.getType() == MessageType.REQUEST_SUBMIT_SUCCESS) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Parameter change request submitted successfully!");
                showMainDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Server Error", "SQL Error while inserting data from screen.");
            }
        });

        actionButtons.getChildren().addAll(submitBtn, backBtn);
        mainContainer.getChildren().addAll(title, grid, actionButtons);
    }

    // Displays the pending requests queue allowing the department manager to approve or reject them
    private void switchToDeptManagerApprovalScreen() {
    	mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Manage Pending Requests Approval");
        title.getStyleClass().add("section-title");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<ParameterRequest> requestsList = new ListView<>();
        requestsList.getStyleClass().add("list-view");
        requestsList.setPrefHeight(200); requestsList.setPrefWidth(500);

        requestsList.setCellFactory(lv -> new ListCell<ParameterRequest>() {
            @Override
            protected void updateItem(ParameterRequest item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("ID: %d | Park: %s | Parameter: %s -> New Value: %d", 
                            item.getRequestId(), item.getParkName(), item.getParameterName(), item.getRequestedValue());
                    setText(text);
                    
                    if ("Approved".equals(item.getStatus())) {
                        setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #256029; -fx-font-weight: bold;"); 
                    } else if ("Rejected".equals(item.getStatus())) {
                        setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c63737; -fx-font-weight: bold;"); 
                    } else {
                        setStyle(""); 
                    }
                }
            }
        });
        
        Message response = logic.requestPendingRequests();
        if (response != null && response.getType() == MessageType.GET_PENDING_REQUESTS_RESPONSE) {
            ArrayList<ParameterRequest> serverList = (ArrayList<ParameterRequest>) response.getData();
            requestsList.getItems().addAll(serverList);
        }
        
        HBox actionButtons = new HBox(10);
        Button approveBtn = new Button("Approve Request");
        approveBtn.setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
        approveBtn.getStyleClass().add("btn-primary");
        
        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: #ffcccc; -fx-font-weight: bold;");
        rejectBtn.getStyleClass().add("btn-danger");
        
        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-nav-back");
        backBtn.setOnAction(e -> showMainDashboard());

      
        approveBtn.setOnAction(e -> {
        	ParameterRequest selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.getStatus())) return;

            Message res = logic.handleRequestDecision(selectedItem, "Approved");
            if (res != null && res.getType() == MessageType.UPDATE_REQUEST_SUCCESS) {
                selectedItem.setStatus("Approved");
                requestsList.refresh(); 
                System.out.println("Request Approved and UI refreshed!");
            }
        });

        rejectBtn.setOnAction(e -> {
        	ParameterRequest selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.getStatus())) return;

            Message res = logic.handleRequestDecision(selectedItem, "Rejected");
            if (res != null && res.getType() == MessageType.UPDATE_REQUEST_SUCCESS) {
                selectedItem.setStatus("Rejected");
                requestsList.refresh(); 
                System.out.println("Request Rejected and UI refreshed!");
            }
        });

        Button refreshRequestsBtn = new Button("Refresh List");
        refreshRequestsBtn.getStyleClass().add("btn-secondary");
        refreshRequestsBtn.setOnAction(e -> {
            Message refreshResponse = logic.requestPendingRequests();
            if (refreshResponse != null && refreshResponse.getType() == MessageType.GET_PENDING_REQUESTS_RESPONSE) {
                ArrayList<ParameterRequest> serverList = (ArrayList<ParameterRequest>) refreshResponse.getData();
                requestsList.getItems().clear();  
                requestsList.getItems().addAll(serverList); 
                System.out.println("Requests list refreshed manually!");
            }
        }); 
        actionButtons.getChildren().addAll(approveBtn, rejectBtn, refreshRequestsBtn, backBtn);
        mainContainer.getChildren().addAll(title, new Label("Pending Parameter Requests (From DB):"), requestsList, actionButtons);
    }

 /// Displays the promotions management form and updates the park discount parameter in the database
    private void switchToSharedPromotionsScreen() {
        mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Promotions Management");
        title.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("form-card");
        grid.setVgap(8);
        grid.setHgap(10);

        TextField couponNameField = new TextField();
        TextField discountField = new TextField();
        discountField.setPromptText("e.g. 20 for 20%");

        grid.add(new Label("Coupon Name:"), 0, 0);
        grid.add(couponNameField, 1, 0);

        grid.add(new Label("Discount Percentage (%):"), 0, 1);
        grid.add(discountField, 1, 1);

        Button btnSubmitPromo = new Button("Submit Promotion");
        btnSubmitPromo.getStyleClass().add("btn-primary");

        Label listTitle = new Label("Coupons List:");
        listTitle.getStyleClass().add("bold-label");
        //listTitle.setStyle("-fx-font-weight: bold;");
        
        ListView<String> promoList = new ListView<>();
        promoList.getStyleClass().add("list-view");
        promoList.setPrefHeight(120);

        btnSubmitPromo.setOnAction(e -> {
            String discountStr = discountField.getText().trim();
            String couponName = couponNameField.getText().trim();

            // 1. Validation: Ensure both required input properties are populated
            if (discountStr.isEmpty() || couponName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all fields before submitting!");
                return;
            }

            // 2. Validation: Ensure discount context contains numeric digits only to prevent parsing exceptions
            if (!discountStr.matches("\\d+")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Discount percentage must contain digits only!");
                return;
            }

            // 3. Validation: Enforce absolute business rule range boundaries (0 to 100)
            double discountPercent = Double.parseDouble(discountStr);
            if (discountPercent < 0 || discountPercent > 100) {
                showAlert(Alert.AlertType.WARNING, "Invalid Value", "Discount percentage must be between 0 and 100!");
                return;
            }

            // 4. Secure Processing Execution Sequence: Calculate db fractional coefficient representation
            double dbDiscountValue = discountPercent / 100.0;
            
            String currentPark = CurUser.getParkName();
            if (currentPark == null || currentPark.isEmpty()) {
                currentPark = "Banias";
            }

            // 5. Dispatch network update pipeline data to persistence services
            Message response = logic.sendPromotionUpdate(currentPark, dbDiscountValue);
            
            if (response != null && response.getType() == MessageType.PROMOTION_ACTIVATED_SUCCESS) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Promotion '" + couponName + "' activated successfully for " + currentPark + "!");
                
                String newPromoText = String.format("%s | %s%% Off", couponName, discountStr);
                promoList.getItems().add(0, newPromoText);
                
                // Reset form criteria back to initial pristine blank states
                couponNameField.clear(); 
                discountField.clear(); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Server Error", "Server failed to activate promotion.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setOnAction(e -> showMainDashboard());

        mainContainer.getChildren().addAll(title, grid, btnSubmitPromo, new Separator(), listTitle, promoList, backBtn);
    }

    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Dispatches a manual request query to the server context to grab real-time occupancy fields.
     * Updates the text and color properties dynamically without disrupting layout sequences.
     */
    private void updateLiveCapacity() {
        if (lblLiveCapacity == null) {
            return;
        }
        
        try {
           
            String parkName = "Banias";
            if (parkSelectorComboBox != null && parkSelectorComboBox.getValue() != null) {
                parkName = parkSelectorComboBox.getValue();
            } else {
                parkName = CurUser.getParkName();
                if (parkName == null || parkName.isEmpty()) {
                    parkName = "Banias";
                }
            }

            Message request = new Message(MessageType.GET_PARK_OCCUPANCY, parkName);
            Message response = (Message) client.ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == MessageType.GET_PARK_OCCUPANCY_RESPONSE) {
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
}