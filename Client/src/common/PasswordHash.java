package common;

public class PasswordHash {
	/**
     * Converts a plain password into a hash string.
     */
	public static String hashPassword(String password) {
		if(password == null) return null;
		int hash = password.hashCode();
		return "hash_" + hash;
	}
}
