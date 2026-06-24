package client.logic;

/**
 * Pricing strategy for regular and family visitors.
 */
public class RegularPricingStrategy implements PricingStrategy {
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