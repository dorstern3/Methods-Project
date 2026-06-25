package common;

import java.io.Serializable;

/**
 * Entity class representing an Order in the GoNature system. Implements
 * Serializable to allow transmission over the network via OCSF.
 */
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private int orderNumber;
	private String parkName;
	private String orderDate;
	private String entryTime;
	private int numberOfVisitors;
	private String id;
	private String email;
	private String phoneNumber;
	private String visitorType;
	private String status;
	private String qrCode;
	private String dateOfPlacingOrder;
	private String exitTime;

	/**
	 * Constructs a new Order with essential fields for a request. * @param parkName
	 * The name of the park.
	 * 
	 * @param orderDate        The date of the visit.
	 * @param entryTime        The entry time.
	 * @param numberOfVisitors The number of people in the group.
	 * @param id               The ID of the person placing the order.
	 * @param email            Contact email.
	 * @param phoneNumber      Contact phone number.
	 * @param visitorType      The type of visitor (e.g., 'Regular', 'Subscriber').
	 * @param status           The current status of the order.
	 */
	public Order(String parkName, String orderDate, String entryTime, int numberOfVisitors, String id, String email,
			String phoneNumber, String visitorType, String status) {
		this.parkName = parkName;
		this.orderDate = orderDate;
		this.entryTime = entryTime;
		this.numberOfVisitors = numberOfVisitors;
		this.id = id;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.visitorType = visitorType;
		this.status = status;
	}

	/**
	 * Constructs a full Order object, typically used when retrieving data from the
	 * database. * @param orderNumber The unique identifier of the order.
	 * 
	 * @param parkName           The name of the park.
	 * @param orderDate          The date of the visit.
	 * @param entryTime          The entry time.
	 * @param numberOfVisitors   The number of visitors.
	 * @param id                 The ID of the order owner.
	 * @param email              Contact email.
	 * @param phoneNumber        Contact phone number.
	 * @param visitorType        The type of visitor.
	 * @param status             The current order status.
	 * @param qrCode             The generated QR code.
	 * @param dateOfPlacingOrder The date the order was created.
	 * @param exitTime           The exit time.
	 */
	public Order(int orderNumber, String parkName, String orderDate, String entryTime, int numberOfVisitors, String id,
			String email, String phoneNumber, String visitorType, String status, String qrCode,
			String dateOfPlacingOrder, String exitTime) {
		this.orderNumber = orderNumber;
		this.parkName = parkName;
		this.orderDate = orderDate;
		this.entryTime = entryTime;
		this.numberOfVisitors = numberOfVisitors;
		this.id = id;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.visitorType = visitorType;
		this.status = status;
		this.qrCode = qrCode;
		this.dateOfPlacingOrder = dateOfPlacingOrder;
		this.exitTime = exitTime;
	}

	// --- Getters and Setters ---

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}

	public int getNumberOfVisitors() {
		return numberOfVisitors;
	}

	public void setNumberOfVisitors(int numberOfVisitors) {
		this.numberOfVisitors = numberOfVisitors;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(String visitorType) {
		this.visitorType = visitorType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getDateOfPlacingOrder() {
		return dateOfPlacingOrder;
	}

	public void setDateOfPlacingOrder(String dateOfPlacingOrder) {
		this.dateOfPlacingOrder = dateOfPlacingOrder;
	}

	public String getExitTime() {
		return exitTime;
	}

	public void setExitTime(String exitTime) {
		this.exitTime = exitTime;
	}
}