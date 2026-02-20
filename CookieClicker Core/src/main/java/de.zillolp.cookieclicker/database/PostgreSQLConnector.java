package de.zillolp.cookieclicker.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.MySQLConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import java.io.InputStream;
import java.util.Scanner;

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
                executeSchemaScript();
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

    private void executeSchemaScript() {
        try (Connection conn = getConnection();
             InputStream is = plugin.getResource("postgresql_schema.sql")) {

            if (is == null) {
                plugin.getLogger().warning("postgresql_schema.sql not found in resources");
                return;
            }

            // Читаем весь файл
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            String sql = scanner.hasNext() ? scanner.next() : "";
            scanner.close();

            // Умный сплиттер: разбиваем по ; но игнорируем ; внутри $$ dollar-quote блоков
            java.util.List<String> statements = splitSqlStatements(sql);

            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try (java.sql.Statement st = conn.createStatement()) {
                        st.execute(trimmed);
                    } catch (SQLException e) {
                        plugin.getLogger().log(Level.WARNING,
                                "Error executing schema statement: " + trimmed.substring(0, Math.min(80, trimmed.length())), e);
                    }
                }
            }

            plugin.getLogger().info("✅ PostgreSQL schema initialized successfully!");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute schema script", e);
        }
    }

    /**
     * Разбивает SQL скрипт на отдельные операторы, корректно обрабатывая
     * $$ dollar-quote блоки (PL/pgSQL функции). Символы ; внутри $$ ... $$
     * не считаются разделителями операторов.
     */
    private java.util.List<String> splitSqlStatements(String sql) {
        java.util.List<String> result = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inDollarQuote = false;
        int i = 0;

        while (i < sql.length()) {
            // Проверяем начало/конец $$ блока
            if (i + 1 < sql.length() && sql.charAt(i) == '$' && sql.charAt(i + 1) == '$') {
                inDollarQuote = !inDollarQuote;
                current.append("$$");
                i += 2;
                continue;
            }

            char c = sql.charAt(i);

            if (c == ';' && !inDollarQuote) {
                // Конец оператора
                String statement = current.toString().trim();
                if (!statement.isEmpty()) {
                    result.add(statement);
                }
                current = new StringBuilder();
            } else {
                current.append(c);
            }
            i++;
        }

        // Добавляем последний оператор (без финального ;)
        String last = current.toString().trim();
        if (!last.isEmpty()) {
            result.add(last);
        }

        return result;
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