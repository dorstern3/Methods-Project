package client.logic;

/**
 * Pricing strategy implementation for organized groups.
 * Applies specific business rules and discounts based on whether the group's visit 
 * was pre-booked or is a casual drop-in.
 *  Business Rules:
 * - Pre-booked Group: Receives a 25% discount on the base ticket price, and the group guide enters for free.
 * - Casual Group: Receives a 10% discount on the base ticket price, and the group guide must pay.
 */
public class GroupPricingStrategy implements PricingStrategy {
    
    /**
     * Calculates the final entrance price for an organized group.
     * @param visitors    The total number of people in the group (including the guide).
     * @param basePrice   The standard full price for a single ticket.
     * @param isPreBooked A boolean flag indicating if the group ordered their tickets in advance (true) or arrived casually (false).
     * @return The total calculated price for the entire group as a double.
     */
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