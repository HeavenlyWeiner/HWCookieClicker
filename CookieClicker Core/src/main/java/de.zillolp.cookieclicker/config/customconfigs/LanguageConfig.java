package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.ShopType;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import de.zillolp.cookieclicker.runnables.StatsWallUpdater;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageConfig extends CustomConfig {

    public LanguageConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public String getTranslatedLanguage(String keyName) {
        if (!(fileConfiguration.contains(keyName))) {
            return keyName;
        }
        String language = replaceColorCodes(fileConfiguration.getString(keyName, "N/A"));
        language = language.replace("%Ae%", "Ä");
        language = language.replace("%ae%", "ä");
        language = language.replace("%Oe%", "Ö");
        language = language.replace("%oe%", "ö");
        language = language.replace("%Ue%", "Ü");
        language = language.replace("%ue%", "ü");
        language = language.replace("%sz%", "ß");
        language = language.replace("%>%", "»");
        language = language.replace("%<%", "«");
        language = language.replace("%*%", "×");
        language = language.replace("%|%", "┃");
        language = language.replace("%->%", "➜");
        language = language.replace("%_>%", "➥");
        language = language.replace("%!%", "✔");
        return language;
    }

    public String getTranslatedLanguage(PluginLanguage pluginLanguage) {
        return getTranslatedLanguage(pluginLanguage.name());
    }

    public String[] getTranslatedLanguages(PluginLanguage pluginLanguage) {
        String keyName = pluginLanguage.name();
        List<String> languages = new ArrayList<>();
        for (String language : getConfigurationSection(keyName)) {
            languages.add(getTranslatedLanguage(keyName + "." + language));
        }
        return languages.toArray(new String[0]);
    }

    public String getLanguageWithPrefix(PluginLanguage pluginLanguage) {
        return getTranslatedLanguage(PluginLanguage.PREFIX) + getTranslatedLanguage(pluginLanguage);
    }

    public String getReplacedLanguageWithPrefix(PluginLanguage pluginLanguage, UUID uuid) {
        return getTranslatedLanguage(PluginLanguage.PREFIX) + getReplacedLanguage(pluginLanguage, uuid);
    }

    private String replaceValues(String language, UUID uuid) {
        ClickerPlayerManager clickerPlayerManager = plugin.getClickerPlayerManager();
        StatsWallUpdater statsWallUpdater = plugin.getStatsWallUpdater();
        if (clickerPlayerManager == null || statsWallUpdater == null) {
            return language;
        }
        ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
        long cookies = clickerStatsProfile.getCookies();
        String playerName = clickerStatsProfile.getName();
        language = language.replace("%player%", playerName);
        language = language.replace("%cookies%", formatNumber(cookies));
        language = language.replace("%boostedcookies%", formatNumber(cookies * 2));
        language = language.replace("%perclick%", formatNumber(clickerStatsProfile.getPerClick()));
        language = language.replace("%clickerclicks%", formatNumber(clickerStatsProfile.getClickerClicks()));

        long place = 0;
        LinkedHashMap<String, Long> sortedAlltimeData = statsWallUpdater.getSortedAlltimeData();
        if (sortedAlltimeData != null && (!(sortedAlltimeData.isEmpty())) && sortedAlltimeData.containsKey(playerName)) {
            try {
                place = new ArrayList<>(sortedAlltimeData.keySet()).indexOf(playerName) + 1;
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        language = language.replace("%place%", formatNumber(place));
        return language;
    }

    private String getReplacedLanguage(String keyName, UUID uuid) {
        return replaceValues(getTranslatedLanguage(keyName), uuid);
    }

    public String getReplacedLanguage(PluginLanguage pluginLanguage, UUID uuid) {
        return getReplacedLanguage(pluginLanguage.name(), uuid);
    }

    public String[] getReplaceLanguages(PluginLanguage pluginLanguage, UUID uuid) {
        String keyName = pluginLanguage.name();
        List<String> languages = new ArrayList<>();
        for (String language : getConfigurationSection(keyName)) {
            languages.add(getReplacedLanguage(keyName + "." + language, uuid));
        }
        return languages.toArray(new String[0]);
    }

    private String getPriceLanguage(String keyName, ShopType shopType, int id, long price) {
        String language = getTranslatedLanguage(keyName);
        PluginConfig pluginConfig = plugin.getPluginConfig();
        language = language.replace("%displayname%", pluginConfig.getDisplayName(shopType, id));
        language = language.replace("%addclick%", formatNumber(pluginConfig.getAddClicks(shopType, id)));
        language = language.replace("%price%", formatNumber(price));
        return language;
    }

    public String getPriceLanguage(PluginLanguage pluginLanguage, ShopType shopType, int id, long price) {
        return getPriceLanguage(pluginLanguage.name(), shopType, id, price);
    }

    public String[] getPriceLanguages(PluginLanguage pluginLanguage, ShopType shopType, int id, long price) {
        String keyName = pluginLanguage.name();
        List<String> languages = new ArrayList<>();
        for (String language : getConfigurationSection(keyName)) {
            languages.add(getPriceLanguage(keyName + "." + language, shopType, id, price));
        }
        return languages.toArray(new String[0]);
    }

    public String[] getStatsWallLanguage(String section, String place, String name, String value) {
        ArrayList<String> languages = new ArrayList<>();
        Optional<UUID> uuidOptional = plugin.getDatabaseManager().getUUIDbyName(name);
        boolean isPlayer = (!(name.equalsIgnoreCase("?"))) && uuidOptional.isPresent();
        for (String language : getConfigurationSection(section)) {
            String replacedLanguage = getTranslatedLanguage(section + "." + language);
            replacedLanguage = replacedLanguage.replace("%place%", place);
            replacedLanguage = replacedLanguage.replace("%name%", name);
            replacedLanguage = replacedLanguage.replace("%value%", value);
            if (isPlayer) {
                replacedLanguage = replaceValues(replacedLanguage, uuidOptional.get());
            } else {
                replacedLanguage = replacedLanguage.replace("%cookies%", "?");
                replacedLanguage = replacedLanguage.replace("%perclick%", "?");
                replacedLanguage = replacedLanguage.replace("%clickerclicks%", "?");
            }
            languages.add(replacedLanguage);
        }
        return languages.toArray(new String[0]);
    }

    public String getAnimatedLanguage(PluginLanguage pluginLanguage, UUID uuid) {
        StringBuilder actionbar = new StringBuilder();
        for (String language : getReplacedLanguage(pluginLanguage, uuid).split("§")) {
            if (!(language.isEmpty())) {
                actionbar.append(language.substring(1));
                continue;
            }
            actionbar.append(language);
        }
        ClickerPlayerManager clickerPlayerManager = plugin.getClickerPlayerManager();
        if (clickerPlayerManager == null) {
            return actionbar.toString();
        }
        ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(uuid);
        int frameCount = clickerGameProfile.getFrameCount();
        if (frameCount >= actionbar.length() - 1) {
            frameCount = 0;
        } else {
            frameCount++;
        }
        clickerGameProfile.setFrameCount(frameCount);
        int length = 3;
        String coloredActionbar;
        if (frameCount + length >= actionbar.length()) {
            coloredActionbar = actionbar.substring(frameCount);
        } else {
            coloredActionbar = actionbar.substring(frameCount, frameCount + length) + getTranslatedLanguage("GOLDEN_COOKIE_COLOR.PRIMARY") + actionbar.substring(frameCount + length);
        }
        actionbar = new StringBuilder(getTranslatedLanguage("GOLDEN_COOKIE_COLOR.PRIMARY") + actionbar.substring(0, frameCount) + getTranslatedLanguage("GOLDEN_COOKIE_COLOR.SECONDARY") + coloredActionbar);
        return actionbar.toString();
    }

    public String formatTime(String language, int time) {
        int hours = (time % 86400) / 3600;
        int minutes = ((time % 86400) % 3600) / 60;
        int seconds = ((time % 86400) % 3600) % 60;
        return language.replace("%time%", String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public String formatNumber(Long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else {
            return new DecimalFormat("0,000").format(number);
        }
    }

    private String replaceColorCodes(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] charArray = replaceSharp.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (char charValue : charArray) {
                stringBuilder.append("&").append(charValue);
            }

            message = message.replace(hexCode, stringBuilder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
