package de.zillolp.cookieclicker.customevents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CookieClickerInteractEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Location location;
    private final InteractType interactType;
    private final boolean blockPlaced;
    private boolean cancel;

    public CookieClickerInteractEvent(Player player, Location location, InteractType interactType, boolean blockPlaced) {
        super(false);
        this.player = player;
        this.location = location;
        this.interactType = interactType;
        this.blockPlaced = blockPlaced;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public InteractType getInteractType() {
        return interactType;
    }

    public boolean isBlockPlaced() {
        return blockPlaced;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public enum InteractType {
        LEFT_CLICK, LEFT_CLICK_BREAK, ADVENTURE_LEFT_CLICK, HAND_RIGHT_CLICK, OFF_HAND_RIGHT_CLICK
    }
}
