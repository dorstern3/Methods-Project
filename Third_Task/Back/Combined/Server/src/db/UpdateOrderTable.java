package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
	 * 
	 * @return true if the order was successfully added, false otherwise.
	 */
	public static boolean saveToWaitingList(Order orderData) {
		try {
			PreparedStatement stmt = DBconnection.getConnection().prepareStatement(
					"INSERT INTO `order` (park_name, order_date, entry_time, number_of_visitors, email, phone_number, id, type_of_visitor, status, date_of_placing_order) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'On waiting list', CURDATE())");

			stmt.setString(1, orderData.getParkName());
			stmt.setString(2, orderData.getOrderDate());
			stmt.setString(3, orderData.getEntryTime());
			stmt.setInt(4, orderData.getNumberOfVisitors());
			stmt.setString(5, orderData.getEmail());
			stmt.setString(6, orderData.getPhoneNumber());

			String visitorType = orderData.getVisitorType();
			stmt.setString(8, visitorType);

			if (visitorType.equals("Subscriber") && orderData.getId() != null && !orderData.getId().isEmpty()) {
				String inputId = orderData.getId();
				int realId = Integer.parseInt(inputId);

				try {
					PreparedStatement idStmt = DBconnection.getConnection()
							.prepareStatement("SELECT id FROM subscriber WHERE id = ? OR sub_number = ?");
					idStmt.setString(1, inputId);
					idStmt.setString(2, inputId);
					java.sql.ResultSet rsId = idStmt.executeQuery();

					if (rsId.next()) {
						realId = rsId.getInt("id");
					}
					rsId.close();
					idStmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

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
	 * Saves a new, confirmed order to the database and generates a unique QR code
	 * for it. * @param orderData The order object.
	 * 
	 * @return The generated QR code string if successful, or null if the operation
	 *         failed.
	 */
	public static String saveNewOrder(Order orderData) {
		try {
			PreparedStatement stmt = DBconnection.getConnection().prepareStatement(
					"INSERT INTO `order` (park_name, order_date, entry_time, number_of_visitors, email, phone_number, id, type_of_visitor, status, date_of_placing_order) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending confirmation', CURDATE())",
					Statement.RETURN_GENERATED_KEYS);

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
				String inputId = orderData.getId();
				realId = Integer.parseInt(inputId);

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
				stmt.setNull(7, Types.INTEGER);
			}

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int generatedOrderNumber = rs.getInt(1);
					String qrCode = "QR-" + generatedOrderNumber;

					PreparedStatement updateStmt = DBconnection.getConnection()
							.prepareStatement("UPDATE `order` SET QR_code = ? WHERE order_number = ?");
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
			System.out.println("Error saving new order: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of a specific order in the database. If the order is
	 * canceled, checks the waiting list for the next eligible traveler. * @param
	 * orderNumber The ID of the order to update.
	 * 
	 * @param status The new status to set (e.g., 'Confirmed', 'Canceled').
	 * @return An ArrayList containing a boolean (success status) and a String
	 *         (waiting list notification, or null).
	 */
	public static ArrayList<Object> updateOrderStatus(int orderNumber, String status) {
		ArrayList<Object> result = new ArrayList<>();
		boolean isUpdated = false;
		String waitingListMsg = null;

		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("UPDATE gonature_db_new.order SET status = ? WHERE order_number = ?");
			ps.setString(1, status);
			ps.setInt(2, orderNumber);

			int rowsAffected = ps.executeUpdate();
			if (rowsAffected > 0) {
				isUpdated = true;

				if (status.equals("Canceled")) {
					waitingListMsg = DBselect.checkWaitingList(orderNumber);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		result.add(isUpdated);
		result.add(waitingListMsg);

		return result;
	}
}