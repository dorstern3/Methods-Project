package client.gui;

import client.logic.CurUser;
import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {
	
	private static final String BASE_URL = "/client/gui/";
	
	// Added
	@FXML Label workerInfo;
	
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
	// End
	
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
	
	@FXML
	public void logoutbtn() {
		client.logic.CurUser.logout();
	}
    
}