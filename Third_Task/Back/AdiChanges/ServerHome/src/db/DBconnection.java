package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
	
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gonature_db_new?serverTimezone=UTC";
    private static final String USER = "root";
    
    private static final int POOL_SIZE = 10;
    private static DBconnectionPool connectionPool = null;

    // Static rank that will hold the connection of the individual server
    private static Connection conn = null;

    
    /**
     * Initializes the custom database connection pool with the provided user password.
     * Enforces single-instance allocation boundaries across concurrent framework workflows.
     *
     * @param dbPassword The password string needed to authenticate against the MySQL database.
     * @throws SQLException If an invalid password is provided or database engine access fails.
     */
    public static void initializePool(String dbPassword) throws SQLException {
        if (connectionPool == null) {
            try {
                connectionPool = new DBconnectionPool(POOL_SIZE, DB_URL, USER, dbPassword);
                System.out.println(">>> Connection Pool initialized successfully.");
            } catch (SQLException e) {
                System.err.println(">>> Critical Error: Failed to initialize Custom Pool!");
                e.printStackTrace();
                throw e;
            }
        }
    }
    
    /**
     * Borrows an active physical database connection reference from the underlying pool.
     * This operation blocks the executing thread if all available connections are currently out.
     *
     * @return An active database {@link Connection} ready for query execution workloads.
     * @throws SQLException If the pool has not been initialized or the thread is interrupted while waiting.
     */
    public static Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            throw new SQLException("Connection pool is not initialized! Did you forget to call initializePool()?");
        }
        try {
            return connectionPool.getConnection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Thread was interrupted while waiting for a connection from the pool.", e);
        }
    }

    /**
     * Returns an active database connection back to the shared pool array lifecycle.
     * Safely bypasses the routine operations if the core connection pool metadata is unallocated.
     *
     * @param conn The database {@link Connection} reference instance to recycle.
     */
    public static void release(Connection conn) {
        if (connectionPool != null) {
            connectionPool.releaseConnection(conn);
        }
    }

    /**
     * Shuts down the backend database connection pool infrastructure completely.
     * Iterates over allocated elements to close any physical sockets connected to the database server.
     */
    public static void shutdown() {
        if (connectionPool != null) {
            connectionPool.closeAll();
        }
    }
}