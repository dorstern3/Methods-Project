package screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ServiceRepScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature - Service Representative");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_LEFT);

        
        Label title = new Label("Service Representative Panel");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab familyTab = new Tab("Family Subscription");
        Tab singleTab = new Tab("Single Subscription");
        Tab groupTab = new Tab("Group Guide");

        // Tab 1: Family Subscription (With Family Members field)
        VBox familyVBox = new VBox(10);
        familyVBox.setPadding(new Insets(10));
        GridPane familyGrid = new GridPane();
        familyGrid.setVgap(10);
        familyGrid.setHgap(10);

        familyGrid.add(new Label("First Name:"), 0, 0);
        familyGrid.add(new TextField(), 1, 0);
        familyGrid.add(new Label("Last Name:"), 0, 1);
        familyGrid.add(new TextField(), 1, 1);
        familyGrid.add(new Label("ID Number:"), 0, 2);
        familyGrid.add(new TextField(), 1, 2);
        familyGrid.add(new Label("Mobile Number:"), 0, 3);
        familyGrid.add(new TextField(), 1, 3);
        familyGrid.add(new Label("Email:"), 0, 4);
        familyGrid.add(new TextField(), 1, 4);
        familyGrid.add(new Label("Family Members Amount:"), 0, 5);
        familyGrid.add(new TextField(), 1, 5);

        Button familySubmitBtn = new Button("Register to System");
        familyVBox.getChildren().addAll(familyGrid, familySubmitBtn);
        familyTab.setContent(familyVBox);

        // Tab 2: Single Subscription 
        VBox singleVBox = new VBox(10);
        singleVBox.setPadding(new Insets(10));
        GridPane singleGrid = new GridPane();
        singleGrid.setVgap(10);
        singleGrid.setHgap(10);

        singleGrid.add(new Label("First Name:"), 0, 0);
        singleGrid.add(new TextField(), 1, 0);
        singleGrid.add(new Label("Last Name:"), 0, 1);
        singleGrid.add(new TextField(), 1, 1);
        singleGrid.add(new Label("ID Number:"), 0, 2);
        singleGrid.add(new TextField(), 1, 2);
        singleGrid.add(new Label("Mobile Number:"), 0, 3);
        singleGrid.add(new TextField(), 1, 3);
        singleGrid.add(new Label("Email:"), 0, 4);
        singleGrid.add(new TextField(), 1, 4);

        Button singleSubmitBtn = new Button("Register to System");
        singleVBox.getChildren().addAll(singleGrid, singleSubmitBtn);
        singleTab.setContent(singleVBox);

        // Tab 3: Group Guide 
        VBox groupVBox = new VBox(10);
        groupVBox.setPadding(new Insets(10));
        GridPane groupGrid = new GridPane();
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        groupGrid.add(new Label("First Name:"), 0, 0);
        groupGrid.add(new TextField(), 1, 0);
        groupGrid.add(new Label("Last Name:"), 0, 1);
        groupGrid.add(new TextField(), 1, 1);
        groupGrid.add(new Label("ID Number:"), 0, 2);
        groupGrid.add(new TextField(), 1, 2);
        groupGrid.add(new Label("Mobile Number:"), 0, 3);
        groupGrid.add(new TextField(), 1, 3);
        groupGrid.add(new Label("Email:"), 0, 4);
        groupGrid.add(new TextField(), 1, 4);

        Button groupSubmitBtn = new Button("Register to System");
        groupVBox.getChildren().addAll(groupGrid, groupSubmitBtn);
        groupTab.setContent(groupVBox);

        // Add all tabs to TabPane
        tabPane.getTabs().addAll(familyTab, singleTab, groupTab);

        vbox.getChildren().addAll(title, tabPane);

        Scene scene = new Scene(vbox, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}