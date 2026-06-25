package common;

import java.io.Serializable;

/**
 * Data entity class representing a single row in the Park Occupancy Report.
 * Tracks structural occupancy levels for days when the park did not reach maximum capacity.
 * Implements Serializable to facilitate cross-network transmission between server and client layers.
 */
public class OccupancyReportRow implements Serializable{
	   
	   private static final long serialVersionUID = 1L; 
	   
	   /** The specific calendar date of the tracking record in YYYY-MM-DD format. */
	   private String visitDate;
	   
	   /** The cumulative total number of unique visitors who entered the park on this date. */
	   private int totalDailyVisitors;
	   
	   /** The calculated capacity utilization rate expressed as a percentage relative to max capacity. */
	   private float capacityPercentage;
	   
	   /**
	    * Constructs a new OccupancyReportRow instance initialized with computed tracking metrics.
	    *
	    * @param visitDate          The explicit calendar date string for this record row.
	    * @param totalDailyVisitors The cumulative total headcount of visitors admitted.
	    * @param capacityPercentage The calculated utilization percentage value.
	    */
	   public OccupancyReportRow(String visitDate, int totalDailyVisitors, float capacityPercentage) {
			this.visitDate = visitDate;
			this.totalDailyVisitors = totalDailyVisitors;
			this.capacityPercentage = capacityPercentage;
	   }
	   
	   /** @return The calendar date string. */
	   public String getVisitDate() {
		   return visitDate;
	   }
	   
	   /** @param visitDate The calendar date string to set. */
	   public void setVisitDate(String visitDate) {
		   this.visitDate = visitDate;
	   }
	   
	   /** @return The cumulative daily visitor headcount. */
	   public int getTotalDailyVisitors() {
		   return totalDailyVisitors;
	   }
	   
	   /** @param totalDailyVisitors The cumulative daily visitor headcount to set. */
	   public void setTotalDailyVisitors(int totalDailyVisitors) {
		   this.totalDailyVisitors = totalDailyVisitors;
	   }
	   
	   /** @return The calculated park capacity utilization percentage. */
	   public float getCapacityPercentage() {
		   return capacityPercentage;
	   }
	   
	   /** @param capacityPercentage The calculated park capacity utilization percentage to set. */
	   public void setCapacityPercentage(float capacityPercentage) {
		   this.capacityPercentage = capacityPercentage;
	   }
}