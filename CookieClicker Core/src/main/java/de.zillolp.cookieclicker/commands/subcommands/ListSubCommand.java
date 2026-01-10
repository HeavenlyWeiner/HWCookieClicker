package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PluginPermission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ListSubCommand extends SubCommand {

    public ListSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
    }

    @Override
    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        ArrayList<Location> clickerLocations = plugin.getCookieClickerManager().getClickerLocations();
        commandSender.sendMessage("§6All CookieClicker§7:");
        int number = 1;
        for (Location clickerLocation : clickerLocations) {
            double x = clickerLocation.getX() + 0.5;
            double y = clickerLocation.getY() + 1;
            double z = clickerLocation.getZ() + 0.5;
            TextComponent textComponent = new TextComponent("§7- §eClicker-" + number);
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to teleport")));
            commandSender.spigot().sendMessage(textComponent);
            number++;
        }
        int clickerLocationSize = clickerLocations.size();
        if (clickerLocationSize == 1) {
            commandSender.sendMessage("§7» There is §6" + clickerLocationSize + " §7Clicker.");
            return true;
        }
        commandSender.sendMessage("§7» There are §6" + clickerLocationSize + " §7Clickers.");
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return permissionsConfig.hasPermission(sender, PluginPermission.ADMIN_PERMISSION);
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }
}
