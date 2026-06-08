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
        mainTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab parkManagerTab = new Tab("Park Manager");
        VBox parkButtonsContainer = new VBox(15);
        parkButtonsContainer.setPadding(new Insets(20));
        parkButtonsContainer.setAlignment(Pos.CENTER);

        Button btnRequestParams = new Button("Request Parameter Change");
        Button btnProduceReports = new Button("Generate Reports");
        Button btnPromotions = new Button("Promotions");

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

        btnManageRequests.setMaxWidth(Double.MAX_VALUE);
        btnDeptReports.setMaxWidth(Double.MAX_VALUE);
        btnDeptPromotions.setMaxWidth(Double.MAX_VALUE);

        deptButtonsContainer.getChildren().addAll(btnManageRequests, btnDeptReports, btnDeptPromotions);
        deptManagerTab.setContent(deptButtonsContainer);

// tabPane.getTabs().addAll(parkManagerTab, deptManagerTab);
     // Filtering the display of tabs according to the role of the employee connected from the static department
        String userRole = client.logic.CurUser.getRole();
        
        if ("Park Manager".equals(userRole)) {
            
            tabPane.getTabs().add(parkManagerTab);
        } else if ("Department Manager".equals(userRole)) {
           
            tabPane.getTabs().add(deptManagerTab);
        } else {
           
            tabPane.getTabs().addAll(parkManagerTab, deptManagerTab);
        }
        
        mainContainer.getChildren().addAll(mainTitle, tabPane);

        btnRequestParams.setOnAction(e -> switchToParkManagerRequestScreen());
        btnPromotions.setOnAction(e -> switchToSharedPromotionsScreen());
        btnProduceReports.setOnAction(e -> ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports"));
        
        btnManageRequests.setOnAction(e -> switchToDeptManagerApprovalScreen());
        btnDeptPromotions.setOnAction(e -> switchToSharedPromotionsScreen());
        btnDeptReports.setOnAction(e -> ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports"));
        
    }

    // Displays the interface for park managers to submit a parameter change request
    private void switchToParkManagerRequestScreen() {
    	mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Submit Parameter Change Request");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        ComboBox<String> paramComboBox = new ComboBox<>();
        paramComboBox.getItems().addAll(
                "Maximum Visitors Capacity",
                "Order Gap for Casual Visitors",
                "Estimated Stay Time (Default 4 hours)"
        );
        paramComboBox.setPromptText("Select parameter to change");

        TextField valTxtField = new TextField();

        grid.add(new Label("Requested Parameter:"), 0, 0);
        grid.add(paramComboBox, 1, 0);

        grid.add(new Label("Requested New Value:"), 0, 1);
        grid.add(valTxtField, 1, 1);

        HBox actionButtons = new HBox(10);
        Button submitBtn = new Button("Send Request for Department Manager Approval");
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showMainDashboard());

        submitBtn.setOnAction(e -> {
            String selectedUIParam = paramComboBox.getValue();
            String newValueStr = valTxtField.getText();

            if (selectedUIParam == null || newValueStr.isEmpty()) {
                System.out.println("⚠️ Please fill all fields before submitting!");
                return;
            }
            String dbParamName = "";
            if (selectedUIParam.contains("Capacity")) dbParamName = "max_capacity";
            else if (selectedUIParam.contains("Gap")) dbParamName = "casual_gap";
            else if (selectedUIParam.contains("Stay")) dbParamName = "estimated_stay_time";

            Message response = logic.sendParameterRequest("Banias", 1, dbParamName, 500, Integer.parseInt(newValueStr));
            if (response != null && response.getType() == MessageType.REQUEST_SUBMIT_SUCCESS) {
            	System.out.println("🚀 Success! Data inserted directly from the text field into MySQL!");
                showMainDashboard();
            } else {
            	System.out.println("❌ SQL Error while inserting data from screen.");
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
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<ParameterRequest> requestsList = new ListView<>();
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
        
        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: #ffcccc; -fx-font-weight: bold;");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showMainDashboard());

        approveBtn.setOnAction(e -> {
        	ParameterRequest selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.getStatus())) return;

            Message res = logic.handleRequestDecision(selectedItem, "Approved");
            if (res != null && res.getType() == MessageType.UPDATE_REQUEST_SUCCESS) {
                selectedItem.setStatus("Approved");
                requestsList.refresh(); 
                System.out.println("✅ Request Approved and UI refreshed!");
            }
        });

        rejectBtn.setOnAction(e -> {
        	ParameterRequest selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.getStatus())) return;

            Message res = logic.handleRequestDecision(selectedItem, "Rejected");
            if (res != null && res.getType() == MessageType.UPDATE_REQUEST_SUCCESS) {
                selectedItem.setStatus("Rejected");
                requestsList.refresh(); 
                System.out.println("❌ Request Rejected and UI refreshed!");
            }
        });

        actionButtons.getChildren().addAll(approveBtn, rejectBtn, backBtn);
        mainContainer.getChildren().addAll(title, new Label("Pending Parameter Requests (From DB):"), requestsList, actionButtons);
    }

    // Displays the promotions management form and updates the park discount parameter in the database
    private void switchToSharedPromotionsScreen() {
    	mainContainer.getChildren().clear();
        mainContainer.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Promotions Management");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);

        TextField couponNameField = new TextField();
        TextField discountField = new TextField();
        discountField.setPromptText("e.g. 20 for 20%");
        TextField startDateField = new TextField();
        TextField endDateField = new TextField();

        grid.add(new Label("Coupon Name:"), 0, 0);
        grid.add(couponNameField, 1, 0);

        grid.add(new Label("Discount Percentage (%):"), 0, 1);
        grid.add(discountField, 1, 1);

        grid.add(new Label("Start Date (DD/MM/YYYY):"), 0, 2);
        grid.add(startDateField, 1, 2);

        grid.add(new Label("End Date (DD/MM/YYYY):"), 0, 3);
        grid.add(endDateField, 1, 3);

        Button btnSubmitPromo = new Button("Submit Promotion");

        Label listTitle = new Label("Coupons List:");
        listTitle.setStyle("-fx-font-weight: bold;");
        
        ListView<String> promoList = new ListView<>();
        promoList.setPrefHeight(120);

        btnSubmitPromo.setOnAction(e -> {
        	String discountStr = discountField.getText();
            String couponName = couponNameField.getText();

            if (discountStr.isEmpty() || couponName.isEmpty()) {
                System.out.println("⚠️ Please fill Coupon Name and Discount fields!");
                return;
            }

            try {
                double discountPercent = Double.parseDouble(discountStr);
                if (discountPercent < 0 || discountPercent > 100) {
                    System.out.println("⚠️ Discount must be between 0 and 100!");
                    return;
                }
                double dbDiscountValue = discountPercent / 100.0;
                Message response = logic.sendPromotionUpdate("Banias", dbDiscountValue);
                
                if (response != null && response.getType() == MessageType.PROMOTION_ACTIVATED_SUCCESS) {
                    System.out.println("🚀 Success! Promotion '" + couponName + "' activated via Server!");
                    String newPromoText = String.format("%s | %s%% Off | %s - %s", 
                            couponName, discountStr, startDateField.getText(), endDateField.getText());
                    promoList.getItems().add(0, newPromoText);
                    couponNameField.clear(); discountField.clear(); 
                    startDateField.clear(); endDateField.clear();
                } else {
                    System.out.println("❌ Server failed to activate promotion.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("⚠️ Discount must be a valid number!");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showMainDashboard());

        mainContainer.getChildren().addAll(title, grid, btnSubmitPromo, new Separator(), listTitle, promoList, backBtn);
    }

    // Holds structural information of parameter requests for custom user interface list rendering
    private static class ListCellData {
        int requestId;
        String parkName;
        String parameterName;
        int requestedValue;
        String displayText;
        String status = "Pending";

        public ListCellData(int requestId, String parkName, String parameterName, int requestedValue, String displayText) {
            this.requestId = requestId;
            this.parkName = parkName;
            this.parameterName = parameterName;
            this.requestedValue = requestedValue;
            this.displayText = displayText;
        }
    }
}