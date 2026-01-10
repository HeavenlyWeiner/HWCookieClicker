package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.PluginConfig;

import java.util.LinkedHashMap;

public class ResetTimerUpdater extends CustomUpdater {
    private final PluginConfig pluginConfig;
    private final StatsWallUpdater statsWallUpdater;
    private int time;

    public ResetTimerUpdater(CookieClicker plugin) {
        super(plugin, true, 20);
        pluginConfig = plugin.getPluginConfig();
        statsWallUpdater = plugin.getStatsWallUpdater();
        time = pluginConfig.getResetTimerTime();
    }

    @Override
    protected void tick() {
        time--;
        if (time != 0) {
            return;
        }
        resetTime();
        LinkedHashMap<String, Long> cachedTimedData = statsWallUpdater.getCachedTimedData();
        if (cachedTimedData.isEmpty()) {
            return;
        }
        cachedTimedData.clear();
        statsWallUpdater.updateTimeStatsWall();
    }

    public int getTime() {
        return time;
    }

    public void resetTime() {
        time = pluginConfig.getResetTimerTime();
    }
}
