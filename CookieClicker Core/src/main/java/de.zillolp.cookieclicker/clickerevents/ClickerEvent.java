package de.zillolp.cookieclicker.clickerevents;

import de.zillolp.cookieclicker.enums.ClickerEventType;
import org.bukkit.entity.Player;

public abstract class ClickerEvent {
    protected final Player player;
    private final ClickerEventType clickerEventType;
    private double time;

    public ClickerEvent(Player player, ClickerEventType clickerEventType, double time) {
        this.player = player;
        this.clickerEventType = clickerEventType;
        this.time = time;
    }

    public Player getPlayer() {
        return player;
    }

    public ClickerEventType getClickerEventType() {
        return clickerEventType;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void removeTime(double time) {
        setTime(getTime() - time);
    }
}
