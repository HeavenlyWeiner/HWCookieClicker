package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.clickerevents.ClickerEvent;
import de.zillolp.cookieclicker.clickerevents.GoldenCookieEvent;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.manager.ClickerEventManager;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventActionbarUpdater extends CustomUpdater {
    private final PluginConfig pluginConfig;
    private final LanguageConfig languageConfig;
    private final ClickerPlayerManager clickerPlayerManager;
    private final ClickerEventManager clickerEventManager;

    public EventActionbarUpdater(CookieClicker plugin) {
        super(plugin, false, 1);
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        clickerEventManager = plugin.getClickerEventManager();
    }

    @Override
    protected void tick() {
        HashMap<UUID, HashMap<ClickerEventType, ClickerEvent>> activeEvents = clickerEventManager.getActiveEvents();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!activeEvents.containsKey(uuid)) {
                continue;
            }
            Player.Spigot spigotPlayer = player.spigot();
            ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(uuid);
            for (Map.Entry<ClickerEventType, ClickerEvent> clickerEventEntry : activeEvents.get(uuid).entrySet()) {
                ClickerEvent clickerEvent = clickerEventEntry.getValue();
                if (clickerEvent == null || clickerEvent.getTime() <= 0) {
                    continue;
                }
                if (!(clickerEvent instanceof GoldenCookieEvent)) {
                    continue;
                }
                for (Location location : plugin.getCookieClickerManager().getClickerLocations()) {
                    if (player.getLocation().distance(location) > 5) {
                        continue;
                    }

                    if (clickerGameProfile.isUnderLastPlayerMove(pluginConfig.getAFKCooldownSeconds())) {
                        spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.AFK_MESSAGE, uuid)));
                        break;
                    }

                    if (clickerGameProfile.isOverCPS(pluginConfig.getMaximumClicksPerSecond())) {
                        spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getReplacedLanguage(PluginLanguage.MAX_CPS, uuid)));
                        break;
                    }

                    spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(languageConfig.getAnimatedLanguage(PluginLanguage.CLICK_MESSAGE, uuid)));
                    break;
                }
            }
        }
    }
}
