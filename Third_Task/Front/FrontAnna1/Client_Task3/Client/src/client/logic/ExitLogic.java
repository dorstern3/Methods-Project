package client.logic;

public class ExitLogic {
    
    // --- FUTURE IMPLEMENTATION VARIABLES ---
    // private Object response;
    // private boolean isDataReady = false;

    public ExitLogic() {}

    /**
     * Simulates the registration of a visitor's exit to update park capacity.
     * @param visitorId The ID of the visitor or order leaving the park.
     * @return true if the exit was registered successfully.
     */
    public boolean registerExit(String visitorId) {
        /*
         * TODO: FUTURE SERVER INTEGRATION
         * 1. Change this method signature to: public synchronized boolean registerExit(String visitorId)
         * 2. Create a message: Message msg = new Message(Action.REGISTER_EXIT, visitorId);
         * 3. Send to server: ClientUI.clientChat.accept(msg);
         * 4. The server will execute an UPDATE query to decrement the current visitors count in the park.
         * 5. Wait for server response: while(!isDataReady) { wait(); }
         * 6. Return the success status received from the server.
         */

        // ---------- CURRENT MOCK Front-End Simulation ----------
        if (visitorId != null && !visitorId.isEmpty()) {
            System.out.println("ExitLogic: Visitor/Order " + visitorId + " has exited. Park capacity updated.");
            return true;
        }
        
        System.out.println("ExitLogic: Failed to register exit. Invalid ID.");
        return false;
    }
}