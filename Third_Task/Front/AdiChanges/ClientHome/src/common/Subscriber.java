package common;

import java.io.Serializable;

/**
 * Represents a Subscriber entity in the GoNature system.
 * Implements Serializable to allow network transmission.
 */
public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;

    private int subNum;
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private String card;
    private int familyMembers;

    // Default Constructor
    public Subscriber() {}

    // Full Constructor
    public Subscriber(int subNum, String fname, String lName, String email, String phone, String card, int familyMembers) {
        this.subNum = subNum;
        this.fname = fname;
        this.lname = lName;
        this.email = email;
        this.phone = phone;
        this.card = card;
        this.familyMembers = familyMembers;
    }

    // Getters and Setters
    public int getSubNum() { return subNum; }
    public void setSubNum(int subNum) { this.subNum = subNum; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLName(String lName) { this.lname = lName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCard() { return card; }
    public void setCard(String card) { this.card = card; }

    public int getFamilyMembers() { return familyMembers; }
    public void setFamilyMembers(int familyMembers) { this.familyMembers = familyMembers; }
}