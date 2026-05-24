package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

import application.Main;

public class DashboardController {


    public void openOrderScreen(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/Order.fxml", "Order Screen");
    }

    public void openReportsScreen(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports Screen");
    }
    
    
}
