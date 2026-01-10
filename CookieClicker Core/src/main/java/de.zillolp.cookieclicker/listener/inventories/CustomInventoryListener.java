package de.zillolp.cookieclicker.listener.inventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CustomInventoryListener implements Listener {
    protected final CookieClicker plugin;
    protected final PluginConfig pluginConfig;
    protected final LanguageConfig languageConfig;
    protected final CustomInventoryType customInventoryType;
    protected final ClickerPlayerManager clickerPlayerManager;
    protected final SoundManager soundManager;

    public CustomInventoryListener(CookieClicker plugin, CustomInventoryType customInventoryType) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        soundManager = plugin.getSoundManager();
        this.customInventoryType = customInventoryType;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        String inventoryTitle = plugin.getReflectionUtil().getInventoryTitle(event);
        if (inventoryTitle != null && (!(inventoryTitle.equalsIgnoreCase(languageConfig.getTranslatedLanguage(customInventoryType.getPluginLanguage()))))) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ClickerInventoryProfile clickerInventoryProfile = clickerPlayerManager.getInventoryProfile(player);
        CustomInventory customInventory = clickerInventoryProfile.getCustomInventories().get(customInventoryType);
        if (customInventory == null) {
            return;
        }
        if (clickedInventory != customInventory.getInventory()) {
            return;
        }
        event.setCancelled(true);
        if (clickerInventoryProfile.isOverLastInventoryInteraction(63)) {
            return;
        }
        clickerInventoryProfile.updateLastInventoryInteraction();
        ItemMeta itemMeta = currentItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        onClick(event);
    }

    protected Material getItemType(CustomItemType customItemType) {
        return pluginConfig.getType(customInventoryType, customItemType);
    }

    protected int getItemSlot(CustomItemType customItemType) {
        return pluginConfig.getSlot(customInventoryType, customItemType);
    }

    public abstract void onClick(InventoryClickEvent event);
}
