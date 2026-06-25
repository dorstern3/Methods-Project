package client.logic;

import java.util.ArrayList;
import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Logic class responsible for managing subscriber profile retrieval and update workflows.
 * Acts as the intermediary layer between the subscriber editor interface and the server architecture,
 * packaging collections into synchronized networking messages.
 */
public class SubUpdateLogic {
    
	/**
     * Fetches full subscriber data rows from the server.
     *
     * @param subNumber The unique 4-digit subscriber number string used to query the database.
     * @return          An ArrayList of Strings containing the subscriber's complete profile attributes 
     * (ID, fname, lname, email, phone, credit card, family limit), or null if the fetch fails.
     */
    public ArrayList<String> getSubscriberDetails(String subNumber) {
        try {
            Message request = new Message(MessageType.GET_SUBSCRIBER_DETAILS, subNumber);
            Message response = (Message) ClientUI.clientChat.accept(request);

            if (response != null && response.getType() == MessageType.GET_SUBSCRIBER_DETAILS_RESPONSE) {
                return (ArrayList<String>) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during details fetch.");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Dispatches edited billing, contact, and family headcount data arrays to the server database.
     *
     * @param subNumber     The unique identification subscriber number string.
     * @param fname         The first name of the subscriber.
     * @param lname         The last name of the subscriber.
     * @param email         The updated contact email string.
     * @param phone         The updated contact phone number string.
     * @param creditCard    The credit card billing details string (can be empty).
     * @param familyMembers The valid numerical headcount string representing the family members.
     * @return              true if the server successfully committed the update database transactions, false otherwise.
     */
    public boolean updateSubscriberDetails(String subNumber, String fname, String lname, String email, String phone, String creditCard, String familyMembers) {
        try {
            ArrayList<String> dataList = new ArrayList<>();
            dataList.add(subNumber);
            dataList.add(fname);
            dataList.add(lname);
            dataList.add(email);
            dataList.add(phone);
            dataList.add(creditCard);
            dataList.add(familyMembers); // Index 6
            
            Message request = new Message(MessageType.UPDATE_SUBSCRIBER_DETAILS, dataList);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.UPDATE_SUBSCRIBER_DETAILS_RESPONSE) {
                return (boolean) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during subscriber details update.");
            e.printStackTrace();
        }
        return false;
    }
}