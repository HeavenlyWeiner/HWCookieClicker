package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.PluginPermission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends SubCommand {
    public HelpSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        int number = 0;
        if (args.length == 2) {
            String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
            if (!(isNumeric(args[1]))) {
                commandSender.sendMessage(PREFIX + "§cYour input §4" + args[1] + " §cis not a number!");
                return true;
            }
            number = Integer.parseInt(args[1]);
        }
        if (number <= 1) {
            commandSender.sendMessage("§6§lThe CookieClicker commands:");
            commandSender.sendMessage("§e/cookieclicker stats <player>.");
            commandSender.sendMessage("§7Shows you the stats of a player.");
            if (!(permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION))) {
                return true;
            }
            commandSender.sendMessage("§e/cookieclicker reload.");
            commandSender.sendMessage("§7Reloads the settings.");
            commandSender.sendMessage("§e/cookieclicker list.");
            commandSender.sendMessage("§7Shows you a list of all CookieClickers.");
            commandSender.sendMessage("§e/cookieclicker set clicker <number>.");
            commandSender.sendMessage("§7Creates a CookieClicker.");
            commandSender.sendMessage("§e/cookieclicker remove clicker <number>.");
            commandSender.sendMessage("§7Removes a CookieClicker.");
            commandSender.sendMessage("§e/cookieclicker set statswall <alltime/time> <number>.");
            commandSender.sendMessage("§7Creates the statswall.");
            commandSender.sendMessage("§e/cookieclicker set resettimer.");
            commandSender.sendMessage("§7Sets the resettimer.");
            commandSender.sendMessage("§e/cookieclicker remove resettimer.");
            commandSender.sendMessage("§7Removes the resettimer.");
            TextComponent textComponent = new TextComponent("§8[§6§l»§8]");
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cookieclicker help 2"));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Next Page")));
            commandSender.spigot().sendMessage(new TextComponent("§7------------------------------ "), textComponent);
            return true;
        }
        if (!(permissionsConfig.hasPermission(commandSender, PluginPermission.ADMIN_PERMISSION))) {
            return false;
        }
        commandSender.sendMessage("§e/cookieclicker modify <player> add <type> <amount>.");
        commandSender.sendMessage("§7Gives a player something.");
        commandSender.sendMessage("§e/cookieclicker modify <player> set <type> <amount>.");
        commandSender.sendMessage("§7Sets a player something.");
        commandSender.sendMessage("§e/cookieclicker modify <player> remove <type> <amount>.");
        commandSender.sendMessage("§7Removes a player something.");
        commandSender.sendMessage("§e/cookieclicker reset <player>.");
        commandSender.sendMessage("§7Resets the stats of a player.");

        TextComponent textComponent = new TextComponent("§8[§6§l«§8]");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cookieclicker help 1"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Last Page")));
        commandSender.spigot().sendMessage(textComponent, new TextComponent(" §7------------------------------"));
        return true;
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return true;
    }
}
