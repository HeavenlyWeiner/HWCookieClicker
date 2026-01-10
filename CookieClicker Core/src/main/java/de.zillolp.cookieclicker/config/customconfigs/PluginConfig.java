package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.CustomInventoryType;
import de.zillolp.cookieclicker.enums.CustomItemType;
import de.zillolp.cookieclicker.enums.CustomParticleEffectType;
import de.zillolp.cookieclicker.enums.ShopType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

public class PluginConfig extends CustomConfig {
    private final String itemsPath = "items.";
    private final String optionsPath = "options.";

    public PluginConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public boolean hasClickerHolograms() {
        return fileConfiguration.getBoolean("Clicker holograms", true);
    }

    public boolean hasColoredDust(ShopType shopType, long id) {
        return fileConfiguration.getBoolean(shopType.getConfigSection() + "." + id + ".colored_dust", false);
    }

    public Particle getParticle(ShopType shopType, long id) {
        try {
            return Particle.valueOf(fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".particle_type", Particle.CLOUD.name()));
        } catch (IllegalArgumentException ignored) {
        }
        return Particle.CLOUD;
    }

    public Particle getSecondParticle(ShopType shopType, long id) {
        try {
            return Particle.valueOf(fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".second_particle_type", Particle.CLOUD.name()));
        } catch (IllegalArgumentException ignored) {
        }
        return Particle.CLOUD;
    }

    public Color getColor(ShopType shopType, long id) {
        int red = fileConfiguration.getInt(shopType.getConfigSection() + "." + id + ".color.red", 0);
        int green = fileConfiguration.getInt(shopType.getConfigSection() + "." + id + ".color.green", 0);
        int blue = fileConfiguration.getInt(shopType.getConfigSection() + "." + id + ".color.blue", 0);
        return Color.fromRGB(red, green, blue);
    }

    public int getResetTimerTime() {
        return fileConfiguration.getInt("Resettimer", 60) * 60;
    }

    public int getCacheSynchronizationTime() {
        return fileConfiguration.getInt("Cache synchronization", 10) * 1200;
    }

    public int getMaximumClicksPerSecond() {
        return fileConfiguration.getInt("Maximum CPS", 15);
    }

    public long getAFKCooldownSeconds() {
        return fileConfiguration.getLong("AFK cooldown seconds", 15) * 1000;
    }

    public int getGoldenCookieRange() {
        return fileConfiguration.getInt("Golden cookie range", 10000);
    }

    public int getCookieExplosionRange() {
        return fileConfiguration.getInt("Cookie explosion range", 5000);
    }

    public int getCookieAmount() {
        return fileConfiguration.getInt("Cookie amount", 20);
    }

    public long getCookiesPerCookieMultiplier() {
        return fileConfiguration.getLong("Cookies per cookie multiplier", 10);
    }

    public int getExplosionDurationSeconds() {
        return fileConfiguration.getInt("Explosion duration seconds", 30);
    }

    public int getGoldenDurationSeconds() {
        return fileConfiguration.getInt("Golden duration seconds", 60);
    }

    public int getPerClickMultiplier() {
        return fileConfiguration.getInt("Per click multiplier", 2);
    }

    public int getRows(ShopType shopType) {
        return fileConfiguration.getInt(optionsPath + shopType.getConfigSection() + ".rows", 6);
    }

    public int getStartSlot(ShopType shopType) {
        return fileConfiguration.getInt(optionsPath + shopType.getConfigSection() + ".start_slot", 0);
    }

    public int getStopSlot(ShopType shopType) {
        return fileConfiguration.getInt(optionsPath + shopType.getConfigSection() + ".stop_slot", 45);
    }

    public Integer[] getSkippedSlots(ShopType shopType) {
        return fileConfiguration.getIntegerList(optionsPath + shopType.getConfigSection() + ".skipped_slots").toArray(new Integer[0]);
    }

    public Material getType(CustomInventoryType customInventoryType, CustomItemType customItemType) {
        String typeName = fileConfiguration.getString(itemsPath + customInventoryType.name().toLowerCase() + "." + customItemType.name().toLowerCase() + ".item_type", Material.AIR.name());
        Material type = Material.matchMaterial(typeName);
        if (type == null) {
            return Material.BARRIER;
        }
        return type;
    }

    public Material getType(ShopType shopType, long id) {
        String typeName = getTypeName(shopType, id);
        if (typeName.equalsIgnoreCase("COOKIE_HEAD") || typeName.equalsIgnoreCase("REAL_PLAYER_HEAD")) {
            return Material.PLAYER_HEAD;
        }
        Material type = Material.matchMaterial(typeName);
        if (type == null) {
            return Material.BARRIER;
        }
        return type;
    }

    public String getTypeName(ShopType shopType, long id) {
        return fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".item_type", Material.BARRIER.name());
    }

    public Material getBlockType(ShopType shopType, long id) {
        String typeName = fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".block_type", Material.COMMAND_BLOCK.name());
        if (typeName.equalsIgnoreCase("COOKIE_HEAD") || typeName.equalsIgnoreCase("REAL_PLAYER_HEAD")) {
            return Material.PLAYER_HEAD;
        }
        Material type = Material.matchMaterial(typeName);
        if (type == null || (!(type.isBlock()))) {
            return Material.COMMAND_BLOCK;
        }
        return type;
    }

    public String getSkullTexture(ShopType shopType, long id) {
        String skullTexture = fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".skull_texture", "");
        if (skullTexture.isEmpty()) {
            return "http://textures.minecraft.net/texture/fbb30fd1214d7eb0bfec256a406395d13bb8f32b42c932a582b60e421f15421";
        }
        return skullTexture;
    }

    public CustomParticleEffectType getCustomParticleEffectType(ShopType shopType, long id) {
        String typeName = fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".animation_type", CustomParticleEffectType.SPIRAL.name()).toUpperCase();
        try {
            return CustomParticleEffectType.valueOf(typeName);
        } catch (IllegalArgumentException ignored) {
            return CustomParticleEffectType.SPIRAL;
        }
    }

    public int getSlot(CustomInventoryType customInventoryType, CustomItemType customItemType) {
        return fileConfiguration.getInt(itemsPath + customInventoryType.name().toLowerCase() + "." + customItemType.name().toLowerCase() + ".slot", 0);
    }

    public long getBasePrice(ShopType shopType, int id) {
        return fileConfiguration.getLong(shopType.getConfigSection() + "." + id + ".base_price", 999999999);
    }

    public double getPriceMultiplier(ShopType shopType, int id) {
        return fileConfiguration.getDouble(shopType.getConfigSection() + "." + id + ".price_multiplier", 2.0);
    }

    public long getAddClicks(ShopType shopType, int id) {
        return fileConfiguration.getLong(shopType.getConfigSection() + "." + id + ".add_clicks", 0);
    }

    public String getDisplayName(ShopType shopType, int id) {
        return fileConfiguration.getString(shopType.getConfigSection() + "." + id + ".display_name", "NULL");
    }
}
