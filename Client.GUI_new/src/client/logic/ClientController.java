package client.logic;

import java.io.IOException;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Order;
import ocsf.client.AbstractClient;

public class ClientController extends AbstractClient{
	
	private OrderLogic orderLogic = OrderLogic.getInstance();
	
	// Set the connection to the server
	public ClientController(String host, int port) throws IOException{
		super(host, port);
	}

	
	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("[SERVER]: Sending response back to Logic");
		orderLogic.setResponseFromServer(msg);									// Send the response back to logic level
	}
	
	// Send a request to the server
	public void accept(Object msg) {
		try {
			System.out.println("[CLIENT] Sending request to Test Server...");
			sendToServer(msg);													// Send the message to the Server
		} catch(IOException e){
			System.out.println("could not send a message to server");
		}
		
	}
	

}
