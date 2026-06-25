package client.logic;

import java.io.IOException;
import javafx.application.Platform;
import ocsf.client.AbstractClient;

/**
 * Controller class managing the underlying network connection to the server.
 * Extends AbstractClient from the OCSF framework to implement a synchronous,
 * thread-safe request-response communication model.
 */
public class ClientController extends AbstractClient {
	
	private Object lastResponse = null;
    private boolean isDataReady = false;
	
	/**
	 * Constructs a new ClientController and initializes the socket connection.
	 * * @param host        The server's IP address or hostname.
	 * @param port        The active network port number of the server.
	 * @throws IOException If an I/O error occurs when opening the socket channel.
	 */
	public ClientController(String host, int port) throws IOException {
		super(host, port);
	}

	/**
	 * Callback automatically invoked by the OCSF architecture when a new network 
	 * payload is received from the server. Stores the message and awakens the blocked thread.
	 * * @param msg The incoming message object dispatched by the server.
	 */
	@Override
	protected synchronized void handleMessageFromServer(Object msg) {
		this.lastResponse = msg; 
        this.isDataReady = true;      
        notify(); // Awakens the calling thread waiting inside the accept method
	}
	
	/**
	 * Dispatches a request payload to the server synchronously.
	 * Blocks the calling execution thread until a matching server response is received.
	 * * @param msg The structural request message object being sent to the server.
	 * @return    The corresponding evaluation response object received from the server.
	 */
	public synchronized Object accept(Object msg) {
		isDataReady = false;
        lastResponse = null;
		try {
			sendToServer(msg);
			// Block until handleMessageFromServer toggles the data status flag
			while (!isDataReady) { 
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
		} catch (IOException e) {
			System.out.println("Could not send a message to server");
			isDataReady = true;
			notify(); // Prevents the application thread from hanging indefinitely on network failure
		}
		return lastResponse;
	}
	
	/**
	 * Cleanly shuts down and terminates the application execution context when 
	 * the server connection is closed in an orderly fashion.
	 */
	@Override
	protected void connectionClosed() {
		Platform.exit();
		System.exit(0);
	}
	
	/**
	 * Cleanly shuts down and terminates the application execution context when 
	 * an unexpected connection exception drops the active network session.
	 * * @param exception The intercepted network exception context causing the termination.
	 */
	@Override
	protected void connectionException(Exception exception) {
	    Platform.exit();
	    System.exit(0); 
	}
}