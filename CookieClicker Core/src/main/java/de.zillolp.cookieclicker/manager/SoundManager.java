package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.SoundType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SoundManager {
    private final CookieClicker plugin;
    private final PluginConfig pluginConfig;
    private boolean useSounds;

    public SoundManager(CookieClicker plugin) {
        pluginConfig = plugin.getPluginConfig();
        this.plugin = plugin;
        load();
    }

    public void load() {
        FileConfiguration fileConfiguration = pluginConfig.getFileConfiguration();
        useSounds = fileConfiguration.getBoolean("Sounds");
        for (SoundType soundType : SoundType.values()) {
            String root = "sounds." + soundType.name().toLowerCase();
            soundType.setSound(fileConfiguration.getString(root + ".type", "ENTITY_VILLAGER_NO"));
            soundType.setVolume((float) fileConfiguration.getDouble(root + ".volume", 1.0));
            soundType.setPitch((float) fileConfiguration.getDouble(root + ".pitch", 1.0));
        }
    }

    public void playSound(Player player, SoundType soundType) {
        if (!(useSounds)) {
            return;
        }
        String soundName = soundType.getSound();
        Object sound = plugin.getReflectionUtil().getSound(soundName);
        if (sound == null) {
            plugin.getLogger().log(Level.SEVERE, "Sound: " + soundName + " doesn't exist!");
            return;
        }
        if (Bukkit.isPrimaryThread()) {
            player.playSound(player, (org.bukkit.Sound) sound, soundType.getVolume(), soundType.getPitch());
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> player.playSound(player, (org.bukkit.Sound) sound, soundType.getVolume(), soundType.getPitch()));
    }
}
