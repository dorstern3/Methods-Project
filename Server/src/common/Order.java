package common;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable{
	
	private int orderNumber;
	private Date orderDate;
	private int numberOfVisitors;
	private int confirmationCode;
	private int subscriberId;
	private Date dateOfPlacingOrder;
	
	public Order(int orderNumber, Date orderDate, int numberOfVisitors, int confirmationCode, int subscriberId, Date dateOfPlacingOrder) {
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.numberOfVisitors = numberOfVisitors;
		this.confirmationCode = confirmationCode;
		this.subscriberId = subscriberId;
		this.dateOfPlacingOrder = dateOfPlacingOrder;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public int getNumberOfVisitors() {
		return numberOfVisitors;
	}

	public void setNumberOfVisitors(int numberOfVisitors) {
		this.numberOfVisitors = numberOfVisitors;
	}

	public int getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(int confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public int getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	public Date getDateOfPlacingOrder() {
		return dateOfPlacingOrder;
	}

	public void setDateOfPlacingOrder(Date dateOfPlacingOrder) {
		this.dateOfPlacingOrder = dateOfPlacingOrder;
	}
	
	
}