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
 * Facilitates credentials collection, interaction with authentication logic,
 * and role-based routing for park employees.
 */
public class EmployeeLoginController {
	
	/** Business logic controller handling authentication tasks. */
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
    
    /**
	 * Initializes the controller component and prepares the business logic layer.
	 */
    public void initialize() {
    	loginLogic = new LoginLogic();
    }

    /**
	 * Navigates the user back to the primary Role Selection landing screen.
	 * * @param event the action event triggered by clicking the back button
	 */
    @FXML
    void clickBack(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/RoleSelection.fxml", "Role Selection");
    }

    /**
	 * Extracts credentials input, performs server authentication, and performs
	 * structural screen routing based on the authorized employee role.
	 * * @param event the action event triggered by clicking the login button
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
        	}
        }
        else {
        	errorMessage.setText((String)response.getData());
        	return;
        }
    }
    
}