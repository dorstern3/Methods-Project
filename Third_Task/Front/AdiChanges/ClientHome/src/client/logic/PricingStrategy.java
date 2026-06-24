package client.logic;

/**
 * Strategy interface for calculating entrance prices.
 */
public interface PricingStrategy {
    /**
     * Calculates the price for a specific visitor type.
     * @param visitors    Number of visitors
     * @param basePrice   The full price for a single ticket
     * @param isPreBooked Whether the visit was pre-booked
     * @return The calculated price before global promotions
     */
    double calculate(int visitors, double basePrice, boolean isPreBooked);
}