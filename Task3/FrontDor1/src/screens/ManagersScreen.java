package screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ManagersScreen extends Application {

    private Scene mainManagersScene;
    private Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        primaryStage.setTitle("GoNature - Managers Panel");

        
        // MAIN MANAGERS SCREEN
        VBox rootVBox = new VBox(20);
        rootVBox.setPadding(new Insets(20));
        rootVBox.setAlignment(Pos.TOP_CENTER);

        Label mainTitle = new Label("Managers Screen");
        mainTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab 1: Park Manager 
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

        // Tab 2: Dept Manager 
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

        
        // Park Manager Actions
        btnRequestParams.setOnAction(e -> switchToParkManagerRequestScreen());
        btnPromotions.setOnAction(e -> switchToSharedPromotionsScreen()); 
        
        // Department Manager Actions
        btnManageRequests.setOnAction(e -> switchToDeptManagerApprovalScreen());
        btnDeptPromotions.setOnAction(e -> switchToSharedPromotionsScreen());  

        // Initial Show
        primaryStage.setScene(mainManagersScene);
        primaryStage.show();
    }

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

        grid.add(new Label("Requested Parameter:"), 0, 0);
        grid.add(paramComboBox, 1, 0);

        grid.add(new Label("Requested New Value:"), 0, 1);
        grid.add(new TextField(), 1, 1);

        HBox actionButtons = new HBox(10);
        Button submitBtn = new Button("Send Request for Department Manager Approval");
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

        actionButtons.getChildren().addAll(submitBtn, backBtn);
        vbox.getChildren().addAll(title, grid, actionButtons);

        Scene parkManagerScene = new Scene(vbox, 550, 250);
        mainStage.setScene(parkManagerScene);
    }

   
    private void switchToDeptManagerApprovalScreen() {
        VBox requestsVBox = new VBox(10);
        requestsVBox.setPadding(new Insets(20));
        requestsVBox.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Manage Pending Requests Approval");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<String> requestsList = new ListView<>();
        requestsList.getItems().addAll(
                "Carmel Park - Change max capacity to 500",
                "Yarkon Park - Change stay time duration to 3 hours"
        );
        requestsList.setPrefHeight(150);

        HBox actionButtons = new HBox(10);
        Button approveBtn = new Button("Approve Request");
        approveBtn.setStyle("-fx-background-color: lightgreen;");
        
        Button rejectBtn = new Button("Reject Request");
        rejectBtn.setStyle("-fx-background-color: #ffcccc;");

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

        actionButtons.getChildren().addAll(approveBtn, rejectBtn, backBtn);
        requestsVBox.getChildren().addAll(title, new Label("Park Managers Requests List:"), requestsList, actionButtons);

        Scene deptManagerScene = new Scene(requestsVBox, 550, 400);
        mainStage.setScene(deptManagerScene);
    }

   
    private void switchToSharedPromotionsScreen() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Promotions Management");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Form Section
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);

        grid.add(new Label("Coupon Name:"), 0, 0);
        grid.add(new TextField(), 1, 0);

        grid.add(new Label("Discount Percentage (%):"), 0, 1);
        grid.add(new TextField(), 1, 1);

        grid.add(new Label("Start Date (DD/MM/YYYY):"), 0, 2);
        grid.add(new TextField(), 1, 2);

        grid.add(new Label("End Date (DD/MM/YYYY):"), 0, 3);
        grid.add(new TextField(), 1, 3);

        Button btnSubmitPromo = new Button("Submit Promotion");

        // Coupons List Section
        Label listTitle = new Label("Coupons List:");
        listTitle.setStyle("-fx-font-weight: bold;");
        
        ListView<String> promoList = new ListView<>();
        promoList.getItems().addAll(
                "Winter Sale | 20% Off | 01/12/2026 - 28/02/2027",
                "Weekend Special | 15% Off | 05/06/2026 - 07/06/2026",
                "Holiday Special | 30% Off | 20/09/2026 - 25/09/2026"
        );
        promoList.setPrefHeight(120);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> mainStage.setScene(mainManagersScene));

        vbox.getChildren().addAll(title, grid, btnSubmitPromo, new Separator(), listTitle, promoList, backBtn);

        Scene scene = new Scene(vbox, 550, 480);
        mainStage.setScene(scene);
    }
}