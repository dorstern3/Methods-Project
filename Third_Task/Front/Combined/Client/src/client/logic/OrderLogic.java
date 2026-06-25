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
 * GoNature system. Acts as the Control layer in the ECB architecture, managing
 * communication between the user interface and the server regarding order
 * management.
 */
public class OrderLogic {

	/**
	 * Holds details of an order that is currently pending further action. This is
	 * typically used when an order cannot be immediately confirmed and requires
	 * joining a waiting list or selecting alternative dates.
	 */
	public static Order pendingOrderDetails;

	/**
	 * Default constructor for the OrderLogic class.
	 */
	public OrderLogic() {
	}

	/**
	 * Checks if a specific park has available capacity for a newly requested order.
	 * Communicates with the server to perform the capacity validation. * @param
	 * newOrder The order details containing park name, date, time, and number of
	 * visitors to check.
	 * 
	 * @return true if there is sufficient capacity available for the order, false
	 *         otherwise.
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
	 * Submits a new confirmed order to be saved in the database via the server.
	 * * @param newOrder The order object containing all required details to be
	 * saved.
	 * 
	 * @return A generated String representing the QR code for the order if the
	 *         operation is successful, or null if the saving process fails.
	 */
	public String saveNewOrder(Order newOrder) {
		Message saveMsg = new Message(MessageType.SAVE_NEW_ORDER, newOrder);
		Message saveReply = (Message) ClientUI.clientChat.accept(saveMsg);
		if (saveReply != null && saveReply.getType() == MessageType.SAVE_SUCCESS) {
			return (String) saveReply.getData();
		}
		return null;
	}

	/**
	 * Registers a pending order into the park's waiting list via the server. This
	 * method is called when the park is full at the requested time. * @param order
	 * The pending order object to be added to the waiting list.
	 * 
	 * @return true if the order was successfully added to the waiting list, false
	 *         otherwise.
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
	 * Retrieves the details of a specific order from the server database, ensuring
	 * that the traveler requesting the details is authorized to view them. * @param
	 * orderNumber The unique identifier number of the requested order.
	 * 
	 * @param travelerId The ID of the traveler attempting to fetch the order
	 *                   details.
	 * @return The requested Order object if it is found and the traveler is
	 *         authorized, or null if the order does not exist or authorization
	 *         fails.
	 */
	public Order fetchOrderDetails(int orderNumber, String travelerId) {
		ArrayList<Object> searchParams = new ArrayList<>();
		searchParams.add(orderNumber);
		searchParams.add(travelerId);

		Message msg = new Message(MessageType.FETCH_ORDER_DETAILS, searchParams);
		Message reply = (Message) ClientUI.clientChat.accept(msg);

		if (reply != null && reply.getType() == MessageType.FETCH_ORDER_RESULT) {
			return (Order) reply.getData();
		}
		return null;
	}

	/**
	 * Updates the status of an existing order (e.g., from 'Booked' to 'Canceled' or
	 * 'Confirmed'). Communicates the change to the server to persist the update in
	 * the database. * @param orderNumber The unique identifier of the order to be
	 * updated.
	 * 
	 * @param newStatus The new status string to be applied to the order.
	 * @return An Object array where: - Index 0 is a boolean indicating the success
	 *         of the update operation. - Index 1 is a String containing an optional
	 *         notification message (e.g., if a waiting list slot was triggered due
	 *         to cancellation).
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
	 * Performs a logout operation for the current traveler by notifying the server.
	 * * @param travelerId The ID of the traveler attempting to log out.
	 * 
	 * @return true if the logout request was processed successfully by the server,
	 *         false otherwise.
	 */
	public boolean logoutTraveler(String travelerId) {
		Message msg = new Message(MessageType.TRAVELER_LOGOUT, travelerId);
		Message response = (Message) client.ClientUI.clientChat.accept(msg);
		return response != null && response.getType() == MessageType.LOGOUT_SUCCESS;
	}
	/**
	 * Checks if a traveler has a future active order that can be managed.
	 */
	public boolean checkManageableOrderExists(String travelerId) {
		Message msg = new Message(MessageType.CHECK_ORDER_EXISTENCE, travelerId); // Or a custom type if preferred
		Message response = (Message) ClientUI.clientChat.accept(msg);
		if (response != null && response.getType() == MessageType.CHECK_ORDER_RESPONSE) {
			return (boolean) response.getData();
		}
		return false;
	}

	/**
	 * Checks if a traveler is currently inside the park and can exit.
	 */
	public boolean checkActiveCheckInExists(String travelerId) {
		Message msg = new Message(MessageType.CHECK_ORDER_EXISTENCE, "CHECK_EXIT:" + travelerId); 
		Message response = (Message) ClientUI.clientChat.accept(msg);
		if (response != null && response.getType() == MessageType.CHECK_ORDER_RESPONSE) {
			return (boolean) response.getData();
		}
		return false;
	}

	/**
	 * Sends an order update request directly to the server. * @param order The
	 * updated Order object containing the changes.
	 * 
	 * @return A String representing the server's response or status regarding the
	 *         update.
	 */
	public String sendOrderUpdate(Order order) {
		Object response = ClientUI.clientChat.accept(order);
		return (String) response;
	}

	/**
	 * Requests a complete list of all available orders from the server. * @return
	 * An ArrayList containing all Order objects fetched from the server.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Order> getAllOrders() {
		Object response = ClientUI.clientChat.accept("101");
		return (ArrayList<Order>) response;
	}

	/**
	 * Disconnects the client from the server gracefully, closing the active network
	 * connection.
	 */
	public void disconnect() {
		try {
			ClientUI.clientChat.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
     * Requests the list of active park names dynamically from the server database.
     * @return An ArrayList of park names, or an empty list if the request fails.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> fetchParkNames() {
        Message msg = new Message(MessageType.GET_PARKS, null);
        Message reply = (Message) client.ClientUI.clientChat.accept(msg);
        
        if (reply != null && reply.getType() == MessageType.GET_PARKS_RESPONSE) {
            return (ArrayList<String>) reply.getData();
        }
        return new ArrayList<>(); // Return empty list as a safe fallback on network failure
    }
}