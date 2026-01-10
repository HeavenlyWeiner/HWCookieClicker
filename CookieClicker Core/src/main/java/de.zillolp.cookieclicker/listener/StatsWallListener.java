package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.config.customconfigs.PermissionsConfig;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.profile.PlayerProfile;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsWallListener implements Listener {
    private final Logger logger;
    private final ReflectionUtil reflectionUtil;
    private final LanguageConfig languageConfig;
    private final LocationConfig locationConfig;
    private final PermissionsConfig permissionsConfig;
    private final CookieClickerManager cookieClickerManager;

    public StatsWallListener(CookieClicker plugin) {
        logger = plugin.getLogger();
        reflectionUtil = plugin.getReflectionUtil();
        languageConfig = plugin.getLanguageConfig();
        locationConfig = plugin.getLocationConfig();
        permissionsConfig = plugin.getPermissionsConfig();
        cookieClickerManager = plugin.getCookieClickerManager();
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getHand() != EquipmentSlot.HAND || clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Material type = clickedBlock.getType();
        BlockState blockState = clickedBlock.getState();
        if (type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD && (!(blockState instanceof Sign))) {
            return;
        }
        Location location = clickedBlock.getLocation();
        if (!(cookieClickerManager.getStatsWallHeadsLocations().containsKey(location))) {
            return;
        }
        event.setCancelled(true);
        if (!(blockState instanceof Skull)) {
            return;
        }
        Skull skull = (Skull) blockState;
        PlayerProfile playerProfile = skull.getOwnerProfile();
        if (playerProfile == null) {
            return;
        }
        String name = playerProfile.getName();
        if (name == null || name.isEmpty() || name.equalsIgnoreCase("?")) {
            return;
        }
        Player player = event.getPlayer();
        player.performCommand("cookieclicker stats " + name);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        BlockState blockState = block.getState();
        if (type != Material.PLAYER_HEAD && type != Material.PLAYER_WALL_HEAD && (!(blockState instanceof Skull)) && (!(blockState instanceof Sign))) {
            return;
        }
        Location location = block.getLocation();
        LinkedHashMap<Location, String> statsWallLocationPaths = cookieClickerManager.getStatsWallHeadsLocations();
        boolean hasLocation = statsWallLocationPaths.keySet().stream().anyMatch(location1 -> reflectionUtil.isSameLocation(location1, location));
        if (!(hasLocation)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!(permissionsConfig.hasPermission(player, PluginPermission.ADMIN_PERMISSION))) {
            player.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.NO_PERMISSION));
            return;
        }
        String path = statsWallLocationPaths.get(location);
        String[] roots = path.replace(".", "_").split("_");
        if (roots.length < 3) {
            logger.log(Level.WARNING, "Invalid stats wall path format: " + path + " (expected at least 3 parts, got " + roots.length + ")");
            player.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PREFIX) + "§cError removing stats wall: Invalid location data");
            return;
        }

        int place;
        try {
            place = Integer.parseInt(roots[2]);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid place number in stats wall path: " + roots[2], e);
            player.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PREFIX) + "§cError removing stats wall: Invalid place number");
            return;
        }

        String statsWallType = roots[1];

        statsWallLocationPaths.remove(locationConfig.getLocation(path + ".head"));
        statsWallLocationPaths.remove(locationConfig.getLocation(path + ".sign"));
        locationConfig.removeLocation(path);

        if (statsWallType.equalsIgnoreCase("Alltime")) {
            cookieClickerManager.getAlltimeLocations().remove(place);
        } else {
            cookieClickerManager.getTimeLocations().remove(place);
        }
        player.sendMessage(languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX) + "§7You have removed §6" + statsWallType + "-" + place + "§7.");
    }
}
