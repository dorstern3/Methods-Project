package client.gui;

import client.logic.ReportsLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


public class ReportsController {
	
	
	@FXML private AnchorPane reportContainer;
	@FXML private Button btnVisitorsReport;       // Dept Manager
	@FXML private Button btnCancellationsReport;  // Dept Manager
	@FXML private Button btnTotalVisitorsReport;  // Park Manager
	@FXML private Button btnOccupancyReport;      // Park Manager
	
	private ReportsLogic reportsLogic;
	
	@FXML
	public void initialize() {
		reportsLogic = new ReportsLogic();
		
		String userRole = "DepartmentManager"; // Needs to be dynamic 
		
		if("ParkManager".equals(userRole)) {
			// Hide Department Manager buttons completely
			btnVisitorsReport.setVisible(false);
			btnVisitorsReport.setManaged(false);
			btnCancellationsReport.setVisible(false);
			btnCancellationsReport.setManaged(false);
		}
		else if("DepartmentManager".equals(userRole)) {
			// Hide Park Manager buttons completely
			btnTotalVisitorsReport.setVisible(false);
			btnTotalVisitorsReport.setManaged(false);
			btnOccupancyReport.setVisible(false);
			btnOccupancyReport.setManaged(false);
		}
		
	}
	
	// Create a bar chart for the report 
	public void onClickVisitorsReport(){
		// reportsLogic.getVisitorsReport();
		
		reportContainer.getChildren().clear();
		
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Entry Time");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Average Stay Duration (Hours)");
		
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle("Visitor Report: Entry Times & Stay Duration Analysis");
		
		AnchorPane.setTopAnchor(barChart, 0.0);
		AnchorPane.setBottomAnchor(barChart, 0.0);
		AnchorPane.setLeftAnchor(barChart, 0.0);
		AnchorPane.setRightAnchor(barChart, 0.0);
		
		XYChart.Series<String, Number> singlesSeries = new XYChart.Series<>();
		singlesSeries.setName("Individual Visitors");
		
		XYChart.Series<String, Number> groupsSeries = new XYChart.Series<>();
		groupsSeries.setName("Organized Groups");
		
		/*
			// Fetch processed data from logic layer and populate the chart
			for (VisitorReportItem item : reportsLogic.getVisitorsReport()) {
				// X = Time slot, Y = Average duration for singles
				singlesSeries.getData().add(new XYChart.Data<>(item.getTimeSlot(), item.getSinglesAvgStay()));
				
				// X = Time slot, Y = Average duration for groups
				groupsSeries.getData().add(new XYChart.Data<>(item.getGroupsAvgStay()));
			}
			
			// Add both series to the chart (JavaFX renders them side-by-side)
			barChart.getData().addAll(singlesSeries, groupsSeries);
		 */
		
		// Inject the generated chart into the UI container
		reportContainer.getChildren().add(barChart);
	}
	
	public void onClickCanclingReport(){
		reportsLogic.getCanclingReport();
		reportContainer.getChildren().clear();
	}

	public void openDashboard(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
	}
}
