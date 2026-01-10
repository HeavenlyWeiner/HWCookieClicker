package de.zillolp.cookieclicker.enums;

public enum PluginPermission {
    ADMIN_PERMISSION("cookieclicker.admin"),
    PREMIUM_PERMISSION("cookieclicker.premium");

    private final String defaultPermission;

    PluginPermission(String defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    public String getDefaultPermission() {
        return defaultPermission;
    }

}
