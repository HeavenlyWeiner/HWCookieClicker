package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public abstract class CustomUpdater implements Runnable {
    protected final CookieClicker plugin;
    protected final BukkitScheduler bukkitScheduler;
    private final BukkitTask bukkitTask;

    public CustomUpdater(CookieClicker plugin, boolean isAsync, long ticks) {
        this.plugin = plugin;
        bukkitScheduler = plugin.getServer().getScheduler();
        if (isAsync) {
            bukkitTask = bukkitScheduler.runTaskTimerAsynchronously(plugin, this, 0, ticks);
        } else {
            bukkitTask = bukkitScheduler.runTaskTimer(plugin, this, 0, ticks);
        }
    }

    @Override
    public void run() {
        tick();
    }

    protected abstract void tick();

    public void stop() {
        if (bukkitTask == null || bukkitTask.isCancelled()) {
            return;
        }
        bukkitTask.cancel();
    }
}
