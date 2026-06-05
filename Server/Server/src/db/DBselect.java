package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBselect {

    public static String identifyTravelerInDB(String travelerId) {
        try {
            Connection conn = DBconnection.getConnection();
            
            // 1. קודם כל בודקים אם הוא מדריך קבוצות
            String guideQuery = "SELECT fname FROM guide WHERE guide_id = ?";
            PreparedStatement pstmtGuide = conn.prepareStatement(guideQuery);
            pstmtGuide.setInt(1, Integer.parseInt(travelerId)); 
            ResultSet rsGuide = pstmtGuide.executeQuery();
            
            if (rsGuide.next()) {
                String fname = rsGuide.getString("fname");
                rsGuide.close();
                pstmtGuide.close();
                return "Guide: " + fname; // מצאנו מדריך
            }
            rsGuide.close();
            pstmtGuide.close();
            
            // 2. אם הוא לא מדריך, נבדוק אם הוא מנוי משפחתי
            String subQuery = "SELECT fname, family_members FROM subscriber WHERE subscriber_id = ?";
            PreparedStatement pstmtSub = conn.prepareStatement(subQuery);
            pstmtSub.setInt(1, Integer.parseInt(travelerId)); 
            ResultSet rsSub = pstmtSub.executeQuery();
            
            if (rsSub.next()) {
                String fname = rsSub.getString("fname");
                int familyMembers = rsSub.getInt("family_members");
                rsSub.close();
                pstmtSub.close();
                // מחזירים גם את השם וגם את כמות בני המשפחה המותרת
                return "Subscriber: " + fname + ":" + familyMembers; 
            }
            rsSub.close();
            pstmtSub.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 3. אם הוא לא מדריך ולא מנוי - הוא מטייל רגיל
        return "Regular Traveler";
    }
}