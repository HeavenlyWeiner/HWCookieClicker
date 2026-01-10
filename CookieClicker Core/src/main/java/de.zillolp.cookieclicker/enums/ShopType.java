package de.zillolp.cookieclicker.enums;

public enum ShopType {
    DEFAULT("default_shop", true),
    PREMIUM("premium_shop", true),
    BLOCK_DESIGN("block_design_shop", false),
    HIT_PARTICLE_DESIGN("hit_particle_design_shop", false),
    MENU_DESIGN("menu_design_shop", false),
    BOOSTER("booster_shop", true);

    private final String configSection;
    private final boolean infinitePrice;

    ShopType(String configSection, boolean infinitePrice) {
        this.configSection = configSection;
        this.infinitePrice = infinitePrice;
    }

    public String getConfigSection() {
        return configSection;
    }

    public boolean isInfinitePrice() {
        return infinitePrice;
    }
}
