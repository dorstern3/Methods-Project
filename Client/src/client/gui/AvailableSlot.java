package client.gui;

/**
 * Represents a specific time slot availability for a visit.
 */
public class AvailableSlot {
	private String date;
	private String time;

	/**
	 * Constructs a new AvailableSlot.
	 * 
	 * @param date The date of the slot.
	 * @param time The time of the slot.
	 */
	public AvailableSlot(String date, String time) {
		this.date = date;
		this.time = time;
	}

	/**
	 * Gets the date of the slot.
	 * 
	 * @return The date string.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Gets the time of the slot.
	 * 
	 * @return The time string.
	 */
	public String getTime() {
		return time;
	}
}