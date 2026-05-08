package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application {
    private EchoServer server;
    private TableView<ConnectedClient> table = new TableView<>();
    private ObservableList<ConnectedClient> clientsList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature Server Monitor");

        TableColumn<ConnectedClient, String> ipCol = new TableColumn<>("IP Address");
        ipCol.setCellValueFactory(new PropertyValueFactory<>("ip"));
        ipCol.setPrefWidth(120);

        TableColumn<ConnectedClient, String> hostCol = new TableColumn<>("Host Name");
        hostCol.setCellValueFactory(new PropertyValueFactory<>("host"));
        hostCol.setPrefWidth(120);

        TableColumn<ConnectedClient, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.setItems(clientsList);
        table.getColumns().addAll(ipCol, hostCol, statusCol);
        table.setPlaceholder(new Label("No clients connected"));

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(new Label("CONNECTED CLIENTS LIST:"), table);

        Scene scene = new Scene(root, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();

        server = new EchoServer(5555, this);
        try {
            server.listen();
        } catch (Exception e) {
            System.out.println("Error: Could not listen for clients!");
        }
    }

    public void updateClientDetails(String ip, String host, String status, String port) {
        Platform.runLater(() -> {
            String cleanIp = ip.replace("/", "").trim();
            String cleanPort = port.trim();

            // חיפוש הלקוח הספציפי לפי IP ופורט
            ConnectedClient existingClient = clientsList.stream()
                    .filter(c -> c.getIp().equals(cleanIp) && c.getPort().equals(cleanPort))
                    .findFirst()
                    .orElse(null);

            if (status.equals("Connected")) {
                    clientsList.add(new ConnectedClient(cleanIp, host, status, cleanPort));
                
            } else {
                // ניתוק: הסרת הלקוח מהרשימה כדי שייעלם מהטבלה
                if (existingClient != null) {
                    clientsList.remove(existingClient);
                }
            }
            table.refresh();
        });
    }
}