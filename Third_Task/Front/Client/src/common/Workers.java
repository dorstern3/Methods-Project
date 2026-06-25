package common;

import java.io.Serializable;

/**
 * Represents a System Worker entity in the GoNature system.
 * Implements Serializable to allow network transmission.
 */
public class Workers implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String parkName;

    // Default Constructor
    public Workers() {}

    // Full Constructor
    public Workers(String firstName, String lastName, String email, String role, String parkName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.parkName = parkName;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }
}