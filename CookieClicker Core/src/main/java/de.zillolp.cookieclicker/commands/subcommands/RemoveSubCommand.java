package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import de.zillolp.cookieclicker.manager.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveSubCommand extends SubCommand {
    private final LocationConfig locationConfig;
    private final HologramManager hologramManager;

    public RemoveSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
        locationConfig = plugin.getLocationConfig();
        hologramManager = plugin.getHologramManager();
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
        if (args.length == 2) {
            if (!(args[1].equalsIgnoreCase("resettimer"))) {
                return false;
            }
            String locationName = "ResetTimer";
            if (!(locationConfig.isLocation(locationName))) {
                commandSender.sendMessage(PREFIX + "§cThe resettimer is not set.");
                return true;
            }
            Location resetTimerLocation = locationConfig.getLocation("ResetTimer");
            if (resetTimerLocation != null) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    hologramManager.deleteHologramsByLocation(player1, resetTimerLocation);
                }
            }
            locationConfig.removeLocation(locationName);
            commandSender.sendMessage(PREFIX + "§7You have §asuccessfully §7removed the §bresettimer§7.");
            return true;
        }
        if (args.length != 3) {
            return false;
        }
        if (!(args[1].equalsIgnoreCase("clicker"))) {
            return false;
        }
        if (!(isNumeric(args[2]))) {
            commandSender.sendMessage(PREFIX + "§cYour input §4" + args[2] + " §cis not a number.");
            return true;
        }
        int number = Integer.parseInt(args[2]);
        String locationName = "CookieClicker.Clicker-" + number;
        if (!(locationConfig.isLocation(locationName))) {
            commandSender.sendMessage(PREFIX + "§cThis CookieClicker does not exist.");
            return true;
        }
        Location location = locationConfig.getLocation(locationName);
        plugin.getCookieClickerManager().getClickerLocations().remove(location);
        locationConfig.removeLocation(locationName);
        location.getBlock().getState().update(true);

        HologramManager hologramManager = plugin.getHologramManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            hologramManager.deleteHologramsByLocation(player, location);
        }
        commandSender.sendMessage(PREFIX + "§7You deleted the the §bCookieClicker-" + number + "§7.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION);
    }

    @Override
    public List<String> getCustomTabCompletions(String[] args, int position) {
        if (position != 3 || !args[1].equalsIgnoreCase("clicker")) {
            return null;
        }
        return locationConfig.getClickerNumbers("CookieClicker");
    }
}
