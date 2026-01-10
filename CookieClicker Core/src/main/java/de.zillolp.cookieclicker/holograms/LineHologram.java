package de.zillolp.cookieclicker.holograms;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LineHologram implements Hologram {
    private final UUID uuid;
    private final Hologram hologram;
    private String currentLine;
    private Location spawnLocation;
    private boolean isSpawned;

    public LineHologram(CookieClicker plugin, String line) {
        uuid = UUID.randomUUID();
        currentLine = line;
        isSpawned = false;
        hologram = plugin.getVersionManager().getHologram(line);
    }

    @Override
    public void spawn(Player player, Location location) {
        spawnLocation = location;
        hologram.spawn(player, location);
        isSpawned = true;
    }

    @Override
    public void destroy(Player player) {
        hologram.destroy(player);
        isSpawned = false;
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
