package de.zillolp.cookieclicker.config;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.*;

public class ConfigManager {
    private final CookieClicker plugin;
    private PluginConfig pluginConfig;
    private LanguageConfig languageConfig;
    private LocationConfig locationConfig;
    private MySQLConfig mySQLConfig;
    private PermissionsConfig permissionsConfig;

    public ConfigManager(CookieClicker plugin) {
        this.plugin = plugin;
        registerConfigs();
    }

    private void registerConfigs() {
        pluginConfig = new PluginConfig(plugin, "config.yml");
        languageConfig = new LanguageConfig(plugin, "language.yml");
        locationConfig = new LocationConfig(plugin, "locations.yml");
        mySQLConfig = new MySQLConfig(plugin, "mysql.yml");
        permissionsConfig = new PermissionsConfig(plugin, "permissions.yml");
    }

    public void reloadConfigs() {
        pluginConfig.loadConfiguration();
        languageConfig.loadConfiguration();
        locationConfig.loadConfiguration();
        mySQLConfig.loadConfiguration();
        permissionsConfig.loadConfiguration();
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public LocationConfig getLocationConfig() {
        return locationConfig;
    }

    public MySQLConfig getMySQLConfig() {
        return mySQLConfig;
    }

    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }
}
