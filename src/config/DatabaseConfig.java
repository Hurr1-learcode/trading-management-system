// Database configuration constants
package config;

public class DatabaseConfig {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/quanly_giaodich";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "MySQL#Str0ng2025&";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Connection pool settings
    public static final int MAX_POOL_SIZE = 4;
    public static final int MIN_POOL_SIZE = 3;
    public static final int CONNECTION_TIMEOUT = 30000;
}
