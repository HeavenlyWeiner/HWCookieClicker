package de.zillolp.cookieclicker.holograms;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class MovingHologram implements Hologram {
    private final UUID uuid;
    private final CookieClicker plugin;
    private final Hologram hologram;
    private final double speed;
    private double time;
    private String currentLine;
    private Location spawnLocation;
    private boolean isSpawned;
    private BukkitTask bukkitTask;

    public MovingHologram(CookieClicker plugin, String line, double time, double speed) {
        uuid = UUID.randomUUID();
        this.plugin = plugin;
        this.speed = speed;
        this.time = time;
        currentLine = line;
        isSpawned = false;
        hologram = plugin.getVersionManager().getHologram(line);
    }

    @Override
    public void spawn(Player player, Location location) {
        spawnLocation = location;
        hologram.spawn(player, location);
        isSpawned = true;
        bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            time--;
            if (time <= 0) {
                plugin.getHologramManager().deleteHologram(player, uuid);
                return;
            }
            hologram.moveHologram(player, hologram.getCurrentLocation().add(0, speed, 0));
        }, 0, 1);
    }

    @Override
    public void destroy(Player player) {
        hologram.destroy(player);
        isSpawned = false;
        if (bukkitTask.isCancelled()) {
            return;
        }
        bukkitTask.cancel();
    }

    @Override
    public void changeLine(Player player, String line) {
        if (currentLine.equals(line) || hologram == null) {
            return;
        }
        currentLine = line;
        if (!(isSpawned)) {
            spawn(player, spawnLocation);
            return;
        }
        hologram.changeLine(player, line);
    }

    @Override
    public void moveHologram(Player player, Location location) {
        // never used
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public Location getCurrentLocation() {
        return hologram.getCurrentLocation();
    }
}
