package client.logic;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Order;
import ocsf.client.AbstractClient;

public class ClientController extends AbstractClient{
	
	public ArrayList<Order> OrderDB = new ArrayList<>(); 	// Test DB
	private OrderLogic orderLogic = OrderLogic.getInstance();
	
	// Set the connection to the server
	public ClientController(String host, int port) {
		super(host, port);
		OrderDB.add(new Order(101 , new Date() , 5 , 1111 , 10 , new Date()));
		OrderDB.add(new Order(102 , new Date() , 3 , 2222 , 12 , new Date()));
	}

	
	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("[TEST SERVER] Test Server: Sending response back to Logic");
		orderLogic.setResponseFromServer(msg);									// Send the response back to logic level
	}
	
	// Send a request to the server
	public void accept(Object msg) {
		/*try {
			sendToServer(msg);													// Send the message to the Server
		} catch(IOException e){
			System.out.println("could not send a message to server");
		}*/
		System.out.println("[TEST SERVER] Client: Sending request to Test Server...");
		testServer(msg);														// Send the message to the Test Server
	}
	
	
	// Test server
	private void testServer(Object msg) {
		if(msg instanceof String) {
			if (msg.equals("GET_ALL_ORDERS")) {
				handleMessageFromServer(OrderDB);
			}
		}
		else{
			Order orderToUpdate = (Order) msg;
			if (OrderDB.get(0).getOrderNumber() == orderToUpdate.getOrderNumber()) {
				OrderDB.get(0).setNumberOfVisitors(orderToUpdate.getNumberOfVisitors());
				OrderDB.get(0).setDateOfPlacingOrder(orderToUpdate.getOrderDate());
				handleMessageFromServer("Succsess");
			}
			else if (OrderDB.get(1).getOrderNumber() == orderToUpdate.getOrderNumber()) {
				OrderDB.get(1).setNumberOfVisitors(orderToUpdate.getNumberOfVisitors());
				OrderDB.get(1).setDateOfPlacingOrder(orderToUpdate.getOrderDate());
				handleMessageFromServer("Succsess");
			}
			else {
				handleMessageFromServer("Failed to Update");
			}
		}
	}
}
