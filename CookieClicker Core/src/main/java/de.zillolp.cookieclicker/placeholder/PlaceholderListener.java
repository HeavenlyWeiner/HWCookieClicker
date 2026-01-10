package de.zillolp.cookieclicker.placeholder;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlaceholderListener extends PlaceholderExpansion {
    private final CookieClicker plugin;

    public PlaceholderListener(CookieClicker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "ZilloLP";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cookieclicker";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        ClickerStatsProfile clickerStatsProfile = plugin.getClickerPlayerManager().getStatsProfile(player.getUniqueId());
        if (clickerStatsProfile == null) {
            return "N/A";
        }
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        switch (identifier.toUpperCase()) {
            case "COOKIES":
                // %cookieclicker_cookies%
                return languageConfig.formatNumber(clickerStatsProfile.getCookies());
            case "PERCLICK":
                // %cookieclicker_perclick%
                return languageConfig.formatNumber(clickerStatsProfile.getPerClick());
            case "CLICKERCLICKS":
                // %cookieclicker_clickerclicks%
                return languageConfig.formatNumber(clickerStatsProfile.getClickerClicks());
            case "PLACE":
                // %cookieclicker_place%
                long place = 0;
                String playerName = player.getName();
                LinkedHashMap<String, Long> sortedAlltimeData = plugin.getStatsWallUpdater().getSortedAlltimeData();
                if (sortedAlltimeData == null || sortedAlltimeData.isEmpty() || (!(sortedAlltimeData.containsKey(playerName)))) {
                    break;
                }
                try {
                    place = new ArrayList<>(sortedAlltimeData.keySet()).indexOf(playerName) + 1;
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
                return languageConfig.formatNumber(place);
        }
        return "N/A";
    }
}
