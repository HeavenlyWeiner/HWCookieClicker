package de.zillolp.cookieclicker.holograms;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.Hologram;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class Hologram_v1_20_R3 implements Hologram {
    private final UUID uuid;
    private final ReflectionUtil reflectionUtil;
    private final String idName;
    private final String entityDataName;
    private String line;
    private ArmorStand armorStand;
    private Location spawnLocation;

    public Hologram_v1_20_R3(CookieClicker plugin, String line) {
        uuid = UUID.randomUUID();
        reflectionUtil = plugin.getReflectionUtil();

        int subVersion = plugin.getVersionManager().getSubVersion();
        if (subVersion == 1) {
            idName = "af";
            entityDataName = "aj";
        } else if (subVersion == 2) {
            idName = "ah";
            entityDataName = "al";
        } else {
            idName = "aj";
            entityDataName = "an";
        }
        this.line = line;
    }

    @Override
    public void spawn(Player player, Location location) {
        spawnLocation = location;
        World world = location.getWorld();
        if (world == null) {
            world = Bukkit.getWorlds().getFirst();
        }
        armorStand = new ArmorStand((ServerLevel) reflectionUtil.getServerObject(world, reflectionUtil.getCraftObjectClass(reflectionUtil.isSpigot(), "CraftWorld")), location.getX(), location.getY(), location.getZ());

        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setNoGravity(true);
        armorStand.setNoBasePlate(true);
        armorStand.setSmall(true);
        armorStand.setSilent(true);
        armorStand.setMarker(true);

        reflectionUtil.sendPacket(new ClientboundAddEntityPacket(armorStand), player);
        changeLine(player, line);
    }

    @Override
    public void destroy(Player player) {
        if (armorStand == null) {
            return;
        }
        reflectionUtil.sendPacket(new ClientboundRemoveEntitiesPacket((int) reflectionUtil.invokeMethod(armorStand, idName)), player);
    }

    @Override
    public void changeLine(Player player, String line) {
        if (armorStand == null) {
            return;
        }
        this.line = line;
        armorStand.setCustomName(ComponentUtils.formatList(Collections.singletonList(line)));
        reflectionUtil.sendPacket(new ClientboundSetEntityDataPacket((int) reflectionUtil.invokeMethod(armorStand, idName),
                ((SynchedEntityData) reflectionUtil.invokeMethod(armorStand, entityDataName)).getNonDefaultValues()), player);
    }

    @Override
    public void moveHologram(Player player, Location location) {
        if (armorStand == null) {
            return;
        }
        armorStand.teleportTo(location.getX(), location.getY(), location.getZ());
        reflectionUtil.sendPacket(new ClientboundTeleportEntityPacket(armorStand), player);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public Location getCurrentLocation() {
        return armorStand.getBukkitEntity().getLocation();
    }
}
