package de.zillolp.cookieclicker.database;

import com.zaxxer.hikari.HikariDataSource;
import de.zillolp.cookieclicker.CookieClicker;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnector {
    private final Logger logger;
    private final HikariDataSource hikariDataSource = new HikariDataSource();
    private boolean isConnected = false;

    public DatabaseConnector(CookieClicker plugin, boolean useMySQL, String filename, String serverName, String port, String databaseName, String user, String password) {
        logger = plugin.getLogger();
        try {
            if (useMySQL) {
                configureMySQLDataSource(serverName, port, databaseName, user, password);
            } else {
                configureSQLiteDataSource(plugin, filename);
            }
            configureHikariCP();
            testConnection();
        } catch (IllegalStateException | IllegalArgumentException exception) {
            logger.log(Level.SEVERE, "Failed to initialize database connection - configuration error", exception);
            isConnected = false;
        } catch (RuntimeException exception) {
            logger.log(Level.SEVERE, "Failed to initialize database connection - runtime error", exception);
            isConnected = false;
        }
    }

    private void configureMySQLDataSource(String serverName, String port, String databaseName, String user, String password) {
        hikariDataSource.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", serverName);
        hikariDataSource.addDataSourceProperty("port", port);
        hikariDataSource.addDataSourceProperty("databaseName", databaseName);
        hikariDataSource.addDataSourceProperty("user", user);
        hikariDataSource.addDataSourceProperty("password", password);
    }

    private void configureSQLiteDataSource(CookieClicker plugin, String filename) {
        File databaseFile = new File(plugin.getDataFolder(), filename + ".db");
        if (!(databaseFile.exists())) {
            File parentDir = databaseFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                logger.log(Level.SEVERE, "Could not create database directory: " + parentDir.getAbsolutePath());
            }
            try {
                if (!(databaseFile.createNewFile())) {
                    logger.log(Level.SEVERE, "Could not create database file: " + databaseFile.getAbsolutePath());
                }
            } catch (IOException exception) {
                logger.log(Level.SEVERE, "Error creating database file: " + databaseFile.getAbsolutePath());
            }
        }
        hikariDataSource.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        hikariDataSource.addDataSourceProperty("synchronous", "NORMAL");
        hikariDataSource.addDataSourceProperty("cache_size", "10000");
        hikariDataSource.addDataSourceProperty("temp_store", "memory");
    }

    private void configureHikariCP() {
        hikariDataSource.setMinimumIdle(2);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setConnectionTimeout(30000); // 30 Sekunden
        hikariDataSource.setIdleTimeout(600000); // 10 Minuten
        hikariDataSource.setMaxLifetime(1800000); // 30 Minuten
        hikariDataSource.setLeakDetectionThreshold(60000); // 1 Minute
        hikariDataSource.setPoolName("CookieClicker-HikariCP");

        hikariDataSource.setConnectionTestQuery("SELECT 1");
    }

    private void testConnection() {
        try (Connection connection = getConnection()) {
            isConnected = connection.isValid(5);
        } catch (SQLException exception) {
            isConnected = false;
            logger.log(Level.SEVERE, "Database connection test failed", exception);
        }
    }

    public void close() {
        if (hikariDataSource.isClosed()) {
            return;
        }
        hikariDataSource.close();
    }

    public boolean hasConnection() {
        return isConnected && !hikariDataSource.isClosed();
    }

    public Connection getConnection() throws SQLException {
        if (hikariDataSource.isClosed()) {
            logger.log(Level.SEVERE, "DataSource is already closed");
            throw new SQLException("DataSource is already closed");
        }
        return hikariDataSource.getConnection();
    }
}
