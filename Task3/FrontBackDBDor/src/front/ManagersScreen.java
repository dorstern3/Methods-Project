package front;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManagersScreen extends Application {

    private Scene mainManagersScene;
    private Stage mainStage;
    private Connection dbConnection;

    // Launches the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }

    // Initializes the main window, establishes database connection, and builds the primary dashboard
    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        primaryStage.setTitle("GoNature - Managers Panel");

        try {
            String url = "jdbc:mysql://localhost:3306/gonature_db_new?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "Aa123456"; 
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.dbConnection = DriverManager.getConnection(url, username, password);
            System.out.println("📬 ManagersScreen connected to MySQL successfully!");
        } catch (Exception e) {
            System.out.println("❌ Database connection failed in screen!");
            e.printStackTrace();
        }

        VBox rootVBox = new VBox(20);
        rootVBox.setPadding(new Insets(20));
        rootVBox.setAlignment(Pos.TOP_CENTER);

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

        tabPane.getTabs().addAll(parkManagerTab, deptManagerTab);
        rootVBox.getChildren().addAll(mainTitle, tabPane);

        mainManagersScene = new Scene(rootVBox, 550, 450);

        btnRequestParams.setOnAction(e -> switchToParkManagerRequestScreen());
        btnPromotions.setOnAction(e -> switchToSharedPromotionsScreen()); 
        
        btnManageRequests.setOnAction(e -> switchToDeptManagerApprovalScreen());
        btnDeptPromotions.setOnAction(e -> switchToSharedPromotionsScreen()); 

        primaryStage.setScene(mainManagersScene);
        primaryStage.show();
    }

    // Displays the interface for park managers to submit a parameter change request
    private void switchToParkManagerRequestScreen() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

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
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

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

            String sql = "INSERT INTO parameter_requests (park_name, worker_id, parameter_name, current_value, requested_value) VALUES (?, ?, ?, ?, ?);";
            
            try {
                int newValue = Integer.parseInt(newValueStr);
                
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.setString(1, "Banias"); 
                stmt.setInt(2, 1);           
                stmt.setString(3, dbParamName);
                stmt.setInt(4, 500);         
                stmt.setInt(5, newValue);
                
                stmt.executeUpdate();
                System.out.println("🚀 Success! Data inserted directly from the text field into MySQL!");
                
                stmt.close();
                mainStage.setScene(mainManagersScene); 
                
            } catch (NumberFormatException ex) {
                System.out.println("⚠️ Value must be a valid integer!");
            } catch (Exception ex) {
                System.out.println("❌ SQL Error while inserting data from screen.");
                ex.printStackTrace();
            }
        });

        actionButtons.getChildren().addAll(submitBtn, backBtn);
        vbox.getChildren().addAll(title, grid, actionButtons);

        Scene parkManagerScene = new Scene(vbox, 550, 250);
        mainStage.setScene(parkManagerScene);
    }

    // Displays the pending requests queue allowing the department manager to approve or reject them
    private void switchToDeptManagerApprovalScreen() {
        VBox requestsVBox = new VBox(10);
        requestsVBox.setPadding(new Insets(20));
        requestsVBox.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Manage Pending Requests Approval");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<ListCellData> requestsList = new ListView<>();
        requestsList.setPrefHeight(200);
        requestsList.setPrefWidth(500);

        requestsList.setCellFactory(lv -> new ListCell<ListCellData>() {
            @Override
            protected void updateItem(ListCellData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.displayText);
                    if ("Approved".equals(item.status)) {
                        setStyle("-fx-background-color: #c8e6c9; -fx-text-fill: #256029; -fx-font-weight: bold;"); 
                    } else if ("Rejected".equals(item.status)) {
                        setStyle("-fx-background-color: #ffcdd2; -fx-text-fill: #c63737; -fx-font-weight: bold;"); 
                    } else {
                        setStyle(""); 
                    }
                }
            }
        });

        try {
            String selectSql = "SELECT request_id, park_name, parameter_name, requested_value FROM parameter_requests WHERE status = 'Pending';";
            PreparedStatement selectStmt = dbConnection.prepareStatement(selectSql);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("request_id");
                String park = rs.getString("park_name");
                String param = rs.getString("parameter_name");
                int reqValue = rs.getInt("requested_value");

                String readableText = String.format("ID: %d | Park: %s | Parameter: %s -> New Value: %d", id, park, param, reqValue);
                requestsList.getItems().add(new ListCellData(id, park, param, reqValue, readableText));
            }
            rs.close();
            selectStmt.close();
        } catch (Exception ex) {
            System.out.println("❌ Failed to load pending requests from database.");
            ex.printStackTrace();
        }

        HBox actionButtons = new HBox(10);
        Button approveBtn = new Button("Approve Request");
        approveBtn.setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
        
        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: #ffcccc; -fx-font-weight: bold;");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

        approveBtn.setOnAction(e -> {
            ListCellData selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.status)) return;

            try {
                dbConnection.setAutoCommit(false); 

                String updateReqSql = "UPDATE parameter_requests SET status = 'Approved' WHERE request_id = ?;";
                PreparedStatement updateReqStmt = dbConnection.prepareStatement(updateReqSql);
                updateReqStmt.setInt(1, selectedItem.requestId);
                updateReqStmt.executeUpdate();
                updateReqStmt.close();

                String updateParkSql = "UPDATE parks SET " + selectedItem.parameterName + " = ? WHERE park_name = ?;";
                PreparedStatement updateParkStmt = dbConnection.prepareStatement(updateParkSql);
                updateParkStmt.setInt(1, selectedItem.requestedValue);
                updateParkStmt.setString(2, selectedItem.parkName);
                updateParkStmt.executeUpdate();
                updateParkStmt.close();

                dbConnection.commit(); 
                dbConnection.setAutoCommit(true);

                selectedItem.status = "Approved";
                selectedItem.displayText = "[APPROVED] " + selectedItem.displayText;
                requestsList.refresh(); 
                System.out.println("✅ Request ID " + selectedItem.requestId + " has been Approved and Park data updated!");

            } catch (Exception ex) {
                try { dbConnection.rollback(); } catch (Exception se) {}
                System.out.println("❌ SQL Error during approval process.");
                ex.printStackTrace();
            }
        });

        rejectBtn.setOnAction(e -> {
            ListCellData selectedItem = requestsList.getSelectionModel().getSelectedItem();
            if (selectedItem == null || !"Pending".equals(selectedItem.status)) return;

            try {
                String updateReqSql = "UPDATE parameter_requests SET status = 'Rejected' WHERE request_id = ?;";
                PreparedStatement updateReqStmt = dbConnection.prepareStatement(updateReqSql);
                updateReqStmt.setInt(1, selectedItem.requestId);
                updateReqStmt.executeUpdate();
                updateReqStmt.close();

                selectedItem.status = "Rejected";
                selectedItem.displayText = "[REJECTED] " + selectedItem.displayText;
                requestsList.refresh(); 
                System.out.println("❌ Request ID " + selectedItem.requestId + " has been Rejected.");

            } catch (Exception ex) {
                System.out.println("❌ SQL Error during rejection process.");
                ex.printStackTrace();
            }
        });

        actionButtons.getChildren().addAll(approveBtn, rejectBtn, backBtn);
        requestsVBox.getChildren().addAll(title, new Label("Pending Parameter Requests (From DB):"), requestsList, actionButtons);

        Scene deptManagerScene = new Scene(requestsVBox, 550, 400);
        mainStage.setScene(deptManagerScene);
    }

    // Displays the promotions management form and updates the park discount parameter in the database
    private void switchToSharedPromotionsScreen() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

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

                String sql = "UPDATE parks SET additonal_discount = ? WHERE park_name = ?;";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.setDouble(1, dbDiscountValue);
                stmt.setString(2, "Banias"); 

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("🚀 Success! Promotion '" + couponName + "' activated. Updated Banias discount to " + discountPercent + "%!");
                    
                    String newPromoText = String.format("%s | %s%% Off | %s - %s", 
                            couponName, discountStr, startDateField.getText(), endDateField.getText());
                    promoList.getItems().add(0, newPromoText);
                }
                stmt.close();

            } catch (NumberFormatException ex) {
                System.out.println("⚠️ Discount must be a valid number!");
            } catch (Exception ex) {
                System.out.println("❌ SQL Error while saving promotion.");
                ex.printStackTrace();
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

        vbox.getChildren().addAll(title, grid, btnSubmitPromo, new Separator(), listTitle, promoList, backBtn);

        Scene scene = new Scene(vbox, 550, 480);
        mainStage.setScene(scene);
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