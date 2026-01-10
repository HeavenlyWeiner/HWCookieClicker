package de.zillolp.cookieclicker.custominventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.interfaces.ItemBuilder;
import de.zillolp.cookieclicker.manager.DesignManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.UUID;
import java.util.stream.IntStream;

public abstract class CustomInventory {
    protected final CookieClicker plugin;
    protected final PluginConfig pluginConfig;
    protected final LanguageConfig languageConfig;
    protected final ItemBuilder itemBuilder;
    protected final Player player;
    protected final UUID uuid;
    protected final Inventory inventory;
    protected final String title;
    protected final int size;
    protected final CustomInventoryType customInventoryType;
    private final DesignManager designManager;
    private final BukkitScheduler bukkitScheduler;

    public CustomInventory(CookieClicker plugin, Player player, String title, int size, CustomInventoryType customInventoryType) {
        this.plugin = plugin;
        pluginConfig = plugin.getPluginConfig();
        languageConfig = plugin.getLanguageConfig();
        designManager = plugin.getDesignManager();
        itemBuilder = plugin.getItemBuilder();
        this.player = player;
        uuid = player.getUniqueId();
        this.title = title;
        this.size = size;
        this.customInventoryType = customInventoryType;
        inventory = Bukkit.createInventory(null, size, title);
        bukkitScheduler = plugin.getServer().getScheduler();
        bukkitScheduler.runTaskLaterAsynchronously(plugin, () -> create(), 4);
    }


    public void openInventory(Player player) {
        if (inventory == null) {
            return;
        }
        bukkitScheduler.runTaskAsynchronously(plugin, () -> update());
        player.openInventory(inventory);
    }

    public abstract void create();

    public abstract void update();

    public void reload() {
        inventory.clear();
        create();
    }

    public void design() {
        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack != null) {
                return;
            }
            inventory.setItem(slot, itemBuilder.build(designManager.getMenuDesign(uuid), languageConfig.getTranslatedLanguage(PluginLanguage.DESIGN_GLASS_NAME), 1));
        });
    }

    protected Material getItemType(CustomItemType customItemType) {
        return pluginConfig.getType(customInventoryType, customItemType);
    }

    protected int getItemSlot(CustomItemType customItemType) {
        return pluginConfig.getSlot(customInventoryType, customItemType);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
