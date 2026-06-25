package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import common.Order;

/**
 * Handles simulation data access logic for the GoNature system, including
 * reminder management and timeout processing. This class interacts directly
 * with the database to simulate time-based business scenarios such as automated
 * reminders and order cancellations.
 */
public class DBSimulation {

	/**
	 * Fetches all orders scheduled for tomorrow that are currently in 'Booked'
	 * state, and updates their status to 'Pending confirmation' as part of the
	 * reminder simulation. * @return An ArrayList of Order objects that require
	 * reminders for tomorrow.
	 */
	public static ArrayList<Order> getPendingRemindersForTomorrow() {
		ArrayList<Order> ordersToRemind = new ArrayList<>();

		String selectQuery = "SELECT * FROM gonature_db_new.`order` "
				+ "WHERE order_date = DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND status = 'Booked'";

		Connection conn = null;
		try {
			conn = DBconnection.getConnection();
			try (PreparedStatement psSelect = conn.prepareStatement(selectQuery);
					ResultSet rs = psSelect.executeQuery()) {
				while (rs.next()) {
					ordersToRemind.add(mapResultSetToOrder(rs));
				}
			}

			if (!ordersToRemind.isEmpty()) {
				String updateQuery = "UPDATE gonature_db_new.`order` SET status = 'Pending confirmation' "
						+ "WHERE order_number = ?";
				try (PreparedStatement psUpdate = conn.prepareStatement(updateQuery)) {
					for (Order order : ordersToRemind) {
						psUpdate.setInt(1, order.getOrderNumber());
						psUpdate.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (conn != null) {
				db.DBconnection.release(conn);
			}
		}
		return ordersToRemind;
	}

	/**
	 * Processes unconfirmed waitlist slots that reached their 1-hour timeout.
	 * Automatically cancels them and promotes the next eligible traveler in line
	 * from the waiting list. * @return An ArrayList of log strings representing
	 * simulated notification alerts sent to travelers.
	 */
	public static ArrayList<String> handleWaitlistTimeouts() {
		ArrayList<String> logs = new ArrayList<>();
		String findTimeoutsQuery = "SELECT * FROM gonature_db_new.`order` WHERE status = 'Waiting list unconfirmed'";
		String cancelOrderQuery = "UPDATE gonature_db_new.`order` SET status = 'Canceled' WHERE order_number = ?";

		String findNextInWaitlist = "SELECT * FROM gonature_db_new.`order` WHERE status = 'On waiting list' AND park_name = ? AND order_date = ? AND entry_time = ? ORDER BY date_of_placing_order ASC LIMIT 1";
		String promoteNextQuery = "UPDATE gonature_db_new.`order` SET status = 'Waiting list unconfirmed' WHERE order_number = ?";
		Connection conn = null;
		try {
			conn = DBconnection.getConnection();
			
			try (PreparedStatement psFind = conn.prepareStatement(findTimeoutsQuery);
					ResultSet rs = psFind.executeQuery()) {

			while (rs.next()) {
				int orderNum = rs.getInt("order_number");
				String park = rs.getString("park_name");
				String date = rs.getDate("order_date").toString();
				String time = rs.getTime("entry_time").toString();
				String email = rs.getString("email");
				String phone = rs.getString("phone_number");

				try (PreparedStatement psCancel = conn.prepareStatement(cancelOrderQuery)) {
					psCancel.setInt(1, orderNum);
					psCancel.executeUpdate();
				}

				logs.add("Title: Simulation: 1-hour timeout reached\n" + "To Email: " + email + " / Phone: " + phone
						+ "\n" + "Message: Dear Traveler, since you did not confirm within 1 hour, your order #"
						+ orderNum + " has been automatically CANCELED.");

				try (PreparedStatement psNext = conn.prepareStatement(findNextInWaitlist)) {
					psNext.setString(1, park);
					psNext.setString(2, date);
					psNext.setString(3, time);
					try (ResultSet rsNext = psNext.executeQuery()) {
						if (rsNext.next()) {
							int nextOrderNum = rsNext.getInt("order_number");
							String nextEmail = rsNext.getString("email");
							String nextPhone = rsNext.getString("phone_number");

							try (PreparedStatement psPromote = conn.prepareStatement(promoteNextQuery)) {
								psPromote.setInt(1, nextOrderNum);
								psPromote.executeUpdate();
							}
							logs.add("Title: Simulation: Waitlist Promotion\n" + "To Email: " + nextEmail + " / Phone: "
									+ nextPhone + "\n" + "Message: Good news! A spot has opened up at " + park
									+ ".\nPlease confirm your order #" + nextOrderNum + " within 1 hour.");
						}
					}
				}
			}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (conn != null) {
				db.DBconnection.release(conn);
			}
		}
		return logs;
	}

	/**
	 * Processes pending order confirmations that reached their 2-hour timeout.
	 * Automatically cancels them and triggers a check on the waiting list for
	 * subsequent candidates. * @return An ArrayList of log strings containing the
	 * simulated cancellation and promotion notifications.
	 */
	public static ArrayList<String> handleConfirmationTimeouts() {
		ArrayList<String> logs = new ArrayList<>();
		ArrayList<String[]> ordersToCancel = new ArrayList<>();

		String findTimeoutsQuery = "SELECT order_number, email, phone_number FROM gonature_db_new.`order` WHERE status = 'Pending confirmation'";
		Connection mainConn = null;
		
		try {
			mainConn = DBconnection.getConnection();
			
			try (PreparedStatement psFind = mainConn.prepareStatement(findTimeoutsQuery);
					ResultSet rs = psFind.executeQuery()) {

				while (rs.next()) {
					ordersToCancel.add(new String[] { String.valueOf(rs.getInt("order_number")), rs.getString("email"),
							rs.getString("phone_number") });
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (mainConn != null) {
				db.DBconnection.release(mainConn);
			}
		}
		for (String[] orderData : ordersToCancel) {
			int orderNum = Integer.parseInt(orderData[0]);
			String email = orderData[1];
			String phone = orderData[2];
			Connection conn = null; 
			try {
				conn = DBconnection.getConnection(); 
				
				try (PreparedStatement psCancel = conn.prepareStatement(
						"UPDATE gonature_db_new.`order` SET status = 'Canceled' WHERE order_number = ?")) {
							
				psCancel.setInt(1, orderNum);
				psCancel.executeUpdate();

				logs.add("Title: Simulation: 2-hour timeout reached\n" + "To Email: " + email + " / Phone: " + phone
						+ "\n" + "Message: Dear Traveler, since you did not confirm your attendance, your order #"
						+ orderNum + " has been automatically CANCELED.");

				String msg = DBselect.checkWaitingList(orderNum);
				if (msg != null)
					logs.add(msg);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			finally {
				if (conn != null) {
					db.DBconnection.release(conn);
				}
			}
		}
		return logs;
	}

	/**
	 * Maps a single database row from a ResultSet into a valid Common Order Entity.
	 * * @param rs The active ResultSet cursor positioned at a valid row.
	 * 
	 * @return A fully populated Order object.
	 * @throws SQLException If a database access error occurs or extracting columns
	 *                      fails.
	 */
	private static Order mapResultSetToOrder(ResultSet rs) throws SQLException {
		return new Order(rs.getInt("order_number"), rs.getString("park_name"), rs.getDate("order_date").toString(),
				rs.getTime("entry_time").toString().substring(0, 5), rs.getInt("number_of_visitors"),
				String.valueOf(rs.getInt("id")), rs.getString("email"), rs.getString("phone_number"),
				rs.getString("type_of_visitor"), rs.getString("status"), rs.getString("QR_code"),
				rs.getDate("date_of_placing_order") != null ? rs.getDate("date_of_placing_order").toString() : null,
				rs.getTime("exit_time") != null ? rs.getTime("exit_time").toString() : null);
	}
}