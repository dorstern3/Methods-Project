package common;

import java.io.Serializable;

public class OccupancyReportRow implements Serializable{
	   
	   private static final long serialVersionUID = 1L;
	   private String visitDate;
	   private int totalDailyVisitors;
	   private float capacityPercentage;
	   
	   public OccupancyReportRow(String visitDate, int totalDailyVisitors, float capacityPercentage) {
			this.visitDate = visitDate;
			this.totalDailyVisitors = totalDailyVisitors;
			this.capacityPercentage = capacityPercentage;
	   }
	   
	   public String getVisitDate() {
		   return visitDate;
	   }
	   public void setVisitDate(String visitDate) {
		   this.visitDate = visitDate;
	   }
	   public int getTotalDailyVisitors() {
		   return totalDailyVisitors;
	   }
	   public void setTotalDailyVisitors(int totalDailyVisitors) {
		   this.totalDailyVisitors = totalDailyVisitors;
	   }
	   public float getCapacityPercentage() {
		   return capacityPercentage;
	   }
	   public void setCapacityPercentage(float capacityPercentage) {
		   this.capacityPercentage = capacityPercentage;
	   }
}
