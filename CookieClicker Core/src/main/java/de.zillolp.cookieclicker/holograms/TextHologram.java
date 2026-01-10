package de.zillolp.cookieclicker.holograms;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.Hologram;
import de.zillolp.cookieclicker.manager.VersionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class TextHologram implements Hologram {
    private final UUID uuid;
    private final VersionManager versionManager;
    private final ArrayList<Hologram> holograms = new ArrayList<>();
    private String[] lines;
    private int linesSize;
    private Location spawnLocation;

    public TextHologram(CookieClicker plugin, String[] lines) {
        uuid = UUID.randomUUID();
        this.lines = lines;
        linesSize = lines.length;
        versionManager = plugin.getVersionManager();
    }

    @Override
    public void spawn(Player player, Location location) {
        spawnLocation = location;
        double holoHeight = 0.3 * (linesSize - 1);
        for (int number = 0; number < linesSize; number++) {
            String line = this.lines[number];
            if (line.equalsIgnoreCase("%empty%") || line.equalsIgnoreCase("")) {
                continue;
            }
            Hologram hologram = versionManager.getHologram(line);
            hologram.spawn(player, location.clone().add(0, holoHeight - 0.3 * number, 0));
            holograms.add(hologram);
        }
    }

    @Override
    public void destroy(Player player) {
        for (Hologram hologram : holograms) {
            hologram.destroy(player);
        }
        holograms.clear();
    }

    public void changeLines(Player player, String[] changingLines) {
        if (Arrays.equals(lines, changingLines)) {
            return;
        }
        int changingSize = changingLines.length;
        if (spawnLocation != null && changingSize != linesSize) {
            lines = changingLines;
            linesSize = changingSize;
            destroy(player);
            spawn(player, spawnLocation);
            return;
        }
        for (int number = 0; number < changingSize; number++) {
            String line = changingLines[number];
            if (line.equals(lines[number])) {
                continue;
            }
            if (line.equalsIgnoreCase("%empty%") || line.equalsIgnoreCase("")) {
                continue;
            }
            if (holograms.isEmpty() || number >= holograms.size()) {
                break;
            }
            Hologram hologram = holograms.get(number);
            if (hologram == null) {
                continue;
            }
            hologram.changeLine(player, line);
        }
        lines = changingLines;
        linesSize = changingSize;
    }

    @Override
    public void changeLine(Player player, String line) {
        // never used
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
        // never used
        return null;
    }
}
