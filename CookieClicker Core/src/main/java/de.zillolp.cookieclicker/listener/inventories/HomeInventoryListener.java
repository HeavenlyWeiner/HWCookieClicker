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

public class HomeInventoryListener extends CustomInventoryListener {

    public HomeInventoryListener(CookieClicker plugin, CustomInventoryType customInventoryType) {
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
        if (type == getItemType(CustomItemType.SHOP) && slot == getItemSlot(CustomItemType.SHOP) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.SHOP))) {
            CustomInventory customInventory = customInventories.get(CustomInventoryType.SHOP);
            if (customInventory == null) {
                return;
            }
            soundManager.playSound(player, SoundType.SHOP);
            customInventory.openInventory(player);
        } else if (type == getItemType(CustomItemType.DESIGN) && slot == getItemSlot(CustomItemType.DESIGN) && displayName.equalsIgnoreCase(languageConfig.getTranslatedLanguage(PluginLanguage.DESIGN))) {
            CustomInventory customInventory = customInventories.get(CustomInventoryType.DESIGN);
            if (customInventory == null) {
                return;
            }
            soundManager.playSound(player, SoundType.DESIGN);
            customInventory.openInventory(player);
        }
    }
}
