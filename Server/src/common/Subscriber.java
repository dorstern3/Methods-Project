package common;

import java.io.Serializable;

public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subscriberId; // שינוי ל-int כדי להתאים ל-Order שלו
    private String firstName;
    private String lastName;

    public Subscriber(int subscriberId, String firstName, String lastName) {
        this.subscriberId = subscriberId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters בסגנון שלו
    public int getSubscriberId() { return subscriberId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}