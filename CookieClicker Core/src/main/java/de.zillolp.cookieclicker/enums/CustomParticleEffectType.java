package de.zillolp.cookieclicker.enums;

public enum CustomParticleEffectType {
    SPIRAL(9500),
    CIRCLE_RINGS(3250),
    EXPLOSION_WAVE(3000),
    STAR_BURST(4000),
    MAGICAL_WHIRLWIND(8500);

    private final long delay;

    CustomParticleEffectType(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}
