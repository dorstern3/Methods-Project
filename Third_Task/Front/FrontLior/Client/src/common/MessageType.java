package common;

/**
 * Enumeration representing all distinctive cross-network application message types.
 * Used by the client-server communication layer to identify request actions and route 
 * responses correctly via serialized message structures.
 */
public enum MessageType {
	GET_FULL_PRICE, GET_FULL_PRICE_RESPONSE,

	CHECK_PROMOTIONS, CHECK_PROMOTIONS_RESPONSE,

	VALIDATE_ORDER, VALIDATE_ORDER_RESPONSE,

	CHECK_CAPACITY, CHECK_CAPACITY_RESPONSE,

	CONFIRM_PAYMENT, CONFIRM_PAYMENT_RESPONSE,

	VERIFY_GUIDE, VERIFY_GUIDE_RESPONSE,

	VERIFY_SUBSCRIBER, VERIFY_SUBSCRIBER_RESPONSE,

	EXIT_PARK, EXIT_PARK_RESPONSE,

	GET_VISITOR_REPORT, GET_VISITOR_REPORT_RESPONSE,

	GET_CANCELLATION_REPORT, GET_CANCELLATION_REPORT_RESPONSE,

	GET_PARKS_CANCELLATION_REPORT, GET_PARKS_CANCELLATION_REPORT_RESPONSE,

	GET_TOTAL_VISITOR_REPORT, GET_TOTAL_VISITOR_REPORT_RESPONSE,

	GET_OCCUPANCY_REPORT, GET_OCCUPANCY_REPORT_RESPONSE,

	GET_PARKS, GET_PARKS_RESPONSE,

	REGISTER_FAMILY_SUBSCRIBER, REGISTER_SINGLE_SUBSCRIBER, REGISTER_GUIDE, REGISTRATION_SUCCESS, REGISTRATION_FAILED,

	SUBMIT_PARAMETER_REQUEST, GET_PENDING_PARAMETER_REQUESTS, UPDATE_PARAMETER_REQUEST_STATUS, REQUEST_SUBMIT_SUCCESS,
	PROMOTION_ACTIVATED_SUCCESS, UPDATE_REQUEST_SUCCESS, GET_PENDING_REQUESTS_RESPONSE, ACTIVATE_PROMOTION,
	PROMOTION_ACTIVATED_FAILED, UPDATE_REQUEST_FAILED, REQUEST_SUBMIT_FAILED, GET_PARK_OCCUPANCY,
	GET_PARK_OCCUPANCY_RESPONSE,

	LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT_REQUEST, LOGOUT_SUCCESS, TRAVELER_LOGOUT, TRAVELER_LOGIN,

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
	UPDATE_ORDER_RESULT,

	/** Request to simulate sending 24-hour reminders for upcoming visits. */
	SIMULATE_24H_REMINDER, 
	
	/** Response containing the list of orders that were sent a reminder. */
	SIMULATE_24H_REMINDER_RESPONSE,

	/** Request to simulate a waitlist timeout (canceling unconfirmed and notifying next in line). */
	SIMULATE_WAITLIST_TIMEOUT, 
	
	/** Response containing the log messages from the waitlist timeout simulation. */
	SIMULATE_WAITLIST_RESPONSE,

	/** Request to simulate a confirmation timeout (canceling unconfirmed orders and checking waitlist). */
	SIMULATE_CONFIRMATION_TIMEOUT, 
	
	/** Response containing the log messages from the confirmation timeout simulation. */
	SIMULATE_CONFIRMATION_RESPONSE,
	
	/** Request to automatically cancel all expired waiting list entries for the current day. */
	CLEAN_WAITING_LIST,
	
	/** Response containing the number of waiting list entries that were successfully canceled. */
	CLEAN_WAITING_LIST_RESULT, UPDATE_SUBSCRIBER_DETAILS_RESPONSE, UPDATE_SUBSCRIBER_DETAILS, 
	
	GET_SUBSCRIBER_DETAILS, 
	GET_SUBSCRIBER_DETAILS_RESPONSE

}