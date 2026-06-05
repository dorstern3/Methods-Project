package common;

/**
 * Enum defining the types of messages sent between the client and server.
 */
public enum MessageType {
    IDENTIFY_TRAVELER,         // בקשה מהלקוח: תבדוק לי את תעודת הזהות הזו
    IDENTIFY_TRAVELER_RESPONSE // תשובה מהשרת: הנה סוג המטייל (מנוי/מדריך/רגיל)
    // חברי הצוות יוסיפו פה פקודות נוספות בהמשך, למשל:
    // GET_REPORTS,
    // CREATE_ORDER
}