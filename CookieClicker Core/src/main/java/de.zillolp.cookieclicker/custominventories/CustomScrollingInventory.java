package de.zillolp.cookieclicker.custominventories;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public abstract class CustomScrollingInventory extends CustomInventory {
    protected final ArrayList<Integer> skippedSlots;
    protected final int startSlot;
    protected final int stopSlot;
    protected final LinkedList<ItemStack> items = new LinkedList<>();
    protected int page;

    public CustomScrollingInventory(CookieClicker plugin, Player player, String title, int size, Integer[] skippedSlots, int startSlot, int stopSlot, CustomInventoryType customInventoryType) {
        super(plugin, player, title, size, customInventoryType);
        ArrayList<Integer> allSkippedSlots = new ArrayList<>(Arrays.asList(skippedSlots));
        allSkippedSlots.removeIf(number -> number < startSlot || number >= stopSlot);
        this.skippedSlots = allSkippedSlots;
        this.startSlot = startSlot;
        this.stopSlot = stopSlot;
        page = 0;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean hasNext() {
        return items.size() > (stopSlot - startSlot - skippedSlots.size()) * (page + 1);
    }

    protected boolean hasLast() {
        return page > 0;
    }

    public void nextPage() {
        if (!(hasNext())) {
            return;
        }
        page++;
    }

    public void lastPage() {
        if (!(hasLast())) {
            return;
        }
        page--;
    }

    public int getStartSlot() {
        return startSlot;
    }

    public int getStopSlot() {
        return stopSlot;
    }

    public Integer getItemNumber(int slot) {
        int count = 0;
        for (int number = startSlot; number <= stopSlot; number++) {
            if (!(skippedSlots.contains(number))) {
                count++;
            }
            if (number == slot) {
                return count + (page * (stopSlot - startSlot - skippedSlots.size() + 1));
            }
        }
        return 0;
    }

    public void loadPage() {
        int field = 0;
        for (int slot = startSlot; slot < stopSlot; slot++) {
            if (skippedSlots.contains(slot)) {
                continue;
            }
            int item = field + (stopSlot - startSlot - skippedSlots.size()) * page;
            if (item < items.size()) {
                inventory.setItem(slot, items.get(item));
            } else {
                inventory.setItem(slot, new ItemStack(Material.AIR));
            }
            field++;
        }
    }

    public LinkedList<ItemStack> getItems() {
        return items;
    }
}
