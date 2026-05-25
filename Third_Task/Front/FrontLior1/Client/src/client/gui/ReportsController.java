package client.gui;

import client.logic.ReportsLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class ReportsController {
	
	private ReportsLogic reportsLogic;
	
	@FXML
	public void initialize() {
		reportsLogic = new ReportsLogic();
	}
	
	
	public void onClickVisitorsReport(){
		reportsLogic.getVisitorsReport();
	}
	
	public void onClickCanclingReport(){
		reportsLogic.getCanclingReport();
	}
	public void onClickPauseReport(){
		reportsLogic.getPauseReport();
	}
	
	public void openDashboard(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
	}
}
