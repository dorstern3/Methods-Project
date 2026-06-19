package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Handles the business logic for traveler-related operations. Acts as the
 * Control layer in the ECB architecture, facilitating communication between the
 * GUI and the Server.
 */
public class TravelerLogic {

	/**
	 * Sends a login request to the server for the traveler. * @param travelerId The
	 * ID of the traveler attempting to log in.
	 * 
	 * @return "SUCCESS" if the login was successful, or the error message if it
	 *         failed.
	 */
	public String loginTraveler(String travelerId) {
		Message msg = new Message(MessageType.TRAVELER_LOGIN, travelerId);
		Message response = (Message) ClientUI.clientChat.accept(msg);

		if (response != null && response.getType() == MessageType.LOGIN_FAILED) {
			return (String) response.getData();
		}
		return "SUCCESS";
	}

	/**
	 * Sends a request to the server to identify the traveler type (e.g.,
	 * Subscriber, Guide, Regular). * @param travelerId The ID or subscriber number
	 * of the traveler.
	 * 
	 * @return A string containing the traveler type and relevant info (e.g.,
	 *         "Subscriber:Dana:4"), or null if not found.
	 */
	public String identifyTraveler(String travelerId) {
		Message messageToServer = new Message(MessageType.IDENTIFY_TRAVELER, travelerId);
		Message responseMsg = (Message) ClientUI.clientChat.accept(messageToServer);

		if (responseMsg != null && responseMsg.getType() == MessageType.IDENTIFY_TRAVELER_RESPONSE) {
			return (String) responseMsg.getData();
		}
		return null;
	}
}