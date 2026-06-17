package client.gui;

import java.util.ArrayList;
import java.util.Map;

import client.logic.CurUser;
import client.logic.ReportsLogic;
import client.logic.ScreenSwitch;
import common.CancellationReportRow;
import common.OccupancyReportRow;
import common.TotalVisitorsReportRow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	@FXML private Label message;
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
		String userRole = CurUser.getRole();
		//String userRole = "DepartmentManager";
		//String userRole = "ParkManager";
		
		if("Park_manager".equals(userRole)) {
			// Hide Department Manager buttons completely
			btnVisitorsReport.setVisible(false);
			btnVisitorsReport.setManaged(false);
			btnCancellationsReport.setVisible(false);
			btnCancellationsReport.setManaged(false);
			parkSelection.setVisible(false);
			parkSelection.setManaged(false);
		}
		else if("Dept_manager".equals(userRole)) {
			
			// Load available parks into selection drop down for the Department Manager
			ArrayList<String> parks = reportsLogic.getParks();
			parkSelection.getItems().addAll(parks);
			parkSelection.getItems().add("All parks");
			
			// Hide Park Manager buttons completely
			btnTotalVisitorsReport.setVisible(false);
			btnTotalVisitorsReport.setManaged(false);
			btnOccupancyReport.setVisible(false);
			btnOccupancyReport.setManaged(false);
		}
		
	}
	
	/**
	 * Event handler for the Visitors Report button click.
	 * Fetches visitor entry times and stay duration data from the logic layer
	 * and populates a dynamic BarChart inside the container.
	 */
	public void onClickVisitorsReport(){
		
		if(!validateDates()) {return;}
		// Ensure a park is selected before generation
		if (btnVisitorsReport.isVisible() && parkSelection.getValue() == null) {
	        System.out.println("Error: Please select a park first!");
	        message.setText("Error: Please select a park first!");
	        return;
	    }
		// Global visitors report is not supported
		if(parkSelection.getValue().equals("All parks")) {
			System.out.println("Error: Can not make all parks visitor report");
			 message.setText("Error: Can not make all parks visitor report");
			return;
		}
		
		String parkName = parkSelection.getValue(); 
	    String start = startDate.getValue().toString();
	    String end = endDate.getValue().toString();
	    
	    
		reportContainer.getChildren().clear();
		BarChart<String,Number> barChart = createChart("Visitor Report: Entry Times & Stay Duration Analysis" ,"Entry Time" , "Average Stay Duration (Hours)");
		
		XYChart.Series<String, Number> singlesSeries = new XYChart.Series<>();
		singlesSeries.setName("Individual Visitors");
		XYChart.Series<String, Number> groupsSeries = new XYChart.Series<>();
		groupsSeries.setName("Organized Groups");

		Map<String,Double[]> data = reportsLogic.getVisitorsReport(parkName , start , end);
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
	 * Dynamically alters the first column based on selection:
	 * Lists data by weekday if a single park is selected, or maps data by park name for a global report.
	 */
	public void onClickCanclingReport(){
		
		if(!validateDates()) {return;}
		if (btnCancellationsReport.isVisible() && parkSelection.getValue() == null) {
	        System.out.println("Error: Please select a park first!");
	        message.setText("Error: Please select a park first!");
	        return;
	    }
		String parkName = parkSelection.getValue(); 
	    String start = startDate.getValue().toString();
	    String end = endDate.getValue().toString();
	    
		reportContainer.getChildren().clear();
		TableView<CancellationReportRow> table = createBaseTable();
		
		TableColumn<CancellationReportRow, String> firstCol;
		ArrayList<CancellationReportRow> data;
		
		// Dynamically toggle column behavior and source based on Manager's scope selection
		if("All parks".equals(parkName)) {
			firstCol = new TableColumn<>("Park name");
			firstCol.setCellValueFactory(new PropertyValueFactory<>("dayOfTheWeek")); // Maps park name to day property placeholder
			data = reportsLogic.getParksCancellationReport(start, end);
		}
		else {
			firstCol = new TableColumn<>("Day");
			firstCol.setCellValueFactory(new PropertyValueFactory<>("dayOfTheWeek"));
			data = reportsLogic.getCancellationReport(parkName , start, end);
		}
	
		TableColumn<CancellationReportRow, Integer> canceledCol = new TableColumn<>("Canceled");
		TableColumn<CancellationReportRow, Integer> noShowCol = new TableColumn<>("No Show");
		TableColumn<CancellationReportRow, Float> avgPerDayCol = new TableColumn<>("Average cancellation");
		
		firstCol.setPrefWidth(200);
		canceledCol.setPrefWidth(200);
		noShowCol.setPrefWidth(200);
		avgPerDayCol.setPrefWidth(200);
		

		canceledCol.setCellValueFactory(new PropertyValueFactory<>("canceledCount"));
		noShowCol.setCellValueFactory(new PropertyValueFactory<>("noShowCount"));
		avgPerDayCol.setCellValueFactory(new PropertyValueFactory<>("avgCanceledPerDay"));
		
	    table.getColumns().addAll(firstCol, canceledCol, noShowCol ,avgPerDayCol);

		if (data != null) {table.getItems().addAll(data);}
		reportContainer.getChildren().addAll(table);
	}
	
	/**
	 * Event handler for the Total Visitors Report button click.
	 * Generates and displays a TableView configured to show the total visitor counts
	 * categorized by date and visitor type.
	 */
	public void onClickTotalVisitorsReport() {
		
		if(!validateDates()) {return;}
		String parkName = CurUser.getParkName();
		//String parkName = "Banias"; 
	    String start = startDate.getValue().toString();
	    String end = endDate.getValue().toString();
	    
		reportContainer.getChildren().clear();
		
		TableView<TotalVisitorsReportRow> table = createBaseTable();
		AnchorPane.setBottomAnchor(table, 60.0); // Make space for the summary label at the bottom
		
		TableColumn<TotalVisitorsReportRow, String> dateCol = new TableColumn<>("Date");
		TableColumn<TotalVisitorsReportRow, String> regularCol = new TableColumn<>("Regular");
		TableColumn<TotalVisitorsReportRow, Integer> groupCol = new TableColumn<>("Group");
		
		dateCol.setPrefWidth(200);
		regularCol.setPrefWidth(200);
		groupCol.setPrefWidth(200);
		
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		regularCol.setCellValueFactory(new PropertyValueFactory<>("regularCount"));
		groupCol.setCellValueFactory(new PropertyValueFactory<>("groupCount"));
	    table.getColumns().addAll(dateCol, regularCol, groupCol);

		ArrayList<TotalVisitorsReportRow> data = reportsLogic.getTotalVisitorsReportData(parkName, start, end);
		int totalRegular = 0;
	    int totalGroups = 0;
		if (data != null && !data.isEmpty()) {
			// Extract final calculated summary item injected by logic layer
			TotalVisitorsReportRow summaryRow = data.get(data.size() - 1);
			// Set summary values to label and delete is from the table
	        if("SUMMARY".equals(summaryRow.getDate())) {
	        	totalRegular = summaryRow.getRegularCount();
	        	totalGroups = summaryRow.getGroupCount();	
	        	data.remove(data.size() - 1); // Delete from data array to hide from table view
	        }
	        
	        table.getItems().addAll(data);
	    }
	    int totalOverall = totalRegular + totalGroups;
	    Label summaryLabel = new Label(
	        String.format("Total for selected period: Regulars: %d | Groups: %d | Total Visitors: %d", 
	        totalRegular, totalGroups, totalOverall)
	    );
	    
	    summaryLabel.setStyle(
	            "-fx-font-weight: bold; " +
	            "-fx-font-size: 14px; " +
	            "-fx-background-color: #f4f4f4; " + 
	            "-fx-border-color: #cccccc; " +
	            "-fx-border-radius: 5px; " +
	            "-fx-background-radius: 5px; " +
	            "-fx-padding: 10px;"
	        );
	    
	    // Configure full width alignment at the bottom layout container anchor points
	    AnchorPane.setBottomAnchor(summaryLabel, 0.0);
	    AnchorPane.setLeftAnchor(summaryLabel, 0.0);
	    AnchorPane.setRightAnchor(summaryLabel, 0.0);
	    
		reportContainer.getChildren().addAll(table, summaryLabel);
		
	}
	
	/**
	 * Event handler for the Occupancy Report button click.
	 * Generates and displays a TableView configured to track park occupancy percentage rates
	 * across different dates and designated time slots.
	 */
	public void onClickOccupancyReport() {
		
		if(!validateDates()) {return;}
		String parkName = CurUser.getParkName();
		//String parkName = "Banias"; 
	    String start = startDate.getValue().toString();
	    String end = endDate.getValue().toString();
	    
		reportContainer.getChildren().clear();
	
		TableView<OccupancyReportRow> table = createBaseTable();
		
		TableColumn<OccupancyReportRow, String> dateCol = new TableColumn<>("Date");
		TableColumn<OccupancyReportRow, Integer> totalCol = new TableColumn<>("Total Daily Visitors");
		TableColumn<OccupancyReportRow, Float> percentageCol = new TableColumn<>("Occupancy %");
		
		dateCol.setPrefWidth(200);
		totalCol.setPrefWidth(200);
		percentageCol.setPrefWidth(200);
		
		dateCol.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalDailyVisitors"));
		percentageCol.setCellValueFactory(new PropertyValueFactory<>("capacityPercentage"));
		
		
		table.getColumns().add(dateCol);
		table.getColumns().add(totalCol);
		table.getColumns().add(percentageCol);
		
		ArrayList<OccupancyReportRow> data = reportsLogic.getOccupancyReport(parkName, start, end);
		if (data != null) {
	        table.getItems().addAll(data);
	    }
		
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
		ScreenSwitch.switchScreen("/client/gui/ManagersScreen.fxml","Manager");
	}
	
	/**
	 * Helper method to validate that the user has selected both start and end dates.
	 * @return true if both dates are selected, false otherwise.
	 */
	private boolean validateDates() {
		message.setText("");
		message.setStyle("-fx-text-fill: black;");
		if(startDate.getValue() == null || endDate.getValue() == null) {
			reportContainer.getChildren().clear();
			System.out.println("Error: Please select both Start Date and End Date!");
			message.setText("Error: Please select both Start Date and End Date!");
			message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			reportContainer.getChildren().add(message);
			return false;
		}
		if(startDate.getValue().isAfter(endDate.getValue())) {
			reportContainer.getChildren().clear();
			System.out.println("Error: Start date cannot be after end date!");
			message.setText("Error: Start date cannot be after end date!");
			message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			reportContainer.getChildren().add(message);
			return false;
		}
		
		return true;
	}
}


