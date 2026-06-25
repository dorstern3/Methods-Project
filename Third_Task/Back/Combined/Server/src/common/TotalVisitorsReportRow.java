package common;

import java.io.Serializable;

/**
 * Represents a single row in the Total Visitors Report.
 * Used to track daily visitor headcounts split by regular/subscribers and groups.
 */
public class TotalVisitorsReportRow implements Serializable{

	private static final long serialVersionUID = 1L;
	private String date;
	private int regularCount;
    private int groupCount;
    
	public TotalVisitorsReportRow(String date, int regularCount, int groupCount) {
		this.date = date;
		this.regularCount = regularCount;
		this.groupCount = groupCount;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getRegularCount() {
		return regularCount;
	}
	public void setRegularCount(int regularCount) {
		this.regularCount = regularCount;
	}
	public int getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}
	
	
	
	
}
