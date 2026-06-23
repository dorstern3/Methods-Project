package db;

import java.sql.Connection;
import java.sql.PreparedStatement;

import common.Message;
import common.MessageType;
import ocsf.server.ConnectionToClient;

public class DBcustomerService {
	public static void handleRegisterFamilySubscriber(Message message, ConnectionToClient client) {
		Object[] params = (Object[]) message.getData();
		int id = (int) params[0];
		String fname = (String) params[1];
		String lname = (String) params[2];
		String email = (String) params[3];
		String phone = (String) params[4];
		int familyMembers = (int) params[5];

		int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
		boolean success = false;

		String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, email);
			pstmt.setString(5, phone);
			pstmt.setNull(6, java.sql.Types.VARCHAR);
			pstmt.setInt(7, familyMembers);
			pstmt.setInt(8, generatedSubNum);

			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Family subscriber registered successfully. Sub Number: " + generatedSubNum);
			}
		} catch (Exception e) {
			System.err.println("Server: Database error during family registration.");
			e.printStackTrace();
		}

		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum)
					: new Message(MessageType.REGISTRATION_FAILED, null);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleRegisterSingleSubscriber(Message message, ConnectionToClient client) {
		Object[] params = (Object[]) message.getData();
		int id = (int) params[0];
		String fname = (String) params[1];
		String lname = (String) params[2];
		String email = (String) params[3];
		String phone = (String) params[4];
		int familyMembers = (int) params[5];

		int generatedSubNum = new java.util.Random().nextInt(9000) + 1000;
		boolean success = false;

		String query = "INSERT INTO gonature_db_new.subscriber (id, fname, lname, email, phone_number, credit_card_number, family_members, sub_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, fname);
			pstmt.setString(3, lname);
			pstmt.setString(4, email);
			pstmt.setString(5, phone);
			pstmt.setNull(6, java.sql.Types.VARCHAR);
			pstmt.setInt(7, familyMembers);
			pstmt.setInt(8, generatedSubNum);

			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Single subscriber registered successfully. Sub Number: " + generatedSubNum);
			}
		} catch (Exception e) {
			System.err.println("Server: Database error during single registration.");
			e.printStackTrace();
		}

		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, generatedSubNum)
					: new Message(MessageType.REGISTRATION_FAILED, null);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleRegisterGuide(Message message, ConnectionToClient client) {
		Object[] params = (Object[]) message.getData();
		String fname = (String) params[0];
		String lname = (String) params[1];
		String email = (String) params[2];
		String phone = (String) params[3];
		boolean success = false;

		String query = "INSERT INTO gonature_db_new.Guide (fname, lname, email, phone_number) VALUES (?, ?, ?, ?);";

		try (Connection conn = DBconnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, fname);
			pstmt.setString(2, lname);
			pstmt.setString(3, email);
			pstmt.setString(4, phone);

			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: Group Guide registered successfully: " + fname + " " + lname);
			}
		} catch (Exception e) {
			System.err.println("Server: Database error during guide registration.");
			e.printStackTrace();
		}

		try {
			Message response = success ? new Message(MessageType.REGISTRATION_SUCCESS, null)
					: new Message(MessageType.REGISTRATION_FAILED, null);
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
