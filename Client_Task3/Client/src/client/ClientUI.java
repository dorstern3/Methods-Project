package client;

import java.io.IOException;

import client.logic.ClientController;

public class ClientUI  {

	public static ClientController clientChat;
	
	// Set a new client to talk to the server
	public void setClient(String host , int port) {
		try { 
			clientChat = new ClientController(host,port);
		} catch(IOException e) {
			System.out.println("Connection failed!");
			return;
		}
	}
	
}
