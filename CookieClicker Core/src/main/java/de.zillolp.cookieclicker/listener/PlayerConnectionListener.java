package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.PacketReader;
import de.zillolp.cookieclicker.manager.ClickerEventManager;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.DesignManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

public class PlayerConnectionListener implements Listener {
    private final CookieClicker plugin;
    private final PacketReader packetReader;
    private final ClickerPlayerManager clickerPlayerManager;
    private final ClickerEventManager clickerEventManager;
    private final DesignManager designManager;
    private final BukkitScheduler bukkitScheduler;

    public PlayerConnectionListener(CookieClicker plugin) {
        this.plugin = plugin;
        packetReader = plugin.getPacketReader();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        clickerEventManager = plugin.getClickerEventManager();
        designManager = plugin.getDesignManager();
        bukkitScheduler = plugin.getServer().getScheduler();
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        bukkitScheduler.runTaskAsynchronously(plugin, () -> {
            clickerPlayerManager.registerPlayer(player);
            bukkitScheduler.runTaskLater(plugin, () -> {
                packetReader.inject(player);
                designManager.sendClickerBlockDesign(player);
            }, 4);
        });
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        bukkitScheduler.runTaskAsynchronously(plugin, () -> clickerPlayerManager.unregisterPlayer(player));
        bukkitScheduler.runTask(plugin, () -> clickerEventManager.deactivateEvents(player.getUniqueId()));
        packetReader.unInject(player);
    }
}
