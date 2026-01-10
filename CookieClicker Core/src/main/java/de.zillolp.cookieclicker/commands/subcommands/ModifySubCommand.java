package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.custominventories.CustomInventory;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.profiles.ClickerInventoryProfile;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class ModifySubCommand extends SubCommand {
    private final ReflectionUtil reflectionUtil;
    private final DatabaseManager databaseManager;
    private final ClickerPlayerManager clickerPlayerManager;

    public ModifySubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
        reflectionUtil = plugin.getReflectionUtil();
        databaseManager = plugin.getDatabaseManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
    }

    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        if (args.length != 5) {
            return false;
        }
        if (!(databaseManager.playerExists(args[1]))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PLAYER_NOT_FOUND));
            return true;
        }
        String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
        Optional<UUID> uuidOptional = databaseManager.getUUIDbyName(args[1]);
        if (!(uuidOptional.isPresent())) {
            plugin.getLogger().log(Level.SEVERE, "Error getting UUID of Player: " + args[1]);
            return false;
        }
        String type = args[3].toUpperCase();
        if ((!(type.equalsIgnoreCase("COOKIES"))) && (!(type.equalsIgnoreCase("PER_CLICK"))) && (!(type.equalsIgnoreCase("CLICKER_CLICKS")))) {
            commandSender.sendMessage(PREFIX + "§cYour input §4" + args[3] + " §cis not a value type!");
            return true;
        }
        if (!(isNumeric(args[4]))) {
            commandSender.sendMessage(PREFIX + "§cYour input §4" + args[4] + " §cis not a number!");
            return true;
        }

        UUID uuid = uuidOptional.get();
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && Bukkit.getOnlinePlayers().contains(player)) {
            ClickerInventoryProfile clickerInventoryProfile = clickerPlayerManager.getInventoryProfile(player);
            for (CustomInventory customInventory : clickerInventoryProfile.getCustomInventories().values()) {
                Inventory inventory = reflectionUtil.getOpenInventory(player);
                if (inventory != null && inventory == customInventory.getInventory()) {
                    player.closeInventory();
                    break;
                }
            }
        }

        long value = Long.parseLong(args[4]);
        ClickerStatsProfile clickerStatsProfile = clickerPlayerManager.getStatsProfile(uuidOptional.get());
        switch (args[2].toUpperCase()) {
            case "ADD":
                setValue(clickerStatsProfile, type, getValue(clickerStatsProfile, type) + value);
                break;
            case "SET":
                setValue(clickerStatsProfile, type, value);
                break;
            case "REMOVE":
                setValue(clickerStatsProfile, type, getValue(clickerStatsProfile, type) - value);
                break;
            default:
                return false;
        }
        databaseManager.saveClickerStatsProfile(clickerStatsProfile);
        commandSender.sendMessage(PREFIX + "§7You have modified §7the §6" + type + " §7from §e" + clickerStatsProfile.getName() + "§7.");
        return true;
    }

    private long getValue(ClickerStatsProfile clickerStatsProfile, String type) {
        switch (type) {
            case "COOKIES":
                return clickerStatsProfile.getCookies();
            case "PER_CLICK":
                return clickerStatsProfile.getPerClick();
            case "CLICKER_CLICKS":
                return clickerStatsProfile.getClickerClicks();
            default:
                return 0;
        }
    }

    private void setValue(ClickerStatsProfile clickerStatsProfile, String type, long value) {
        switch (type) {
            case "COOKIES":
                clickerStatsProfile.setCookies(value);
                break;
            case "PER_CLICK":
                clickerStatsProfile.setPerClick(value);
                break;
            case "CLICKER_CLICKS":
                clickerStatsProfile.setClickerClicks(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION);
    }

    @Override
    public List<String> getCustomTabCompletions(String[] args, int position) {
        if (position != 2) {
            return null;
        }
        ArrayList<String> playerNames = new ArrayList<>();
        for (ClickerStatsProfile clickerStatsProfile : plugin.getClickerPlayerManager().getClickerStatsProfiles().values()) {
            playerNames.add(clickerStatsProfile.getName());
        }
        return playerNames;
    }
}
