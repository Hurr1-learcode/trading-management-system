// Database connection utility with connection pooling
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import config.DatabaseConfig;
import exception.DataAccessException;

public class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static DatabaseUtil instance;
    private BlockingQueue<Connection> connectionPool;
    private boolean isInitialized = false;
    
    private DatabaseUtil() {
        initializeConnectionPool();
    }
    
    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }
    
    private void initializeConnectionPool() {
        try {
            // Load MySQL driver
            Class.forName(DatabaseConfig.DB_DRIVER);
            LOGGER.info("MySQL driver loaded successfully");
            
            connectionPool = new LinkedBlockingQueue<>(DatabaseConfig.MAX_POOL_SIZE);
            
            // Test initial connection
            Connection testConn = createNewConnection();
            testConn.close();
            LOGGER.info("Database connection test successful");
            
            // Create initial connections
            for (int i = 0; i < DatabaseConfig.MIN_POOL_SIZE; i++) {
                Connection conn = createNewConnection();
                connectionPool.offer(conn);
            }
            
            isInitialized = true;
            LOGGER.info("Database connection pool initialized successfully with " + DatabaseConfig.MIN_POOL_SIZE + " connections");
            
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL driver not found! Please add mysql-connector-j.jar to classpath", e);
            throw new RuntimeException("MySQL driver not found! Please add mysql-connector-j.jar to classpath", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database. Check connection parameters: " + DatabaseConfig.DB_URL, e);
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.DB_URL,
            DatabaseConfig.DB_USER,
            DatabaseConfig.DB_PASSWORD
        );
    }
    
    public Connection getConnection() throws DataAccessException {
        if (!isInitialized) {
            throw new DataAccessException("Database connection pool not initialized");
        }
        
        try {
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed()) {
                conn = createNewConnection();
            }
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get database connection", e);
        }
    }
    
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connectionPool.offer(connection);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error releasing connection", e);
            }
        }
    }
    
    public void closeAllConnections() {
        while (!connectionPool.isEmpty()) {
            try {
                Connection conn = connectionPool.poll();
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
}
