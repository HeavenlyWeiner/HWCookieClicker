package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.config.customconfigs.PermissionsConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {
    protected final CookieClicker plugin;
    protected final LanguageConfig languageConfig;
    protected final PermissionsConfig permissionsConfig;
    private final String mainCommand;
    private final String[] subCommands;

    public SubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        this.plugin = plugin;
        languageConfig = plugin.getLanguageConfig();
        permissionsConfig = plugin.getPermissionsConfig();
        this.mainCommand = mainCommand;
        this.subCommands = subCommands;
    }

    public abstract boolean onlyPlayer();

    public abstract boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args);

    public abstract boolean hasPermission(CommandSender commandSender);

    public String getMainCommand() {
        return mainCommand;
    }

    public boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        return value.matches("\\d+");
    }

    public List<String> getTabCommands(String subCommand, String command, int position) {
        if (subCommands.length < position) {
            return new ArrayList<>();
        }
        position--;
        List<String> tabCommands = Arrays.asList(subCommands[position].split(";"));
        if (position > 0) {
            position--;
        } else if (mainCommand.equalsIgnoreCase(command)) {
            return tabCommands;
        }
        for (String replacedCommand : subCommands[position].split(";")) {
            if (replacedCommand.isEmpty() || (replacedCommand.equalsIgnoreCase(command) && subCommand.equalsIgnoreCase(mainCommand))) {
                return tabCommands;
            }
        }
        return new ArrayList<>();
    }

    public List<String> getCustomTabCompletions(String[] args, int position) {
        return null;
    }
}
