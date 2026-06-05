package common;

import java.io.Serializable;

public class ParameterRequestMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // Defines the type of operation carried out by the message
    public enum OpCode {
        SUBMIT_REQUEST,      
        FETCH_PENDING,       
        UPDATE_STATUS        
    }

    private OpCode opCode;
    private Object data; 

    // Initializes the message with a specific operation code and data payload
    public ParameterRequestMessage(OpCode opCode, Object data) {
        this.opCode = opCode;
        this.data = data;
    }

    // Returns the operation code of the message
    public OpCode getOpCode() { return opCode; }
    
    // Returns the data payload contained within the message
    public Object getData() { return data; }
}