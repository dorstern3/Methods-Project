package client;

import java.io.IOException;
import client.logic.ClientController;

/**
 * Provides a static access point to the client controller for the entire
 * application.
 */
public class ClientUI {

	/** The shared client controller instance. */
	public static ClientController clientChat;

	/**
	 * Initializes a new client controller to communicate with the server.
	 * 
	 * @param host The server's IP address or hostname.
	 * @param port The server's port number.
	 */
	public void setClient(String host, int port) {
		try {
			clientChat = new ClientController(host, port);
		} catch (IOException e) {
			System.out.println("Connection failed!");
			return;
		}
	}
}