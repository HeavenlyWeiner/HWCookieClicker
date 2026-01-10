package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClickerPlayerManager {
    private final CookieClicker plugin;
    private final DatabaseManager databaseManager;
    private final HologramManager hologramManager;
    private final ConcurrentHashMap<UUID, ClickerInventoryProfile> clickerInventoryProfiles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ClickerGameProfile> clickerGameProfiles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ClickerStatsProfile> clickerStatsProfiles = new ConcurrentHashMap<>();

    public ClickerPlayerManager(CookieClicker plugin) {
        this.plugin = plugin;
        databaseManager = plugin.getDatabaseManager();
        hologramManager = plugin.getHologramManager();
    }

    public void registerPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        clickerInventoryProfiles.put(uuid, new ClickerInventoryProfile(player, plugin));
        clickerGameProfiles.put(uuid, new ClickerGameProfile());
        clickerStatsProfiles.put(uuid, new ClickerStatsProfile(uuid, player.getName(), plugin));
        hologramManager.spawnHolograms(player);
    }

    public void unregisterPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        hologramManager.deleteHolograms(player);
        plugin.getDesignManager().stopParticleEffects(uuid);

        databaseManager.saveClickerStatsProfile(getStatsProfile(uuid));

        clickerGameProfiles.remove(uuid);
        clickerInventoryProfiles.remove(uuid);
    }

    public ClickerInventoryProfile getInventoryProfile(Player player) {
        UUID uuid = player.getUniqueId();
        if (clickerInventoryProfiles.containsKey(uuid)) {
            return clickerInventoryProfiles.get(uuid);
        }
        return new ClickerInventoryProfile(player, plugin);
    }

    public ClickerGameProfile getGameProfile(UUID uuid) {
        if (clickerGameProfiles.containsKey(uuid)) {
            return clickerGameProfiles.get(uuid);
        }
        return new ClickerGameProfile();
    }

    public ClickerStatsProfile getStatsProfile(UUID uuid) {
        if (clickerStatsProfiles.containsKey(uuid)) {
            return clickerStatsProfiles.get(uuid);
        }
        return new ClickerStatsProfile(uuid, plugin);
    }

    public ConcurrentHashMap<UUID, ClickerStatsProfile> getClickerStatsProfiles() {
        return clickerStatsProfiles;
    }
}
