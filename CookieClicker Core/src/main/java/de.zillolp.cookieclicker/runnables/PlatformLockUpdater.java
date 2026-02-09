package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Platform Lock Updater
 * Периодическая очистка старых блокировок платформ
 * Запускается каждую минуту (1200 ticks)
 */
public class PlatformLockUpdater extends CustomUpdater {

    public PlatformLockUpdater(CookieClicker plugin) {
        super(plugin, false, 1200); // Каждую минуту (1200 ticks = 60 секунд)
    }

    @Override
    protected void tick() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Используем PlatformLockManager для очистки
                plugin.getPlatformLockManager().cleanupOldLocks().thenAccept(unlockedCount -> {
                    if (unlockedCount > 0) {
                        plugin.getLogger().info(String.format(
                                "🔓 Auto-unlocked %d player(s) due to inactivity timeout (5 min)",
                                unlockedCount
                        ));
                    }
                });
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error cleaning up platform locks", e);
            }
        });
    }
}
