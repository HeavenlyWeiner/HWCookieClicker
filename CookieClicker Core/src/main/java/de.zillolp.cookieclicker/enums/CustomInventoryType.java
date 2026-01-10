package de.zillolp.cookieclicker.enums;

public enum CustomInventoryType {
    HOME(PluginLanguage.HOME_TITLE),
    DESIGN(PluginLanguage.DESIGN_TITLE),
    BLOCK_DESIGN(PluginLanguage.BLOCK_DESIGN_TITLE),
    HIT_PARTICLE_DESIGN(PluginLanguage.HIT_PARTICLE_DESIGN_TITLE),
    MENU_DESIGN(PluginLanguage.MENU_DESIGN_TITLE),
    SHOP(PluginLanguage.SHOP_TITLE),
    PREMIUM_SHOP(PluginLanguage.PREMIUM_SHOP_TITLE);

    private final PluginLanguage pluginLanguage;

    CustomInventoryType(PluginLanguage pluginLanguage) {
        this.pluginLanguage = pluginLanguage;
    }

    public PluginLanguage getPluginLanguage() {
        return pluginLanguage;
    }
}
