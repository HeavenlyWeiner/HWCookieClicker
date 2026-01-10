package de.zillolp.cookieclicker.interfaces;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Hologram {

    UUID getUUID();

    void spawn(Player player, Location location);

    void destroy(Player player);

    void changeLine(Player player, String line);

    void moveHologram(Player player, Location location);

    Location getSpawnLocation();

    Location getCurrentLocation();
}
