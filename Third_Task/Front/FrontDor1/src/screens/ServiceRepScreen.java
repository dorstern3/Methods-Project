package screens;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
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
        primaryStage.setTitle("GoNature - נציג שירות");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        Label title = new Label("רישום לקוח חדש (מנוי משפחתי / מדריך קבוצות)");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("מנוי משפחתי", "מדריך קבוצות");
        roleComboBox.setPromptText("בחר סוג רישום");

        grid.add(new Label("סוג רישום:"), 0, 0);
        grid.add(roleComboBox, 1, 0);

        grid.add(new Label("שם פרטי:"), 0, 1);
        grid.add(new TextField(), 1, 1);

        grid.add(new Label("שם משפחה:"), 0, 2);
        grid.add(new TextField(), 1, 2);

        grid.add(new Label("מספר זהות:"), 0, 3);
        grid.add(new TextField(), 1, 3);

        grid.add(new Label("מס' טלפון נייד:"), 0, 4);
        grid.add(new TextField(), 1, 4);

        grid.add(new Label("אימייל:"), 0, 5);
        grid.add(new TextField(), 1, 5);

        grid.add(new Label("מספר בני משפחה (למנוי):"), 0, 6);
        grid.add(new TextField(), 1, 6);

        Button submitBtn = new Button("הירשם למערכת");
        
        vbox.getChildren().addAll(title, grid, submitBtn);

        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}