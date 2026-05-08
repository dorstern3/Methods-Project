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
		System.out.println("[LOGIC] Sending Order to client");
		ClientUI.clientChat.accept(order); 										// 	Send the order to update to the server
		waitForServer();
		return (String) response;
	}
	
	public synchronized ArrayList<Order> getAllOrders(){
		isDataReady = false;
		System.out.println("[LOGIC] Sending message to client");
		ClientUI.clientChat.accept("123");										// Ask the server for all the orders
		waitForServer();
		return (ArrayList<Order>) response;
	}
	
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
	
	public void disconnect() {
		try {
			System.out.println("[Logic] Close connection");
			ClientUI.clientChat.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
