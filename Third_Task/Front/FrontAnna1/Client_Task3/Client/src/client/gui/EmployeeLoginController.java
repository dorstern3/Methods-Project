package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Employee Login screen.
 * Handles employee authentication and navigation back to the role selection screen.
 */
public class EmployeeLoginController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    /**
     * Handles the Back button click.
     * Navigates the user back to the Role Selection screen.
     * 
     * @param event the action event triggered by clicking the Back button
     */
    @FXML
    void clickBack(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
    }

    /**
     * Handles the Login button click.
     * Retrieves the entered username and password for authentication.
     * 
     * @param event the action event triggered by clicking the Login button
     */
    @FXML
    void clickLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        System.out.println("Login attempt - Username: " + username + " | Password: " + password);
    }
}