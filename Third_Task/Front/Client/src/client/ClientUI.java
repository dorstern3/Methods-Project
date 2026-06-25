package client;

import java.io.IOException;

import client.logic.ClientController;

/**
 * The main user interface launcher and coordinator for the client side.
 * Holds the static reference to the central communication controller.
 */
public class ClientUI  {

	/** The global controller instance handling client-server network communication. */
	public static ClientController clientChat;
	
	/**
	 * Initializes and sets a new client controller to enable communication with the server.
	 * * @param host the IP address or hostname of the server
	 * @param port the port number the server is listening on
	 */
	public void setClient(String host , int port) {
		try { 
			clientChat = new ClientController(host,port);
		} catch(IOException e) {
			System.out.println("Connection failed!");
			return;
		}
	}
	
}
