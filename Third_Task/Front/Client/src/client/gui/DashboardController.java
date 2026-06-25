package client.gui;

import client.logic.CurUser;
import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the central Dashboard screen.
 * Handles employee navigation across the application screens dynamically based on button actions
 * and displays logged-in worker session information.
 */
public class DashboardController {
	
	private static final String BASE_URL = "/client/gui/";
	
	@FXML Label workerInfo;
	
	/**
	 * Initializes the dashboard view by loading and styling the current logged-in 
	 * park employee information into the session label.
	 */
	@FXML
    public void initialize() {
		workerInfo.setText(CurUser.getMyInfo());
		workerInfo.setStyle(
        	    "-fx-background-color: #F8F9FA; " +      
        	    "-fx-border-color: #E0E0E0; " +          
        	    "-fx-border-width: 1px; " +              
        	    "-fx-background-radius: 10px; " +        
        	    "-fx-border-radius: 10px; " +            
        	    "-fx-font-family: 'Segoe UI', sans-serif; " + 
        	    "-fx-font-size: 13px; " +                
        	    "-fx-text-fill: #333333; " +             
        	    "-fx-line-spacing: 5px;"                 
        	);
		workerInfo.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
		workerInfo.setAlignment(javafx.geometry.Pos.TOP_LEFT);
	}

	/**
	 * Dynamically determines the target FXML resource path based on the clicked button's ID
	 * and switches the primary window stage scene.
	 * * @param event the action event triggered by clicking any navigation sidebar button
	 */
	@FXML
    public void handleNavigation(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String title = clickedButton.getText();
        
        String buttonId = clickedButton.getId(); 

        if (buttonId == null || buttonId.isEmpty()) {
            System.out.println("Error - button id most be defined");
            return;
        }
        String fullFxmlPath = BASE_URL + buttonId + ".fxml";
        ScreenSwitch.switchScreen(fullFxmlPath, title);
    }
	
	/**
	 * Handles the logout action sequence for the park employee.
	 */
	@FXML
	public void logoutbtn() {
		client.logic.CurUser.logout();
	}
    
}