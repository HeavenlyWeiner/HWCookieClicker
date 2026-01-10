package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsUpdater extends CustomUpdater {
    private final DatabaseManager databaseManager;
    private final ClickerPlayerManager clickerPlayerManager;

    public StatsUpdater(CookieClicker plugin, long ticks) {
        super(plugin, true, ticks);
        databaseManager = plugin.getDatabaseManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
    }

    @Override
    protected void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ClickerStatsProfile profile = clickerPlayerManager.getStatsProfile(player.getUniqueId());
            if (profile != null && profile.isDirty()) {
                databaseManager.saveClickerStatsProfile(profile);
            }
        }
    }
}
