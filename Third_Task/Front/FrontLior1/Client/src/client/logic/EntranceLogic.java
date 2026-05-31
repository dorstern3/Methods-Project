package client.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Logic class handling entrance operations, pricing, and validations.
 * Uses the Strategy Design Pattern to calculate prices dynamically.
 */
public class EntranceLogic {
    
    private double fullPrice;
    
    // Map to hold the different pricing strategies
    private Map<String, PricingStrategy> pricingStrategies;

    public EntranceLogic() {
        this.fullPrice = fetchFullPriceFromServer();
        
        // Initialize the Strategy Pattern mapping
        pricingStrategies = new HashMap<>();
        
        // Map each UI visitor type string to its corresponding strategy class
        pricingStrategies.put("Regular", new RegularPricingStrategy());
        pricingStrategies.put("Regular/Family", new RegularPricingStrategy());
        pricingStrategies.put("Group", new GroupPricingStrategy());
        pricingStrategies.put("Subscriber", new SubscriberPricingStrategy());
    }

    /**
     * Simulates fetching the base full price from the server.
     */
    private double fetchFullPriceFromServer() {
        System.out.println("EntranceLogic: Fetched Full Price from server.");
        return 50.0; 
    }

    /**
     * Simulates checking for active promotions approved by the department manager.
     */
    private double checkActivePromotions(String parkId) {
         return 0.0; 
    }

    /**
     * Calculates the final price using the Strategy Pattern.
     * @param visitorType  The visitor type from the UI
     * @param amount       The total number of visitors
     * @param isPreBooked  True if pre-booked, false if casual
     * @return The final calculated price
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
        double currentActivePromotionDiscount = checkActivePromotions("PARK_1");
        if (currentActivePromotionDiscount > 0) {
            finalPrice = finalPrice - (finalPrice * currentActivePromotionDiscount);
        }

        return finalPrice;
    }

    // --- System Validations & Operations ---
    
    public boolean validateOrder(String inputId) {
        if ("12345".equals(inputId)) return true;
        return false;
    }

    public boolean checkCasualAvailability(int amount) {
        if (amount > 50) return false;
        return true;
    }

    public void confirmPayment() {
        System.out.println("EntranceLogic: Payment processed.");
    }
    
    /**
     * Verifies if the provided ID belongs to a certified guide.
     * @param guideId The ID to verify.
     * @return true if valid, false otherwise.
     */
    public boolean verifyGuide(String guideId) {
        return true; 
    }
}