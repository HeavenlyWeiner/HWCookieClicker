package de.zillolp.cookieclicker.custominventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.ShopType;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.Map;

public class HitParticleDesignInventory extends CustomScrollingInventory {
    private final ItemStack nextItem;
    private final ItemStack backItem;
    private final Material perClickType;
    private final Material cookiesType;
    private final int nextItemSlot;
    private final int backItemSlot;
    private final int perClickItemSlot;
    private final int cookiesItemSlot;


    public HitParticleDesignInventory(CookieClicker plugin, Player player, String title, int size, Integer[] skippedSlots, int startSlot, int stopSlot, CustomInventoryType customInventoryType) {
        super(plugin, player, title, size, skippedSlots, startSlot, stopSlot, customInventoryType);
        nextItem = itemBuilder.build(getItemType(CustomItemType.NEXT_PAGE), languageConfig.getTranslatedLanguage(PluginLanguage.NEXT), 1, "19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");
        backItem = itemBuilder.build(getItemType(CustomItemType.LAST_PAGE), languageConfig.getTranslatedLanguage(PluginLanguage.BACK), 1, "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9");

        perClickType = getItemType(CustomItemType.PER_CLICK);
        cookiesType = getItemType(CustomItemType.COOKIES);

        nextItemSlot = getItemSlot(CustomItemType.NEXT_PAGE);
        backItemSlot = getItemSlot(CustomItemType.LAST_PAGE);
        perClickItemSlot = getItemSlot(CustomItemType.PER_CLICK);
        cookiesItemSlot = getItemSlot(CustomItemType.COOKIES);
    }

    @Override
    public void create() {
        update();
        design();
    }

    @Override
    public void update() {
        LinkedList<ItemStack> items = getItems();
        items.clear();

        ShopType shopType = ShopType.HIT_PARTICLE_DESIGN;
        ClickerPlayerManager clickerPlayerManager = plugin.getClickerPlayerManager();

        ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuid);
        long cookies = clickerStatsProfile.getCookies();
        for (Map.Entry<Integer, Long> priceEntry : clickerStatsProfile.getPrices(shopType).entrySet()) {
            int id = priceEntry.getKey();
            long price = priceEntry.getValue();
            Material type = pluginConfig.getType(shopType, id);
            if (clickerStatsProfile.getItems(shopType).get(id)) {
                if (clickerStatsProfile.getParticleDesign() == id) {
                    items.add(itemBuilder.build(type, languageConfig.getPriceLanguage(PluginLanguage.ITEM_BOUGHT_SELECTED, shopType, id, price), 1, languageConfig.getPriceLanguages(PluginLanguage.ITEM_BOUGHT_SELECTED_INFO, shopType, id, price), true));
                    continue;
                }
                items.add(itemBuilder.build(type, languageConfig.getPriceLanguage(PluginLanguage.ITEM_BOUGHT, shopType, id, price), 1, languageConfig.getPriceLanguages(PluginLanguage.ITEM_BOUGHT_INFO, shopType, id, price), true));
                continue;
            }
            if (cookies >= price) {
                items.add(itemBuilder.build(type, languageConfig.getPriceLanguage(PluginLanguage.PRICE_BUYABLE, shopType, id, price), 1, languageConfig.getPriceLanguages(PluginLanguage.ITEM_INFO, shopType, id, price), true));
                continue;
            }
            items.add(itemBuilder.build(type, languageConfig.getPriceLanguage(PluginLanguage.PRICE_NOT_BUYABLE, shopType, id, price), 1, languageConfig.getPriceLanguages(PluginLanguage.ITEM_INFO, shopType, id, price), true));
        }

        loadPage();
        if (hasNext()) {
            inventory.setItem(nextItemSlot, nextItem);
        }
        if (getPage() <= 0 || hasLast()) {
            inventory.setItem(backItemSlot, backItem);
        }

        inventory.setItem(perClickItemSlot, itemBuilder.build(perClickType, languageConfig.getReplacedLanguage(PluginLanguage.PER_CLICK, uuid), 1));
        inventory.setItem(cookiesItemSlot, itemBuilder.build(cookiesType, languageConfig.getReplacedLanguage(PluginLanguage.YOUR_COOKIES, uuid), 1));
    }
}
