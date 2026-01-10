package de.zillolp.cookieclicker.commands.maincommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.commands.subcommands.SubCommand;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MainCommand implements TabExecutor {
    protected final CookieClicker plugin;
    protected final LanguageConfig languageConfig;
    protected final HashMap<String, List<SubCommand>> subCommands = new HashMap<>();

    public MainCommand(CookieClicker plugin) {
        this.plugin = plugin;
        languageConfig = plugin.getLanguageConfig();
    }

    @Override
    public abstract boolean onCommand(CommandSender commandSender, Command command, String label, String[] args);

    public void registerSubCommand(SubCommand subCommand) {
        String mainCommand = subCommand.getMainCommand();
        List<SubCommand> mainSubCommands = subCommands.getOrDefault(mainCommand, new ArrayList<>());
        subCommands.putIfAbsent(mainCommand, mainSubCommands);
        mainSubCommands.add(subCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        int length = args.length;
        List<String> tabCommands = new ArrayList<>();
        for (Map.Entry<String, List<SubCommand>> mainSubCommands : subCommands.entrySet()) {
            for (SubCommand subCommand : mainSubCommands.getValue()) {
                if (!(subCommand.hasPermission(commandSender))) {
                    continue;
                }
                String mainCommand = subCommand.getMainCommand();
                if (length > 1) {
                    if (!(args[0].equalsIgnoreCase(mainCommand))) {
                        continue;
                    }
                    List<String> subTabCommands = subCommand.getCustomTabCompletions(args, length);
                    if (subTabCommands == null) {
                        subTabCommands = subCommand.getTabCommands(args[0], args[length - 2], length - 1);
                    }
                    List<String> filteredCommands = getPossibleTabCommands(args[length - 1], subTabCommands);
                    for (String filteredCommand : filteredCommands) {
                        if (!(tabCommands.contains(filteredCommand))) {
                            tabCommands.add(filteredCommand);
                        }
                    }
                    continue;
                }
                if (!(mainCommand.toLowerCase().startsWith(args[0].toLowerCase()))) {
                    continue;
                }
                if (!(tabCommands.contains(mainCommand))) {
                    tabCommands.add(mainCommand);
                }
            }
        }
        return tabCommands;
    }

    private List<String> getPossibleTabCommands(String input, List<String> commands) {
        ArrayList<String> tabCommands = new ArrayList<>();
        for (String command : commands) {
            if (!(command.toLowerCase().startsWith(input.toLowerCase()))) {
                continue;
            }
            tabCommands.add(command);
        }
        return tabCommands;
    }
}
