package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Graphical user interface for monitoring and activating the GoNature server.
 * Displays connected clients and manages database pool initialization.
 */
public class ServerGUI extends Application {
    private EchoServer server;
    private TableView<ConnectedClient> table = new TableView<>();
    private ObservableList<ConnectedClient> clientsList = FXCollections.observableArrayList();
    
    private PasswordField passwordInput = new PasswordField();
    private Button connectBtn = new Button("Connect Server");
    private Label statusLabel = new Label("Status: Server Offline");
    
    /**
     * Main method to launch the JavaFX application.
     * * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);  
    }

    // Defining the columns in the table and connecting them to the corresponding fields in the customer object
    @SuppressWarnings("unchecked")
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
        
        HBox inputLayout = new HBox(10);
        inputLayout.setPadding(new Insets(5, 0, 5, 0));
        passwordInput.setPromptText("DB Password");
        passwordInput.setPrefWidth(150);
        inputLayout.getChildren().addAll(new Label("DB Pass:"), passwordInput, connectBtn);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(inputLayout, statusLabel, new Label("CONNECTED CLIENTS LIST:"), table);

        connectBtn.setOnAction(event -> handleServerActivation());
        
        Scene scene = new Scene(root, 400, 350);
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Server UI is closing. Cleaning up resources...");
            
            db.DBconnection.shutdown();
            
            System.exit(0); 
        });
        primaryStage.show();
    }
    /**
     * Handles the server activation when the connect button is clicked.
     * Validates the password input, initializes the DB pool, and starts the OCSF server.
     */
    private void handleServerActivation() {
        String dbPassword = passwordInput.getText();
       
        if (dbPassword.isEmpty()) {
            statusLabel.setText("Status: Error - DB Password cannot be empty!");
            return;
        }

        try {        
            // 1. Try to initialize the connection pool with the provided password
            db.DBconnection.initializePool(dbPassword);
            
            // 2. Start the OCSF Server
            server = new EchoServer(5555, this);
            server.listen();
            
            // 3. Update GUI state on successful connection
            statusLabel.setText("Status: Server Online & Database Connected.");
            connectBtn.setDisable(true);
            passwordInput.setDisable(true);
        } catch (Exception e) {
            statusLabel.setText("Status: Error - DB connection or Server startup failed!");
            connectBtn.setDisable(false);
            passwordInput.setDisable(false);
        }
    }
    
    /**
     * Updates the connected clients table when a client connects or disconnects.
     * * @param ip     The IP address of the client.
     * @param host   The host name of the client.
     * @param status The connection status (Connected/Disconnected).
     * @param port   The port number used by the client connection.
     */
    // Update function that receives data from the server and updates the table according to a client connection or disconnection
    public void updateClientDetails(String ip, String host, String status, String port) {
        Platform.runLater(() -> {
            String cleanIp = ip.replace("/", "").trim();
            String cleanPort = port.trim();

            // Search for an existing client in the list by a unique combination of IP agnd port to avoid duplicates
            ConnectedClient existingClient = clientsList.stream()
                    .filter(c -> c.getIp().equals(cleanIp) && c.getPort().equals(cleanPort))
                    .findFirst()
                    .orElse(null);

            if (status.equals("Connected")) {
                    clientsList.add(new ConnectedClient(cleanIp, host, status, cleanPort));
                
            } else {
                // Disconnect: Removing the customer from the list so that they disappear from the table
                if (existingClient != null) {
                    clientsList.remove(existingClient);
                }
            }
            table.refresh();
        });
    }
}