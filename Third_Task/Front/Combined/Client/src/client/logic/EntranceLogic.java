package client.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import client.ClientUI;
import common.Message;
import common.MessageType;

/**
 * Logic class handling entrance operations, pricing, and validations.
 * Acts as the intermediary between the Entrance GUI and the Server.
 * Uses the Strategy Design Pattern to calculate entry prices dynamically based on visitor types.
 */
public class EntranceLogic {
    
    private double fullPrice;
    
    // Map to hold the different pricing strategies
    private Map<String, PricingStrategy> pricingStrategies;

    /**
     * Constructs a new EntranceLogic instance.
     * Initializes the pricing strategies mapping and fetches the base full price 
     * for the current park directly from the server.
     */
    public EntranceLogic() {
        this.fullPrice = fetchFullPriceFromServer();
        
        // Initialize the Strategy Pattern mapping
        pricingStrategies = new HashMap<>();
        
        // Map each UI visitor type string to its corresponding strategy class
        pricingStrategies.put("Regular/Family", new RegularPricingStrategy());
        pricingStrategies.put("Group", new GroupPricingStrategy());
        pricingStrategies.put("Subscriber", new SubscriberPricingStrategy());
    }

    /**
     * Fetches the base full ticket price for the specific park from the server.
     * * @return The full price as a double. Returns 50.0 as a fallback if the server request fails.
     */
    private double fetchFullPriceFromServer() {
        System.out.println("EntranceLogic: Requesting Full Price from server.");
        
        String parkName = CurUser.getParkName(); 
        
        try {
            Message request = new Message(MessageType.GET_FULL_PRICE, parkName);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.GET_FULL_PRICE_RESPONSE) {
                return (double) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error fetching full price from server.");
            e.printStackTrace();
        }
        
        return 50.0; // Fallback default price if server fails
    }

    /**
     * Checks the server for any active promotions or additional discounts for the park.
     * * @param parkId The name of the park to check.
     * @return The active discount as a decimal (e.g., 0.1 for 10%), or 0.0 if none exists or the connection fails.
     */
    private double checkActivePromotions(String parkId) {
        System.out.println("EntranceLogic: Checking active promotions for " + parkId);
        
        try {
            // Package the request with the park name
            Message request = new Message(MessageType.CHECK_PROMOTIONS, parkId);
            
            // Send to server and wait for the response
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.CHECK_PROMOTIONS_RESPONSE) {
                return (double) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during promotions check.");
            e.printStackTrace();
        }
        
        return 0.0; // Fallback to 0 discount if server fails
    }
    
    /**
     * Calculates the final ticket price using the Strategy Pattern and applies active promotions.
     * * @param visitorType  The visitor classification type selected from the UI (e.g., "Group", "Subscriber").
     * @param amount       The total number of visitors in the current transaction.
     * @param isPreBooked  Boolean flag indicating if the visit was pre-booked (true) or is a casual drop-in (false).
     * @return The final calculated total price as a double.
     */
    public double calculatePrice(String visitorType, int amount, boolean isPreBooked) {
        
        // 1. Retrieve the appropriate strategy (No Switch-Case needed!)
        PricingStrategy strategy = pricingStrategies.get(visitorType);
        
        // Fallback: If type is not found, default to Regular
        if (strategy == null) {
            strategy = new RegularPricingStrategy();
        }

        // 2. Execute the calculation of the specific strategy
        double finalPrice = strategy.calculate(amount, fullPrice, isPreBooked);

        // 3. Apply any global promotions relevant to the whole park
        double currentActivePromotionDiscount = checkActivePromotions(CurUser.getParkName());
        if (currentActivePromotionDiscount > 0) {
            finalPrice = finalPrice - (finalPrice * currentActivePromotionDiscount);
        }

        return finalPrice;
    }

    // --- System Validations & Operations ---
    
    /**
     * Validates a pre-booked order by cross-referencing the ID/QR Code with the database.
     * Checks constraints such as date, scheduled time, and confirmation status.
     * * @param orderId The Order ID or QR code to validate.
     * @return An Object array containing [visitorsAmount, visitorType] upon successful validation.
     * If validation fails, returns a String containing the specific error code (e.g., "NOT_FOUND").
     */
    public Object validateOrder(String orderId) {
        System.out.println("EntranceLogic: Requesting Order validation for ID: " + orderId);
        
        try {
            Message request = new Message(MessageType.VALIDATE_ORDER, orderId);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.VALIDATE_ORDER_RESPONSE) {
                Object data = response.getData();
                
                // If the server sent a String, it's an error code
                if (data instanceof String) {
                    return data;
                } 
                // If the server sent an ArrayList, validation was successful
                else if (data instanceof ArrayList) {
                    ArrayList<Object> listData = (ArrayList<Object>) data;
                    if (!listData.isEmpty()) {
                        return new Object[] { listData.get(0), listData.get(1) }; 
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during Order Validation.");
            e.printStackTrace();
        }
        
        return "SYSTEM_ERROR";
    }
    
    /**
     * Evaluates if the specific park has enough available capacity to accommodate casual visitors.
     * Takes into account the park's maximum capacity and the reserved casual gap.
     * * @param amount   The number of casual visitors wishing to enter.
     * @param parkName The explicit name of the destination park.
     * @return true if there is enough space to admit the visitors, false if the park is near or at maximum capacity.
     */
    public boolean checkCasualAvailability(int amount, String parkName) {
        System.out.println("EntranceLogic: Checking casual availability for " + amount + " visitors in " + parkName);
        
        try {
            // Pack both amount and park name into a list to send to the server
            java.util.ArrayList<Object> dataList = new java.util.ArrayList<>();
            dataList.add(amount);
            dataList.add(parkName);
            
            Message request = new Message(MessageType.CHECK_CAPACITY, dataList);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.CHECK_CAPACITY_RESPONSE) {
                return (boolean) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during capacity check.");
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Confirms the entry transaction after payment, communicates with the server to update the database,
     * alters order statuses, and increments the real-time park occupancy.
     * Packages structural validation parameters dynamically to coordinate cross-network operations.
     *
     * @param visitorsAmount The total headcount size of the arriving group.
     * @param orderId        The unique tracking order key string (passed as null if it's a casual visitor).
     * @param parkName       The explicit park destination name context string.
     * @param visitorType    The structural type category of the traveler (e.g., "Regular", "Subscriber", "Group").
     * @param visitorId      The explicit personal Identification or Subscriber Number index of the leader.
     * @return The active or newly database-allocated Order ID string mapping, or null if the execution collapses.
     */
    public String confirmPayment(int visitorsAmount, String orderId, String parkName, String visitorType, String visitorId) {
        System.out.println("EntranceLogic: Confirming payment for " + visitorsAmount + " visitors. ID: " + visitorId);
        
        try {
            // Package all distinct transaction parameters sequentially inside a serialized structural collection
            java.util.ArrayList<Object> dataList = new java.util.ArrayList<>();
            dataList.add(visitorsAmount); // Index 0: Occupancy volume mapping
            dataList.add(orderId);        // Index 1: Booking reference tracking string
            dataList.add(parkName);       // Index 2: Target park context string
            dataList.add(visitorType);    // Index 3: Categorized visitor type string
            dataList.add(visitorId);      // Index 4: Identification string (Newly added criteria)
            
            Message request = new Message(MessageType.CONFIRM_PAYMENT, dataList);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.CONFIRM_PAYMENT_RESPONSE) {
                // Extract and cast the response object token as a concrete database tracking reference index
                return (String) response.getData();
            }
        } catch (Exception e) {
            System.err.println("EntranceLogic: Critical exception intercepted during payment execution context communication.");
            e.printStackTrace();
        }
        return null; // Return null to explicitly declare network pipeline execution drops
    }
    
    /**
     * Verifies if the provided identification string belongs to a certified and registered group guide.
     * * @param guideId The Guide ID string to verify against the database.
     * @return true if the guide exists and is certified, false otherwise.
     */
    public boolean verifyGuide(String guideId) {
        System.out.println("EntranceLogic: Requesting Guide verification for ID: " + guideId);
        
        try {
            Message request = new Message(MessageType.VERIFY_GUIDE, guideId);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.VERIFY_GUIDE_RESPONSE) {
                return (boolean) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during Guide Verification.");
            e.printStackTrace();
        }
        
        return false; 
    }
    
    /**
     * Verifies if the provided identification string belongs to a valid, active subscriber in the system.
     * * @param subscriberId The Subscriber Number or ID to verify against the database.
     * @return true if the subscriber is valid and found, false otherwise.
     */
    public boolean verifySubscriber(String subscriberId) {
        System.out.println("EntranceLogic: Requesting Subscriber verification for ID: " + subscriberId);
        
        try {
            Message request = new Message(MessageType.VERIFY_SUBSCRIBER, subscriberId);
            Message response = (Message) ClientUI.clientChat.accept(request);
            
            if (response != null && response.getType() == MessageType.VERIFY_SUBSCRIBER_RESPONSE) {
                return (boolean) response.getData();
            }
        } catch (Exception e) {
            System.err.println("Error communicating with server during Subscriber Verification.");
            e.printStackTrace();
        }
        
        return false; 
    }
}