package client.logic;

import java.io.IOException;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Order;
import javafx.application.Platform;
import ocsf.client.AbstractClient;

public class ClientController extends AbstractClient{
	
	private OrderLogic orderLogic = OrderLogic.getInstance();
	
	// Set the connection to the server
	public ClientController(String host, int port) throws IOException{
		super(host, port);
	}

	
	@Override
	// Send the response back to logic level
	protected void handleMessageFromServer(Object msg) {
		orderLogic.setResponseFromServer(msg);
	}
	
	// Send a request to the server
	public void accept(Object msg) {
		try {
			sendToServer(msg);
		} catch(IOException e){
			System.out.println("Could not send a message to server");
		}
		
	}
	
	@Override
	// Exit and close application when server is down
	protected void connectionClosed() {
		Platform.exit();
		System.exit(0);
	}
	
	@Override
	// Exit and close application when server is down unexpectedly
	protected void connectionException(Exception exception) {
	    Platform.exit();
	    System.exit(0); 
	}
}
