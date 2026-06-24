package client.logic;

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
     * Packages the order ID, traveler ID, and the current park name into a network message.
     * * @param orderId    The unique Order ID or scanned QR Code string of the exiting visitor.
     * @param travelerId The identification string of the specific traveler (e.g., ID or Subscriber Number). 
     * This can be passed as null if the exit is performed by an authorized park employee.
     * @return true if the server successfully registered the exit and updated the database, false otherwise.
     */
    public boolean registerExit(String orderId, String travelerId) {
        System.out.println("Client Logic: Requesting exit for Order ID: " + orderId + " (Traveler ID: " + travelerId + ")");
        
        try {
            // 1. Identify if the current user is an employee to fetch their park name.
            // Using the static CurUser methods matching your exact implementation.
            String currentPark = null;
            if (CurUser.isLoggedIn() && CurUser.getRole() != null) {
                currentPark = CurUser.getParkName();
            }

            // 2. Package the data into a 3-element array. 
            // currentPark will be null for visitors, which the server understands and handles.
            Object[] requestData = new Object[] { orderId, travelerId, currentPark };
            
            // 3. Send the EXIT_PARK message to the server
            Message request = new Message(MessageType.EXIT_PARK, requestData);
            Message response = (Message) ClientUI.clientChat.accept(request);            
            
            // 4. Check for the correct response type (EXIT_PARK_RESPONSE)
            if (response != null && response.getType() == MessageType.EXIT_PARK_RESPONSE) {
                return (boolean) response.getData(); 
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during exit registration.");
            e.printStackTrace();
        }
        
        return false; 
    }
}