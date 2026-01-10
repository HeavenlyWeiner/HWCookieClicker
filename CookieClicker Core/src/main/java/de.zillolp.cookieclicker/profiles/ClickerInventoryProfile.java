package de.zillolp.cookieclicker.profiles;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.custominventories.*;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.ShopType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ClickerInventoryProfile {
    private final CookieClicker plugin;
    private final Player player;
    private final UUID uuid;
    private final HashMap<CustomInventoryType, CustomInventory> customInventories = new HashMap<>();
    private long lastInventoryInteraction;

    public ClickerInventoryProfile(Player player, CookieClicker plugin) {
        this.plugin = plugin;
        this.player = player;
        uuid = player.getUniqueId();
        lastInventoryInteraction = System.currentTimeMillis();
        load();
    }

    public void load() {
        PluginConfig pluginConfig = plugin.getPluginConfig();
        LanguageConfig languageConfig = plugin.getLanguageConfig();
        for (CustomInventoryType customInventoryType : CustomInventoryType.values()) {
            ShopType shopType;
            CustomInventory customInventory = null;
            switch (customInventoryType) {
                case HOME:
                    customInventory = new HomeInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), 5 * 9, customInventoryType);
                    break;
                case DESIGN:
                    customInventory = new DesignInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), 3 * 9, customInventoryType);
                    break;
                case BLOCK_DESIGN:
                    shopType = ShopType.BLOCK_DESIGN;
                    customInventory = new BlockDesignInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), pluginConfig.getRows(shopType) * 9,
                            pluginConfig.getSkippedSlots(shopType), pluginConfig.getStartSlot(shopType), pluginConfig.getStopSlot(shopType), customInventoryType);
                    break;
                case HIT_PARTICLE_DESIGN:
                    shopType = ShopType.HIT_PARTICLE_DESIGN;
                    customInventory = new HitParticleDesignInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), pluginConfig.getRows(shopType) * 9,
                            pluginConfig.getSkippedSlots(shopType), pluginConfig.getStartSlot(shopType), pluginConfig.getStopSlot(shopType), customInventoryType);
                    break;
                case MENU_DESIGN:
                    shopType = ShopType.MENU_DESIGN;
                    customInventory = new MenuDesignInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), pluginConfig.getRows(shopType) * 9,
                            pluginConfig.getSkippedSlots(shopType), pluginConfig.getStartSlot(shopType), pluginConfig.getStopSlot(shopType), customInventoryType);
                    break;
                case SHOP:
                    shopType = ShopType.DEFAULT;
                    customInventory = new ShopInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), pluginConfig.getRows(shopType) * 9,
                            pluginConfig.getSkippedSlots(shopType), pluginConfig.getStartSlot(shopType), pluginConfig.getStopSlot(shopType), customInventoryType);
                    break;
                case PREMIUM_SHOP:
                    shopType = ShopType.PREMIUM;
                    customInventory = new PremiumShopInventory(plugin, player, languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()), pluginConfig.getRows(shopType) * 9,
                            pluginConfig.getSkippedSlots(shopType), pluginConfig.getStartSlot(shopType), pluginConfig.getStopSlot(shopType), customInventoryType);
                    break;
            }
            if (customInventory == null) {
                continue;
            }
            customInventories.put(customInventoryType, customInventory);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isOverLastInventoryInteraction(long delay) {
        return lastInventoryInteraction + delay > System.currentTimeMillis();
    }

    public void updateLastInventoryInteraction() {
        lastInventoryInteraction = System.currentTimeMillis();
    }

    public HashMap<CustomInventoryType, CustomInventory> getCustomInventories() {
        return customInventories;
    }
}
