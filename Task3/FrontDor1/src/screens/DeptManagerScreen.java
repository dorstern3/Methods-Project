package screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DeptManagerScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature - מנהל מחלקה");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Label title = new Label("ניהול בקשות ממתינות לאישור");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ListView<String> requestsList = new ListView<>();
        // נתונים דמה לתצוגה בלבד
        requestsList.getItems().addAll(
                "פארק כרמל - שינוי מכסה מרבית ל-500",
                "פארק ירקון - שינוי זמן שהייה ל-3 שעות"
        );
        requestsList.setPrefHeight(150);

        HBox actionButtons = new HBox(10);
        Button approveBtn = new Button("אשר בקשה ");
        approveBtn.setStyle("-fx-background-color: lightgreen;");
        
        Button rejectBtn = new Button("דחה בקשה ");
        rejectBtn.setStyle("-fx-background-color: #ffcccc;");

        actionButtons.getChildren().addAll(approveBtn, rejectBtn);

        vbox.getChildren().addAll(title, new Label("רשימת בקשות מנהלי פארקים:"), requestsList, actionButtons);

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}