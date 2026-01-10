package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CustomConfig {
    protected final CookieClicker plugin;
    private final String name;
    protected FileConfiguration fileConfiguration;
    private File file;

    public CustomConfig(CookieClicker plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        loadConfiguration();
    }

    public void loadConfiguration() {
        file = new File(plugin.getDataFolder(), name);
        if (!(file.exists())) {
            plugin.saveResource(name, true);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    protected void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + file.getName() + ": " + exception);
        }
    }

    public String[] getConfigurationSection(String sectionName) {
        if (!(fileConfiguration.contains(sectionName))) {
            return new String[0];
        }
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(sectionName);
        if (configurationSection == null) {
            return new String[0];
        }
        return configurationSection.getKeys(false).toArray(new String[0]);
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
}
