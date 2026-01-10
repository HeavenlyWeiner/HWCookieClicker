package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.clickerevents.ClickerEvent;
import de.zillolp.cookieclicker.clickerevents.CookieExplosionEvent;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import de.zillolp.cookieclicker.manager.ClickerEventManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;

public class EventUpdater extends CustomUpdater {
    private final ClickerEventManager clickerEventManager;

    public EventUpdater(CookieClicker plugin) {
        super(plugin, false, 20);
        clickerEventManager = plugin.getClickerEventManager();
    }

    @Override
    protected void tick() {
        HashMap<UUID, HashMap<ClickerEventType, ClickerEvent>> activeEvents = clickerEventManager.getActiveEvents();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            if (!activeEvents.containsKey(uuid)) {
                continue;
            }
            HashMap<ClickerEventType, ClickerEvent> clickerEvents = activeEvents.get(uuid);
            Iterator<Map.Entry<ClickerEventType, ClickerEvent>> iterator = clickerEvents.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ClickerEventType, ClickerEvent> clickerEventEntry = iterator.next();
                ClickerEvent clickerEvent = clickerEventEntry.getValue();
                if (clickerEvent == null) {
                    continue;
                }
                clickerEvent.removeTime(1);
                if (clickerEvent.getTime() > 0) {
                    continue;
                }
                if (clickerEvent instanceof CookieExplosionEvent) {
                    ArrayList<Item> items = ((CookieExplosionEvent) clickerEvent).getItems();
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        for (Item item : items) {
                            item.remove();
                        }
                        items.clear();
                    });
                }
                iterator.remove();
            }
        }
    }
}
