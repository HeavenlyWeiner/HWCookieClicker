package de.zillolp.cookieclicker.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.MySQLConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class PostgreSQLConnector {
    private final CookieClicker plugin;
    private final MySQLConfig mySQLConfig;
    private HikariDataSource dataSource;

    public PostgreSQLConnector(CookieClicker plugin) {
        this.plugin = plugin;
        this.mySQLConfig = plugin.getConfigManager().getMySQLConfig();
    }

    public void connect() {
        try {
            // Получаем настройки из mysql.yml
            String type = mySQLConfig.getFileConfiguration().getString("Type", "mysql");
            String host = mySQLConfig.getFileConfiguration().getString("Host");
            String port = mySQLConfig.getFileConfiguration().getString("Port");
            String database = mySQLConfig.getFileConfiguration().getString("Database");
            String user = mySQLConfig.getFileConfiguration().getString("User");
            String password = mySQLConfig.getFileConfiguration().getString("Password");

            // Определяем тип БД и строку подключения
            String jdbcUrl;
            String driverClassName;

            if (type.equalsIgnoreCase("postgresql")) {
                jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
                driverClassName = "org.postgresql.Driver";
            } else {
                jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }

            // Настройка HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName(driverClassName);

            // Оптимизация пула соединений
            config.setMinimumIdle(2);
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1");

            // Создаём пул соединений
            this.dataSource = new HikariDataSource(config);

            // Проверка соединения
            try (Connection connection = dataSource.getConnection()) {
                plugin.getLogger().info("✅ Successfully connected to " + type.toUpperCase() + " database!");
            }

        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "❌ Failed to connect to database!", exception);
        }
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection closed.");
        }
    }

    public void close() {
        disconnect();
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is not initialized or has been closed");
        }
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }
}