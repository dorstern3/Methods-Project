package client.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import client.ClientUI;
import common.Order;

public class OrderLogic {
	
	// Singleton
	private static OrderLogic instance;
	private boolean isDataReady = false;
	private Object response;
	
	private OrderLogic() {}
	
	public static OrderLogic getInstance() {
		if(instance == null ) {
			instance = new OrderLogic();
		}
		return instance;
	}
	
	
	// Send an order object to be updated
	public  synchronized String sendOrderUpdate(Order order) {
		isDataReady = false;
		ClientUI.clientChat.accept(order); 										// 	Send the order to update to the server
		waitForServer();
		return (String) response;
	}
	
	// Call for all the orders from the server
	public synchronized ArrayList<Order> getAllOrders(){
		isDataReady = false;
		ClientUI.clientChat.accept("123");										// Ask the server for all the order of subscriber 123
		waitForServer();
		return (ArrayList<Order>) response;
	}
	
	// Helper function to set the response and let the function that calls the server stop the waiting
	public synchronized void setResponseFromServer(Object response) {
		this.response = response;
		isDataReady = true;
		notify();
	}
	
	private void waitForServer() {
		try {
			while(!isDataReady) { wait(); }
		} catch (InterruptedException e) { e.printStackTrace();}
	}
	
	
	// Disconnect the client from server
	public void disconnect() {
		try {
			ClientUI.clientChat.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
