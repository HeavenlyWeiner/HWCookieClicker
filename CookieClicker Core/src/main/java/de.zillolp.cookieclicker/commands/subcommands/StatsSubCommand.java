package de.zillolp.cookieclicker.commands.subcommands;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.database.DatabaseManager;
import de.zillolp.cookieclicker.enums.PluginLanguage;
import de.zillolp.cookieclicker.enums.SoundType;
import de.zillolp.cookieclicker.manager.SoundManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StatsSubCommand extends SubCommand {
    private final BukkitScheduler bukkitScheduler;
    private final DatabaseManager databaseManager;
    private final SoundManager soundManager;

    public StatsSubCommand(CookieClicker plugin, String mainCommand, String... subCommands) {
        super(plugin, mainCommand, subCommands);
        bukkitScheduler = plugin.getServer().getScheduler();
        databaseManager = plugin.getDatabaseManager();
        soundManager = plugin.getSoundManager();
    }

    @Override
    public boolean onlyPlayer() {
        return false;
    }

    @Override
    public boolean onCommand(CookieClicker plugin, CommandSender commandSender, Command command, String[] args) {
        if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.ONLY_PLAYER));
                return true;
            }
            Player player = (Player) commandSender;
            UUID uuid = player.getUniqueId();
            soundManager.playSound(player, SoundType.STATS_INFO);
            bukkitScheduler.runTaskAsynchronously(plugin, () -> {
                if (!(databaseManager.playerExists(uuid))) {
                    commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PLAYER_NOT_FOUND));
                    return;
                }
                for (String line : languageConfig.getReplaceLanguages(PluginLanguage.PLAYER_STATS_INFO, uuid)) {
                    commandSender.sendMessage(line);
                }
            });
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        if (commandSender instanceof Player) {
            soundManager.playSound(((Player) commandSender), SoundType.STATS_INFO);
        }
        bukkitScheduler.runTaskAsynchronously(plugin, () -> {
            if (!(databaseManager.playerExists(args[1]))) {
                commandSender.sendMessage(languageConfig.getLanguageWithPrefix(PluginLanguage.PLAYER_NOT_FOUND));
                return;
            }
            Optional<UUID> uuidOptional = databaseManager.getUUIDbyName(args[1]);
            if (!(uuidOptional.isPresent())) {
                return;
            }
            for (String line : languageConfig.getReplaceLanguages(PluginLanguage.PLAYER_STATS_INFO, uuidOptional.get())) {
                commandSender.sendMessage(line);
            }
        });
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return true;
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
