package server;

import java.io.*;

import ocsf.server.*;
import db.DBconnection;
import db.DBselect;

import java.sql.*;
import java.util.ArrayList;
import common.Order;
import common.Message;
import common.MessageType;

/**
 * The main server class for the GoNature system. Extends AbstractServer to
 * handle client connections and process incoming messages.
 */
public class EchoServer extends AbstractServer {

	private ServerGUI gui;

	/**
	 * Constructs the server.
	 * 
	 * @param port The port number to listen on.
	 * @param gui  The server's graphical user interface controller.
	 */
	public EchoServer(int port, ServerGUI gui) {
		super(port);
		this.gui = gui;
	}

	/**
	 * Handles messages received from a client, routing them to the appropriate
	 * database logic based on the MessageType.
	 * 
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received from client: " + msg);
		try {
			if (msg instanceof Message) {
				Message message = (Message) msg;

				switch (message.getMessageType()) {
				case IDENTIFY_TRAVELER:
					String travelerId = (String) message.getMessageData();
					System.out.println("Server is now looking for traveler ID: " + travelerId);

					String result = db.DBselect.identifyTravelerInDB(travelerId);

					Message responseMsg = new Message(MessageType.IDENTIFY_TRAVELER_RESPONSE, result);
					try {
						client.sendToClient(responseMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;
				case CHECK_AVAILABILITY:
					ArrayList<Object> orderDetails = (ArrayList<Object>) message.getMessageData();

					boolean isAvailable = DBselect.checkAvailability(orderDetails);

					Message replyMsg = new Message(MessageType.CHECK_AVAILABILITY_RESULT, isAvailable);
					try {
						client.sendToClient(replyMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case ENTER_WAITING_LIST:
					ArrayList<Object> waitlistData = (ArrayList<Object>) ((common.Message) msg).getMessageData();

					boolean isWaitlistSaved = db.UpdateOrderTable.saveToWaitingList(waitlistData);

					Message waitlistReply = new Message(MessageType.ENTER_WAITING_LIST_RESULT, isWaitlistSaved);
					try {
						client.sendToClient(waitlistReply);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case SAVE_NEW_ORDER:
					ArrayList<Object> orderDataToSave = (ArrayList<Object>) message.getMessageData();

					String generatedQR = db.UpdateOrderTable.saveNewOrder(orderDataToSave);

					if (generatedQR != null) {
						Message successMsg = new Message(MessageType.SAVE_SUCCESS, generatedQR);
						try {
							client.sendToClient(successMsg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							client.sendToClient(new Message(MessageType.ERROR, null));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					break;
				case GET_ALTERNATIVE_DATES:
					try {
						ArrayList<Object> originalOrder = (ArrayList<Object>) message.getMessageData();

						ArrayList<String> altDatesList = db.DBselect.getAlternativeDatesList(originalOrder);

						Message altDatesReply = new Message(MessageType.GET_ALTERNATIVE_DATES_RESULT, altDatesList);
						client.sendToClient(altDatesReply);

					} catch (Exception e) {
						System.out.println("Error processing alternative dates request");
						e.printStackTrace();
					}
					break;
				case FETCH_ORDER_DETAILS: {
					ArrayList<Object> searchParams = (ArrayList<Object>) message.getMessageData();
					int orderNum = (int) searchParams.get(0);
					String travelerIdStr = (String) searchParams.get(1);

					ArrayList<Object> validatedOrderDetails = db.DBselect.fetchOrderWithValidation(orderNum,
							travelerIdStr);

					try {
						client.sendToClient(new Message(MessageType.FETCH_ORDER_RESULT, validatedOrderDetails));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
				case UPDATE_ORDER_STATUS: {
					ArrayList<Object> updateData = (ArrayList<Object>) message.getMessageData();
					int orderNumToUpdate = (int) updateData.get(0);
					String newStatus = (String) updateData.get(1);

					ArrayList<Object> updateResultList = db.UpdateOrderTable.updateOrderStatus(orderNumToUpdate,newStatus);

					try {
						client.sendToClient(new Message(MessageType.UPDATE_ORDER_RESULT, updateResultList));
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}

				default:
					System.out.println("Unknown message type received.");
				}
			} else {
				System.out.println("Error: Message is not of type Message.class");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a list of orders from the database for a specific subscriber.
	 * 
	 * @param subId The subscriber ID.
	 * @return A list of Order objects belonging to the subscriber.
	 */
	private ArrayList<Order> getOrdersBySubscriberId(String subId) {
		ArrayList<Order> ordersList = new ArrayList<>();
		String query = "SELECT * FROM `order` WHERE subscriber_id = ?";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, subId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ordersList.add(new Order(rs.getInt("order_number"), rs.getDate("order_date"),
							rs.getInt("number_of_visitors"), rs.getInt("confirmation_code"), rs.getInt("subscriber_id"),
							rs.getDate("date_of_placing_order")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ordersList;
	}

	/**
	 * Updates existing order details (date and number of visitors) in the database.
	 * 
	 * @param orderNum The order ID to update.
	 * @param newDate  The new date for the order.
	 * @param visitors The updated number of visitors.
	 * @return true if the update was successful, false otherwise.
	 */
	private boolean updateOrderDetails(int orderNum, java.util.Date newDate, int visitors) {
		String query = "UPDATE `order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setDate(1, new java.sql.Date(newDate.getTime()));
			pstmt.setInt(2, visitors);
			pstmt.setInt(3, orderNum);
			return pstmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Handles actions performed when a new client connects, such as saving their
	 * details and updating the Server GUI.
	 * 
	 * @param client The newly connected client.
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		String ip = client.getInetAddress().getHostAddress().replace("/", "");
		String hostName = client.getInetAddress().getHostName();

		String info = client.toString();
		String port = info.substring(info.lastIndexOf(" ") + 1).replaceAll("[^0-9]", "");

		client.setInfo("ip", ip);
		client.setInfo("host", hostName);
		client.setInfo("port", port);

		System.out.println("Client connected! IP: " + ip + " Port: " + port);
		if (gui != null) {
			gui.updateClientDetails(ip, hostName, "Connected", port);
		}
	}

	/**
	 * Handles client disconnection when it disconnects cleanly.
	 * 
	 * @param client The disconnected client.
	 */
	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		handleDisconnection(client);
	}

	/**
	 * Handles client disconnection in the event of an error or forced
	 * disconnection.
	 * 
	 * @param client    The client that threw an exception.
	 * @param exception The exception thrown.
	 */
	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		handleDisconnection(client);
	}

	/**
	 * Helper method to handle updating the GUI and printing logs upon client
	 * disconnection.
	 * 
	 * @param client The disconnected client.
	 */
	private void handleDisconnection(ConnectionToClient client) {
		String ip = (String) client.getInfo("ip");
		String host = (String) client.getInfo("host");
		String port = (String) client.getInfo("port");

		if (ip != null && port != null) {
			System.out.println("Client disconnected! IP: " + ip + " Port: " + port);
			if (gui != null) {
				gui.updateClientDetails(ip, host, "Disconnected", port);
			}
		}
	}

	/**
	 * Notification method invoked when the server starts listening for connections.
	 */
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}
}