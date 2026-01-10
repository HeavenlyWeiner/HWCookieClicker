package de.zillolp.cookieclicker.listener.inventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.SoundType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DesignInventoryListener extends CustomInventoryListener {

    public DesignInventoryListener(CookieClicker plugin, CustomInventoryType customInventoryType) {
        super(plugin, customInventoryType);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        Material type = currentItem.getType();
        int slot = event.getSlot();
        String displayName = currentItem.getItemMeta().getDisplayName();
        HashMap<CustomInventoryType, CustomInventory> customInventories = clickerPlayerManager.getInventoryProfile(player).getCustomInventories();
        if (type == getItemType(CustomItemType.BLOCK_DESIGN) && slot == getItemSlot(CustomItemType.BLOCK_DESIGN) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.BLOCK_DESIGN))) {
            soundManager.playSound(player, SoundType.BLOCK_DESIGN);
            customInventories.get(CustomInventoryType.BLOCK_DESIGN).openInventory(player);
        } else if (type == getItemType(CustomItemType.HIT_PARTICLE_DESIGN) && slot == getItemSlot(CustomItemType.HIT_PARTICLE_DESIGN) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.HIT_PARTICLE_DESIGN))) {
            soundManager.playSound(player, SoundType.HIT_PARTICLE_DESIGN);
            customInventories.get(CustomInventoryType.HIT_PARTICLE_DESIGN).openInventory(player);
        } else if (type == getItemType(CustomItemType.MENU_DESIGN) && slot == getItemSlot(CustomItemType.MENU_DESIGN) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.MENU_DESIGN))) {
            soundManager.playSound(player, SoundType.MENU_DESIGN);
            customInventories.get(CustomInventoryType.MENU_DESIGN).openInventory(player);
        } else if (type == getItemType(CustomItemType.LAST_PAGE) && slot == getItemSlot(CustomItemType.LAST_PAGE) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.BACK))) {
            soundManager.playSound(player, SoundType.BACK);
            customInventories.get(CustomInventoryType.HOME).openInventory(player);
        }
    }
}
