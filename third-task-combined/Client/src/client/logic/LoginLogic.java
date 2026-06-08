package client.logic;

import client.ClientUI;
import common.Message;
import common.MessageType;
import common.PasswordHash;

/**
 * Business logic layer for Login authentication.
 * Handles user session initialization based on server responses.
 */
public class LoginLogic {
	
	/**
     * Authenticates credentials and initializes the local CurUser session if successful.
     * @return The MessageType indicating the result of the login.
     */
	public MessageType authenticateUser(String username , String rawPassword) {
		
		//String hashedPassword = PasswordHash.hashPassword(rawPassword);
		Object[] requestUser = new Object[] {username , rawPassword};
		Message msg = new Message(MessageType.LOGIN_REQUEST , requestUser);
		Message response = (Message) ClientUI.clientChat.accept(msg);
		if (response != null) {
            MessageType type = response.getType();
            if (type == MessageType.LOGIN_SUCCESS) {
                Object[] serverUser = (Object[]) response.getData();
                CurUser.login(
                    (int) serverUser[0],      // WorkerId
                    (String) serverUser[1],   // firstName
                    (String) serverUser[2],   // lastName
                    (String) serverUser[3],   // email
                    (String) serverUser[4],   // role
                    (String) serverUser[5]    // parkName
                );
            }
            return type;
		}
		return MessageType.LOGIN_FAILED;
	}
}
