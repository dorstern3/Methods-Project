package common;

import java.io.Serializable;

/**
 * A generic message object used for communication between the client and the
 * server.
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private MessageType messageType;
	private Object messageData;

	/**
	 * Constructor for a message with data. * @param messageType The type of the
	 * message.
	 * 
	 * @param messageData The data attached to the message.
	 */
	public Message(MessageType messageType, Object messageData) {
		this.messageType = messageType;
		this.messageData = messageData;
	}

	/**
	 * Constructor for a message without data. * @param messageType The type of the
	 * message.
	 */
	public Message(MessageType messageType) {
		this.messageType = messageType;
		this.messageData = null;
	}

	/**
	 * Gets the type of the message. * @return The message type.
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Gets the data attached to the message. * @return The message data, or null if
	 * no data is attached.
	 */
	public Object getMessageData() {
		return messageData;
	}
}