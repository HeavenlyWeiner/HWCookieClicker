package de.zillolp.cookieclicker.customparticles;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.manager.VersionManager;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public abstract class CustomParticleEffect implements Runnable {
    protected final CookieClicker plugin;
    protected final BukkitScheduler bukkitScheduler;
    protected final Player player;
    protected final Location location;
    protected final boolean coloredDust;
    protected final Particle particle;
    protected final Color color;
    private final long tickSpeed;
    private BukkitTask bukkitTask;

    public CustomParticleEffect(CookieClicker plugin, Player player, Location location, boolean coloredDust, Particle particle, Color color, long tickSpeed) {
        this.plugin = plugin;
        this.player = player;
        this.location = location;
        this.coloredDust = coloredDust;
        this.particle = particle;
        this.color = color;
        this.tickSpeed = tickSpeed;
        bukkitScheduler = plugin.getServer().getScheduler();
    }

    public abstract void sendEffect();

    @Override
    public void run() {
        sendEffect();
    }

    protected void sendParticle(Location location, Particle particle, int amount, double motionX, double motionY, double motionZ, double speed) {
        if (coloredDust) {
            VersionManager versionManager = plugin.getVersionManager();
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
            if (versionManager.getVersionNumber() <= 20 && versionManager.getSubVersion() <= 5) {
                player.spawnParticle(Particle.valueOf("REDSTONE"), location, amount, dustOptions);
                return;
            }
            player.spawnParticle(Particle.DUST, location, amount, dustOptions);
            return;
        }

        Class<?> dataType = particle.getDataType();
        if (dataType == Float.class) {
            player.spawnParticle(particle, location, amount, motionX, motionY, motionZ, speed, 0.0f);
        } else if (dataType != Void.class) {
            player.spawnParticle(particle, location, amount, motionX, motionY, motionZ, speed, null);
        } else {
            player.spawnParticle(particle, location, amount, motionX, motionY, motionZ, speed);
        }
    }

    public void start() {
        stop();
        bukkitTask = bukkitScheduler.runTaskTimerAsynchronously(plugin, this, 0, tickSpeed);
    }

    public void stop() {
        if (bukkitTask == null || bukkitTask.isCancelled()) {
            return;
        }
        bukkitTask.cancel();
    }
}
