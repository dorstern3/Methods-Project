package client.logic;

public class EntranceLogic {
    
    // Dynamic variable to hold the base price fetched from the server
    private double fullPrice;

    public EntranceLogic() {
        // Fetch the base price when the logic class is instantiated
        this.fullPrice = fetchFullPriceFromServer();
    }

    /**
     * Simulates fetching the base full price from the server/DB.
     */
    private double fetchFullPriceFromServer() {
        /*
         * TODO: FUTURE SERVER INTEGRATION
         * 1. Create message: Message msg = new Message(Action.GET_FULL_PRICE);
         * 2. Send to server: ClientUI.clientChat.accept(msg);
         * 3. Wait for response...
         * 4. Return the double value received.
         */
        System.out.println("EntranceLogic: Fetched Full Price from server (Simulated).");
        return 50.0; // Simulated default price
    }

    /**
     * Simulates checking for active promotions approved by the department manager.
     */
    private double checkActivePromotions(String parkId) {
         /*
          * TODO: FUTURE SERVER INTEGRATION
          * Query the DB for active promotions for this park where status = 'Active'.
          */
         return 0.0; // Set to 0.0 for now
    }

    /**
     * Calculates the final price based on visitor type, amount, and booking status.
     * @param visitorType  The visitor type: "Regular", "Subscriber", or "Group"
     * @param amount       The total number of visitors
     * @param isPreBooked  True if the visit was pre-booked, false for casual visitors
     * @return The final calculated price
     */
    public double calculatePrice(String visitorType, int amount, boolean isPreBooked) {
        double finalPrice = 0;
        int payingVisitors = amount;

        switch (visitorType) {
            case "Regular":
                if (isPreBooked) {
                    // Category 1: Pre-booked private visit - 15% discount
                    finalPrice = payingVisitors * fullPrice * 0.85;
                } else {
                    // Category 2: Casual private visit - Full price
                    finalPrice = payingVisitors * fullPrice;
                }
                break;

            case "Group":
                if (isPreBooked) {
                    // Category 3: Pre-booked group - 25% discount, guide enters for free
                    payingVisitors = amount - 1; // Guide is free
                    if (payingVisitors < 0) payingVisitors = 0;
                    
                    finalPrice = payingVisitors * fullPrice * 0.75;
                } else {
                    // Category 4: Casual group - 10% discount, guide pays
                    finalPrice = payingVisitors * fullPrice * 0.90;
                }
                break;

            case "Subscriber":
                // Category 5: Subscribers - calculate based on booking status, then apply 10% discount
                double baseSubscriberPrice = (isPreBooked) ? 
                                             (payingVisitors * fullPrice * 0.85) : // Like pre-booked private
                                             (payingVisitors * fullPrice);         // Like casual private
                
                // Additional 10% subscriber discount
                finalPrice = baseSubscriberPrice * 0.90;
                break;
                
            default:
                finalPrice = payingVisitors * fullPrice; // Default fallback
                break;
        }

        // Check for any active global promotions approved by the department manager
        double currentActivePromotionDiscount = checkActivePromotions("PARK_1");
        
        if (currentActivePromotionDiscount > 0) {
            finalPrice = finalPrice - (finalPrice * currentActivePromotionDiscount);
        }

        return finalPrice;
    }

    // --- Mock methods for previous functionalities ---
    
    public boolean validateOrder(String inputId) {
        if ("12345".equals(inputId)) {
            System.out.println("EntranceLogic: Pre-booked Order " + inputId + " verified.");
            return true;
        }
        System.out.println("EntranceLogic: Order " + inputId + " not found.");
        return false;
    }

    public boolean checkCasualAvailability(int amount) {
        System.out.println("EntranceLogic: Checking space for " + amount + " casual visitors.");
        if (amount > 50) {
            System.out.println("EntranceLogic: Not enough space.");
            return false;
        }
        return true;
    }

    public void confirmPayment() {
        System.out.println("EntranceLogic: Payment processed and entry registered in system.");
    }
}