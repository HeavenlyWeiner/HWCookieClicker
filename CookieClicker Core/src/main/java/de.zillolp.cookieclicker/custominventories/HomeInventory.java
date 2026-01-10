package de.zillolp.cookieclicker.custominventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HomeInventory extends CustomInventory {
    private final ItemStack shopItem;
    private final ItemStack designItem;
    private final Material perClickType;
    private final Material cookiesType;
    private final int shopItemSlot;
    private final int designItemSlot;
    private final int perClickItemSlot;
    private final int cookiesItemSlot;

    public HomeInventory(CookieClicker plugin, Player player, String title, int size, CustomInventoryType customInventoryType) {
        super(plugin, player, title, size, customInventoryType);

        shopItem = itemBuilder.build(getItemType(CustomItemType.SHOP), languageConfig.getTranslatedLanguage(PluginLanguage.SHOP), 1);
        designItem = itemBuilder.build(getItemType(CustomItemType.DESIGN), languageConfig.getTranslatedLanguage(PluginLanguage.DESIGN), 1);

        perClickType = getItemType(CustomItemType.PER_CLICK);
        cookiesType = getItemType(CustomItemType.COOKIES);

        shopItemSlot = getItemSlot(CustomItemType.SHOP);
        designItemSlot = getItemSlot(CustomItemType.DESIGN);
        perClickItemSlot = getItemSlot(CustomItemType.PER_CLICK);
        cookiesItemSlot = getItemSlot(CustomItemType.COOKIES);
    }

    @Override
    public void create() {
        inventory.setItem(shopItemSlot, shopItem);
        inventory.setItem(designItemSlot, designItem);
        update();
        design();
    }

    @Override
    public void update() {
        inventory.setItem(perClickItemSlot, itemBuilder.build(perClickType, languageConfig.getReplacedLanguage(PluginLanguage.PER_CLICK, uuid), 1));
        inventory.setItem(cookiesItemSlot, itemBuilder.build(cookiesType, languageConfig.getReplacedLanguage(PluginLanguage.YOUR_COOKIES, uuid), 1));
    }
}
