package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import de.zillolp.cookieclicker.runnables.StatsWallUpdater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        plugin.closeInventories();
        plugin.unloadPlayers();
        plugin.getConfigManager().reloadConfigs();
        plugin.getResetTimerUpdater().resetTime();
        plugin.getCookieClickerManager().loadLocations();
        plugin.getSoundManager().load();
        StatsWallUpdater statsWallUpdater = plugin.getStatsWallUpdater();
        statsWallUpdater.getSortedAlltimeData().clear();
        statsWallUpdater.getCachedTimedData().clear();
        statsWallUpdater.updateAlltimeStatsWall();
        statsWallUpdater.updateTimeStatsWall();
        plugin.loadPlayers();
        commandSender.sendMessage(languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX) + "§7The §bsettings §7have been reloaded.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION);
    }
}
