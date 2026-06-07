package client.logic;

/**
 * Global session class to hold the currently authenticated user.
 * Built as a simple static utility class for ease of use across screens.
 * @author Adi
 */
public class CurUser {
    
    private static int employeeId;
    private static String firstName;
    private static String lastName;
    private static String email;
    private static String role;
    private static String parkName;
    private static boolean isLoggedIn = false;
    
    /**
     * Initializes the static user session fields upon successful login.
     */
    public static void login(int id, String fName, String lName, String mail, String userRole, String park) {
        employeeId = id;
        firstName = fName;
        lastName = lName;
        email = mail;
        role = userRole;
        parkName = park;
        isLoggedIn = true;
        System.out.println("Successful login ");
    }
    
    /**
     * Clears all fields and resets the session on logout.
     */
    public static void logout() {
        employeeId = 0;
        firstName = null;
        lastName = null;
        email = null;
        role = null;
        parkName = null;
        isLoggedIn = false;
        System.out.println("successful logout");
    }
    
    // --- Static Getters ---
    public static int getEmployeeId() { return employeeId; }
    public static String getFirstName() { return firstName; }
    public static String getLastName() { return lastName; }
    public static String getFullName() { return firstName + " " + lastName; }
    public static String getEmail() { return email; }
    public static String getRole() { return role; }
    public static String getParkName() { return parkName; }
    public static boolean isLoggedIn() { return isLoggedIn; }
}