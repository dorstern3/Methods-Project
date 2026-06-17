package client.logic;

/**
 * Pricing strategy for organized groups.
 */
public class GroupPricingStrategy implements PricingStrategy {
    @Override
    public double calculate(int visitors, double basePrice, boolean isPreBooked) {
        int payingVisitors = visitors;
        
        if (isPreBooked) {
            // Category 3: Pre-booked group - 25% discount, guide enters for free
            payingVisitors = visitors - 1; 
            if (payingVisitors < 0) payingVisitors = 0;
            return payingVisitors * basePrice * 0.75;
        } else {
            // Category 4: Casual group - 10% discount, guide pays
            return payingVisitors * basePrice * 0.90;
        }
    }
}