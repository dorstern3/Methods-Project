package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    public void openOrderScreen(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/Order.fxml", "Order Screen");
    }

    @FXML
    public void openReportsScreen(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/Reports.fxml", "Reports Screen");
    }

    @FXML
    public void openEntranceScreen(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/EntranceForm.fxml", "Entrance Control");
    }

    @FXML
    public void openExitScreenEmployee(ActionEvent event) {
    	ScreenSwitch.switchScreen("/client/gui/ExitForm.fxml", "Park Exit Registration");
    }
    @FXML
    public void openExitScreenVisitor(ActionEvent event) {
        // Navigation to the visitor-specific exit screen
        ScreenSwitch.switchScreen("/client/gui/ExitParkVisitor.fxml", "Exit Park");
    }
}