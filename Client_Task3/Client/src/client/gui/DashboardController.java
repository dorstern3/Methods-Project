package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {
	@FXML
	void clickUpdateOrder(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/Order.fxml", "Update Order Details");
	}

	@FXML
	public void openOrderScreen(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
		// ScreenSwitch.switchScreen("/client/gui/WaitListForm.fxml", "Waiting List");
		// ScreenSwitch.switchScreen("/client/gui/AlternativeDatesForm.fxml",
		// "Alternative Dates");
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
	public void openExitScreen(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/ExitForm.fxml", "Park Exit Registration");
	}
}