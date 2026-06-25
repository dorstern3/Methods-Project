package common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a parameter change request from a park manager.
 * Requires approval from the department manager.
 */
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

    /**
     * Constructor for existing requests fetched from the database.
     */
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

    /**
     * Constructor for new requests before saving to the database.
     */
    public ParameterRequest(String parkName, int workerId, String parameterName, int currentValue, int requestedValue) {
        this.parkName = parkName;
        this.workerId = workerId;
        this.parameterName = parameterName;
        this.currentValue = currentValue;
        this.requestedValue = requestedValue;
    }

    /** @return The request ID. */
    public int getRequestId() { return requestId; }
    
    /** @return The park name. */
    public String getParkName() { return parkName; }
    
    /** @return The manager's worker ID. */
    public int getWorkerId() { return workerId; }
    
    /** @return The name of the parameter. */
    public String getParameterName() { return parameterName; }
    
    /** @return The current configuration value. */
    public int getCurrentValue() { return currentValue; }
    
    /** @return The new requested value. */
    public int getRequestedValue() { return requestedValue; }
    
    /** @return The request status (Pending/Approved/Rejected). */
    public String getStatus() { return status; }
    
    /** @return The creation date and time. */
    public Timestamp getRequestDate() { return requestDate; }

    /** @param requestId The request ID to set. */
    public void setRequestId(int requestId) { this.requestId = requestId; }
    
    /** @param parkName The park name to set. */
    public void setParkName(String parkName) { this.parkName = parkName; }
    
    /** @param workerId The worker ID to set. */
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    
    /** @param parameterName The parameter name to set. */
    public void setParameterName(String parameterName) { this.parameterName = parameterName; }
    
    /** @param currentValue The current value to set. */
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
    
    /** @param requestedValue The requested value to set. */
    public void setRequestedValue(int requestedValue) { this.requestedValue = requestedValue; }
    
    /** @param status The status to set. */
    public void setStatus(String status) { this.status = status; }
    
    /** @param requestDate The request date to set. */
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }
}