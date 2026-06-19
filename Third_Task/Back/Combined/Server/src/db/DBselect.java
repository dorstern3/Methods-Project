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
	 * Identifies the traveler type (Subscriber, Guide, or Regular) by ID. * @param
	 * travelerId The ID or subscriber number.
	 * 
	 * @return A string representing the traveler type and relevant info.
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
		}

		return "Regular Traveler";
	}

	/**
	 * Checks if a park has enough available capacity for a new order. * @param
	 * orderDetails The order object containing park, date, time, and number of
	 * visitors.
	 * 
	 * @return true if there is sufficient capacity, false otherwise.
	 */
	public static boolean checkAvailability(Order orderDetails) {
		String park = orderDetails.getParkName();
		String date = orderDetails.getOrderDate();
		String time = orderDetails.getEntryTime();
		int newVisitors = orderDetails.getNumberOfVisitors();

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
	 * Generates a list of alternative available dates and times for an order.
	 * * @param originalOrder The original order request.
	 * 
	 * @return An ArrayList of strings representing available slots.
	 */
	public static ArrayList<String> getAlternativeDatesList(Order originalOrder) {
		ArrayList<String> availableSlots = new ArrayList<>();

		try {
			String dateStr = originalOrder.getOrderDate();
			LocalDate originalDate = LocalDate.parse(dateStr);

			LocalDate[] datesToCheck = { originalDate.minusDays(1), originalDate, originalDate.plusDays(1) };
			String[] timesToCheck = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00" };

			for (LocalDate checkDate : datesToCheck) {
				if (checkDate.isBefore(LocalDate.now())) {
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
	 * Checks the waiting list for the next eligible order after a cancellation.
	 * Updates the status of the eligible order to 'Pending confirmation' and
	 * generates a notification. * @param canceledOrderNum The order number of the
	 * canceled order.
	 * 
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

					Order orderDetailsForCheck = new Order(park, date.toString(), time.toString().substring(0, 5),
							waitingVisitors, null, null, null, null, "Booked" // שאר הנתונים לא משנים לבדיקת זמינות מקום
					);

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
	 * order's owner ID. * @param orderNumber The unique identifier of the order.
	 * 
	 * @param travelerId The ID provided by the traveler.
	 * @return An Order object if validation passes, or null if it fails.
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
}