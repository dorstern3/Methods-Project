package client.logic;

import java.io.IOException;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Order;
import javafx.application.Platform;
import ocsf.client.AbstractClient;

public class ClientController extends AbstractClient{
	
	private Object lastResponse = null;
    private boolean isDataReady = false;
	
	// Set the connection to the server
	public ClientController(String host, int port) throws IOException{
		super(host, port);
	}

	 
	@Override
	// Send the response back to logic level
	protected synchronized void handleMessageFromServer(Object msg) {
		this.lastResponse = msg; 
        this.isDataReady = true;      
        notify();
	}
	
	// Send a request to the server
	public synchronized Object accept(Object msg) {
		isDataReady = false;
        lastResponse = null;
		try {
			sendToServer(msg);
			while (!isDataReady) { 
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            }
		} catch(IOException e){
			System.out.println("Could not send a message to server");
			isDataReady = true;
			notify();
		}
		return lastResponse;
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
