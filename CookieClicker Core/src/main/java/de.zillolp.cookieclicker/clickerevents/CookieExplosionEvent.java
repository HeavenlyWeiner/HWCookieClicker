package de.zillolp.cookieclicker.clickerevents;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.enums.ClickerEventType;
import de.zillolp.cookieclicker.manager.VersionManager;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class CookieExplosionEvent extends ClickerEvent {
    private final CookieClicker plugin;
    private final ReflectionUtil reflectionUtil;
    private final String teamName = "CookieExplosion";
    private final String playersFieldName;
    private final ArrayList<Item> items = new ArrayList<>();

    public CookieExplosionEvent(CookieClicker plugin, Player player, ClickerEventType clickerEventType, double time) {
        super(player, clickerEventType, time);
        this.plugin = plugin;
        reflectionUtil = plugin.getReflectionUtil();
        VersionManager versionManager = plugin.getVersionManager();
        if (versionManager.getVersionNumber() < 21 || (versionManager.getVersionNumber() == 21 && versionManager.getSubVersion() < 5)) {
            playersFieldName = "g";
        } else {
            playersFieldName = "h";
        }
    }

    public void addGlowing(Item item) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            PlayerTeam playerTeam = new PlayerTeam(new Scoreboard(), teamName);
            playerTeam.setColor(ChatFormatting.GOLD);
            reflectionUtil.sendPacket(ClientboundSetPlayerTeamPacket.createPlayerPacket(playerTeam, teamName, ClientboundSetPlayerTeamPacket.Action.ADD), player);
            Collection<String> players = (Collection<String>) reflectionUtil.invokeMethod(playerTeam, playersFieldName);
            String uuid = item.getUniqueId().toString();
            players.remove(uuid);
            players.add(uuid);
            reflectionUtil.sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true), player);
            item.setGlowing(true);
        }, 20);
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
