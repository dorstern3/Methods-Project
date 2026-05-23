package screens;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ParkManagerScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature - מנהל פארק");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Label title = new Label("הגשת בקשה לשינוי פרמטרי פארק");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        ComboBox<String> paramComboBox = new ComboBox<>();
        paramComboBox.getItems().addAll(
                "מכסת מבקרים מרבית",
                "פער הזמנות למבקרים מזדמנים",
                "זמן שהייה מוערך (ברירת מחדל 4 שעות)"
        );
        paramComboBox.setPromptText("בחר פרמטר לשינוי");

        grid.add(new Label("פרמטר מבוקש:"), 0, 0);
        grid.add(paramComboBox, 1, 0);

        grid.add(new Label("ערך חדש מבוקש:"), 0, 1);
        grid.add(new TextField(), 1, 1);

        Button submitBtn = new Button("שלח בקשה לאישור מנהל המחלקה");

        vbox.getChildren().addAll(title, grid, submitBtn);

        Scene scene = new Scene(vbox, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}