package utility;

public class validation {

	
	
	static public boolean isValidId(String id) {
		if(id == null) return false;
		String trimmed = id.trim();
		if(trimmed.length() != 9) return false;
		
		for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
		return true;
	}
	
	public static boolean isValidName(String name) {
	    if (name == null) return false;
	    String trimmed = name.trim();
	    
	    if (trimmed.length() < 2 || trimmed.length() > 30) return false;
	    
	    for (int i = 0; i < trimmed.length(); i++) {
	        char ch = trimmed.charAt(i);
	        boolean isEnglishLetter = (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
	        if (!isEnglishLetter && ch != ' ' && ch != '-') {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim();
        
        if (trimmed.length() != 10) return false;
        if (!trimmed.startsWith("05")) return false;
        
        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                return false;
            }
        }
        return true;
    }
	
	public static boolean isPositiveNumber(String number) {
        if (number == null) return false;
        try {
            int val = Integer.parseInt(number.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
