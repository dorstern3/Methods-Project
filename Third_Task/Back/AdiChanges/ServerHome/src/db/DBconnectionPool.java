package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A thread-safe, circular buffer implementation of a Database Connection Pool.
 * Manages a fixed set of physical database connections to enhance system performance 
 * by reusing existing connections instead of establishing new ones for each database request.
 */
public class DBconnectionPool {
	
    private final String dbUrl;
    private final String user;
    private final String pass;
    private final int maxSize;

    private final Connection[] pool;
    private int head = 0;
    private int tail = 0;
    private int currentSize = 0;

    private static int activeConnectionsCount = 0;
    
    /**
     * Constructs and initializes a fixed-size connection pool.
     * Establishes all underlying physical connections to the database synchronously.
     *
     * @param maxCapacity The maximum number of physical connections to keep in the pool.
     * @param dbUrl       The database URL connection configuration.
     * @param user        The database user identification name.
     * @param pass        The database password parameter.
     * @throws SQLException If a database access error occurs during initialization.
     */
    public DBconnectionPool(int maxCapacity, String dbUrl, String user, String pass) throws SQLException {
        this.maxSize = maxCapacity;
        this.dbUrl = dbUrl;
        this.user = user;
        this.pass = pass;
        this.pool = new Connection[maxCapacity];

        for (int i = 0; i < maxCapacity; i++) {
            pool[i] = DriverManager.getConnection(dbUrl, user, pass);
            tail = (tail + 1) % maxSize;
            currentSize++;
        }
        System.out.println(">>> Circular Connection Pool created with " + maxCapacity + " physical connections.");
    }

    /**
     * Borrows an available connection from the head of the circular pool.
     * If the pool is currently empty, the executing thread will enter a waiting state
     * until another thread releases an active connection back into the pool.
     *
     * @return An active database {@link Connection} object reference.
     * @throws InterruptedException If the current thread is interrupted while waiting.
     */
    public synchronized Connection getConnection() throws InterruptedException {
        while (currentSize == 0) {
            System.out.println("DEBUG: Pool is empty. Thread " + Thread.currentThread().getName() + " is waiting...");
            wait(); 
        }

        Connection conn = pool[head];
        pool[head] = null;
        head = (head + 1) % maxSize;
        currentSize--;
        activeConnectionsCount++;
        return conn;
    }

    /**
     * Releases an active database connection and inserts it back to the tail of the circular pool.
     * Validates the connection's health and attempts to re-establish it if it has been closed.
     * Notifies any threads currently waiting in line for a connection.
     *
     * @param conn The database {@link Connection} object to return to the pool structure.
     */
    public synchronized void releaseConnection(Connection conn) {
    	activeConnectionsCount--;
    	System.out.println(">>> Connections currently out of the pool: " + activeConnectionsCount);
        if (conn == null) return;

        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(dbUrl, user, pass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        pool[tail] = conn;
        tail = (tail + 1) % maxSize;
        currentSize++;

        notifyAll(); 
    }

    /**
     * Iterates through the pool structure and gracefully closes all remaining open database connections.
     * This method is intended for application shutdown and resource cleanup execution workflows.
     */
    public synchronized void closeAll() {
        for (int i = 0; i < maxSize; i++) {
            try {
                if (pool[i] != null && !pool[i].isClosed()) {
                    pool[i].close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}