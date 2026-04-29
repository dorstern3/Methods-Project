package common;

import java.io.Serializable;

public class Order implements Serializable {
    
    // Unique identifier for OCSF serialization to prevent version mismatches
    private static final long serialVersionUID = 1L;
    
    private int orderNumber;
    private String orderDate;
    private int numberOfVisitors;
    private int confirmationCode;
    private int subscriberId;
    private String dateOfPlacingOrder;

    // Default constructor - essential for data transfer and certain frameworks
    public Order() {
    }

    // Parameterized constructor for easy object creation
    public Order(int orderNumber, String orderDate, int numberOfVisitors, int confirmationCode, int subscriberId, String dateOfPlacingOrder) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.numberOfVisitors = numberOfVisitors;
        this.confirmationCode = confirmationCode;
        this.subscriberId = subscriberId;
        this.dateOfPlacingOrder = dateOfPlacingOrder;
    }

    // Getters and Setters to access and modify the fields from the Controller
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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

    public String getDateOfPlacingOrder() {
        return dateOfPlacingOrder;
    }

    public void setDateOfPlacingOrder(String dateOfPlacingOrder) {
        this.dateOfPlacingOrder = dateOfPlacingOrder;
    }
    
    // Helper method to print the order details to the console for debugging
    @Override
    public String toString() {
        return "Order [orderNumber=" + orderNumber + ", orderDate=" + orderDate + 
               ", numberOfVisitors=" + numberOfVisitors + "]";
    }
}