package de.zillolp.cookieclicker.config.customconfigs;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.PluginPermission;
import org.bukkit.command.CommandSender;

public class PermissionsConfig extends CustomConfig {
    public PermissionsConfig(CookieClicker plugin, String name) {
        super(plugin, name);
    }

    public boolean hasPermission(CommandSender commandSender, PluginPermission pluginPermission) {
        String keyName = pluginPermission.name();
        if (!(fileConfiguration.contains(keyName))) {
            return false;
        }
        return commandSender.hasPermission(fileConfiguration.getString(keyName, pluginPermission.getDefaultPermission()));
    }
}
