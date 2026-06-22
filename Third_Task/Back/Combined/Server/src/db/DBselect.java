package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

import common.Order;

/**
 * Handles database selection and verification operations for the GoNature
 * system.
 */
public class DBselect {

	/**
	 * Identifies the traveler type (Subscriber, Guide, or Regular) by their ID or
	 * subscriber number.
	 * 
	 * @param travelerId The ID or subscriber number of the traveler.
	 * @return A string representing the traveler type and relevant info, or an
	 *         error message if the format does not match existing records.
	 */
	public static String identifyTravelerInDB(String travelerId) {
		try {
			Connection conn = DBconnection.getConnection();

			if (travelerId.length() == 4) {
				PreparedStatement stmtSub = conn
						.prepareStatement("SELECT fname, family_members FROM subscriber WHERE sub_number = ?");
				stmtSub.setString(1, travelerId);
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

				return "ERROR: No subscriber found with number " + travelerId;
			}

			if (travelerId.length() == 5) {
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

				PreparedStatement stmtSubId = conn
						.prepareStatement("SELECT fname, family_members FROM subscriber WHERE id = ?");
				stmtSubId.setString(1, travelerId);
				ResultSet rsSubId = stmtSubId.executeQuery();

				if (rsSubId.next()) {
					String name = rsSubId.getString("fname");
					int familyMembers = rsSubId.getInt("family_members");
					rsSubId.close();
					stmtSubId.close();
					return "Subscriber:" + name + ":" + familyMembers;
				}
				rsSubId.close();
				stmtSubId.close();
			}

		} catch (Exception e) {
			System.out.println("Error identifying traveler: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return "Regular Traveler";
	}

	/**
	 * Checks the waiting list for the next eligible order after a cancellation.
	 * Updates the status of the eligible order to 'Waiting list unconfirmed' if
	 * availability allows and returns a notification message for the traveler.
	 * * @param canceledOrderNum The order number of the order that was canceled.
	 * 
	 * @return A notification message for the next traveler, or null if no eligible
	 *         order is found.
	 */
	public static String checkWaitingList(int canceledOrderNum) {
		try {
			Connection conn = DBconnection.getConnection();

			PreparedStatement ps1 = conn.prepareStatement(
					"SELECT park_name, order_date FROM gonature_db_new.`order` WHERE order_number = ?");
			ps1.setInt(1, canceledOrderNum);
			ResultSet rs1 = ps1.executeQuery();

			if (rs1.next()) {
				String park = rs1.getString("park_name");
				Date date = rs1.getDate("order_date");

				System.out.println("DEBUG: Order cancelled. Checking waitlist for " + park + " on " + date);

				PreparedStatement ps2 = conn.prepareStatement(
						"SELECT order_number, id, email, phone_number, number_of_visitors, entry_time "
								+ "FROM gonature_db_new.`order` "
								+ "WHERE park_name = ? AND order_date = ? AND status = 'On waiting list' "
								+ "ORDER BY date_of_placing_order ASC, order_number ASC");

				ps2.setString(1, park);
				ps2.setDate(2, date);
				ResultSet rs2 = ps2.executeQuery();

				while (rs2.next()) {
					int nextOrderNum = rs2.getInt("order_number");
					String travelerId = rs2.getString("id");
					String email = rs2.getString("email");
					String phone = rs2.getString("phone_number");
					int waitingVisitors = rs2.getInt("number_of_visitors");
					Time waitTime = rs2.getTime("entry_time");

					System.out.println("DEBUG: Checking candidate Order #" + nextOrderNum + " (needs " + waitingVisitors
							+ " spots at " + waitTime + ")");

					Order orderDetailsForCheck = new Order(park, date.toString(), waitTime.toString().substring(0, 5),
							waitingVisitors, travelerId, email, phone, "Regular", "Booked");

					if (checkAvailability(orderDetailsForCheck)) {
						PreparedStatement psUpdate = conn.prepareStatement(
								"UPDATE gonature_db_new.`order` SET status = 'Waiting list unconfirmed' WHERE order_number = ?");
						psUpdate.setInt(1, nextOrderNum);
						psUpdate.executeUpdate();
						psUpdate.close();

						System.out.println("DEBUG: SUCCESS! Space allocated to Order #" + nextOrderNum);

						rs1.close();
						ps1.close();
						rs2.close();
						ps2.close();

						return "To: " + email + " / " + phone + "\nGood news! Space has freed up in " + park
								+ ".\nPlease confirm your order #" + nextOrderNum + " within 1 hour.";
					} else {
						System.out.println("DEBUG: Still not enough capacity for Order #" + nextOrderNum
								+ ", checking next candidate...");
					}
				}
				rs2.close();
				ps2.close();
			}
			rs1.close();
			ps1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if a park has enough available capacity for a given order request.
	 * * @param orderDetails The order object containing park, date, time, and
	 * number of visitors.
	 * 
	 * @return true if there is sufficient capacity, false otherwise.
	 */
	public static boolean checkAvailability(Order orderDetails) {
		String park = orderDetails.getParkName();
		String date = orderDetails.getOrderDate();
		String time = orderDetails.getEntryTime();
		int newVisitors = orderDetails.getNumberOfVisitors();

		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement stmtPark = conn.prepareStatement(
					"SELECT max_capacity, casual_gap, estimated_staying_time FROM parks WHERE park_name = ?");
			stmtPark.setString(1, park);
			ResultSet rsPark = stmtPark.executeQuery();

			int maxCapacity = 0, casualGap = 0, estimatedStayingTime = 4;
			if (rsPark.next()) {
				maxCapacity = rsPark.getInt("max_capacity");
				casualGap = rsPark.getInt("casual_gap");
				estimatedStayingTime = rsPark.getInt("estimated_staying_time");
			}
			rsPark.close();
			stmtPark.close();

			int allowedOrdersCapacity = maxCapacity - casualGap;
			int requestedHour = Integer.parseInt(time.split(":")[0]);

			for (int i = 0; i < estimatedStayingTime; i++) {
				int currentCheckHour = requestedHour + i;
				PreparedStatement stmtOrder = conn.prepareStatement("SELECT SUM(number_of_visitors) FROM `order` "
						+ "WHERE park_name = ? AND order_date = ? "
						+ "AND status IN ('Booked', 'Confirmed', 'Pending confirmation', 'Entered', 'Waiting list unconfirmed') "
						+ "AND HOUR(entry_time) <= ? " + "AND HOUR(entry_time) + ? > ?");

				stmtOrder.setString(1, park);
				stmtOrder.setString(2, date);
				stmtOrder.setInt(3, currentCheckHour);
				stmtOrder.setInt(4, estimatedStayingTime);
				stmtOrder.setInt(5, currentCheckHour);

				ResultSet rsOrder = stmtOrder.executeQuery();
				int existingVisitors = 0;
				if (rsOrder.next())
					existingVisitors = rsOrder.getInt(1);
				rsOrder.close();
				stmtOrder.close();

				if (existingVisitors + newVisitors > allowedOrdersCapacity) {
					System.out.println("DEBUG: Capacity limit reached at hour " + currentCheckHour + ". Current: "
							+ existingVisitors + ", Need: " + newVisitors);
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
	 * Generates a list of alternative available dates and times for an order that
	 * cannot be booked. * @param originalOrder The original order request.
	 * 
	 * @return An ArrayList of strings representing available alternative slots in
	 *         the format "YYYY-MM-DD HH:MM".
	 */
	public static ArrayList<String> getAlternativeDatesList(Order originalOrder) {
		ArrayList<String> availableSlots = new ArrayList<>();

		try {
			String dateStr = originalOrder.getOrderDate();
			LocalDate originalDate = LocalDate.parse(dateStr);

			LocalDate[] datesToCheck = { originalDate.minusDays(1), originalDate, originalDate.plusDays(1) };
			String[] timesToCheck = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00" };

			LocalDate today = LocalDate.now();

			for (LocalDate checkDate : datesToCheck) {
				if (!checkDate.isAfter(today)) {
					continue;
				}

				for (String checkTime : timesToCheck) {
					Order tempOrder = new Order(originalOrder.getParkName(), checkDate.toString(), checkTime,
							originalOrder.getNumberOfVisitors(), originalOrder.getId(), originalOrder.getEmail(),
							originalOrder.getPhoneNumber(), originalOrder.getVisitorType(), originalOrder.getStatus());

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
	 * Retrieves an order and verifies that the provided traveler ID matches the
	 * order's owner ID. * @param orderNumber The unique identifier of the order.
	 * 
	 * @param travelerId The ID provided by the traveler.
	 * @return The Order object if validation passes, or null if it fails.
	 */
	public static Order fetchOrderWithValidation(int orderNumber, String travelerId) {
		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM gonature_db_new.order WHERE order_number = ? AND id = ?");
			ps.setInt(1, orderNumber);
			ps.setInt(2, Integer.parseInt(travelerId));

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Order fetchedOrder = new Order(rs.getInt("order_number"), rs.getString("park_name"),
						rs.getDate("order_date").toString(), rs.getTime("entry_time").toString().substring(0, 5),
						rs.getInt("number_of_visitors"), String.valueOf(rs.getInt("id")), rs.getString("email"),
						rs.getString("phone_number"), rs.getString("type_of_visitor"), rs.getString("status"),
						rs.getString("QR_code"),
						rs.getDate("date_of_placing_order") != null ? rs.getDate("date_of_placing_order").toString()
								: null,
						rs.getTime("exit_time") != null ? rs.getTime("exit_time").toString() : null);
				return fetchedOrder;
			}
		} catch (SQLException e) {
			System.out.println("Error fetching validated order: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
	
	
	public static String getSubscriberId(String subscriberNum) {
		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement ps = conn
					.prepareStatement("SELECT id FROM gonature_db_new.subscriber WHERE sub_number = ?");
			ps.setString(1, subscriberNum);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("id");
			}
		} catch (SQLException e) {
			System.out.println("Error fetching validated order: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}
}