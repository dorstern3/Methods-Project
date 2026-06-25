package common;

import java.io.Serializable;

/**
 * Data entity class representing a single row in the Cancellation and No-Show Report.
 * Holds aggregated metrics for order cancellations and traveler no-shows, segmented by reporting periods.
 * Implements Serializable to facilitate cross-network transmission between server and client layers.
 */
public class CancellationReportRow implements Serializable {
	private static final long serialVersionUID = 1L;

	String dayOfTheWeek;
	int canceledCount;
	int noShowCount;
	float avgCanceledPerDay; 
	
	/**
	 * Constructs a new CancellationReportRow instance initialized with calculated report metrics.
	 *
	 * @param dayOfTheWeek      The descriptive timeframe label or day name for this row tracking.
	 * @param canceledCount     The cumulative total number of canceled orders.
	 * @param noShowCount       The cumulative total number of unfulfilled no-show orders.
	 * @param avgCanceledPerDay The daily average value computed for order cancellations.
	 */
	public CancellationReportRow(String dayOfTheWeek, int canceledCount, int noShowCount, float avgCanceledPerDay) {
		super();
		this.dayOfTheWeek = dayOfTheWeek;
		this.canceledCount = canceledCount;
		this.noShowCount = noShowCount;
		this.avgCanceledPerDay = avgCanceledPerDay;
	}
	/** @return The period name or day classification string. */
	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	/** @param dayOfTheWeek The period name or day classification string to set. */
	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	/** @return The total count of canceled orders. */
	public int getCanceledCount() {
		return canceledCount;
	}

	/** @param canceledCount The total count of canceled orders to set. */
	public void setCanceledCount(int canceledCount) {
		this.canceledCount = canceledCount;
	}

	/** @return The total count of no-show orders. */
	public int getNoShowCount() {
		return noShowCount;
	}

	/** @param noShowCount The total count of no-show orders to set. */
	public void setNoShowCount(int noShowCount) {
		this.noShowCount = noShowCount;
	}

	/** @return The calculated daily average of canceled orders. */
	public float getAvgCanceledPerDay() {
		return avgCanceledPerDay;
	}

	/** @param avgCanceledPerDay The daily average of canceled orders to set. */
	public void setAvgCanceledPerDay(float avgCanceledPerDay) {
		this.avgCanceledPerDay = avgCanceledPerDay;
	}
	
}
