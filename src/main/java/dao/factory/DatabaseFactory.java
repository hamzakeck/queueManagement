package dao.factory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection factory using Singleton pattern
 */
public class DatabaseFactory {
    private static DatabaseFactory instance;
    private String url;
    private String username;
    private String password;
    private String driver;

    private DatabaseFactory() {
        try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("dao/factory/jdbc.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new IOException("Unable to find jdbc.properties");
            }
            props.load(input);

            this.driver = props.getProperty("jdbc.driver");
            this.url = props.getProperty("jdbc.url");
            this.username = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");

            Class.forName(driver);
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Failed to initialize DatabaseFactory: " + e.getMessage());
        }
    }

    public static DatabaseFactory getInstance() {
        if (instance == null) {
            synchronized (DatabaseFactory.class) {
                if (instance == null) {
                    instance = new DatabaseFactory();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
