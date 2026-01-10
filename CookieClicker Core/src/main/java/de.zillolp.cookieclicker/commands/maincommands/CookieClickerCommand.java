package de.zillolp.cookieclicker.commands.maincommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.commands.subcommands.SubCommand;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.manager.VersionManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class CookieClickerCommand extends MainCommand {
    private final VersionManager versionManager;

    public CookieClickerCommand(CookieClicker plugin) {
        super(plugin);
        versionManager = plugin.getVersionManager();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String PREFIX = languageConfig.getTranslatedLanguage(PluginLanguage.PREFIX);
        if (!(versionManager.checkVersion())) {
            for (String wrongVersionMessage : versionManager.getWrongVersionMessage()) {
                commandSender.sendMessage(PREFIX + wrongVersionMessage);
            }
            return true;
        }
        if (!(plugin.getDatabaseConnector().hasConnection())) {
            commandSender.sendMessage(PREFIX + "§cThe plugin isn't connected to the database!");
            return true;
        }
        if (args.length == 0) {
            PluginDescriptionFile pluginDescription = plugin.getDescription();
            List<String> authors = pluginDescription.getAuthors();
            String authorsString = String.join(", ", authors);
            commandSender.sendMessage("§6§lPlugin Info:");
            commandSender.sendMessage("§7Plugin Name§8: §e" + pluginDescription.getName());
            commandSender.sendMessage("§7Plugin Version§8: §e" + pluginDescription.getVersion());
            if (authors.size() == 1) {
                commandSender.sendMessage("§7Author§8: §e" + authorsString);
            } else {
                commandSender.sendMessage("§7Authors§8: §e" + authorsString);
            }
            commandSender.sendMessage("§7Discord§8: §e" + pluginDescription.getWebsite());
            return true;
        }
        if (!(subCommands.containsKey(args[0].toLowerCase()))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.UNKNOWN_COMMAND));
            return true;
        }
        SubCommand subCommand = subCommands.get(args[0]).get(0);
        if (subCommand.onlyPlayer() && (!(commandSender instanceof Player))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.ONLY_PLAYER));
            return true;
        }
        Player player = (Player) commandSender;
        if (!(subCommand.hasPermission(player))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.NO_PERMISSION));
            return true;
        }
        ClickerGameProfile clickerGameProfile = plugin.getClickerPlayerManager().getGameProfile(player.getUniqueId());
        if (clickerGameProfile.isOverLastCommandInteraction(500)) {
            return true;
        }
        clickerGameProfile.updateLastCommandInteraction();
        if (!(subCommand.onCommand(plugin, commandSender, command, args))) {
            commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.UNKNOWN_COMMAND));
        }
        return true;
    }
}
