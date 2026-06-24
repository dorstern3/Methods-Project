package common;

import java.io.Serializable;

public class CancellationReportRow implements Serializable {
	private static final long serialVersionUID = 1L;

	String dayOfTheWeek;
	int canceledCount;
	int noShowCount;
	float avgCanceledPerDay;
	
	public CancellationReportRow(String dayOfTheWeek, int canceledCount, int noShowCount, float avgCanceledPerDay) {
		super();
		this.dayOfTheWeek = dayOfTheWeek;
		this.canceledCount = canceledCount;
		this.noShowCount = noShowCount;
		this.avgCanceledPerDay = avgCanceledPerDay;
	}

	public String getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public int getCanceledCount() {
		return canceledCount;
	}

	public void setCanceledCount(int canceledCount) {
		this.canceledCount = canceledCount;
	}

	public int getNoShowCount() {
		return noShowCount;
	}

	public void setNoShowCount(int noShowCount) {
		this.noShowCount = noShowCount;
	}

	public float getAvgCanceledPerDay() {
		return avgCanceledPerDay;
	}

	public void setAvgCanceledPerDay(float avgCanceledPerDay) {
		this.avgCanceledPerDay = avgCanceledPerDay;
	}
	
}
