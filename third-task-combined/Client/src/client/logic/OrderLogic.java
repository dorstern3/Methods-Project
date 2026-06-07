package client.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import client.ClientUI;
import common.Order;

public class OrderLogic {
	
	public OrderLogic() {}
	
	
	// Send an order object to be updated
	public String sendOrderUpdate(Order order) {
		Object response = ClientUI.clientChat.accept(order); 										// 	Send the order to update to the server
		return (String) response;
	}
	
	// Call for all the orders from the server
	public ArrayList<Order> getAllOrders(){
		Object response = ClientUI.clientChat.accept("101");										// Ask the server for all the order of subscriber 123
		return (ArrayList<Order>) response;
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
