package client.logic;

import java.util.ArrayList;
import client.ClientUI;
import common.Message;
import common.MessageType;

public class SubUpdateLogic {
    
    /**
     * Fetches full subscriber data rows using their subscriber identity token.
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
     * Dispatches edited billing, contact, and family headcount data arrays to the database.
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