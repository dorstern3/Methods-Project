package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.Order;

/**
 * Handles database updates and insertions related to orders in the GoNature
 * system, including updating existing orders, saving new orders, and managing
 * the waiting list.
 */
public class UpdateOrderTable {

	/**
	 * Saves a new order request to the waiting list when no capacity is available.
	 * * @param orderData The order object to be saved.
	 * @return true if the order was successfully added, false otherwise.
	 */
	public static boolean saveToWaitingList(Order orderData) {
		try {
			PreparedStatement stmt = DBconnection.getConnection().prepareStatement(
					"INSERT INTO gonature_db_new.order (park_name, order_date, entry_time, number_of_visitors, email, phone_number, id, type_of_visitor, status, date_of_placing_order) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'On waiting list', CURDATE())");

			stmt.setString(1, orderData.getParkName());
			stmt.setString(2, orderData.getOrderDate());
			stmt.setString(3, orderData.getEntryTime());
			stmt.setInt(4, orderData.getNumberOfVisitors());
			stmt.setString(5, orderData.getEmail());
			stmt.setString(6, orderData.getPhoneNumber());

			String visitorType = orderData.getVisitorType();
			stmt.setString(8, visitorType);

			int realId = 0;
			if (orderData.getId() != null && !orderData.getId().isEmpty()) {
				realId = Integer.parseInt(orderData.getId());

				if (visitorType.equals("Subscriber")) {
					try {
						PreparedStatement idStmt = DBconnection.getConnection().prepareStatement(
								"SELECT id FROM gonature_db_new.subscriber WHERE id = ? OR sub_number = ?");
						idStmt.setInt(1, realId);
						idStmt.setInt(2, realId);
						ResultSet rsId = idStmt.executeQuery();

						if (rsId.next()) {
							realId = rsId.getInt("id");
						}
						rsId.close();
						idStmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			if (realId != 0) {
				stmt.setInt(7, realId);
			} else {
				stmt.setNull(7, java.sql.Types.INTEGER);
			}

			int rowsAffected = stmt.executeUpdate();
			stmt.close();

			return rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println("Error saving to waiting list: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves a new, confirmed order to the database and generates a unique QR code.
	 * * @param orderData The order object to be saved.
	 * @return The generated QR code string if successful, or null if failed.
	 */
	public static String saveNewOrder(Order orderData) {
		try {
			PreparedStatement stmt = DBconnection.getConnection().prepareStatement(
					"INSERT INTO gonature_db_new.`order` (park_name, order_date, entry_time, number_of_visitors, email, phone_number, id, type_of_visitor, status, date_of_placing_order) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE())",
					Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, orderData.getParkName());
			stmt.setString(2, orderData.getOrderDate());
			stmt.setString(3, orderData.getEntryTime());
			stmt.setInt(4, orderData.getNumberOfVisitors());
			stmt.setString(5, orderData.getEmail());
			stmt.setString(6, orderData.getPhoneNumber());
			stmt.setString(8, orderData.getVisitorType());
			stmt.setString(9, orderData.getStatus());

			int realId = 0;
			if (orderData.getId() != null && !orderData.getId().isEmpty()) {
				realId = Integer.parseInt(orderData.getId());
				if (orderData.getVisitorType().equals("Subscriber")) {
					try {
						PreparedStatement idStmt = DBconnection.getConnection().prepareStatement(
								"SELECT id FROM gonature_db_new.subscriber WHERE id = ? OR sub_number = ?");
						idStmt.setInt(1, realId);
						idStmt.setInt(2, realId);
						ResultSet rsId = idStmt.executeQuery();
						if (rsId.next())
							realId = rsId.getInt("id");
						rsId.close();
						idStmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (realId != 0)
				stmt.setInt(7, realId);
			else
				stmt.setNull(7, java.sql.Types.INTEGER);

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int generatedOrderNumber = rs.getInt(1);
					String qrCode = "QR-" + generatedOrderNumber;
					PreparedStatement updateStmt = DBconnection.getConnection()
							.prepareStatement("UPDATE gonature_db_new.`order` SET QR_code = ? WHERE order_number = ?");
					updateStmt.setString(1, qrCode);
					updateStmt.setInt(2, generatedOrderNumber);
					updateStmt.executeUpdate();
					updateStmt.close();
					stmt.close();
					return qrCode;
				}
			}
			stmt.close();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of an order. If canceled, checks waiting list for eligible
	 * travelers. If updated to 'Booked', generates and saves a QR code.
	 * * @param orderNumber The order identifier.
	 * @param status      The new status to be set.
	 * @return An ArrayList with a boolean (success) and an optional notification
	 * message.
	 */
	public static ArrayList<Object> updateOrderStatus(int orderNumber, String status) {
		ArrayList<Object> result = new ArrayList<>();
		boolean isUpdated = false;
		String waitingListMsg = null;

		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps;

			if (status.equals("Booked")) {
				ps = conn.prepareStatement(
						"UPDATE gonature_db_new.`order` SET status = ?, QR_code = ? WHERE order_number = ?");
				ps.setString(1, status);
				ps.setString(2, "QR-" + orderNumber);
				ps.setInt(3, orderNumber);
			} else {
				ps = conn.prepareStatement("UPDATE gonature_db_new.`order` SET status = ? WHERE order_number = ?");
				ps.setString(1, status);
				ps.setInt(2, orderNumber);
			}

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				isUpdated = true;
				if (status.equals("Canceled")) {
					waitingListMsg = DBselect.checkWaitingList(orderNumber);
				}
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result.add(isUpdated);
		result.add(waitingListMsg);
		return result;
	}

	/**
	 * Cleans up the waiting list for the current day. Any order scheduled for today
	 * that is still 'On waiting list' will be automatically changed to 'Canceled'.
	 * * @return The number of rows updated, or -1 if an error occurred.
	 */
	public static int cleanWaitingListForToday() {
		try {
			Connection conn = DBconnection.getConnection();
			
			// שינינו ל-gonature_db_new.`order` כדי לשמור על תקינות ועקביות מול שאר הקובץ
			PreparedStatement stmt = conn.prepareStatement(
					"UPDATE gonature_db_new.`order` SET status = 'Canceled' WHERE order_date = CURDATE() AND status = 'On waiting list'"
			);
			
			int rowsUpdated = stmt.executeUpdate();
			stmt.close();
			return rowsUpdated;
			
		} catch (SQLException e) {
			System.out.println("Error cleaning today's waiting list: " + " - " + e.getMessage());
			e.printStackTrace();
			return -1;
		}
	}
}