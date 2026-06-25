package client.logic;

import java.util.ArrayList;

import client.ClientUI;
import common.Subscriber;
import common.Workers;
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
    public Message requestFamilyRegistration(int id, String fname, String lname, String email, String phone, int familyMembers , String creditCard) {
        Object[] params = new Object[] { id, fname, lname, email, phone, familyMembers,creditCard };
        Message msg = new Message(MessageType.REGISTER_FAMILY_SUBSCRIBER, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }
    
    /**
     * Packages and sends a request to register a new Single Subscriber.
     * Explicitly sets the familyMembers count to 1.
     */
    public Message requestSingleRegistration(int id, String fname, String lname, String email, String phone, String creditCard) {
        Object[] params = new Object[] { id, fname, lname, email, phone, 1 ,creditCard}; 
        Message msg = new Message(MessageType.REGISTER_SINGLE_SUBSCRIBER, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }

    /**
     * Packages and sends a request to register a new Group Guide.
     */
    public Message requestGuideRegistration(int id, String fname, String lname, String email, String phone) {
        Object[] params = new Object[] { id, fname, lname, email, phone };
        Message msg = new Message(MessageType.REGISTER_GUIDE, params);
        return (Message) ClientUI.clientChat.accept(msg);
    }
    
    /**
     * Sends a request to the server to fetch all registered subscribers.
     * @return ArrayList of Subscriber objects, or an empty list if the request fails.
     */
    public ArrayList<Subscriber> loadSubscribers() {
        System.out.println("ServiceRepLogic: Requesting subscriber list from server.");
        try {
            Message request = new Message(MessageType.GET_SUBSCRIBERS_LIST, null);
            Message response = (Message) ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == MessageType.GET_SUBSCRIBERS_LIST_RESPONSE) {
                return (ArrayList<Subscriber>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error fetching subscribers list from server.");
            e.printStackTrace();
        }
        return new ArrayList<>(); 
    }
    
    /**
     * Sends a request to the server to fetch all system workers.
     * @return ArrayList of Workers objects, or an empty list if the request fails.
     */
    public ArrayList<Workers> loadWorkers() {
        System.out.println("ServiceRepLogic: Requesting workers list from server.");
        try {
            Message request = new Message(MessageType.GET_WORKERS_LIST, null);
            Message response = (Message) ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == MessageType.GET_WORKERS_LIST_RESPONSE) {
                return (ArrayList<Workers>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error fetching workers list from server.");
            e.printStackTrace();
        }
        return new ArrayList<>(); 
    }
}