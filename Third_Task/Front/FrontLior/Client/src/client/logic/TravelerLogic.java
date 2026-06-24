package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Handles the business logic for traveler-related operations. Acts as the
 * Control layer in the ECB architecture, facilitating communication between the
 * graphical user interface (GUI) and the Server.
 */
public class TravelerLogic {

	/**
	 * Sends a login request to the server for a specific traveler. * @param
	 * travelerId The ID or subscriber number of the traveler attempting to log in.
	 * 
	 * @return A String representing the result: "SUCCESS" if the login was
	 *         successful, or a specific error message string if the login failed.
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
	 * Sends a request to the server to identify the specific type of a traveler
	 * (e.g., Subscriber, Guide, or Regular visitor). * @param travelerId The ID or
	 * subscriber number of the traveler to be identified.
	 * 
	 * @return A formatted String containing the traveler type and relevant
	 *         information (e.g., "Subscriber:Dana:4"), or null if the traveler
	 *         could not be found.
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