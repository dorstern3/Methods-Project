package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Logic class handling the communication between the Service Representative GUI
 * and the Server. Packages user input into Message objects and sends them for database processing.
 */
public class ServiceRepLogic {
	
	public ServiceRepLogic() {}
	
	/**
	 * Packages and sends a request to register a new Family Subscriber.
	 */
	public Message requestFamilyRegistration(int id, String fname, String lname, String email, String phone, int familyMembers) {
		Object[] params = new Object[] { id, fname, lname, email, phone, familyMembers };
		Message msg = new Message(MessageType.REGISTER_FAMILY_SUBSCRIBER, params);
		return (Message) ClientUI.clientChat.accept(msg);
	}
	
	/**
	 * Packages and sends a request to register a new Single Subscriber.
	 * Explicitly sets the familyMembers count to 1.
	 */
	public Message requestSingleRegistration(int id, String fname, String lname, String email, String phone) {
		Object[] params = new Object[] { id, fname, lname, email, phone, 1 }; 
		Message msg = new Message(MessageType.REGISTER_SINGLE_SUBSCRIBER, params);
		return (Message) ClientUI.clientChat.accept(msg);
	}

	/**
	 * Packages and sends a request to register a new Group Guide.
	 * FIXED: Now accepts and packages the Guide ID as the first parameter.
	 */
	public Message requestGuideRegistration(int id, String fname, String lname, String email, String phone) {
		Object[] params = new Object[] { id, fname, lname, email, phone };
		Message msg = new Message(MessageType.REGISTER_GUIDE, params);
		return (Message) ClientUI.clientChat.accept(msg);
	}
	
}