package client.gui;

import client.logic.ScreenSwitch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {
	
	private static final String BASE_URL = "/client/gui/";
	
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