package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

import common.Message;
import common.MessageType;
import ocsf.server.ConnectionToClient;

/**
 * Database handler class for Customer Service operations.
 * Manages the registration processes for new Family Subscribers, Single Subscribers, and Group Guides.
 */
public class DBcustomerService {

	/**
	 * Processes the registration of a new family subscriber into the database.
	 * Generates a unique 4-digit subscriber number and inserts the provided details.
	 *
	 * @param message The network message containing the subscriber's details in an Object array.
	 * @param client  The connection thread representing the client making the request.
	 */
	public static void handleRegisterFamilySubscriber(Message message, ConnectionToClient client) {
		// 1. Extract parameters from the message data
		Object[] params = (Object[]) message.getData();
		int id = (int) params[0];
		String fname = (String) params[1];
		String lname = (String) params[2];
		String email = (String) params[3];
		String phone = (String) params[4];
		int familyMembers = (int) params[5];

		// 2. Generate a random 4-digit subscriber number (1000-9999)
		int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
		boolean success = false;

		String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

		Connection conn = null; 
		try {
			conn = DBconnection.getConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			// 3. Bind parameters to the prepared statement
			pstmt.setInt(1, id);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, email);
			pstmt.setString(5, phone);
			pstmt.setNull(6, java.sql.Types.VARCHAR); // Credit card is not provided at this stage
			pstmt.setInt(7, familyMembers);
			pstmt.setInt(8, generatedSubNum);

			// 4. Execute the database insert operation
			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Family subscriber registered successfully. Sub Number: " + generatedSubNum);
			}
		}
		} catch (Exception e) {
			System.err.println("Server: Database error during family registration.");
			e.printStackTrace();
		}finally {
			if (conn != null) {
				db.DBconnection.release(conn);
			}
		}

		// 5. Dispatch the registration result back to the client
		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum)
					: new Message(MessageType.REGISTRATION_FAILED, null);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the registration of a new single subscriber into the database.
	 * Generates a unique 4-digit subscriber number and inserts the provided details.
	 *
	 * @param message The network message containing the subscriber's details in an Object array.
	 * @param client  The connection thread representing the client making the request.
	 */
	public static void handleRegisterSingleSubscriber(Message message, ConnectionToClient client) {
		// 1. Extract parameters from the message data
		Object[] params = (Object[]) message.getData();
		int id = (int) params[0];
		String fname = (String) params[1];
		String lname = (String) params[2];
		String email = (String) params[3];
		String phone = (String) params[4];
		int familyMembers = (int) params[5];

		// 2. Generate a random 4-digit subscriber number (1000-9999)
		int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
		boolean success = false;
		String failureReason = null;
		String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

		Connection conn = null;
		try {
			conn = DBconnection.getConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			// 3. Bind parameters to the prepared statement
			pstmt.setInt(1, id);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, email);
			pstmt.setString(5, phone);
			pstmt.setNull(6, java.sql.Types.VARCHAR);
			pstmt.setInt(7, familyMembers);
			pstmt.setInt(8, generatedSubNum);

			// 4. Execute the database insert operation
			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Single subscriber registered successfully. Sub Number: " + generatedSubNum);
			}
		}catch (SQLIntegrityConstraintViolationException e) {
			// Catch specific database duplicate entry violations
			System.err.println("Server: Single registration aborted - Duplicate entry constraint violation.");
			failureReason = "DUPLICATE_ID";
			e.printStackTrace();
		}
		} catch (Exception e) {
			System.err.println("Server: Database error during single registration.");
			e.printStackTrace();
		}finally {
			if (conn != null) {
				db.DBconnection.release(conn);
			}
		}
		// 5. Dispatch the registration result back to the client
		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum)
					: new Message(MessageType.REGISTRATION_FAILED, failureReason);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the registration of a new group guide into the database.
	 * Inserts the provided details using the guide's ID as the primary key.
	 *
	 * @param message The network message containing the guide's details in an Object array.
	 * @param client  The connection thread representing the client making the request.
	 */
	public static void handleRegisterGuide(Message message, ConnectionToClient client) {
		// 1. Extract parameters from the message data
		Object[] params = (Object[]) message.getData();
		int id = (int) params[0]; 
		String fname = (String) params[1];
		String lname = (String) params[2];
		String email = (String) params[3];
		String phone = (String) params[4];
		boolean success = false;
		String failureReason = null;
		String query = "INSERT INTO gonature_db_new.Guide (guide_id, fname, lname, email, phone_number) VALUES (?, ?, ?, ?, ?);";

		Connection conn = null;
		try {
			conn = DBconnection.getConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			// 2. Bind parameters to the prepared statement
			pstmt.setInt(1, id);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, email);
			pstmt.setString(5, phone);

			// 3. Execute the database insert operation
			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Group Guide registered successfully: " + fname + " " + lname);
			}
		}
		}catch (SQLIntegrityConstraintViolationException e) {
			// Catch specific database duplicate entry violations
			System.err.println("Server: Guide registration aborted - Duplicate entry constraint violation.");
			failureReason = "DUPLICATE_ID";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Server: Database error during guide registration.");
			e.printStackTrace();
		}finally {
			if (conn != null) {
				db.DBconnection.release(conn);
			}
		}
		// 4. Dispatch the registration result back to the client
		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, null)
					: new Message(MessageType.REGISTRATION_FAILED, failureReason);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}