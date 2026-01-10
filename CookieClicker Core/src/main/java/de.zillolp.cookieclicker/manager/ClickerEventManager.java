package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.clickerevents.ClickerEvent;
import de.zillolp.cookieclicker.clickerevents.CookieExplosionEvent;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import org.bukkit.entity.Item;

import java.util.HashMap;
import java.util.UUID;

public class ClickerEventManager {
    private final HashMap<UUID, HashMap<ClickerEventType, ClickerEvent>> activeEvents = new HashMap<>();

    public void activateEvent(UUID uuid, ClickerEvent clickerEvent) {
        HashMap<ClickerEventType, ClickerEvent> events;
        if (activeEvents.containsKey(uuid)) {
            events = activeEvents.get(uuid);
        } else {
            events = new HashMap<>();
            activeEvents.put(uuid, events);
        }
        events.put(clickerEvent.getClickerEventType(), clickerEvent);
    }

    public void deactivateEvents(UUID uuid) {
        if (!activeEvents.containsKey(uuid)) {
            return;
        }
        for (ClickerEvent clickerEvent : activeEvents.get(uuid).values()) {
            if (clickerEvent instanceof CookieExplosionEvent) {
                CookieExplosionEvent cookieExplosionEvent = (CookieExplosionEvent) clickerEvent;
                for (Item item : cookieExplosionEvent.getItems()) {
                    item.remove();
                }
            }
        }
        activeEvents.remove(uuid);
    }

    public boolean isActive(UUID uuid, ClickerEventType clickerEventType) {
        return activeEvents.containsKey(uuid) && activeEvents.get(uuid).containsKey(clickerEventType);
    }

    public HashMap<UUID, HashMap<ClickerEventType, ClickerEvent>> getActiveEvents() {
        return activeEvents;
    }
}
