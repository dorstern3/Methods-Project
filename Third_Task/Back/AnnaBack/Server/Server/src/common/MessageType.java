package common;

/**
 * Defines the types of messages sent between the client and server. Each enum
 * value corresponds to a specific action or response in the system.
 */
public enum MessageType {
	/** Request to identify a traveler by ID. */
	IDENTIFY_TRAVELER,
	/** Response containing traveler details. */
	IDENTIFY_TRAVELER_RESPONSE,
	/** Request to check park capacity availability. */
	CHECK_AVAILABILITY,
	/** Result of a capacity availability check. */
	CHECK_AVAILABILITY_RESULT,
	/** Request to join a park's waiting list. */
	ENTER_WAITING_LIST,
	/** Result of the attempt to join the waiting list. */
	ENTER_WAITING_LIST_RESULT,
	/** Request to save a new order in the database. */
	SAVE_NEW_ORDER,
	/** Indicates a successful save operation. */
	SAVE_SUCCESS,
	/** Indicates a general error occurred. */
	ERROR,
	/** Request for alternative travel dates. */
	GET_ALTERNATIVE_DATES,
	/** Result containing a list of alternative dates. */
	GET_ALTERNATIVE_DATES_RESULT,
	/** Request to fetch details for a specific order. */
	FETCH_ORDER_DETAILS,
	/** Result containing specific order details. */
	FETCH_ORDER_RESULT,
	/** Request to update the status of an order. */
	UPDATE_ORDER_STATUS,
	/** Result of an order status update. */
	UPDATE_ORDER_RESULT
}