package de.zillolp.cookieclicker.listener.inventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.custominventories.CustomScrollingInventory;
import de.zillolp.cookieclicker.enums.*;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class HitParticleDesignInventoryListener extends CustomInventoryListener {
    public HitParticleDesignInventoryListener(CookieClicker plugin, CustomInventoryType customInventoryType) {
        super(plugin, customInventoryType);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        ItemStack currentItem = event.getCurrentItem();
        Material type = currentItem.getType();
        String displayName = currentItem.getItemMeta().getDisplayName();

        ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
        ClickerInventoryProfile clickerInventoryProfile = clickerPlayerManager.getInventoryProfile(player);
        CustomScrollingInventory customScrollingInventory = (CustomScrollingInventory) clickerInventoryProfile.getCustomInventories().get(customInventoryType);
        int slot = event.getSlot();
        HashMap<CustomInventoryType, CustomInventory> customInventories = clickerInventoryProfile.getCustomInventories();
        if (type == getItemType(CustomItemType.NEXT_PAGE) && slot == getItemSlot(CustomItemType.NEXT_PAGE) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.NEXT))) {
            soundManager.playSound(player, SoundType.NEXT);
            customScrollingInventory.nextPage();
            customScrollingInventory.update();
        } else if (type == getItemType(CustomItemType.LAST_PAGE) && slot == getItemSlot(CustomItemType.LAST_PAGE) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.BACK))) {
            soundManager.playSound(player, SoundType.BACK);
            if (customScrollingInventory.getPage() <= 0) {
                customInventories.get(CustomInventoryType.DESIGN).openInventory(player);
            } else {
                customScrollingInventory.lastPage();
                customScrollingInventory.update();
            }
        }

        if (!(customScrollingInventory.getItems().contains(currentItem))) {
            return;
        }
        PluginConfig pluginConfig = plugin.getPluginConfig();
        ShopType shopType = ShopType.HIT_PARTICLE_DESIGN;
        int id = customScrollingInventory.getItemNumber(slot);
        if (type != pluginConfig.getType(shopType, id)) {
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            InventoryAction inventoryAction = event.getAction();
            HashMap<Integer, Long> prices = clickerStatsProfile.getPrices(shopType);
            long price = prices.getOrDefault(id, 30L);
            if (displayName.equalsIgnoreCase(languageConfig.getPriceLanguage(PluginLanguage.PRICE_BUYABLE, shopType, id, price))) {
                clickerStatsProfile.removeCookies(price);
                prices.replace(id, price + pluginConfig.getBasePrice(shopType, id));
                clickerStatsProfile.getItems(shopType).replace(id, true);
                clickerStatsProfile.setParticleDesign(id);
                soundManager.playSound(player, SoundType.BUY_ALLOW);
                customScrollingInventory.update();
            } else if (displayName.equalsIgnoreCase(languageConfig.getPriceLanguage(PluginLanguage.PRICE_NOT_BUYABLE, shopType, id, price))) {
                soundManager.playSound(player, SoundType.BUY_DENY);
                player.sendMessage(languageConfig.getReplacedLanguageWithPrefix(PluginLanguage.MESSAGE_NOT_BUYABLE, uuid));
            } else if (displayName.equalsIgnoreCase(languageConfig.getPriceLanguage(PluginLanguage.ITEM_BOUGHT_SELECTED, shopType, id, price)) && inventoryAction == InventoryAction.PICKUP_HALF) {
                clickerStatsProfile.setParticleDesign(0);
                soundManager.playSound(player, SoundType.REMOVE_DESIGN);
                customScrollingInventory.update();
            } else if (displayName.equalsIgnoreCase(languageConfig.getPriceLanguage(PluginLanguage.ITEM_BOUGHT, shopType, id, price)) && inventoryAction == InventoryAction.PICKUP_ALL) {
                clickerStatsProfile.setParticleDesign(id);
                soundManager.playSound(player, SoundType.SELECT_DESIGN);
                customScrollingInventory.update();
            }
        });
    }
}
