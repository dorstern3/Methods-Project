package backend;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.sql.*;
import common.ParameterRequestMessage;
import common.ParameterRequest;

public class GoNatureServer extends AbstractServer {
    
    private Connection dbConnection;

    public GoNatureServer(int port, Connection dbConnection) {
        super(port);
        this.dbConnection = dbConnection;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof ParameterRequestMessage) {
            ParameterRequestMessage reqMsg = (ParameterRequestMessage) msg;
            try {
                switch (reqMsg.getOpCode()) {
                    case SUBMIT_REQUEST:
                        handleSubmitRequest((ParameterRequest) reqMsg.getData(), client);
                        break;
                    case UPDATE_STATUS:
                        handleUpdateStatus((ParameterRequest) reqMsg.getData(), client);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUpdateStatus(ParameterRequest request, ConnectionToClient client) throws Exception {
        PreparedStatement updateRequestStmt = null;
        PreparedStatement updateParkStmt = null;
        try {
            dbConnection.setAutoCommit(false);
            String updateRequestSql = "UPDATE parameter_requests SET status = ? WHERE request_id = ?;";
            updateRequestStmt = dbConnection.prepareStatement(updateRequestSql);
            updateRequestStmt.setString(1, request.getStatus());
            updateRequestStmt.setInt(2, request.getRequestId());
            updateRequestStmt.executeUpdate();

            if ("Approved".equalsIgnoreCase(request.getStatus())) {
                String updateParkSql = "UPDATE parks SET " + request.getParameterName() + " = ? WHERE park_name = ?;";
                updateParkStmt = dbConnection.prepareStatement(updateParkSql);
                updateParkStmt.setInt(1, request.getRequestedValue());
                updateParkStmt.setString(2, request.getParkName());
                updateParkStmt.executeUpdate();
            }
            dbConnection.commit();
            if (client != null) client.sendToClient("SUCCESS");
        } catch (SQLException e) {
            if (dbConnection != null) dbConnection.rollback();
            if (client != null) client.sendToClient("SERVER_ERROR: " + e.getMessage());
            throw e;
        } finally {
            if (updateRequestStmt != null) updateRequestStmt.close();
            if (updateParkStmt != null) updateParkStmt.close();
            dbConnection.setAutoCommit(true);
        }
    }

    private void handleSubmitRequest(ParameterRequest req, ConnectionToClient client) throws Exception {
        String sql = "INSERT INTO parameter_requests (park_name, worker_id, parameter_name, current_value, requested_value) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, req.getParkName());
            stmt.setInt(2, req.getWorkerId());
            stmt.setString(3, req.getParameterName());
            stmt.setInt(4, req.getCurrentValue());
            stmt.setInt(5, req.getRequestedValue());
            stmt.executeUpdate();
            if (client != null) client.sendToClient("REQUEST_SUBMITTED_SUCCESSFULLY");
        }
    }
}