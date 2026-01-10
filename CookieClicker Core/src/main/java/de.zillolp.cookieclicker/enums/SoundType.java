package de.zillolp.cookieclicker.enums;

public enum SoundType {
    CLICK_ALLOW,
    CLICK_DENY,
    OPEN_INVENTORY,
    SHOP,
    DESIGN,
    SELECT_DESIGN,
    REMOVE_DESIGN,
    PREMIUM_ALLOW,
    PREMIUM_DENY,
    NEXT,
    BACK,
    BUY_ALLOW,
    BUY_DENY,
    STATS_INFO,
    BLOCK_DESIGN,
    HIT_PARTICLE_DESIGN,
    MENU_DESIGN,
    GOLDEN_COOKIE,
    COOKIE_EXPLOSION;

    private String sound;
    private float volume;
    private float pitch;

    public String getSound() {
        return sound;
    }

    public void setSound(String soundName) {
        sound = soundName;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}
