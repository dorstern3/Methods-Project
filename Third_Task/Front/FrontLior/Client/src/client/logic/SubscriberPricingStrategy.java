package client.logic;

/**
 * Pricing strategy for park subscribers.
 */
public class SubscriberPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(int visitors, double basePrice, boolean isPreBooked) {
        // Category 5: Subscribers - calculate based on booking status
        double baseSubscriberPrice = (isPreBooked) ? 
                                     (visitors * basePrice * 0.85) : 
                                     (visitors * basePrice);
        
        // Additional 10% subscriber discount applied on top
        return baseSubscriberPrice * 0.90;
    }
}