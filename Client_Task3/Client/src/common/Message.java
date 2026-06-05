package common;

import java.io.Serializable;

/**
 * A generic message object used for communication between the client and the server.
 */
public class Message implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private MessageType messageType; // סוג הפקודה
    private Object messageData;      // הנתונים המצורפים (ת.ז., אובייקט הזמנה, וכו')

    /**
     * Constructor for a message with data.
     * 
     * @param messageType the type of the message
     * @param messageData the data to send
     */
    public Message(MessageType messageType, Object messageData) {
        this.messageType = messageType;
        this.messageData = messageData;
    }

    /**
     * Constructor for a message without data.
     * 
     * @param messageType the type of the message
     */
    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.messageData = null;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Object getMessageData() {
        return messageData;
    }
}