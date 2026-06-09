package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Handles database selection and verification operations for the GoNature
 * system.
 */
public class DBselect {
//-------------------------לטפל בTRY CATCH----------------------
	/**
	 * Identifies the traveler type (Subscriber, Guide, or Regular) by ID.
	 * 
	 * @param travelerId The ID or subscriber number.
	 * @return The traveler type and relevant info.
	 */
	public static String identifyTravelerInDB(String travelerId) {
		try {
			Connection conn = DBconnection.getConnection();

			PreparedStatement stmtSub = conn
					.prepareStatement("SELECT fname, family_members FROM subscriber WHERE id = ? OR sub_number = ?");
			stmtSub.setString(1, travelerId);
			stmtSub.setString(2, travelerId);
			ResultSet rsSub = stmtSub.executeQuery();

			if (rsSub.next()) {
				String name = rsSub.getString("fname");
				int familyMembers = rsSub.getInt("family_members");
				rsSub.close();
				stmtSub.close();
				return "Subscriber:" + name + ":" + familyMembers;
			}
			rsSub.close();
			stmtSub.close();

			PreparedStatement stmtGuide = conn.prepareStatement("SELECT fname FROM guide WHERE guide_id = ?");
			stmtGuide.setString(1, travelerId);
			ResultSet rsGuide = stmtGuide.executeQuery();

			if (rsGuide.next()) {
				String name = rsGuide.getString("fname");
				rsGuide.close();
				stmtGuide.close();
				return "Guide:" + name;
			}
			rsGuide.close();
			stmtGuide.close();

		} catch (Exception e) {
			System.out.println("Error identifying traveler: " + e.getMessage());
			e.printStackTrace();
			return "Error"; 
		}

		return "Regular Traveler";
	}

	/**
	 * Checks if a park has enough available capacity for a new order.
	 * 
	 * @param orderDetails List containing park name, date, time, and new visitors
	 *                     count.
	 * @return true if there is sufficient capacity, false otherwise.
	 */
	public static boolean checkAvailability(ArrayList<Object> orderDetails) {
		String park = (String) orderDetails.get(0);
		String date = (String) orderDetails.get(1);
		String time = (String) orderDetails.get(2);
		int newVisitors = (int) orderDetails.get(3);

		int maxCapacity = 0;
		int casualGap = 0;
		int estimatedStayingTime = 4;

		try {
			PreparedStatement stmtPark = DBconnection.getConnection().prepareStatement(
					"SELECT max_capacity, casual_gap, estimated_staying_time FROM parks WHERE park_name = ?");
			stmtPark.setString(1, park);
			ResultSet rsPark = stmtPark.executeQuery();

			if (rsPark.next()) {
				maxCapacity = rsPark.getInt("max_capacity");
				casualGap = rsPark.getInt("casual_gap");
				estimatedStayingTime = rsPark.getInt("estimated_staying_time");
			}
			rsPark.close();

			int allowedOrdersCapacity = maxCapacity - casualGap;
			int requestedHour = Integer.parseInt(time.split(":")[0]);

			for (int i = 0; i < estimatedStayingTime; i++) {
				int currentCheckHour = requestedHour + i;

				PreparedStatement stmtOrder = DBconnection.getConnection()
						.prepareStatement("SELECT SUM(number_of_visitors) FROM `order` "
								+ "WHERE park_name = ? AND order_date = ? "
								+ "AND status IN ('Confirmed', 'Pending confirmation', 'Entered') "
								+ "AND HOUR(entry_time) <= ? " + "AND HOUR(entry_time) + ? > ?");

				stmtOrder.setString(1, park);
				stmtOrder.setString(2, date);
				stmtOrder.setInt(3, currentCheckHour);
				stmtOrder.setInt(4, estimatedStayingTime);
				stmtOrder.setInt(5, currentCheckHour);

				ResultSet rsOrder = stmtOrder.executeQuery();
				int existingVisitors = 0;
				if (rsOrder.next()) {
					existingVisitors = rsOrder.getInt(1);
				}
				rsOrder.close();

				if (existingVisitors + newVisitors > allowedOrdersCapacity) {
					return false;
				}
			}

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Generates a list of alternative available dates/times around the requested
	 * date.
	 * 
	 * @param originalOrder List containing the original order details.
	 * @return A list of available alternative date and time slots.
	 */
	public static ArrayList<String> getAlternativeDatesList(ArrayList<Object> originalOrder) {
		ArrayList<String> availableSlots = new ArrayList<>();

		try {
			String dateStr = (String) originalOrder.get(1);
			LocalDate originalDate = LocalDate.parse(dateStr);

			LocalDate[] datesToCheck = { originalDate.minusDays(1), originalDate, originalDate.plusDays(1) };

			String[] timesToCheck = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00" };

			for (LocalDate checkDate : datesToCheck) {

				if (checkDate.isBefore(LocalDate.now())) {
					continue;
				}

				for (String checkTime : timesToCheck) {
					ArrayList<Object> tempOrder = new ArrayList<>(originalOrder);

					tempOrder.set(1, checkDate.toString());
					tempOrder.set(2, checkTime);

					if (checkAvailability(tempOrder)) {
						availableSlots.add(checkDate.toString() + " " + checkTime);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error generating alternative dates: " + e.getMessage());
		}

		return availableSlots;
	}

	/**
	 * Retrieves specific order details from the database.
	 * 
	 * @param orderNumber The unique identifier of the order.
	 * @return A list containing the order details, or null if not found.
	 */
	public static ArrayList<Object> fetchOrderDetails(int orderNumber) {
		ArrayList<Object> orderData = new ArrayList<>();
		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM gonature_db_new.order WHERE order_number = ?");
			ps.setInt(1, orderNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				orderData.add(rs.getInt("order_number"));
				orderData.add(rs.getString("park_name"));
				orderData.add(rs.getDate("order_date").toString());
				orderData.add(rs.getTime("entry_time").toString().substring(0, 5));
				orderData.add(rs.getInt("number_of_visitors"));
				orderData.add(rs.getString("status"));
				return orderData;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks the waiting list for the next eligible order after a cancellation.
	 * Updates the status of the eligible order to 'Pending confirmation' and
	 * generates a notification.
	 * 
	 * @param canceledOrderNum The order number of the canceled order.
	 * @return A notification message for the next traveler, or null if none found.
	 */
	public static String checkWaitingList(int canceledOrderNum) {
		try {
			Connection conn = DBconnection.getConnection();

			PreparedStatement ps1 = conn.prepareStatement(
					"SELECT park_name, order_date, entry_time FROM gonature_db_new.order WHERE order_number = ?");
			ps1.setInt(1, canceledOrderNum);
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {
				String park = rs1.getString("park_name");
				Date date = rs1.getDate("order_date");
				Time time = rs1.getTime("entry_time");

				PreparedStatement ps2 = conn.prepareStatement(
						"SELECT order_number, email, phone_number, number_of_visitors FROM gonature_db_new.order WHERE park_name = ? AND order_date = ? AND entry_time = ? AND status = 'On waiting list' ORDER BY order_number ASC LIMIT 1");
				ps2.setString(1, park);
				ps2.setDate(2, date);
				ps2.setTime(3, time);
				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next()) {
					int nextOrderNum = rs2.getInt("order_number");
					String email = rs2.getString("email");
					String phone = rs2.getString("phone_number");
					int waitingVisitors = rs2.getInt("number_of_visitors");

					ArrayList<Object> orderDetailsForCheck = new ArrayList<>();
					orderDetailsForCheck.add(park);
					orderDetailsForCheck.add(date.toString());
					orderDetailsForCheck.add(time.toString().substring(0, 5));
					orderDetailsForCheck.add(waitingVisitors);

					if (checkAvailability(orderDetailsForCheck)) {

						PreparedStatement psUpdate = conn.prepareStatement(
								"UPDATE gonature_db_new.order SET status = 'Waiting list unconfirmed' WHERE order_number = ?");
						psUpdate.setInt(1, nextOrderNum);
						psUpdate.executeUpdate();

						return "To: " + email + " / " + phone + "\nGood news! Space has freed up in " + park
								+ ".\nPlease confirm your order #" + nextOrderNum + " within 1 hour.";
					} else {
						System.out.println("Server: Space freed, but not enough for the next waiting group ("
								+ waitingVisitors + " visitors).");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves an order and verifies that the provided traveler ID matches the
	 * order's owner ID.
	 * 
	 * @param orderNumber The unique identifier of the order.
	 * @param travelerId  The ID provided by the traveler attempting to access the
	 *                    order.
	 * @return A list of order details if validation passes, or null if it fails.
	 */
	public static ArrayList<Object> fetchOrderWithValidation(int orderNumber, String travelerId) {
		ArrayList<Object> orderDetails = new ArrayList<>();
		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM gonature_db_new.order WHERE order_number = ?");
			ps.setInt(1, orderNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int ownerId = rs.getInt("id");

				if (ownerId != 0 && Integer.parseInt(travelerId) != ownerId) {
					System.out.println("Security alert: Traveler ID " + travelerId
							+ " tried to access order belonging to ID " + ownerId);
					return null;
				}

				orderDetails.add(rs.getInt("order_number"));
				orderDetails.add(rs.getString("park_name"));
				orderDetails.add(rs.getDate("order_date").toString());
				orderDetails.add(rs.getTime("entry_time").toString().substring(0, 5));
				orderDetails.add(rs.getInt("number_of_visitors"));
				orderDetails.add(rs.getString("status"));
				return orderDetails;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}