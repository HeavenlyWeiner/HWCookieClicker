package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.customevents.CookieClickerInteractEvent;
import de.zillolp.cookieclicker.handler.ClickerHandler;
import de.zillolp.cookieclicker.interfaces.PacketReader;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PacketReader_v1_20_R1 implements PacketReader {
    private final String READER_PREFIX = "Reader-";
    private final CookieClicker plugin;
    private final ReflectionUtil reflectionUtil;
    private final ClickerHandler clickerHandler;
    private final CookieClickerManager cookieClickerManager;
    private final ClickerPlayerManager clickerPlayerManager;
    private final ArrayList<Location> clickerLocations;
    private final int subVersionNumber;
    private final HashMap<UUID, Channel> readerChannels = new HashMap<>();

    public PacketReader_v1_20_R1(CookieClicker plugin) {
        this.plugin = plugin;
        reflectionUtil = plugin.getReflectionUtil();
        clickerHandler = plugin.getClickerHandler();
        cookieClickerManager = plugin.getCookieClickerManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        clickerLocations = cookieClickerManager.getClickerLocations();
        subVersionNumber = plugin.getVersionManager().getSubVersion();
    }

    public void inject(Player player) {
        String connectionFieldName = "c";
        String channelFieldName = "n";
        if (subVersionNumber <= 1) {
            connectionFieldName = "h";
            channelFieldName = "m";
        }
        Channel channel = (Channel) reflectionUtil.getValue(reflectionUtil.getValue(reflectionUtil.getPlayerConnection(player), connectionFieldName), channelFieldName);
        ChannelPipeline channelPipeline = channel.pipeline();
        if (channelPipeline == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        String readerName = READER_PREFIX + uuid;
        if (channelPipeline.names().contains(readerName)) {
            channelPipeline.remove(readerName);
        }
        if (channelPipeline.get("decoder") == null) {
            return;
        }
        channelPipeline.addAfter("decoder", readerName, new MessageToMessageDecoder<Object>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List<Object> out) {
                readPacket(player, packet, out);
            }
        });
        readerChannels.put(uuid, channel);
    }

    public void unInject(Player player) {
        UUID uuid = player.getUniqueId();
        if (!(readerChannels.containsKey(uuid))) {
            return;
        }
        Channel channel = readerChannels.get(uuid);
        if (channel != null) {
            String readerName = READER_PREFIX + uuid;
            ChannelPipeline channelPipeline = channel.pipeline();
            if (channelPipeline.names().contains(readerName)) {
                channelPipeline.remove(readerName);
            }
        }
        readerChannels.remove(uuid);
    }

    private void readPacket(Player player, Object packet, List<Object> list) {
        BlockPos blockPos;
        Location location;
        switch (packet.getClass().getSimpleName()) {
            case "PacketPlayInUseItem":
                BlockHitResult blockHitResult = (BlockHitResult) reflectionUtil.getValue(packet, "a");
                if (blockHitResult == null) {
                    break;
                }
                blockPos = blockHitResult.getBlockPos();
                if (blockPos == null) {
                    break;
                }
                location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                    break;
                }
                PlayerInventory playerInventory = player.getInventory();
                ItemStack itemStack = playerInventory.getItemInMainHand();
                CookieClickerInteractEvent.InteractType interactType = CookieClickerInteractEvent.InteractType.HAND_RIGHT_CLICK;
                if (reflectionUtil.getValue(packet, "b") == InteractionHand.OFF_HAND) {
                    interactType = CookieClickerInteractEvent.InteractType.OFF_HAND_RIGHT_CLICK;
                    itemStack = playerInventory.getItemInOffHand();
                }
                if (itemStack.getType() != Material.AIR && itemStack.getType().isBlock()) {
                    break;
                }
                execute(player, location, interactType).thenAccept(cookieClickerInteractEvent -> {
                    if (!(cookieClickerInteractEvent.isCancelled())) {
                        return;
                    }
                    list.add(packet);
                });
                return;
            case "PacketPlayInBlockDig":
                blockPos = (BlockPos) reflectionUtil.getValue(packet, "a");
                if (blockPos == null) {
                    break;
                }
                if (!(reflectionUtil.getValue(packet, "c").toString().equalsIgnoreCase("START_DESTROY_BLOCK"))) {
                    break;
                }
                location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                    break;
                }
                execute(player, location, CookieClickerInteractEvent.InteractType.LEFT_CLICK).thenAccept(cookieClickerInteractEvent -> {
                    if (cookieClickerInteractEvent.isCancelled()) {
                        list.add(packet);
                        return;
                    }
                    reflectionUtil.setValue(packet, "c", ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK);
                    list.add(packet);
                });
                return;
            case "PacketPlayInArmAnimation":
                Block clickedBlock = player.getTargetBlock(null, 5);
                if (player.getGameMode() != GameMode.ADVENTURE || clickedBlock.getType() == Material.AIR || (!(reflectionUtil.getValue(packet, "a").toString().equalsIgnoreCase("MAIN_HAND")))) {
                    break;
                }
                location = clickedBlock.getLocation();
                if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                    break;
                }
                ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(player.getUniqueId());
                if (clickerGameProfile.isOverLastClickerInteraction(cookieClickerManager.adventureClickDelay)) {
                    break;
                }
                clickerGameProfile.updateLastClickerInteraction();
                execute(player, location, CookieClickerInteractEvent.InteractType.ADVENTURE_LEFT_CLICK);
                break;
        }
        list.add(packet);
    }

    private CompletableFuture<CookieClickerInteractEvent> execute(Player player, Location location, CookieClickerInteractEvent.InteractType interactType) {
        CompletableFuture<CookieClickerInteractEvent> completableFuture = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            CookieClickerInteractEvent cookieClickerInteractEvent = new CookieClickerInteractEvent(player, location, interactType, false);
            plugin.getServer().getPluginManager().callEvent(cookieClickerInteractEvent);

            if (!(cookieClickerInteractEvent.isCancelled())) {
                clickerHandler.handleClickerInteract(cookieClickerInteractEvent);
            }
            completableFuture.complete(cookieClickerInteractEvent);
        });
        return completableFuture;
    }
}
