package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.holograms.TextHologram;
import de.zillolp.cookieclicker.interfaces.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager {
    private final CookieClicker plugin;
    private final PluginConfig pluginConfig;
    private final LanguageConfig languageConfig;
    private final CookieClickerManager cookieClickerManager;
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<UUID, Hologram>> playerHolograms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Set<UUID>>> locationIndex = new ConcurrentHashMap<>();

    public HologramManager(CookieClicker plugin) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        cookieClickerManager = plugin.getCookieClickerManager();
    }

    public void spawnHologram(Player player, Hologram hologram, Location location) {
        if (location.getWorld() != player.getWorld()) {
            return;
        }
        UUID playerUuid = player.getUniqueId();
        UUID hologramId = hologram.getUUID();
        getHolograms(playerUuid).put(hologramId, hologram);

        String locationKey = toLocationKey(location);
        getLocationIndex(playerUuid).computeIfAbsent(locationKey, k -> ConcurrentHashMap.newKeySet()).add(hologramId);

        hologram.spawn(player, location);
    }

    public void spawnHolograms(Player player) {
        if (!(pluginConfig.hasClickerHolograms())) {
            return;
        }
        for (Location location : cookieClickerManager.getClickerLocations()) {
            spawnHologram(player, new TextHologram(plugin, languageConfig.getReplaceLanguages(PluginLanguage.CLICKER_HOLOGRAM, player.getUniqueId())), location.clone().add(0.5, 1, 0.5));
        }
    }

    public void deleteHologram(Player player, UUID hologramId) {
        UUID playerUuid = player.getUniqueId();
        ConcurrentHashMap<UUID, Hologram> holograms = getHolograms(playerUuid);
        if (!(holograms.containsKey(hologramId))) {
            return;
        }
        Hologram hologram = holograms.get(hologramId);
        hologram.destroy(player);
        holograms.remove(hologramId);

        removeFromLocationIndex(playerUuid, hologram.getSpawnLocation(), hologramId);
    }

    public void deleteHologramsByLocation(Player player, Location location) {
        UUID playerUuid = player.getUniqueId();
        String locationKey = toLocationKey(location);
        ConcurrentHashMap<String, Set<UUID>> locIndex = getLocationIndex(playerUuid);

        if (!(locIndex.containsKey(locationKey))) {
            return;
        }

        Set<UUID> hologramIds = new HashSet<>(locIndex.get(locationKey));
        for (UUID hologramId : hologramIds) {
            deleteHologram(player, hologramId);
        }
    }

    public void deleteHolograms(Player player) {
        UUID uuid = player.getUniqueId();
        for (Hologram hologram : new ArrayList<>(getHolograms(uuid).values())) {
            if (hologram == null) {
                continue;
            }
            hologram.destroy(player);
        }
        playerHolograms.remove(uuid);
        locationIndex.remove(uuid);
    }

    public List<Hologram> getHologramsByLocation(UUID playerUuid, Location location) {
        String locationKey = toLocationKey(location);
        ConcurrentHashMap<String, Set<UUID>> locIndex = getLocationIndex(playerUuid);

        if (!(locIndex.containsKey(locationKey))) {
            return new ArrayList<>();
        }

        List<Hologram> result = new ArrayList<>();
        ConcurrentHashMap<UUID, Hologram> holograms = getHolograms(playerUuid);
        for (UUID hologramId : locIndex.get(locationKey)) {
            if (holograms.containsKey(hologramId)) {
                result.add(holograms.get(hologramId));
            }
        }
        return result;
    }

    private ConcurrentHashMap<UUID, Hologram> getHolograms(UUID uuid) {
        return playerHolograms.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
    }

    private ConcurrentHashMap<String, Set<UUID>> getLocationIndex(UUID uuid) {
        return locationIndex.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
    }

    private void removeFromLocationIndex(UUID playerUuid, Location location, UUID hologramId) {
        if (location == null) {
            return;
        }
        String locationKey = toLocationKey(location);
        ConcurrentHashMap<String, Set<UUID>> locIndex = getLocationIndex(playerUuid);
        if (locIndex.containsKey(locationKey)) {
            locIndex.get(locationKey).remove(hologramId);
            if (locIndex.get(locationKey).isEmpty()) {
                locIndex.remove(locationKey);
            }
        }
    }

    private String toLocationKey(Location location) {
        if (location == null || location.getWorld() == null) {
            return "";
        }
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }
}
