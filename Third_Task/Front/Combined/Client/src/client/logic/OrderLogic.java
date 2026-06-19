package client.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import client.ClientUI;
import common.Message;
import common.MessageType;
import common.Order;

/**
 * Handles the business logic for order operations in the client-side of the
 * GoNature system. Acts as the Control layer in the ECB architecture,
 * communicating with the server.
 */
public class OrderLogic {
	/**
	 * Holds details of an order that is currently pending further action (e.g.,
	 * joining a waiting list).
	 */
	public static Order pendingOrderDetails;

	public OrderLogic() {
	}

	/**
	 * Checks if a park has available capacity for a given order by sending a
	 * request to the server. * @param newOrder The order details to check.
	 * 
	 * @return true if capacity is available, false otherwise.
	 */
	public boolean checkAvailability(Order newOrder) {
		Message msg = new Message(MessageType.CHECK_AVAILABILITY, newOrder);
		Message reply = (Message) ClientUI.clientChat.accept(msg);
		if (reply != null && reply.getType() == MessageType.CHECK_AVAILABILITY_RESULT) {
			return (boolean) reply.getData();
		}
		return false;
	}

	/**
	 * Saves a new order to the database via the server. * @param newOrder The order
	 * to save.
	 * 
	 * @return The generated QR code string if successful, or null on failure.
	 */
	public String saveNewOrder(Order newOrder) {
		Message saveMsg = new Message(MessageType.SAVE_NEW_ORDER, newOrder);
		Message saveReply = (Message) ClientUI.clientChat.accept(saveMsg);
		if (saveReply != null && saveReply.getType() == MessageType.SAVE_SUCCESS) {
			return (String) saveReply.getData(); // מחזיר את ה-QR
		}
		return null;
	}

	/**
	 * Adds an order to the waiting list via the server. * @param order The order to
	 * add to the waiting list.
	 * 
	 * @return true if successfully added, false otherwise.
	 */
	public boolean enterWaitingList(Order order) {
		Message msg = new Message(MessageType.ENTER_WAITING_LIST, order);
		Message reply = (Message) ClientUI.clientChat.accept(msg);
		if (reply != null && reply.getType() == MessageType.ENTER_WAITING_LIST_RESULT) {
			return (boolean) reply.getData();
		}
		return false;
	}

	/**
	 * Fetches details of a specific order from the server. * @param orderNumber The
	 * unique identifier of the order.
	 * 
	 * @param travelerId The ID of the traveler requesting the details.
	 * @return The Order object if found and authorized, null otherwise.
	 */
	public Order fetchOrderDetails(int orderNumber, String travelerId) {
		ArrayList<Object> searchParams = new ArrayList<>();
		searchParams.add(orderNumber);
		searchParams.add(travelerId);

		Message msg = new Message(MessageType.FETCH_ORDER_DETAILS, searchParams);
		Message reply = (Message) ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.FETCH_ORDER_RESULT) {
			return (Order) reply.getData(); // השרת יחזיר אובייקט Order
		}
		return null;
	}

	/**
	 * Updates the status of an existing order via the server. * @param orderNumber
	 * The identifier of the order.
	 * 
	 * @param newStatus The new status to be applied.
	 * @return An object array where index 0 is a boolean (success status) and index
	 *         1 is an optional notification message.
	 */
	public Object[] updateOrderStatus(int orderNumber, String newStatus) {
		ArrayList<Object> updateData = new ArrayList<>();
		updateData.add(orderNumber);
		updateData.add(newStatus);

		Message msg = new Message(MessageType.UPDATE_ORDER_STATUS, updateData);
		Message reply = (Message) ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.UPDATE_ORDER_RESULT) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> resultList = (ArrayList<Object>) reply.getData();
			boolean isUpdated = (boolean) resultList.get(0);
			String waitlistMsg = (String) resultList.get(1);
			return new Object[] { isUpdated, waitlistMsg };
		}
		return new Object[] { false, null };
	}

	/**
	 * Performs logout for the traveler by notifying the server. * @param travelerId
	 * The ID of the traveler to log out.
	 * 
	 * @return true if the logout request was successful.
	 */
	public boolean logoutTraveler(String travelerId) {
		Message msg = new Message(MessageType.TRAVELER_LOGOUT, travelerId);
		Message response = (Message) client.ClientUI.clientChat.accept(msg);
		return response != null && response.getType() == MessageType.LOGOUT_SUCCESS;
	}

	/**
	 * Sends an order update request to the server. * @param order The order to
	 * update.
	 * 
	 * @return A server response message.
	 */
	public String sendOrderUpdate(Order order) {
		Object response = ClientUI.clientChat.accept(order); // Send the order to update to the server
		return (String) response;
	}

	/**
	 * Requests a list of all orders from the server. * @return An ArrayList of all
	 * available orders.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getAllOrders() {
		Object response = ClientUI.clientChat.accept("101"); // Ask the server for all the order of subscriber 123
		return (ArrayList<Order>) response;
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		try {
			ClientUI.clientChat.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
