package server;

import java.io.*;

import ocsf.server.*;
import db.DBconnection;
import java.sql.*;
import java.util.ArrayList;
import common.Order;
import common.Message;
import common.MessageType;

// Logic from the OCSF folder
public class EchoServer extends AbstractServer {

	private ServerGUI gui;

	public EchoServer(int port, ServerGUI gui) {
		super(port);
		this.gui = gui;
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received from client: " + msg);
		try {
			// 1. מוודאים שקיבלנו את "המעטפה" התקנית שלנו
			if (msg instanceof Message) {
				Message message = (Message) msg;

				// 2. ניתוב הבקשה לפי סוג הפקודה (MessageType)
				switch (message.getMessageType()) {
				case IDENTIFY_TRAVELER:
					// 1. שולפים את תעודת הזהות מתוך המעטפה
					String travelerId = (String) message.getMessageData();
					System.out.println("Server is now looking for traveler ID: " + travelerId);

					// 2. קריאה לפונקציה שכתבנו בדאטה-בייס לקבלת התשובה האמיתית (מנוי/מדריך/רגיל)
					String result = db.DBselect.identifyTravelerInDB(travelerId);

					// 3. אריזת התשובה למעטפה חדשה ושליחתה חזרה ללקוח שממתין במסך
					Message responseMsg = new Message(MessageType.IDENTIFY_TRAVELER_RESPONSE, result);
					try {
						client.sendToClient(responseMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}

					break;

				// בהמשך השותפים שלך יוסיפו כאן case-ים נוספים, למשל:
				// case UPDATE_ORDER:
				// case GET_REPORTS:

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

	// Retrieving a list of orders from the database for a specific subscriber
	private ArrayList<Order> getOrdersBySubscriberId(String subId) {
		ArrayList<Order> ordersList = new ArrayList<>();
		String query = "SELECT * FROM `order` WHERE subscriber_id = ?";

		// Singleton connection: note that the connection remains open and is not closed
		// at the end of the operation
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

	// Update existing booking details (date and number of visitors) in the database
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

	// Action performed as soon as a new customer connects: saving their details and
	// updating the GUI
	@Override
	protected void clientConnected(ConnectionToClient client) {
		String ip = client.getInetAddress().getHostAddress().replace("/", "");
		String hostName = client.getInetAddress().getHostName();

		// Truly unique port extraction for each connection
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

	// Handling client disconnection when it disconnects in an orderly manner
	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		handleDisconnection(client);
	}

	// Handling client disconnection in the event of an error or forced
	// disconnection
	@Override
	protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
		handleDisconnection(client);
	}

	// Printing to the terminal and updating the graphical interface to remove the
	// customer from the list
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

	// Notification as soon as the server starts listening for connections on the
	// specified port
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}
}