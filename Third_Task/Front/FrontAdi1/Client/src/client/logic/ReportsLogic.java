package client.logic;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Logic layer class for processing and managing report data on the client side.
 * This class handles communication requests for various business reports and currently
 * serves mock data to populate the user interface during the front-end development phase.
 * * @author Adi
 * @version 1.0
 */
public class ReportsLogic {
	
	/**
	 * Constructs a new ReportsLogic controller instance.
	 */
	public ReportsLogic () {}

	/**
	 * Retrieves data for the Visitor Report (Entry times and average stay duration analysis).
	 * Map contains time slots as keys and an array of averages as values:
	 * Index 0: Average stay duration for individual visitors.
	 * Index 1: Average stay duration for organized groups.
	 * * @return A ordered Map containing time slot strings mapped to their respective double averages.
	 */
	public Map<String ,Double[]> getVisitorsReport() {
		
		Map<String ,Double[]> reportMap = new LinkedHashMap<>();
		
		// Mock data
		reportMap.put("7:00-8:00", new Double[]{0.0,0.0});
		reportMap.put("8:00-9:00", new Double[]{2.0,2.5});
		reportMap.put("9:00-10:00", new Double[]{1.5,4.0});
		reportMap.put("10:00-11:00", new Double[]{3.0,1.5});
		reportMap.put("11:00-12:00", new Double[]{5.0,5.5});
		reportMap.put("12:00-13:00", new Double[]{1.5,4.0});
		reportMap.put("13:00-14:00", new Double[]{3.0,1.5});
		reportMap.put("14:00-15:00", new Double[]{2.0,2.3});
		reportMap.put("15:00-16:00", new Double[]{1.5,1.5});
		reportMap.put("16:00-17:00", new Double[]{2.0,2.6});
		reportMap.put("17:00-18:00", new Double[]{0.0,0.0});
		reportMap.put("18:00-19:00", new Double[]{0.0,0.0});

		
		return reportMap;
	}
	
	/**
	 * Retrieves statistics for the Cancellations and No-Shows report.
	 * Map contains days of the week as keys and an array of order counts as values:
	 * Index 0: Number of orders cancelled by the client.
	 * Index 1: Number of orders that resulted in a No-Show (not realized).
	 * * @return A ordered Map containing day names mapped to their respective double counts.
	 */
	public Map<String, Double[]> getCanclingReport() {
		
		Map<String ,Double[]> reportMap = new LinkedHashMap<>();
		
		// Mock data
		reportMap.put("Sunday", new Double[]{12.0, 5.0});
		reportMap.put("Monday", new Double[]{8.0, 3.0});
		reportMap.put("Tuesday", new Double[]{15.0, 7.0});
		reportMap.put("Wednesday", new Double[]{6.0, 2.0});
		reportMap.put("Thursday", new Double[]{10.0, 4.0});
		reportMap.put("Friday", new Double[]{22.0, 11.0});
		reportMap.put("Saturday", new Double[]{18.0, 9.0});
		
		return reportMap;
	}
	
	
	public void getTotalVisitorsReportData(){
		return;
	}
	
	public void getOccupancyReport() {
		return;
	}
	
	
}
