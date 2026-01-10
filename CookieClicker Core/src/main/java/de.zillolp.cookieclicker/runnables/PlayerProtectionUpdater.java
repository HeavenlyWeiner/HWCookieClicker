package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerProtectionUpdater extends CustomUpdater {
    private final PluginConfig pluginConfig;
    private final ClickerPlayerManager clickerPlayerManager;

    public PlayerProtectionUpdater(CookieClicker plugin) {
        super(plugin, true, 20);
        pluginConfig = plugin.getPluginConfig();
        clickerPlayerManager = plugin.getClickerPlayerManager();
    }

    @Override
    protected void tick() {
        int maximumClicksPerSecond = pluginConfig.getMaximumClicksPerSecond();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(uuid);
            int newClicksPerSecond = clickerGameProfile.getPlayerClicksPerSecond() - maximumClicksPerSecond;
            if (newClicksPerSecond <= 0) {
                clickerGameProfile.setPlayerClicksPerSecond(0);
                continue;
            }
            clickerGameProfile.setPlayerClicksPerSecond(newClicksPerSecond);
        }
    }
}
