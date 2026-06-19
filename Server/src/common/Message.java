package common;

import java.io.Serializable;

/**
 * A generic message class for communication between Client and Server.
 * Must implement Serializable to be sent over the network via OCSF.
 */
public class Message implements Serializable {
    
    // Serial version UID for ensuring class compatibility during serialization
    private static final long serialVersionUID = 1L;
    
    private MessageType type; // The type of the action (e.g., VALIDATE_ORDER)
    private Object data;      // The actual data being sent (String, int, Order, ArrayList, etc.)

    /**
     * Constructor for creating a new Message.
     * @param type The action type from the MessageType enum.
     * @param data The data object to send. Can be null if no data is needed.
     */
    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * @return The type of the message.
     */
    public MessageType getType() { 
        return type; 
    }
    
    /**
     * @return The data attached to the message.
     */
    public Object getData() { 
        return data; 
    }
}