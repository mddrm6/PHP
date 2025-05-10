package framework;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final Connection connection;
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Не получилось найти application.properties");
            } else {
                properties.load(input);
                Class.forName(properties.getProperty("db.driver"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DatabaseConnection() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
