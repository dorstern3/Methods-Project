package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;
import common.Order;
import java.util.ArrayList;

/**
 * Handles the business logic for simulating automated system events. This
 * includes fetching pending reminders and triggering timeouts for the waiting
 * list and pending confirmations by communicating with the server.
 */
public class SimulationLogic {

	/**
	 * Retrieves a list of orders that require a 24-hour visit reminder. Sends a
	 * request to the server to simulate the daily reminder check.
	 * * @return An ArrayList of Order objects that need reminders, or an empty list if none exist.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getPendingReminders() {
		Message response = (Message) ClientUI.clientChat.accept(new Message(MessageType.SIMULATE_24H_REMINDER, null));
		return (response != null) ? (ArrayList<Order>) response.getData() : new ArrayList<>();
	}

	/**
	 * Triggers a simulation to check for waiting list orders that have exceeded
	 * their allowed response time. The server updates statuses and notifies the
	 * next travelers in line.
	 * * @return An ArrayList of String notifications describing the processed waitlist timeouts, or an empty list if there are no updates.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> simulateWaitlistTimeout() {
		Message response = (Message) ClientUI.clientChat.accept(new Message(MessageType.SIMULATE_WAITLIST_TIMEOUT, null));
		return (response != null) ? (ArrayList<String>) response.getData() : new ArrayList<>();
	}

	/**
	 * Triggers a simulation to check for unconfirmed orders that have exceeded
	 * their confirmation window. The server automatically cancels them and manages
	 * availability.
	 * * @return An ArrayList of String notifications describing the processed confirmation timeouts, or an empty list if there are no updates.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> simulateConfirmationTimeout() {
		Message response = (Message) ClientUI.clientChat.accept(new Message(MessageType.SIMULATE_CONFIRMATION_TIMEOUT, null));
		return (response != null) ? (ArrayList<String>) response.getData() : new ArrayList<>();
	}

	/**
	 * Sends a request to the server to clean up today's expired waiting list.
	 * * @return The number of canceled orders, or -1 if an error occurred.
	 */
	public int cleanWaitingListForToday() {
		Message msg = new Message(MessageType.CLEAN_WAITING_LIST, null);
		Message reply = (Message) ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.CLEAN_WAITING_LIST_RESULT) {
			return (int) reply.getData();
		}
		return -1;
	}
}