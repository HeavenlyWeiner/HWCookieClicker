package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.DesignManager;
import de.zillolp.cookieclicker.manager.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {
    private final ClickerPlayerManager clickerPlayerManager;
    private final HologramManager hologramManager;
    private final DesignManager designManager;

    public PlayerMovementListener(CookieClicker plugin) {
        clickerPlayerManager = plugin.getClickerPlayerManager();
        hologramManager = plugin.getHologramManager();
        designManager = plugin.getDesignManager();
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Location locationFrom = event.getFrom();
        Location locationTo = event.getTo();
        if (locationTo == null || (locationFrom.getBlockX() == locationTo.getBlockX() && locationFrom.getBlockZ() == locationTo.getBlockZ())) {
            return;
        }
        clickerPlayerManager.getGameProfile(event.getPlayer().getUniqueId()).updateLastPlayerMove();
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        clickerPlayerManager.getGameProfile(event.getPlayer().getUniqueId()).updateLastPlayerMove();
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        hologramManager.deleteHolograms(player);
        hologramManager.spawnHolograms(player);
        designManager.sendClickerBlockDesign(player);
    }
}
