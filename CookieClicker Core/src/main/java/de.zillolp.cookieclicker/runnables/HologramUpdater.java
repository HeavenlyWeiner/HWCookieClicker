package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.holograms.LineHologram;
import de.zillolp.cookieclicker.holograms.TextHologram;
import de.zillolp.cookieclicker.interfaces.Hologram;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class HologramUpdater extends CustomUpdater {
    private final LanguageConfig languageConfig;
    private final LocationConfig locationConfig;
    private final CookieClickerManager cookieClickerManager;
    private final HologramManager hologramManager;
    private final ResetTimerUpdater resetTimerUpdater;

    public HologramUpdater(CookieClicker plugin) {
        super(plugin, true, 1);
        languageConfig = plugin.getLanguageConfig();
        locationConfig = plugin.getLocationConfig();
        cookieClickerManager = plugin.getCookieClickerManager();
        hologramManager = plugin.getHologramManager();
        resetTimerUpdater = plugin.getResetTimerUpdater();
    }

    @Override
    protected void tick() {
        ArrayList<Location> clickerLocations = cookieClickerManager.getClickerLocations();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            for (Location location : clickerLocations) {
                for (Hologram hologram : hologramManager.getHologramsByLocation(uuid, location)) {
                    if (hologram instanceof TextHologram) {
                        TextHologram textHologram = (TextHologram) hologram;
                        textHologram.changeLines(player, languageConfig.getReplaceLanguages(PluginLanguage.CLICKER_HOLOGRAM, uuid));
                    }
                }
            }
            Location resetTimerLocation = locationConfig.getLocation("ResetTimer");
            if (resetTimerLocation == null) {
                continue;
            }
            String resetTimerLine = languageConfig.formatTime(languageConfig.getReplacedLanguage(PluginLanguage.RESET_TIMER, uuid), resetTimerUpdater.getTime());
            boolean foundResetTimer = false;
            for (Hologram resetTimerHologram : hologramManager.getHologramsByLocation(uuid, resetTimerLocation)) {
                if (resetTimerHologram instanceof LineHologram) {
                    LineHologram lineHologram = (LineHologram) resetTimerHologram;
                    lineHologram.changeLine(player, resetTimerLine);
                    foundResetTimer = true;
                    break;
                }
            }
            if (!foundResetTimer) {
                hologramManager.spawnHologram(player, new LineHologram(plugin, resetTimerLine), resetTimerLocation);
            }
        }
    }
}
