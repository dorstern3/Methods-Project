package client.logic;

import java.util.ArrayList;

import client.ClientUI;
import common.Message;
import common.MessageType;

public class ExitLogic {
    
    public ExitLogic() {}

    /**
     * Registers an exit for a given order ID.
     * @param orderId The ID of the order exiting the park.
     * @return true if the exit was successful, false otherwise.
     */
    public boolean registerExit(String orderId) {
        System.out.println("Client Logic: Requesting exit for Order ID: " + orderId);
        
        try {
            // Sending the EXIT_PARK message to the server
            Message request = new Message(MessageType.EXIT_PARK, orderId);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            // Checking for the correct response type (EXIT_PARK_RESPONSE)
            if (response != null && response.getType() == MessageType.EXIT_PARK_RESPONSE) {
                return (boolean) response.getData(); // Returns true or false from the server
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during exit registration.");
            e.printStackTrace();
        }
        
        return false; // Returns false if something failed
    }
}