package client.logic;

/**
 * Pricing strategy implementation for regular and family visitors.
 * Applies a 15% discount for pre-booked visits, or charges full price for casual visits.
 */
public class RegularPricingStrategy implements PricingStrategy {
    
    /**
     * Calculates the total entrance price for regular or family visitors.
     * * @param visitors    The total number of visitors.
     * @param basePrice   The standard full price for a single ticket.
     * @param isPreBooked true if the tickets were ordered in advance, false if casual drop-in.
     * @return The final calculated price for the group.
     */
    @Override
    public double calculate(int visitors, double basePrice, boolean isPreBooked) {
        if (isPreBooked) {
            // Category 1: Pre-booked private visit - 15% discount
            return visitors * basePrice * 0.85;
        } else {
            // Category 2: Casual private visit - Full price
            return visitors * basePrice;
        }
    }
}