package de.zillolp.cookieclicker.profiles;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.ShopType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ClickerStatsProfile {
    private final DatabaseManager databaseManager;
    private final UUID uuid;
    private final HashMap<ShopType, HashMap<Integer, Long>> shopPrices = new HashMap<>();
    private final HashMap<ShopType, HashMap<Integer, Boolean>> shopItems = new HashMap<>();
    private final AtomicLong cookies;
    private final AtomicLong perClick;
    private final AtomicLong clickerClicks;
    private final AtomicLong blockDesign;
    private final AtomicLong particleDesign;
    private final AtomicLong menuDesign;
    private final AtomicBoolean dirty;
    private volatile String name;

    public ClickerStatsProfile(UUID uuid, CookieClicker plugin) {
        databaseManager = plugin.getDatabaseManager();
        this.uuid = uuid;
        name = "?";
        cookies = new AtomicLong(0L);
        perClick = new AtomicLong(1L);
        clickerClicks = new AtomicLong(0L);
        blockDesign = new AtomicLong(0L);
        particleDesign = new AtomicLong(0L);
        menuDesign = new AtomicLong(0L);
        dirty = new AtomicBoolean(false);
        databaseManager.loadClickerStatsProfile(this, true);
    }

    public ClickerStatsProfile(UUID uuid, String name, CookieClicker plugin) {
        databaseManager = plugin.getDatabaseManager();
        this.uuid = uuid;
        this.name = name;
        cookies = new AtomicLong(0L);
        perClick = new AtomicLong(1L);
        clickerClicks = new AtomicLong(0L);
        blockDesign = new AtomicLong(0L);
        particleDesign = new AtomicLong(0L);
        menuDesign = new AtomicLong(0L);
        dirty = new AtomicBoolean(false);
        if (!(databaseManager.playerExists(uuid))) {
            databaseManager.loadDefaultShops(this);
            databaseManager.saveClickerStatsProfile(this);
            return;
        }
        databaseManager.loadClickerStatsProfile(this, false);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        markDirty();
    }

    public long getCookies() {
        return cookies.get();
    }

    public void setCookies(long cookies) {
        this.cookies.set(cookies);
        markDirty();
    }

    public void addCookies(long cookies) {
        long newValue;
        long current = this.cookies.get();
        if (cookies > 0 && current > Long.MAX_VALUE - cookies) {
            newValue = Long.MAX_VALUE;
        } else if (cookies < 0 && current < Long.MIN_VALUE - cookies) {
            newValue = Long.MIN_VALUE;
        } else {
            newValue = current + cookies;
        }

        this.cookies.set(newValue);
        markDirty();
    }

    public void removeCookies(long cookies) {
        addCookies(-cookies);
    }

    public long getPerClick() {
        return perClick.get();
    }

    public void setPerClick(long perClick) {
        this.perClick.set(perClick);
        markDirty();
    }

    public void addPerClick(long perClick) {
        long newValue;
        long current = this.perClick.get();

        if (perClick > 0 && current > Long.MAX_VALUE - perClick) {
            newValue = Long.MAX_VALUE;
        } else if (perClick < 0 && current < Long.MIN_VALUE - perClick) {
            newValue = Long.MIN_VALUE;
        } else {
            newValue = current + perClick;
        }

        this.perClick.set(newValue);
        markDirty();
    }

    public long getClickerClicks() {
        return clickerClicks.get();
    }

    public void setClickerClicks(long clickerClicks) {
        this.clickerClicks.set(clickerClicks);
        markDirty();
    }

    public void addClickerClicks(long clickerClicks) {
        long newValue;
        long current = this.clickerClicks.get();

        if (clickerClicks > 0 && current > Long.MAX_VALUE - clickerClicks) {
            newValue = Long.MAX_VALUE;
        } else if (clickerClicks < 0 && current < Long.MIN_VALUE - clickerClicks) {
            newValue = Long.MIN_VALUE;
        } else {
            newValue = current + clickerClicks;
        }

        this.clickerClicks.set(newValue);
        markDirty();
    }

    public long getBlockDesign() {
        return blockDesign.get();
    }

    public void setBlockDesign(long blockDesign) {
        this.blockDesign.set(blockDesign);
        markDirty();
    }

    public long getParticleDesign() {
        return particleDesign.get();
    }

    public void setParticleDesign(long particleDesign) {
        this.particleDesign.set(particleDesign);
        markDirty();
    }

    public long getMenuDesign() {
        return menuDesign.get();
    }

    public void setMenuDesign(long menuDesign) {
        this.menuDesign.set(menuDesign);
        markDirty();
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public void markDirty() {
        dirty.set(true);
    }

    public void markClean() {
        dirty.set(false);
    }


    public HashMap<Integer, Long> getPrices(ShopType shopType) {
        return shopPrices.getOrDefault(shopType, new HashMap<>());
    }

    public HashMap<Integer, Boolean> getItems(ShopType shopType) {
        return shopItems.getOrDefault(shopType, new HashMap<>());
    }

    public HashMap<ShopType, HashMap<Integer, Long>> getShopPrices() {
        return shopPrices;
    }

    public HashMap<ShopType, HashMap<Integer, Boolean>> getShopItems() {
        return shopItems;
    }
}
