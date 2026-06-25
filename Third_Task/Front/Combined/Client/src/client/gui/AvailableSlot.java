package client.gui;

/**
 * Represents a specific time slot availability for a park visit.
 * Used as an entity model to populate JavaFX table rows with alternative options.
 */
public class AvailableSlot {
	private String date;
	private String time;

	/**
	 * Constructs a new AvailableSlot instance with a specific date and time.
	 * * @param date the date string of the available slot
	 * @param time the time string of the available slot
	 */
	public AvailableSlot(String date, String time) {
		this.date = date;
		this.time = time;
	}

	/**
	 * Gets the date of the slot.
	 * * @return the date string representing this slot
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Gets the time of the slot.
	 * * @return the time string representing this slot
	 */
	public String getTime() {
		return time;
	}
}