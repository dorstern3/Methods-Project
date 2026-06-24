package client.logic;

/**
 * Pricing strategy implementation for park subscribers.
 * Applies a 10% subscriber discount on top of the standard or pre-booked price.
 */
public class SubscriberPricingStrategy implements PricingStrategy {
    
    /**
     * Calculates the total entrance price for subscribers.
     * * @param visitors    The total number of visitors.
     * @param basePrice   The standard full price for a single ticket.
     * @param isPreBooked true if the tickets were ordered in advance, false if casual drop-in.
     * @return The final calculated price including the subscriber discount.
     */
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