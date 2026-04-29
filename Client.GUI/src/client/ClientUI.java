package client;

import client.logic.ClientController;

public class ClientUI  {

	public static ClientController clientChat;
	
	// Set a new client to talk to the server
	public void setClient(String host , int port) {
		clientChat = new ClientController(host,port);
	}
	
}
