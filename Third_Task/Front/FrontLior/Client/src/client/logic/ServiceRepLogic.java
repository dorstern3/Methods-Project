package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;

public class ServiceRepLogic {
	
	
	
	public ServiceRepLogic() {}
	
	
	public Message requestFamilyRegistration(int id, String fname, String lname, String email, String phone, int familyMembers) {
        Object[] params = new Object[] { id, fname, lname, email, phone, familyMembers };
        Message msg = new Message(MessageType.REGISTER_FAMILY_SUBSCRIBER, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }
	
	public Message requestSingleRegistration(int id, String fname, String lname, String email, String phone) {
        Object[] params = new Object[] { id, fname, lname, email, phone, 1 }; 
        Message msg = new Message(MessageType.REGISTER_SINGLE_SUBSCRIBER, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }

    public Message requestGuideRegistration(String fname, String lname, String email, String phone) {
        Object[] params = new Object[] { fname, lname, email, phone };
        Message msg = new Message(MessageType.REGISTER_GUIDE, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }
    
}
