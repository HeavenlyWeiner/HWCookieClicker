package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;
import de.zillolp.cookieclicker.database.PostgreSQLConnector;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PlatformLockManager {
    private final CookieClicker plugin;
    private final PostgreSQLConnector postgreSQLConnector;
    private final BukkitScheduler scheduler;
    private final boolean enabled;

    public static class PlatformLockResult {
        private final boolean canClick;
        private final String blockedBy;

        public PlatformLockResult(boolean canClick, String blockedBy) {
            this.canClick = canClick;
            this.blockedBy = blockedBy;
        }

        public boolean canClick() {
            return canClick;
        }

        public boolean isBlockedByTelegram() {
            return !canClick && "telegram".equalsIgnoreCase(blockedBy);
        }

        public boolean isBlockedByMinecraft() {
            return !canClick && "minecraft".equalsIgnoreCase(blockedBy);
        }

        public String getBlockedBy() {
            return blockedBy;
        }
    }

    public PlatformLockManager(CookieClicker plugin) {
        this.plugin = plugin;
        this.postgreSQLConnector = plugin.getPostgreSQLConnector();
        this.scheduler = plugin.getServer().getScheduler();

        PluginConfig pluginConfig = plugin.getPluginConfig();
        this.enabled = pluginConfig.getFileConfiguration().getBoolean("platform_lock.enabled", true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Проверяет, может ли игрок кликать на текущей платформе
     * @param playerUUID UUID игрока
     * @return CompletableFuture с результатом проверки
     */
    public CompletableFuture<PlatformLockResult> canClick(UUID playerUUID) {
        if (!enabled) {
            return CompletableFuture.completedFuture(new PlatformLockResult(true, null));
        }

        CompletableFuture<PlatformLockResult> future = new CompletableFuture<>();

        scheduler.runTaskAsynchronously(plugin, () -> {
            try (Connection conn = postgreSQLConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT can_click, blocked_by FROM try_start_clicking(?::uuid, 'minecraft')")) {

                stmt.setString(1, playerUUID.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        boolean canClick = rs.getBoolean("can_click");
                        String blockedBy = rs.getString("blocked_by");
                        future.complete(new PlatformLockResult(canClick, blockedBy));
                    } else {
                        future.complete(new PlatformLockResult(true, null));
                    }
                }

            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to check platform lock for " + playerUUID, e);
                future.complete(new PlatformLockResult(true, null));
            }
        });

        return future;
    }

    /**
     * Разблокирует игрока (admin команда)
     * @param playerUUID UUID игрока
     * @return CompletableFuture<Boolean> успешность операции
     */
    public CompletableFuture<Boolean> unlockPlayer(UUID playerUUID) {
        if (!enabled) {
            return CompletableFuture.completedFuture(true);
        }

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        scheduler.runTaskAsynchronously(plugin, () -> {
            try (Connection conn = postgreSQLConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT unlock_platform(?::uuid)")) {

                stmt.setString(1, playerUUID.toString());
                stmt.executeQuery();
                future.complete(true);

            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to unlock player " + playerUUID, e);
                future.complete(false);
            }
        });

        return future;
    }

    /**
     * Очищает старые блокировки (вызывается по расписанию)
     * @return CompletableFuture<Integer> количество разблокированных записей
     */
    public CompletableFuture<Integer> cleanupOldLocks() {
        if (!enabled) {
            return CompletableFuture.completedFuture(0);
        }

        CompletableFuture<Integer> future = new CompletableFuture<>();

        scheduler.runTaskAsynchronously(plugin, () -> {
            try (Connection conn = postgreSQLConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT cleanup_old_locks()")) {

                try (ResultSet rs = stmt.executeQuery()) {
                    int unlockedCount = rs.next() ? rs.getInt(1) : 0;
                    future.complete(unlockedCount);
                }

            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to cleanup old locks", e);
                future.complete(0);
            }
        });

        return future;
    }
}