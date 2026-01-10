package de.zillolp.cookieclicker.custominventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DesignInventory extends CustomInventory {
    private final ItemStack blockDesignItem;
    private final ItemStack particleItem;
    private final ItemStack menuDesignItem;
    private final ItemStack backItem;
    private final int blockDesignItemSlot;
    private final int particleItemSlot;
    private final int menuDesignItemSlot;
    private final int backItemSlot;

    public DesignInventory(CookieClicker plugin, Player player, String title, int size, CustomInventoryType customInventoryType) {
        super(plugin, player, title, size, customInventoryType);

        blockDesignItem = itemBuilder.build(getItemType(CustomItemType.BLOCK_DESIGN), languageConfig.getTranslatedLanguage(PluginLanguage.BLOCK_DESIGN), 1);
        particleItem = itemBuilder.build(getItemType(CustomItemType.HIT_PARTICLE_DESIGN), languageConfig.getTranslatedLanguage(PluginLanguage.HIT_PARTICLE_DESIGN), 1);
        menuDesignItem = itemBuilder.build(getItemType(CustomItemType.MENU_DESIGN), languageConfig.getTranslatedLanguage(PluginLanguage.MENU_DESIGN), 1);
        backItem = itemBuilder.build(getItemType(CustomItemType.LAST_PAGE), languageConfig.getTranslatedLanguage(PluginLanguage.BACK), 1, "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9");

        blockDesignItemSlot = getItemSlot(CustomItemType.BLOCK_DESIGN);
        particleItemSlot = getItemSlot(CustomItemType.HIT_PARTICLE_DESIGN);
        menuDesignItemSlot = getItemSlot(CustomItemType.MENU_DESIGN);
        backItemSlot = getItemSlot(CustomItemType.LAST_PAGE);
    }

    @Override
    public void create() {
        inventory.setItem(blockDesignItemSlot, blockDesignItem);
        inventory.setItem(particleItemSlot, particleItem);
        inventory.setItem(menuDesignItemSlot, menuDesignItem);
        inventory.setItem(backItemSlot, backItem);
        design();
    }

    @Override
    public void update() {

    }
}
