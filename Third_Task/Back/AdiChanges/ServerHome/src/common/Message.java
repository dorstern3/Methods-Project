package common;

import java.io.Serializable;

/**
 * A generic message class used for communication between the Client and the
 * Server. Implements Serializable to allow transmission over the network via
 * OCSF.
 */
public class Message implements Serializable {

	/**
	 * Serial version UID for maintaining class compatibility during serialization.
	 */
	private static final long serialVersionUID = 1L;

	private MessageType type;
	private Object data;

	/**
	 * Constructs a new Message instance. * @param type The action type defined in
	 * the MessageType enum.
	 * 
	 * @param data The payload data (e.g., Order, String, ArrayList), or null if
	 *             none.
	 */
	public Message(MessageType type, Object data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Retrieves the message action type. * @return The MessageType associated with
	 * this message.
	 */
	public MessageType getType() {
		return type;
	}

	/**
	 * Retrieves the data payload attached to this message. * @return The data
	 * object, or null if no data is attached.
	 */
	public Object getData() {
		return data;
	}
}