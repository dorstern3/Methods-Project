package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class ParameterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int requestId;
    private String parkName;
    private int workerId;
    private String parameterName; 
    private int currentValue;
    private int requestedValue;
    private String status; 
    private Timestamp requestDate;

    // Fully initializes a parameter request with all database fields
    public ParameterRequest(int requestId, String parkName, int workerId, String parameterName, 
                            int currentValue, int requestedValue, String status, Timestamp requestDate) {
        this.requestId = requestId;
        this.parkName = parkName;
        this.workerId = workerId;
        this.parameterName = parameterName;
        this.currentValue = currentValue;
        this.requestedValue = requestedValue;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Initializes a new parameter request with core fields before database insertion
    public ParameterRequest(String parkName, int workerId, String parameterName, int currentValue, int requestedValue) {
        this.parkName = parkName;
        this.workerId = workerId;
        this.parameterName = parameterName;
        this.currentValue = currentValue;
        this.requestedValue = requestedValue;
    }

    // Returns the unique request identifier
    public int getRequestId() { return requestId; }
    
    // Returns the name of the associated park
    public String getParkName() { return parkName; }
    
    // Returns the identifier of the worker who made the request
    public int getWorkerId() { return workerId; }
    
    // Returns the name of the parameter to be changed
    public String getParameterName() { return parameterName; }
    
    // Returns the current value of the parameter
    public int getCurrentValue() { return currentValue; }
    
    // Returns the newly requested value for the parameter
    public int getRequestedValue() { return requestedValue; }
    
    // Returns the approval status of the request
    public String getStatus() { return status; }
    
    // Returns the timestamp when the request was created
    public Timestamp getRequestDate() { return requestDate; }

    // Sets the unique request identifier
    public void setRequestId(int requestId) { this.requestId = requestId; }
    
    // Sets the name of the associated park
    public void setParkName(String parkName) { this.parkName = parkName; }
    
    // Sets the identifier of the worker making the request
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    // Sets the name of the parameter to be changed
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
    
    // Sets the current value of the parameter
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
    
    // Sets the newly requested value for the parameter
    public void setRequestedValue(int requestedValue) { this.requestedValue = requestedValue; }
    
    // Sets the approval status of the request
    public void setStatus(String status) { this.status = status; }
    
    // Sets the timestamp when the request was created
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }
}