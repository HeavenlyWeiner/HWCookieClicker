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
                 CallableStatement stmt = conn.prepareCall("{ ? = call try_start_clicking(?, ?) }")) {

                stmt.registerOutParameter(1, Types.OTHER);
                stmt.setObject(2, playerUUID);
                stmt.setString(3, "minecraft");
                stmt.execute();

                Object result = stmt.getObject(1);
                if (result != null) {
                    String resultStr = result.toString();
                    // Парсим результат типа "(t,)" или "(f,telegram)"
                    boolean canClick = resultStr.contains("t");
                    String blockedBy = null;

                    if (!canClick && resultStr.contains("telegram")) {
                        blockedBy = "telegram";
                    }

                    future.complete(new PlatformLockResult(canClick, blockedBy));
                } else {
                    future.complete(new PlatformLockResult(true, null));
                }

            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to check platform lock for " + playerUUID, e);
                future.complete(new PlatformLockResult(true, null)); // В случае ошибки разрешаем клик
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
                 CallableStatement stmt = conn.prepareCall("{ call unlock_platform(?) }")) {

                stmt.setObject(1, playerUUID);
                stmt.execute();
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
                 CallableStatement stmt = conn.prepareCall("{ ? = call cleanup_old_locks() }")) {

                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.execute();

                int unlockedCount = stmt.getInt(1);
                future.complete(unlockedCount);

            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to cleanup old locks", e);
                future.complete(0);
            }
        });

        return future;
    }
}