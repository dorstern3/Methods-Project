package client.logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import client.ClientUI;
import common.CancellationReportRow;
import common.Message;
import common.MessageType;
import common.OccupancyReportRow;
import common.TotalVisitorsReportRow;


/**
 * Logic layer class for processing and managing report data on the client side.
 * This class handles communication requests for various business reports,
 * processes client-side analytics, and communicates with the server.
 */
public class ReportsLogic {
	
	/**
	 * Constructs a new ReportsLogic controller instance.
	 */
	public ReportsLogic () {}

	/**
	 * Retrieves data for the Visitor Report, analyzing entry times and average stay durations.
	 * The returned {@link Map} maintains insertion order (typically chronological by time slot) 
	 * where each key represents a specific hour slot (e.g., "08:00", "09:00"), and the value is a 
	 * {@code Double[]} array containing calculated duration averages in hours:
	 * Index 0: Average stay duration for individual/regular visitors and subscribers.
	 * Index 1: Average stay duration for organized groups.
	 * @param parkName  The name of the park to filter the report data by.
	 * @param startDate The start date of the analysis period (yyyy-MM-dd).
	 * @param endDate   The end date of the analysis period (yyyy-MM-dd).
	 * @return An ordered {@link Map} of time slots mapped to their respective double duration averages.
	 */
	public Map<String ,Double[]> getVisitorsReport(String parkName , String startDate , String endDate) {
		
		String[] params = new String[] { parkName, startDate, endDate};
		Message msg = new Message(MessageType.GET_VISITOR_REPORT , params);
		try {
			Object response = ClientUI.clientChat.accept(msg);
			Message responseMsg = (Message) response;
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_VISITOR_REPORT_RESPONSE) {
				return (Map<String ,Double[]>) responseMsg.getData();
			}
		}catch (Exception e) {
            System.err.println("Error fetching report from server.");
            e.printStackTrace();
            
        }
		return null;
	}
	
	/**
	 * Retrieves data for the Cancellations and No-Shows report for a single specific park.
	 * Aggregates statistics categorized by the days of the week (Sunday-Saturday).
	 * * @param parkName  The name of the specific park.
	 * @param startDate The start date of the report period (yyyy-MM-dd).
	 * @param endDate   The end date of the report period (yyyy-MM-dd).
	 * @return An {@link ArrayList} of {@link CancellationReportRow} items categorized by weekday.
	 */
	public ArrayList<CancellationReportRow> getCancellationReport(String parkName , String startDate , String endDate) {
		
		String[] params = new String[] {parkName, startDate, endDate};
		Message msg = new Message(MessageType.GET_CANCELLATION_REPORT , params);
		try {
			Object response = ClientUI.clientChat.accept(msg);
			Message responseMsg = (Message) response;
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_CANCELLATION_REPORT_RESPONSE) {
				return (ArrayList<CancellationReportRow>) responseMsg.getData();
			}
		}catch (Exception e) {
            System.err.println("Error fetching report from server.");
            e.printStackTrace();
            
        }
		return null;
	}
	
	/**
	 * Retrieves a global, multi-park cancellation report intended for the Department Manager.
	 * Aggregates cancellation data per individual park rather than by day.
	 * * @param startDate The start date of the report period (yyyy-MM-dd).
	 * @param endDate   The end date of the report period (yyyy-MM-dd).
	 * @return An {@link ArrayList} of {@link CancellationReportRow} items categorized by park name.
	 */
	public ArrayList<CancellationReportRow> getParksCancellationReport(String startDate , String endDate){
		String[] params = new String[] {startDate, endDate};
		Message msg = new Message(MessageType.GET_PARKS_CANCELLATION_REPORT , params);
		try {
			Object response = ClientUI.clientChat.accept(msg);
			Message responseMsg = (Message) response;
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_PARKS_CANCELLATION_REPORT_RESPONSE) {
				return (ArrayList<CancellationReportRow>) responseMsg.getData();
			}
		}catch (Exception e) {
            System.err.println("Error fetching report from server.");
            e.printStackTrace();
            
        }
		return null;
	}
	
	/**
	 * Fetches total daily visitor data and computes an aggregate summary row in the client logic layer.
	 * Iterates over the raw data received from the server to sum up total regulars and groups,
	 * injecting a virtual "SUMMARY" row at the end of the list for the UI layer to intercept.
	 * * @param parkName  The name of the park.
	 * @param startDate The start date of the report period (yyyy-MM-dd).
	 * @param endDate   The end date of the report period (yyyy-MM-dd).
	 * @return An {@link ArrayList} containing daily rows plus a final summary statistics row.
	 */
	public ArrayList<TotalVisitorsReportRow> getTotalVisitorsReportData(String parkName , String startDate , String endDate){
		
		String[] params = new String[] { parkName, startDate, endDate};
		
		Message msg = new Message(MessageType.GET_TOTAL_VISITOR_REPORT , params);
		try {
			Message responseMsg = (Message) ClientUI.clientChat.accept(msg);
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_TOTAL_VISITOR_REPORT_RESPONSE) {
				ArrayList<TotalVisitorsReportRow> serverData = (ArrayList<TotalVisitorsReportRow>) responseMsg.getData();
				// Calculate overall sums for the UI dashboard summary 
			    if (serverData != null && !serverData.isEmpty()) {
			        int totalReg = 0;
			        int totalGrp = 0;
			        for (TotalVisitorsReportRow row : serverData) {
			            totalReg += row.getRegularCount();
			            totalGrp += row.getGroupCount();
			        }
			        // Append a virtual summary row to be intercepted and pulled by the controller
			        serverData.add(new TotalVisitorsReportRow("SUMMARY", totalReg, totalGrp));
			    }
			    return serverData;
			}
		}catch (Exception e) {
            System.err.println("Error fetching report from server.");
            e.printStackTrace();
            
        }
		return null;
	}
	
	
	/**
	 * Retrieves the park occupancy report indicating daily visitors and capacities.
	 * Includes entries for dates where the park was entirely empty (0 visitors).
	 * * @param parkName  The name of the park.
	 * @param startDate The start date of the report period (yyyy-MM-dd).
	 * @param endDate   The end date of the report period (yyyy-MM-dd).
	 * @return An {@link ArrayList} of {@link OccupancyReportRow} records.
	 */
	public ArrayList<OccupancyReportRow> getOccupancyReport(String parkName , String startDate , String endDate) {

		String[] params = new String[] { parkName, startDate, endDate};
		Message msg = new Message(MessageType.GET_OCCUPANCY_REPORT , params);
		try {
			Object response = ClientUI.clientChat.accept(msg);
			Message responseMsg = (Message) response;
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_OCCUPANCY_REPORT_RESPONSE) {
				return (ArrayList<OccupancyReportRow>) responseMsg.getData();
			}
		}catch (Exception e) {
            System.err.println("Error fetching report from server.");
            e.printStackTrace();
        }
		return null;
	}
	
	/**
	 * Fetches the list of all registered park names available in the system database.
	 * Used for populating administrative selection components in the UI.
	 * * @return An {@link ArrayList} containing park name strings.
	 */
	public ArrayList<String> getParks(){
		Message msg = new Message(MessageType.GET_PARKS, null);
		try {
			Object response = ClientUI.clientChat.accept(msg);
			Message responseMsg = (Message) response;
			if(responseMsg !=null && responseMsg.getType() == MessageType.GET_PARKS_RESPONSE) {
				return (ArrayList<String>) responseMsg.getData();
			}
		}catch (Exception e) {
            System.err.println("Error fetching Parks from server.");
            e.printStackTrace();
        }
		return null;
	}
	
}
