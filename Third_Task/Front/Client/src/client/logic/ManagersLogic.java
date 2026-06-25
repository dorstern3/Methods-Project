package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;
import common.ParameterRequest;

/**
 * Business logic layer for Manager operations.
 * Marshals ParameterRequest objects to and from the server.
 */
public class ManagersLogic {

    /**
     * Sends a new parameter change request from a Park Manager to the server.
     */
    public Message sendParameterRequest(String parkName, int workerId, String paramName, int currentValue, int requestedValue) {
        ParameterRequest request = new ParameterRequest(parkName, workerId, paramName, currentValue, requestedValue);
        
        Message msg = new Message(MessageType.SUBMIT_PARAMETER_REQUEST, request);
        return (Message) ClientUI.clientChat.accept(msg);
    }

    /**
     * Requests the list of all pending parameter requests for the Department Manager.
     */
    public Message requestPendingRequests() {
        Message msg = new Message(MessageType.GET_PENDING_PARAMETER_REQUESTS, null);
        return (Message) ClientUI.clientChat.accept(msg);
    }

    /**
     * Sends the department manager's decision (Approve/Reject) to the server.
     */
    public Message handleRequestDecision(ParameterRequest request, String newStatus) {
        request.setStatus(newStatus);
        Message msg = new Message(MessageType.UPDATE_PARAMETER_REQUEST_STATUS, request);
        return (Message) ClientUI.clientChat.accept(msg);
    }
    /**
     * Sends a promotion update request to the server to activate a new discount value for a specific park.
     * Packages the park name and the discount value into an object array.
     * @param parkName      The name of the target park where the promotion should be applied.
     * @param discountValue The promotional discount rate as a decimal (e.g., 0.2 for 20%).
     * @return              The confirmation Message object returned by the server layer.
     */
    public Message sendPromotionUpdate(String parkName, double discountValue) {
        Object[] params = new Object[] { parkName, discountValue };
        Message msg = new Message(MessageType.ACTIVATE_PROMOTION, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }
}