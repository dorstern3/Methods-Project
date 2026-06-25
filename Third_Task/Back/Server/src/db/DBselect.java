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
		Connection conn = null;
		PreparedStatement stmtSub = null;
		ResultSet rsSub = null;
		PreparedStatement stmtGuide = null;
		ResultSet rsGuide = null;
		PreparedStatement stmtSubId = null;
		ResultSet rsSubId = null;

		try {
			conn = DBconnection.getConnection();

			if (travelerId.length() == 4) {
				stmtSub = conn.prepareStatement("SELECT fname, family_members FROM subscriber WHERE sub_number = ?");
				stmtSub.setString(1, travelerId);
				rsSub = stmtSub.executeQuery();

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
				stmtGuide = conn.prepareStatement("SELECT fname FROM guide WHERE guide_id = ?");
				stmtGuide.setString(1, travelerId);
				rsGuide = stmtGuide.executeQuery();

				if (rsGuide.next()) {
					String name = rsGuide.getString("fname");
					rsGuide.close();
					stmtGuide.close();
					return "Guide:" + name;
				}
				rsGuide.close();
				stmtGuide.close();

				stmtSubId = conn
						.prepareStatement("SELECT fname, family_members FROM subscriber WHERE id = ?");
				stmtSubId.setString(1, travelerId);
				rsSubId = stmtSubId.executeQuery();

				if (rsSubId.next()) {
					String name = rsSubId.getString("fname");
					int familyMembers = rsSubId.getInt("family_members");
					rsSubId.close();
					stmtSubId.close();
					return "Subscriber:" + name + ":" + familyMembers;
				}
			}

		} catch (Exception e) {
			System.out.println("Error identifying traveler: " + e.getMessage());
			e.printStackTrace();
			return null;
		}finally {
			if (rsSub != null) { try { rsSub.close(); } catch (SQLException e) {} }
			if (stmtSub != null) { try { stmtSub.close(); } catch (SQLException e) {} }
			if (rsGuide != null) { try { rsGuide.close(); } catch (SQLException e) {} }
			if (stmtGuide != null) { try { stmtGuide.close(); } catch (SQLException e) {} }
			if (rsSubId != null) { try { rsSubId.close(); } catch (SQLException e) {} }
			if (stmtSubId != null) { try { stmtSubId.close(); } catch (SQLException e) {} }
			if (conn != null) { db.DBconnection.release(conn); }
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
		Connection conn = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement psUpdate = null;

		try {
			conn = DBconnection.getConnection();

			ps1 = conn.prepareStatement(
					"SELECT park_name, order_date FROM gonature_db_new.`order` WHERE order_number = ?");
			ps1.setInt(1, canceledOrderNum);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				String park = rs1.getString("park_name");
				Date date = rs1.getDate("order_date");

				System.out.println("DEBUG: Order cancelled. Checking waitlist for " + park + " on " + date);

				ps2 = conn.prepareStatement(
						"SELECT order_number, id, email, phone_number, number_of_visitors, entry_time "
								+ "FROM gonature_db_new.`order` "
								+ "WHERE park_name = ? AND order_date = ? AND status = 'On waiting list' "
								+ "ORDER BY date_of_placing_order ASC, order_number ASC");

				ps2.setString(1, park);
				ps2.setDate(2, date);
				rs2 = ps2.executeQuery();

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
						psUpdate = conn.prepareStatement(
								"UPDATE gonature_db_new.`order` SET status = 'Waiting list unconfirmed' WHERE order_number = ?");
						psUpdate.setInt(1, nextOrderNum);
						psUpdate.executeUpdate();
						psUpdate.close();

						System.out.println("DEBUG: SUCCESS! Space allocated to Order #" + nextOrderNum);

						return "To: " + email + " / " + phone + "\nGood news! Space has freed up in " + park
								+ ".\nPlease confirm your order #" + nextOrderNum + " within 1 hour.";
					} else {
						System.out.println("DEBUG: Still not enough capacity for Order #" + nextOrderNum
								+ ", checking next candidate...");
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (rs1 != null) { try { rs1.close(); } catch (SQLException e) {} }
			if (ps1 != null) { try { ps1.close(); } catch (SQLException e) {} }
			if (rs2 != null) { try { rs2.close(); } catch (SQLException e) {} }
			if (ps2 != null) { try { ps2.close(); } catch (SQLException e) {} }
			if (psUpdate != null) { try { psUpdate.close(); } catch (SQLException e) {} }
			if (conn != null) { db.DBconnection.release(conn); }
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
		Connection conn = null;
		PreparedStatement stmtPark = null;
		ResultSet rsPark = null;
		PreparedStatement stmtOrder = null;
		ResultSet rsOrder = null;

		try {
			conn = DBconnection.getConnection();
			stmtPark = conn.prepareStatement(
					"SELECT max_capacity, casual_gap, estimated_staying_time FROM parks WHERE park_name = ?");
			stmtPark.setString(1, park);
			rsPark = stmtPark.executeQuery();

			int maxCapacity = 0, casualGap = 0, estimatedStayingTime = 4;
			if (rsPark.next()) {
				maxCapacity = rsPark.getInt("max_capacity");
				casualGap = rsPark.getInt("casual_gap");
				estimatedStayingTime = rsPark.getInt("estimated_staying_time");
			}

			int allowedOrdersCapacity = maxCapacity - casualGap;
			int requestedHour = Integer.parseInt(time.split(":")[0]);

			for (int i = 0; i < estimatedStayingTime; i++) {
				int currentCheckHour = requestedHour + i;
				stmtOrder = conn.prepareStatement("SELECT SUM(number_of_visitors) FROM `order` "
						+ "WHERE park_name = ? AND order_date = ? "
						+ "AND status IN ('Booked', 'Confirmed', 'Pending confirmation', 'Entered', 'Waiting list unconfirmed') "
						+ "AND HOUR(entry_time) <= ? " + "AND HOUR(entry_time) + ? > ?");

				stmtOrder.setString(1, park);
				stmtOrder.setString(2, date);
				stmtOrder.setInt(3, currentCheckHour);
				stmtOrder.setInt(4, estimatedStayingTime);
				stmtOrder.setInt(5, currentCheckHour);

				rsOrder = stmtOrder.executeQuery();
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
		}finally {
			if (rsPark != null) { try { rsPark.close(); } catch (SQLException e) {} }
			if (stmtPark != null) { try { stmtPark.close(); } catch (SQLException e) {} }
			if (rsOrder != null) { try { rsOrder.close(); } catch (SQLException e) {} }
			if (stmtOrder != null) { try { stmtOrder.close(); } catch (SQLException e) {} }
			if (conn != null) { db.DBconnection.release(conn); }
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBconnection.getConnection();
			ps = conn
					.prepareStatement("SELECT * FROM gonature_db_new.order WHERE order_number = ? AND id = ?");
			ps.setInt(1, orderNumber);
			ps.setInt(2, Integer.parseInt(travelerId));

			rs = ps.executeQuery();

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
		}finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}

		return null;
	}
	
	

	
	/**
	 * Translates a subscriber number to the corresponding traveler ID.
	 * @param subNumber The subscriber number to translate.
	 * @return The traveler ID as a string, or null if not found.
	 */
	public static String getTravelerIdBySubNumber(String subNumber) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
	        conn = DBconnection.getConnection();
	        ps = conn.prepareStatement("SELECT id FROM gonature_db_new.subscriber WHERE sub_number = ?");
	        ps.setString(1, subNumber);
	        rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getString("id");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }finally {
			if (rs != null) { try { rs.close(); } catch (Exception e) {} }
			if (ps != null) { try { ps.close(); } catch (Exception e) {} }
			if (conn != null) { try { DBconnection.release(conn); } catch (Exception e) {} }
		}
	    return null;
	}
	
	/**
	 * Checks if a traveler has an active order. Active means: Status is NOT
	 * 'Canceled', and if status is 'Entered', it must have an exit_time (meaning
	 * they left). 
	 * @param travelerId The ID of the traveler.
	 * 
	 * @return true if an active order exists, false otherwise.
	 */
	public static boolean hasActiveOrder(String travelerId) {
		Connection conn = null;
	    PreparedStatement ps = null; 

		try {
	    	conn = DBconnection.getConnection();
			
			String query = "SELECT COUNT(*) FROM gonature_db_new.order WHERE id = ? " + "AND status != 'Canceled' "
					+ "AND status != 'Entered' ";
	        ps = conn.prepareStatement(query);
			ps.setString(1, travelerId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			System.out.println("Error checking active order: " + e.getMessage());
			e.printStackTrace();
		}finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}
		return false;
	}
	/**
	 * Checks if a traveler is currently inside the park (Entered) and has not exited yet.
	 */
	public static boolean isCurrentlyInsidePark(String travelerId) {
	    String query = "SELECT COUNT(*) FROM gonature_db_new.order WHERE id = ? " 
	            + "AND status = 'Entered' "
	            + "AND exit_time IS NULL";
	    Connection conn = null;
	    PreparedStatement ps = null; 
	      
	    try {
	    	conn = DBconnection.getConnection();
	        ps = conn.prepareStatement(query);
	        ps.setString(1, travelerId);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error checking inside park status: " + e.getMessage());
	        e.printStackTrace();
	    }finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}
	    return false;
	}
}
