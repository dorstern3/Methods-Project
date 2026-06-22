package client.logic;

import java.util.ArrayList;

import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Logic class responsible for handling park exit operations.
 * Acts as the intermediary between the Exit GUI (Employee or Visitor screens) and the Server.
 * Packages exit requests and processes the server's validation response.
 */
public class ExitLogic {
    
    /**
     * Constructs a new ExitLogic instance.
     */
    public ExitLogic() {}

    /**
     * Registers a visitor's exit from the park by communicating with the server.
     * Packages the order ID and traveler ID into a network message and awaits confirmation.
     * * @param orderId    The unique Order ID or scanned QR Code string of the exiting visitor.
     * @param travelerId The identification string of the specific traveler (e.g., ID or Subscriber Number). 
     * This can be passed as null if the exit is performed by an authorized park employee.
     * @return true if the server successfully registered the exit and updated the database, false otherwise.
     */
    public boolean registerExit(String orderId, String travelerId) {
        System.out.println("Client Logic: Requesting exit for Order ID: " + orderId + " (Traveler ID: " + travelerId + ")");
        
        try {
            // Sending the EXIT_PARK message to the server
            Object[] requestData = new Object[] { orderId, travelerId };
            Message request = new Message(MessageType.EXIT_PARK, requestData);
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