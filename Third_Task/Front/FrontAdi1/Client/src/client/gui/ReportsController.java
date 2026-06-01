package client.gui;

import java.util.Map;

import client.logic.ReportsLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

/**
 * Controller class for managing the reports view in the application.
 * Handles the generation and display of dynamic charts and tables based on the user's role
 * (Park Manager or Department Manager).
 * * @author Adi
 * @version 1.0
 */
public class ReportsController {
	
	
	@FXML private AnchorPane reportContainer;
	@FXML private Button btnVisitorsReport;       // Dept Manager
	@FXML private Button btnCancellationsReport;  // Dept Manager
	@FXML private Button btnTotalVisitorsReport;  // Park Manager
	@FXML private Button btnOccupancyReport;      // Park Manager
	@FXML private DatePicker startDate;
	@FXML private DatePicker endDate;
	@FXML private ComboBox<String> parkSelection;
	
	/** The logic layer instance for processing and retrieving report data. */
	private ReportsLogic reportsLogic;
	
	
	/**
	 * Initializes the controller. This method is automatically called after the FXML file has been loaded.
	 * It initializes the logic layer and handles role-based UI access control by dynamically
	 * hiding buttons that the current user is not authorized to view.
	 */
	@FXML
	public void initialize() {
		reportsLogic = new ReportsLogic();
		
		// Needs to be dynamic
		String userRole = "DepartmentManager";
		// String userRole = "ParkManager";
		
		if("ParkManager".equals(userRole)) {
			// Hide Department Manager buttons completely
			btnVisitorsReport.setVisible(false);
			btnVisitorsReport.setManaged(false);
			btnCancellationsReport.setVisible(false);
			btnCancellationsReport.setManaged(false);
			parkSelection.setVisible(false);
			parkSelection.setManaged(false);
		}
		else if("DepartmentManager".equals(userRole)) {
			// Hide Park Manager buttons completely
			btnTotalVisitorsReport.setVisible(false);
			btnTotalVisitorsReport.setManaged(false);
			btnOccupancyReport.setVisible(false);
			btnOccupancyReport.setManaged(false);
			parkSelection.getItems().add("Park Carmel");
		}
		
	}
	
	/**
	 * Event handler for the Visitors Report button click.
	 * Fetches visitor entry times and stay duration data from the logic layer
	 * and populates a dynamic BarChart inside the container.
	 */
	public void onClickVisitorsReport(){
		
		if(!validateDates()) {return;}
		if (btnVisitorsReport.isVisible() && parkSelection.getValue() == null) {
	        System.out.println("Error: Please select a park first!");
	        return;
	    }
		reportContainer.getChildren().clear();
		BarChart<String,Number> barChart = createChart("Visitor Report: Entry Times & Stay Duration Analysis" ,"Entry Time" , "Average Stay Duration (Hours)");
		
		XYChart.Series<String, Number> singlesSeries = new XYChart.Series<>();
		singlesSeries.setName("Individual Visitors");
		XYChart.Series<String, Number> groupsSeries = new XYChart.Series<>();
		groupsSeries.setName("Organized Groups");

		Map<String,Double[]> data = reportsLogic.getVisitorsReport();
		// Fetch processed data from logic layer and populate the chart
		for (String time : data.keySet()) {
			
			Double[] averages = data.get(time);
			// X = Time slot, Y = Average duration for singles
			singlesSeries.getData().add(new XYChart.Data<>(time,averages[0]));
			// X = Time slot, Y = Average duration for groups
			groupsSeries.getData().add(new XYChart.Data<>(time,averages[1]));
		}
			
		// Inject the generated chart into the UI container
		barChart.getData().add(singlesSeries);
		barChart.getData().add(groupsSeries);
		
		reportContainer.getChildren().add(barChart);
	}
	
	/**
	 * Event handler for the Cancellations Report button click.
	 * Fetches weekly cancellation and no-show statistics from the logic layer
	 * and renders them visually in a BarChart.
	 */
	public void onClickCanclingReport(){
		
		if(!validateDates()) {return;}
		if (btnCancellationsReport.isVisible() && parkSelection.getValue() == null) {
	        System.out.println("Error: Please select a park first!");
	        return;
	    }
		reportContainer.getChildren().clear();
		BarChart<String,Number> barChart = createChart("Cancellations & No-Shows Weekly Distribution", "Days of the Week", "Number of Orders");
		
		XYChart.Series<String, Number> cancelledSeries = new XYChart.Series<>();
		cancelledSeries.setName("Cancelled by Client");
		XYChart.Series<String, Number> noShowSeries = new XYChart.Series<>();
		noShowSeries.setName("No-Show (Not Realized)");
		
		Map<String, Double[]> cancellationData = reportsLogic.getCanclingReport();
		for (String day : cancellationData.keySet()) {
			Double[] counts = cancellationData.get(day);
			cancelledSeries.getData().add(new XYChart.Data<>(day, counts[0]));
			noShowSeries.getData().add(new XYChart.Data<>(day, counts[1]));
		}
	
		
		barChart.getData().add(cancelledSeries);
		barChart.getData().add(noShowSeries);
		reportContainer.getChildren().add(barChart);
		
	}
	
	/**
	 * Event handler for the Total Visitors Report button click.
	 * Generates and displays a TableView configured to show the total visitor counts
	 * categorized by date and visitor type.
	 */
	public void onClickTotalVisitorsReport() {
		
		if(!validateDates()) {return;}
		reportContainer.getChildren().clear();
		TableView<String> table = createBaseTable();
		
		TableColumn<String, String> dateCol = new TableColumn<>("Date");
		TableColumn<String, String> typeCol = new TableColumn<>("Visitor Type");
		TableColumn<String, String> countCol = new TableColumn<>("Number of Visitors");
		
		dateCol.setPrefWidth(200);
		typeCol.setPrefWidth(200);
		countCol.setPrefWidth(200);
		
		table.getColumns().add(dateCol);
		table.getColumns().add(typeCol);
		table.getColumns().add(countCol);
		
		reportContainer.getChildren().add(table);
		
	}
	
	/**
	 * Event handler for the Occupancy Report button click.
	 * Generates and displays a TableView configured to track park occupancy percentage rates
	 * across different dates and designated time slots.
	 */
	public void onClickOccupancyReport() {
		
		if(!validateDates()) {return;}
		reportContainer.getChildren().clear();
		
		TableView<String> table = createBaseTable();
		
		TableColumn<String, String> dateCol = new TableColumn<>("Date");
		TableColumn<String, String> timeCol = new TableColumn<>("Time Slot");
		TableColumn<String, String> percentageCol = new TableColumn<>("Occupancy %");
		
		dateCol.setPrefWidth(200);
		timeCol.setPrefWidth(200);
		percentageCol.setPrefWidth(200);
		
		table.getColumns().add(dateCol);
		table.getColumns().add(timeCol);
		table.getColumns().add(percentageCol);
		
		reportContainer.getChildren().add(table);
	}
	
	/**
	 * Helper method to create and style a generic BarChart with full-stretch layout constraints.
	 * * @param title The main title displayed at the top of the chart.
	 * @param nameX The descriptive label for the horizontal category axis.
	 * @param nameY The descriptive label for the vertical numerical axis.
	 * @return A fully configured BarChart instance bound to AnchorPane anchors.
	 */
	private BarChart<String, Number> createChart(String title, String nameX , String nameY) {
		
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel(nameX);
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel(nameY);
		BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		barChart.setTitle(title);
		
		AnchorPane.setTopAnchor(barChart, 0.0);
		AnchorPane.setBottomAnchor(barChart, 0.0);
		AnchorPane.setLeftAnchor(barChart, 0.0);
		AnchorPane.setRightAnchor(barChart, 0.0);
		
		return barChart;
	}
	
	/**
	 * Helper method to instantiate and pre-configure a basic generic TableView.
	 * Applies auto-resize column policies and full-stretch positioning constraints.
	 * * @param <T>   The type of objects to be held and displayed in the table rows.
	 * @return A stylized TableView component ready to receive columns and items.
	 */
	private <T> TableView<T> createBaseTable() {
		
	    TableView<T> table = new TableView<T>();
	    AnchorPane.setTopAnchor(table, 0.0);
	    AnchorPane.setBottomAnchor(table, 0.0);
	    AnchorPane.setLeftAnchor(table, 0.0);
	    AnchorPane.setRightAnchor(table, 0.0);
	    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 
	    
	    return table;
	}
	
	/**
	 * Redirects the user back to the main dashboard screen.
	 * * @param event The ActionEvent triggered by the back/dashboard button click.
	 */
	public void openDashboard(ActionEvent event) {
		ScreenSwitch.switchScreen("/client/gui/Dashboard.fxml", "Dashboard");
	}
	
	/**
	 * Helper method to validate that the user has selected both start and end dates.
	 * @return true if both dates are selected, false otherwise.
	 */
	private boolean validateDates() {
		if(startDate.getValue() == null || endDate.getValue() == null) {
			System.out.println("Error: Please select both Start Date and End Date!");
			return false;
		}
		if(startDate.getValue().isAfter(endDate.getValue())) {
			System.out.println("Error: Start date cannot be after end date!");
			return false;
		}
		return true;
	}
}


