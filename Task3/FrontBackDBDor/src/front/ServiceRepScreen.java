package front;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

public class ServiceRepScreen extends Application {

    private Connection dbConnection;

    private TextField famFname, famLname, famId, famPhone, famEmail, famMembers;
    private TextField sFname, sLname, sId, sPhone, sEmail;
    private TextField gFname, gLname, gId, gPhone, gEmail;

    // Launches the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }

    // Initializes the user interface panel and establishes database connection
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GoNature - Service Representative");

        try {
            String url = "jdbc:mysql://localhost:3306/gonature_db_new?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "Aa123456"; 
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.dbConnection = DriverManager.getConnection(url, username, password);
            System.out.println("📬 ServiceRepScreen connected to MySQL successfully!");
        } catch (Exception e) {
            System.out.println("❌ Database connection failed in Service Representative Screen!");
            e.printStackTrace();
        }

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

        // ---------------------------------------------------------------------
        // Tab 1: Family Subscription
        // ---------------------------------------------------------------------
        VBox familyVBox = new VBox(10);
        familyVBox.setPadding(new Insets(10));
        GridPane familyGrid = new GridPane();
        familyGrid.setVgap(10);
        familyGrid.setHgap(10);

        famFname = new TextField();
        famLname = new TextField();
        famId = new TextField();
        famPhone = new TextField();
        famEmail = new TextField();
        famMembers = new TextField();

        familyGrid.add(new Label("First Name:"), 0, 0);
        familyGrid.add(famFname, 1, 0);
        familyGrid.add(new Label("Last Name:"), 0, 1);
        familyGrid.add(famLname, 1, 1);
        familyGrid.add(new Label("ID Number:"), 0, 2);
        familyGrid.add(famId, 1, 2);
        familyGrid.add(new Label("Mobile Number:"), 0, 3);
        familyGrid.add(famPhone, 1, 3);
        familyGrid.add(new Label("Email:"), 0, 4);
        familyGrid.add(famEmail, 1, 4);
        familyGrid.add(new Label("Family Members Amount:"), 0, 5);
        familyGrid.add(famMembers, 1, 5);

        Button familySubmitBtn = new Button("Register to System");
        
        familySubmitBtn.setOnAction(e -> {
            if(famId.getText().isEmpty() || famFname.getText().isEmpty() || famMembers.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
                return;
            }
            try {
                String sql = "INSERT INTO subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                
                int generatedSubNum = new Random().nextInt(9000) + 1000;
                
                stmt.setInt(1, Integer.parseInt(famId.getText()));
                stmt.setString(2, famFname.getText());
                stmt.setString(3, famLname.getText());
                stmt.setString(4, famEmail.getText());
                stmt.setString(5, famPhone.getText());
                stmt.setNull(6, java.sql.Types.VARCHAR); 
                stmt.setInt(7, Integer.parseInt(famMembers.getText()));
                stmt.setInt(8, generatedSubNum); 

                stmt.executeUpdate();
                stmt.close();
                
                System.out.println("🚀 Success! Family Subscription registered into 'subscriber' table!");
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", 
                        "Family Subscription Registered Successfully!\nSubscriber Number: " + generatedSubNum);
                
                famFname.clear(); famLname.clear(); famId.clear(); famPhone.clear(); famEmail.clear(); famMembers.clear();
                
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
                ex.printStackTrace();
            }
        });

        familyVBox.getChildren().addAll(familyGrid, familySubmitBtn);
        familyTab.setContent(familyVBox);

        // ---------------------------------------------------------------------
        // Tab 2: Single Subscription
        // ---------------------------------------------------------------------
        VBox singleVBox = new VBox(10);
        singleVBox.setPadding(new Insets(10));
        GridPane singleGrid = new GridPane();
        singleGrid.setVgap(10);
        singleGrid.setHgap(10);

        sFname = new TextField();
        sLname = new TextField();
        sId = new TextField();
        sPhone = new TextField();
        sEmail = new TextField();

        singleGrid.add(new Label("First Name:"), 0, 0);
        singleGrid.add(sFname, 1, 0);
        singleGrid.add(new Label("Last Name:"), 0, 1);
        singleGrid.add(sLname, 1, 1);
        singleGrid.add(new Label("ID Number:"), 0, 2);
        singleGrid.add(sId, 1, 2);
        singleGrid.add(new Label("Mobile Number:"), 0, 3);
        singleGrid.add(sPhone, 1, 3);
        singleGrid.add(new Label("Email:"), 0, 4);
        singleGrid.add(sEmail, 1, 4);

        Button singleSubmitBtn = new Button("Register to System");
        
        singleSubmitBtn.setOnAction(e -> {
            if(sId.getText().isEmpty() || sFname.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
                return;
            }
            try {
                String sql = "INSERT INTO subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                
                int generatedSubNum = new Random().nextInt(9000) + 1000;
                
                stmt.setInt(1, Integer.parseInt(sId.getText()));
                stmt.setString(2, sFname.getText());
                stmt.setString(3, sLname.getText());
                stmt.setString(4, sEmail.getText());
                stmt.setString(5, sPhone.getText());
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setInt(7, 1); 
                stmt.setInt(8, generatedSubNum);

                stmt.executeUpdate();
                stmt.close();
                
                System.out.println("🚀 Success! Single Subscription registered into 'subscriber' table!");
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", 
                        "Single Subscription Registered Successfully!\nSubscriber Number: " + generatedSubNum);
                
                sFname.clear(); sLname.clear(); sId.clear(); sPhone.clear(); sEmail.clear();
                
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
                ex.printStackTrace();
            }
        });

        singleVBox.getChildren().addAll(singleGrid, singleSubmitBtn);
        singleTab.setContent(singleVBox);

        // ---------------------------------------------------------------------
        // Tab 3: Group Guide
        // ---------------------------------------------------------------------
        VBox groupVBox = new VBox(10);
        groupVBox.setPadding(new Insets(10));
        GridPane groupGrid = new GridPane();
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        gFname = new TextField();
        gLname = new TextField();
        gId = new TextField(); 
        gPhone = new TextField();
        gEmail = new TextField();

        groupGrid.add(new Label("First Name:"), 0, 0);
        groupGrid.add(gFname, 1, 0);
        groupGrid.add(new Label("Last Name:"), 0, 1);
        groupGrid.add(gLname, 1, 1);
        groupGrid.add(new Label("ID Number:"), 0, 2);
        groupGrid.add(gId, 1, 2);
        groupGrid.add(new Label("Mobile Number:"), 0, 3);
        groupGrid.add(gPhone, 1, 3);
        groupGrid.add(new Label("Email:"), 0, 4);
        groupGrid.add(gEmail, 1, 4);

        Button groupSubmitBtn = new Button("Register to System");
        
        groupSubmitBtn.setOnAction(e -> {
            if(gFname.getText().isEmpty() || gEmail.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required fields!");
                return;
            }
            try {
                String sql = "INSERT INTO guide (fname, lname, email, phone_number) VALUES (?, ?, ?, ?);";
                PreparedStatement stmt = dbConnection.prepareStatement(sql);
                stmt.setString(1, gFname.getText());
                stmt.setString(2, gLname.getText());
                stmt.setString(3, gEmail.getText());
                stmt.setString(4, gPhone.getText());

                stmt.executeUpdate();
                stmt.close();
                
                System.out.println("🚀 Success! Group Guide registered into 'guide' table!");
                showAlert(Alert.AlertType.INFORMATION, "Registration Success", "Group Guide Registered Successfully!");
                
                gFname.clear(); gLname.clear(); gId.clear(); gPhone.clear(); gEmail.clear();
                
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
                ex.printStackTrace();
            }
        });

        groupVBox.getChildren().addAll(groupGrid, groupSubmitBtn);
        groupTab.setContent(groupVBox);

        tabPane.getTabs().addAll(familyTab, singleTab, groupTab);
        vbox.getChildren().addAll(title, tabPane);

        Scene scene = new Scene(vbox, 500, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Displays structural popup notifications on the screen for success or error feedback
    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}