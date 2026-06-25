package client.gui;

import java.util.ArrayList;
import client.logic.ScreenSwitch;
import client.logic.SubUpdateLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SubscriberEditorController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtSubNumber;
    @FXML
    private TextField txtFname;
    @FXML
    private TextField txtLname;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtCreditCard;
    @FXML
    private TextField txtFamilyMembers;
    @FXML
    private Label lblMessage;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSaveChanges;

    public static String currentSubNumber = "";
    private SubUpdateLogic subUpdateLogic;

    @FXML
    public void initialize() {
        subUpdateLogic = new SubUpdateLogic();
        
        if (currentSubNumber != null && !currentSubNumber.isEmpty()) {
            txtSubNumber.setText(currentSubNumber);
            
            // Fetch subscriber information from the database rows
            ArrayList<String> details = subUpdateLogic.getSubscriberDetails(currentSubNumber);
            
            if (details != null && !details.isEmpty()) {
                txtId.setText(details.get(0));
                txtFname.setText(details.get(1));
                txtLname.setText(details.get(2));
                txtEmail.setText(details.get(3));
                txtPhone.setText(details.get(4));
                txtCreditCard.setText(details.get(5));
                txtFamilyMembers.setText(details.get(6)); // Family Members count
            } else {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Error: Could not retrieve profile data from the server.");
            }
        }
        
        // Enforce boundaries: Crucial metadata properties are structural keys (read-only)
        txtId.setEditable(false);
        txtSubNumber.setEditable(false);
        txtFname.setEditable(false);
        txtLname.setEditable(false);
    }

    @FXML
    void onSaveChangesClicked(ActionEvent event) {
        lblMessage.setText("");

        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String cc = txtCreditCard.getText().trim();
        String famStr = txtFamilyMembers.getText().trim();

        // 1. Email structural validation: Must contain '@'
        if (!email.contains("@")) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Error: Email address must contain '@'!");
            return;
        }

        // 2. Phone structural validation: Digits only, max 10 digits long, non-empty
        if (phone.isEmpty() || !phone.matches("\\d{1,10}")) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Error: Phone number must contain digits only (1 to 10 digits maximum).");
            return;
        }

        // 3. Credit Card billing validation: Digits only
        if (!cc.isEmpty() && !cc.matches("\\d+")) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Error: Credit Card must contain digits only.");
            return;
        }
        
        // 4. Family Quota validation: Digits only, bounded precisely between 1 and 15
        if (famStr.isEmpty() || !famStr.matches("\\d+")) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Error: Family members must be a valid numeric headcount.");
            return;
        }

        try {
            int familyCount = Integer.parseInt(famStr);
            if (familyCount < 1 || familyCount > 15) {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Error: Family members headcount must be between 1 and 15 maximum.");
                return;
            }
        } catch (NumberFormatException e) {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Error: Invalid family count structure.");
            return;
        }

        // 5. Package transmission properties to logic channel layers
        boolean isUpdated = subUpdateLogic.updateSubscriberDetails(
            txtSubNumber.getText(), 
            txtFname.getText(), 
            txtLname.getText(), 
            email, 
            phone, 
            cc, 
            famStr
        );

        if (isUpdated) {
            lblMessage.setStyle("-fx-text-fill: #2e7d32;");
            lblMessage.setText("Profile details updated successfully in the system database!");
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("System Error: Failed to commit structural update transactions.");
        }
    }

    @FXML
    void onCancelClicked(ActionEvent event) {
        currentSubNumber = "";
        ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
    }
}