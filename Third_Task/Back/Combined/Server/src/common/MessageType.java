package common;

/**
 * Enumeration representing all distinctive cross-network application message types.
 * Used by the client-server communication layer to identify request actions and route 
 * responses correctly via serialized message structures.
 */
public enum MessageType {
	/** Request to fetch the base full ticket price for a specific park. */
	GET_FULL_PRICE, 
	/** Response containing the fetched full ticket price data. */
	GET_FULL_PRICE_RESPONSE,

	/** Request to check for any active promotional discounts for a park. */
	CHECK_PROMOTIONS, 
	/** Response containing the active promotional discount rate value. */
	CHECK_PROMOTIONS_RESPONSE,

	/** Request to validate a pre-booked order using an ID or QR code. */
	VALIDATE_ORDER, 
	/** Response containing validated order details or error code configurations. */
	VALIDATE_ORDER_RESPONSE,

	/** Request to evaluate park capacity for admitting casual drop-in visitors. */
	CHECK_CAPACITY, 
	/** Response indicating whether there is sufficient available capacity. */
	CHECK_CAPACITY_RESPONSE,

	/** Request to confirm a payment transaction and register a visitor entry. */
	CONFIRM_PAYMENT, 
	/** Response containing the database tracking order key identifier string. */
	CONFIRM_PAYMENT_RESPONSE,

	/** Request to verify if a given ID belongs to a certified group guide. */
	VERIFY_GUIDE, 
	/** Response indicating whether the group guide is valid and certified. */
	VERIFY_GUIDE_RESPONSE,

	/** Request to verify if a given subscriber number exists in the system. */
	VERIFY_SUBSCRIBER, 
	/** Response containing subscriber verification status and family limits. */
	VERIFY_SUBSCRIBER_RESPONSE,

	/** Request to register a visitor's departure exit from a park. */
	EXIT_PARK, 
	/** Response confirming whether the departure exit was successfully processed. */
	EXIT_PARK_RESPONSE,

	/** Request to generate an average stay duration report for a specific park. */
	GET_VISITOR_REPORT, 
	/** Response containing data blocks tracking average stay durations. */
	GET_VISITOR_REPORT_RESPONSE,

	/** Request to generate a cancellation and no-show metrics report for a park. */
	GET_CANCELLATION_REPORT, 
	/** Response containing the aggregated daily cancellation report data rows. */
	GET_CANCELLATION_REPORT_RESPONSE,

	/** Request by the department manager to fetch global cancellation statistics across all parks. */
	GET_PARKS_CANCELLATION_REPORT, 
	/** Response containing global cancellation report matrices segmented by individual parks. */
	GET_PARKS_CANCELLATION_REPORT_RESPONSE,

	/** Request to generate a daily total visitors summary report for a park. */
	GET_TOTAL_VISITOR_REPORT, 
	/** Response containing the segmented daily total visitor headcount report rows. */
	GET_TOTAL_VISITOR_REPORT_RESPONSE,

	/** Request to generate an occupancy report mapping days when a park was not full. */
	GET_OCCUPANCY_REPORT, 
	/** Response containing daily park occupancy levels and capacity percentages. */
	GET_OCCUPANCY_REPORT_RESPONSE,

	/** Request to retrieve the list of all registered park names from the database. */
	GET_PARKS, 
	/** Response containing the collection list of all registered park name strings. */
	GET_PARKS_RESPONSE,

	/** Request to register a new family subscriber classification row in the system. */
	REGISTER_FAMILY_SUBSCRIBER, 
	/** Request to register a new single subscriber classification row in the system. */
	REGISTER_SINGLE_SUBSCRIBER, 
	/** Request to register a new certified group guide profile row in the system. */
	REGISTER_GUIDE, 
	/** Indicates that a subscriber or guide registration operation succeeded. */
	REGISTRATION_SUCCESS, 
	/** Indicates that a subscriber or guide registration operation failed. */
	REGISTRATION_FAILED,

	/** Request submitted by a park manager to alter system capacity or structural settings. */
	SUBMIT_PARAMETER_REQUEST, 
	/** Request by a department manager to retrieve all open parameter modifications. */
	GET_PENDING_PARAMETER_REQUESTS, 
	/** Request to approve or reject a pending parameter configuration adjustment. */
	UPDATE_PARAMETER_REQUEST_STATUS, 
	/** Indicates that a new parameter modification request was successfully stored. */
	REQUEST_SUBMIT_SUCCESS,
	/** Indicates that a promotional markdown discount was successfully applied. */
	PROMOTION_ACTIVATED_SUCCESS, 
	/** Indicates that a parameter adjustment status transaction was committed. */
	UPDATE_REQUEST_SUCCESS, 
	/** Response containing the collection list of all currently pending parameter requests. */
	GET_PENDING_REQUESTS_RESPONSE, 
	/** Request submitted by a park manager to deploy an active promotional markdown. */
	ACTIVATE_PROMOTION,
	/** Indicates that a promotional markdown deployment failed to commit. */
	PROMOTION_ACTIVATED_FAILED, 
	/** Indicates that a parameter status modification transaction failed. */
	UPDATE_REQUEST_FAILED, 
	/** Indicates that a parameter request submission failed. */
	REQUEST_SUBMIT_FAILED, 
	/** Request to retrieve the dynamic real-time occupancy and maximum capacity of a park. */
	GET_PARK_OCCUPANCY,
	/** Response containing the live occupancy headcounts and maximum capacity numbers. */
	GET_PARK_OCCUPANCY_RESPONSE,

	/** Request to authenticate an employee attempting to access a dashboard view. */
	LOGIN_REQUEST, 
	/** Indicates successful worker credential validation, returning profile data blocks. */
	LOGIN_SUCCESS, 
	/** Indicates failed credential validation or a duplicate login session collision. */
	LOGIN_FAILED, 
	/** Request to terminate an active employee session context cleanly. */
	LOGOUT_REQUEST, 
	/** Response confirming that an employee session has been cleared orderly. */
	LOGOUT_SUCCESS, 
	/** Request to terminate an active traveler session context cleanly. */
	TRAVELER_LOGOUT, 
	/** Request to authenticate and initialize a traveler session context. */
	TRAVELER_LOGIN,

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
	CLEAN_WAITING_LIST_RESULT, 
	
	/** Response confirming whether subscriber data modifications were committed. */
	UPDATE_SUBSCRIBER_DETAILS_RESPONSE, 
	
	/** Request to modify profile parameters for an active subscriber record. */
	UPDATE_SUBSCRIBER_DETAILS, 
	
	/** Request to fetch the active editable profile parameters of a subscriber. */
	GET_SUBSCRIBER_DETAILS, 
	
	/** Response containing the compiled profile details list for a subscriber. */
	GET_SUBSCRIBER_DETAILS_RESPONSE,
	
	/** Request to check if a traveler has any manageable pending or upcoming orders. */
	CHECK_ORDER_EXISTENCE, 
	
	/** Response indicating whether any manageable active orders exist for the traveler. */
	CHECK_ORDER_RESPONSE,
	
	/** Request to check if a traveler is currently inside a park and has not yet exited. */
	CHECK_ORDER_EXISTENCE_FOR_EXIT, 
	
	/** Response confirming whether an active 'Entered' order status exists for the traveler. */
	CHECK_ORDER_RESPONSE_FOR_EXIT,

	/** Request to fetch all subscriber rows logged within the database tables. */
	GET_SUBSCRIBERS_LIST,
	
	/** Response containing the comprehensive collection list of all subscriber entities. */
	GET_SUBSCRIBERS_LIST_RESPONSE,
	
	/** Request to fetch all worker rows logged within the database tables. */
	GET_WORKERS_LIST,
	
	/** Response containing the comprehensive collection list of all worker entities. */
	GET_WORKERS_LIST_RESPONSE,
}