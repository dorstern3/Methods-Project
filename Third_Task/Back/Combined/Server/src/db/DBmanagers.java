package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.Message;
import common.MessageType;
import common.ParameterRequest;
import ocsf.server.ConnectionToClient;

public class DBmanagers {
	 
	public static void handleSubmitParameterRequest(Message message, ConnectionToClient client) {
		ParameterRequest req = (ParameterRequest) message.getData();
		boolean success = false;

		String query = "INSERT INTO gonature_db_new.parameter_requests (park_name, worker_id, parameter_name, current_value, request_value, status) VALUES (?, ?, ?, ?, ?, 'Pending');";
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBconnection.getConnection();
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, req.getParkName()); 
			pstmt.setInt(2, req.getWorkerId());
			pstmt.setString(3, req.getParameterName());
			pstmt.setInt(4, req.getCurrentValue());
			pstmt.setInt(5, req.getRequestedValue());

			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println("Server: New parameter request inserted for park: " + req.getParkName());
			}
		} catch (Exception e) {
			System.err.println("Server: Database error during parameter request submission.");
			e.printStackTrace();
		}finally {
			if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}

		try {
			client.sendToClient(success ? new Message(MessageType.REQUEST_SUBMIT_SUCCESS, null)
					: new Message(MessageType.REQUEST_SUBMIT_FAILED, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleGetPendingParameterRequests(Message message, ConnectionToClient client) {
		ArrayList<ParameterRequest> pendingList = new ArrayList<>();
		String query = "SELECT request_id, park_name, worker_id, parameter_name, current_value, request_value, status, request_date FROM gonature_db_new.parameter_requests WHERE status = 'Pending';";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DBconnection.getConnection();
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				pendingList.add(new ParameterRequest(rs.getInt("request_id"), rs.getString("park_name"),
						rs.getInt("worker_id"), rs.getString("parameter_name"), rs.getInt("current_value"),
						rs.getInt("request_value"), rs.getString("status"), rs.getTimestamp("request_date")));
			}
			System.out.println("Server: Fetched " + pendingList.size() + " pending parameter requests from DB.");
		} catch (Exception e) {
			System.err.println("Server: Database error during fetching pending requests.");
			e.printStackTrace();
		}finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}

		try {
			client.sendToClient(new Message(MessageType.GET_PENDING_REQUESTS_RESPONSE, pendingList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleUpdateParameterRequestStatus(Message message, ConnectionToClient client) {
		ParameterRequest req = (ParameterRequest) message.getData();
		boolean success = false;
		Connection conn = null;
		PreparedStatement updateReqStmt = null;
		PreparedStatement updateParkStmt = null;

		try {
			conn = DBconnection.getConnection();
			conn.setAutoCommit(false);

			String updateReqSql = "UPDATE gonature_db_new.parameter_requests SET status = ? WHERE request_id = ?;";
			updateReqStmt = conn.prepareStatement(updateReqSql);
			updateReqStmt.setString(1, req.getStatus());
			updateReqStmt.setInt(2, req.getRequestId());
			updateReqStmt.executeUpdate();
			

			if ("Approved".equals(req.getStatus())) {
				String updateParkSql = "UPDATE gonature_db_new.Parks SET " + req.getParameterName()
						+ " = ? WHERE park_name = ?;";
				updateParkStmt = conn.prepareStatement(updateParkSql);
					updateParkStmt.setInt(1, req.getRequestedValue());
					updateParkStmt.setString(2, req.getParkName());
					updateParkStmt.executeUpdate();
			}

			conn.commit();
			conn.setAutoCommit(true);
			success = true;
			System.out.println("Server: Transaction completed. Request ID " + req.getRequestId() + " updated to: "
					+ req.getStatus());

		} catch (Exception ex) {
			System.err.println("Server: Error during request status update transaction. Rolling back...");
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception se) {
				se.printStackTrace();
			}
			ex.printStackTrace();
		} finally {
			if (updateReqStmt != null) { try { updateReqStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (updateParkStmt != null) { try { updateParkStmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) {
				try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
				db.DBconnection.release(conn);
			}
		}
		try {
			client.sendToClient(success ? new Message(MessageType.UPDATE_REQUEST_SUCCESS, null)
					: new Message(MessageType.UPDATE_REQUEST_FAILED, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void handleActivatePromotion(Message message, ConnectionToClient client) {
		Object[] params = (Object[]) message.getData();
		String parkName = (String) params[0];
		double dbDiscountValue = (double) params[1];
		boolean success = false;

		String query = "UPDATE gonature_db_new.Parks SET additonal_discount = ? WHERE park_name = ?;";
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DBconnection.getConnection();
			pstmt = conn.prepareStatement(query);
			pstmt.setDouble(1, dbDiscountValue);
			pstmt.setString(2, parkName);

			success = pstmt.executeUpdate() > 0;
			if (success) {
				System.out.println(
						"Server: Activated promotion discount (" + dbDiscountValue + ") for park: " + parkName);
			}
		} catch (Exception e) {
			System.err.println("Server: Database error during promotion activation.");
			e.printStackTrace();
		}finally {
			if (pstmt != null) { try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (conn != null) { db.DBconnection.release(conn); }
		}

		try {
			client.sendToClient(success ? new Message(MessageType.PROMOTION_ACTIVATED_SUCCESS, null)
					: new Message(MessageType.PROMOTION_ACTIVATED_FAILED, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
