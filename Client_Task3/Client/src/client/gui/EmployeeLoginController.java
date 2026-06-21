package client.gui;

import client.logic.CurUser;
import client.logic.LoginLogic;
import client.logic.ScreenSwitch;
import common.Message;
import common.MessageType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Employee Login screen.
 * Handles employee authentication and navigation back to the role selection screen.
 */
public class EmployeeLoginController {
	
	LoginLogic loginLogic;
	
    @FXML
    private Button btnBack;

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;
    @FXML private Label errorMessage;
    
    public void initialize() {loginLogic = new LoginLogic();}
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
        Message response = loginLogic.authenticateUser(username, password);
        if(response.getType() == MessageType.LOGIN_SUCCESS) {
        	System.out.println("Login attempt - Username: " + username + " | Password: " + password);
        	switch(CurUser.getRole()) {
	        	case "Park_manager":{
	        		ScreenSwitch.switchScreen("/client/gui/ManagersScreen.fxml","Manager");
	        		break;
	        	}
	        	case "Dept_manager":{
	        		ScreenSwitch.switchScreen("/client/gui/ManagersScreen.fxml","Manager");
	        		break;
	        	}
	        	case "Entrance_emp":{
	        		ScreenSwitch.switchScreen("/client/gui/EmployeeDashboard.fxml","Employee Dashboard");
	        		break;
	        	}
	        	case "Customer_service":{
	        		ScreenSwitch.switchScreen("/client/gui/ServiceRepScreen.fxml","Service");
	        		break;
	        	}
	        	default:
	        		//ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml","Dashboard");
        	}
        }
        else {
        	errorMessage.setText((String)response.getData());
        	return;
        }
        //System.out.println("Login attempt - Username: " + username + " | Password: " + password);
        //ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml" , "Dashboard");
    }
    
}