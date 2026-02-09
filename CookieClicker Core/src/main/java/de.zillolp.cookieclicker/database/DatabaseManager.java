package de.zillolp.cookieclicker.database;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.MySQLConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.ShopType;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private final CookieClicker plugin;
    private final Logger logger;
    private final PluginConfig pluginConfig;
    private final DatabaseConnector databaseConnector;
    private final PostgreSQLConnector postgreSQLConnector;
    private final boolean usePostgreSQL;
    private final String playersTable = "cookieclicker_players";
    private final String shopsTable = "cookieclicker_shops";

    public DatabaseManager(CookieClicker plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        pluginConfig = plugin.getPluginConfig();
        this.databaseConnector = plugin.getDatabaseConnector();
        this.postgreSQLConnector = plugin.getPostgreSQLConnector();

        // Определяем тип БД
        MySQLConfig mySQLConfig = plugin.getConfigManager().getMySQLConfig();
        FileConfiguration config = mySQLConfig.getFileConfiguration();
        String type = config.getString("Type", "mysql");
        this.usePostgreSQL = type.equalsIgnoreCase("postgresql");

        initialize();
    }

    /**
     * Получает Connection в зависимости от типа БД
     */
    private Connection getConnection() throws SQLException {
        if (usePostgreSQL) {
            return postgreSQLConnector.getConnection();
        } else {
            return databaseConnector.getConnection();
        }
    }

    private void initialize() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + playersTable +
                     "(UUID VARCHAR(64) NOT NULL, NAME VARCHAR(64) NOT NULL, COOKIES BIGINT DEFAULT 0, PER_CLICK BIGINT DEFAULT 0, CLICKER_CLICKS  BIGINT DEFAULT 0," +
                     " BLOCK_DESIGN  BIGINT DEFAULT 0, PARTICLE_DESIGN  BIGINT DEFAULT 0, MENU_DESIGN  BIGINT DEFAULT 0, PRIMARY KEY (UUID))");
             PreparedStatement preparedStatement1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + shopsTable +
                     "(UUID VARCHAR(64) NOT NULL, SHOP_ID VARCHAR(64) NOT NULL, ID INTEGER NOT NULL, ITEMS_PURCHASED  BIGINT NOT NULL, PRIMARY KEY (UUID, SHOP_ID, ID))")) {
            preparedStatement.executeUpdate();
            preparedStatement1.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error creating database: ", exception);
        }
    }

    public boolean playerExists(UUID uuid) {
        return playerExists("UUID", uuid.toString());
    }

    public boolean playerExists(String name) {
        return playerExists("NAME", name);
    }

    private boolean playerExists(String field, String input) {
        boolean isExisting = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + field + " FROM " + playersTable + " WHERE LOWER(" + field + ")= LOWER(?)")) {
            preparedStatement.setString(1, input);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                isExisting = resultSet.next() && resultSet.getString(field) != null;
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error checking if player exists: ", exception);
        }
        return isExisting;
    }

    public void loadProfiles() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT UUID FROM " + playersTable)) {
            ClickerPlayerManager clickerPlayerManager = plugin.getClickerPlayerManager();
            if (clickerPlayerManager == null) {
                return;
            }
            ConcurrentHashMap<UUID, ClickerStatsProfile> clickerStatsProfiles = clickerPlayerManager.getClickerStatsProfiles();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                    if (clickerStatsProfiles.containsKey(uuid)) {
                        continue;
                    }
                    clickerStatsProfiles.put(uuid, new ClickerStatsProfile(uuid, plugin));
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error loading profiles: ", exception);
        }
    }

    public void loadClickerStatsProfile(ClickerStatsProfile clickerStatsProfile, boolean isOffline) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT NAME, COOKIES, PER_CLICK, CLICKER_CLICKS, BLOCK_DESIGN, PARTICLE_DESIGN, MENU_DESIGN FROM " + playersTable + " WHERE UUID= ?")) {
            UUID uuid = clickerStatsProfile.getUuid();
            preparedStatement.setString(1, uuid.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!(resultSet.next())) {
                    return;
                }
                clickerStatsProfile.setCookies(resultSet.getLong("COOKIES"));
                clickerStatsProfile.setPerClick(resultSet.getLong("PER_CLICK"));
                clickerStatsProfile.setClickerClicks(resultSet.getLong("CLICKER_CLICKS"));
                clickerStatsProfile.setBlockDesign(resultSet.getLong("BLOCK_DESIGN"));
                clickerStatsProfile.setParticleDesign(resultSet.getLong("PARTICLE_DESIGN"));
                clickerStatsProfile.setMenuDesign(resultSet.getLong("MENU_DESIGN"));
                if (isOffline) {
                    clickerStatsProfile.setName(resultSet.getString("NAME"));
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error loading playerdata: ", exception);
        }
        loadPlayerPrices(clickerStatsProfile);
    }

    private void loadPlayerPrices(ClickerStatsProfile clickerStatsProfile) {
        HashMap<ShopType, HashMap<Integer, Long>> shopPrices = clickerStatsProfile.getShopPrices();
        HashMap<ShopType, HashMap<Integer, Boolean>> shopItems = clickerStatsProfile.getShopItems();
        FileConfiguration fileConfiguration = pluginConfig.getFileConfiguration();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, ITEMS_PURCHASED, SHOP_ID FROM " + shopsTable + " WHERE UUID= ?")) {
            preparedStatement.setString(1, clickerStatsProfile.getUuid().toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String shopId = resultSet.getString("SHOP_ID");
                    try {
                        int id = resultSet.getInt("ID");
                        ShopType shopType = ShopType.valueOf(shopId);
                        String shopSection = shopType.getConfigSection();
                        String priceSection = shopSection + "." + id;

                        if (!(fileConfiguration.contains(priceSection))) {
                            continue;
                        }

                        long basePrice = pluginConfig.getBasePrice(shopType, id);
                        long itemsPurchased = resultSet.getLong("ITEMS_PURCHASED");

                        shopPrices.computeIfAbsent(shopType, k -> new HashMap<>())
                                .put(id, basePrice * itemsPurchased);

                        if (!shopType.isInfinitePrice()) {
                            shopItems.computeIfAbsent(shopType, k -> new HashMap<>())
                                    .put(id, itemsPurchased >= 2);
                        }
                    } catch (IllegalArgumentException exception) {
                        logger.log(Level.WARNING, "Invalid shop type found in database: " + shopId, exception);
                    }
                }
            }
            loadDefaultShops(clickerStatsProfile);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error loading priceprofiles: ", exception);
        }
    }

    public void loadDefaultShops(ClickerStatsProfile clickerStatsProfile) {
        HashMap<ShopType, HashMap<Integer, Long>> shopPrices = clickerStatsProfile.getShopPrices();
        HashMap<ShopType, HashMap<Integer, Boolean>> shopItems = clickerStatsProfile.getShopItems();
        for (ShopType shopType : ShopType.values()) {
            HashMap<Integer, Long> prices = shopPrices.computeIfAbsent(shopType, k -> new HashMap<>());
            for (String priceID : pluginConfig.getConfigurationSection(shopType.getConfigSection())) {
                int id;
                try {
                    id = Integer.parseInt(priceID);
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Invalid price ID in shop configuration for " + shopType + ": " + priceID + " - Skipping this entry", e);
                    continue;
                }
                prices.putIfAbsent(id, pluginConfig.getBasePrice(shopType, id));
            }

            if (!shopType.isInfinitePrice()) {
                HashMap<Integer, Boolean> items = shopItems.computeIfAbsent(shopType, k -> new HashMap<>());
                for (String priceID : pluginConfig.getConfigurationSection(shopType.getConfigSection())) {
                    int id;
                    try {
                        id = Integer.parseInt(priceID);
                    } catch (NumberFormatException e) {
                        logger.log(Level.WARNING, "Invalid item ID in shop configuration for " + shopType + ": " + priceID + " - Skipping this entry", e);
                        continue;
                    }
                    items.putIfAbsent(id, false);
                }
            }
        }
    }

    public void saveClickerStatsProfile(ClickerStatsProfile clickerStatsProfile) {
        if (clickerStatsProfile == null) {
            return;
        }
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO " + playersTable + "(UUID, NAME, COOKIES, PER_CLICK, CLICKER_CLICKS, BLOCK_DESIGN, PARTICLE_DESIGN, MENU_DESIGN) VALUES (?, ?, ?, ?, ?, ?, ?, ?);")) {
            preparedStatement.setString(1, clickerStatsProfile.getUuid().toString());
            preparedStatement.setString(2, clickerStatsProfile.getName());
            preparedStatement.setLong(3, clickerStatsProfile.getCookies());
            preparedStatement.setLong(4, clickerStatsProfile.getPerClick());
            preparedStatement.setLong(5, clickerStatsProfile.getClickerClicks());
            preparedStatement.setLong(6, clickerStatsProfile.getBlockDesign());
            preparedStatement.setLong(7, clickerStatsProfile.getParticleDesign());
            preparedStatement.setLong(8, clickerStatsProfile.getMenuDesign());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error executing update: ", exception);
            return;
        }
        savePlayerPrices(clickerStatsProfile);
        clickerStatsProfile.markClean();
    }

    private void savePlayerPrices(ClickerStatsProfile clickerStatsProfile) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO " + shopsTable + "(UUID, SHOP_ID, ID, ITEMS_PURCHASED) VALUES (?, ?, ?, ?);")) {
            preparedStatement.setString(1, clickerStatsProfile.getUuid().toString());
            int batchCount = 0;
            for (Map.Entry<ShopType, HashMap<Integer, Long>> shopEntry : clickerStatsProfile.getShopPrices().entrySet()) {
                ShopType shopType = shopEntry.getKey();
                preparedStatement.setString(2, shopType.name());
                for (Map.Entry<Integer, Long> priceEntry : shopEntry.getValue().entrySet()) {
                    int id = priceEntry.getKey();
                    preparedStatement.setInt(3, id);
                    long basePrice = pluginConfig.getBasePrice(shopType, id);
                    long itemsPurchased = (basePrice != 0) ? (priceEntry.getValue() / basePrice) : 0;
                    preparedStatement.setLong(4, itemsPurchased);
                    preparedStatement.addBatch();
                    batchCount++;

                    if (batchCount % 100 == 0) {
                        preparedStatement.executeBatch();
                        batchCount = 0;
                    }
                }
            }
            if (batchCount > 0) {
                preparedStatement.executeBatch();
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error executing batch update: ", exception);
        }
    }

    public Optional<UUID> getUUIDbyName(String name) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT UUID FROM " + playersTable + " WHERE LOWER(NAME)= LOWER(?)")) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(UUID.fromString(resultSet.getString("UUID")));
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error getting UUID: ", exception);
        }
        return Optional.empty();
    }

    public int getRegisteredPlayerAmount() {
        int amount = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS player_count FROM " + playersTable)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    amount = resultSet.getInt("player_count");
                }
            }
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error getting registered Player amount: ", exception);
        }
        return amount;
    }
}