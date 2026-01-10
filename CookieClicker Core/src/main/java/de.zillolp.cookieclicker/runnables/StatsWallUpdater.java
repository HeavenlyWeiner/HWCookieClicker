package de.zillolp.cookieclicker.runnables;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LanguageConfig;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.profiles.ClickerStatsProfile;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.profile.PlayerProfile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class StatsWallUpdater extends CustomUpdater {
    private final CookieClicker plugin;
    private final ReflectionUtil reflectionUtil;
    private final LanguageConfig languageConfig;
    private final ClickerPlayerManager clickerPlayerManager;
    private final LinkedHashMap<String, Long> cachedAlltimeData = new LinkedHashMap<>();
    private final LinkedHashMap<String, Long> cachedTimedData = new LinkedHashMap<>();
    private final LinkedHashMap<String, Long> sortedAlltimeData = new LinkedHashMap<>();
    private final LinkedHashMap<String, Long> sortedTimedData = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Location[]> allTimeLocations;
    private final LinkedHashMap<Integer, Location[]> timeLocations;

    public StatsWallUpdater(CookieClicker plugin) {
        super(plugin, true, 200);
        this.plugin = plugin;
        reflectionUtil = plugin.getReflectionUtil();
        languageConfig = plugin.getLanguageConfig();

        CookieClickerManager cookieClickerManager = plugin.getCookieClickerManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        allTimeLocations = cookieClickerManager.getAlltimeLocations();
        timeLocations = cookieClickerManager.getTimeLocations();

        updateTimeStatsWall();
    }

    @Override
    protected void tick() {
        for (ClickerStatsProfile clickerStatsProfile : clickerPlayerManager.getClickerStatsProfiles().values()) {
            cachedAlltimeData.put(clickerStatsProfile.getName(), clickerStatsProfile.getPerClick());
        }
        updateAlltimeStatsWall();
        if (sortedTimedData.isEmpty() && cachedTimedData.isEmpty()) {
            return;
        }
        updateTimeStatsWall();
    }

    public void updateAlltimeStatsWall() {
        setStatsWall(allTimeLocations, sortedAlltimeData, cachedAlltimeData);
    }

    public void updateTimeStatsWall() {
        setStatsWall(timeLocations, sortedTimedData, cachedTimedData);
    }

    private void setStatsWall(LinkedHashMap<Integer, Location[]> wallLocations, LinkedHashMap<String, Long> sortedData, LinkedHashMap<String, Long> cachedData) {
        sortedData.clear();
        cachedData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(playerData -> sortedData.put(playerData.getKey(), playerData.getValue()));
        for (Map.Entry<Integer, Location[]> statsWall : wallLocations.entrySet()) {
            int place = statsWall.getKey();
            int index = place - 1;
            if (sortedData.size() <= index) {
                setWall(statsWall.getValue(), null, languageConfig.getStatsWallLanguage("STATS_WALL.SIGN_LINES", getPlace(place), "?", "?"));
                continue;
            }
            String name = getPlaceName(sortedData, index);
            setWall(statsWall.getValue(), name, languageConfig.getStatsWallLanguage("STATS_WALL.SIGN_LINES", getPlace(place), name, languageConfig.formatNumber(sortedData.getOrDefault(name, 0L))));
        }
    }

    private void setWall(Location[] locations, String name, String[] lines) {
        Block headBlock = locations[0].getBlock();
        Block signBlock = locations[1].getBlock();
        if (headBlock.getType() == Material.AIR || signBlock.getType() == Material.AIR) {
            return;
        }
        bukkitScheduler.runTask(plugin, () -> {
            BlockState signState = signBlock.getState();
            if (!(signState instanceof Sign)) {
                return;
            }
            Sign sign = (Sign) signState;
            sign.setLine(0, lines[0]);
            sign.setLine(1, lines[1]);
            sign.setLine(2, lines[2]);
            sign.setLine(3, lines[3]);
            sign.update(true);
        });
        if (name != null && (!(name.equals("?"))) && plugin.getDatabaseManager().playerExists(name)) {
            hasPlayerSkull(headBlock, name).thenAccept(hasPlayerSkull -> {
                if (hasPlayerSkull) {
                    return;
                }
                reflectionUtil.getTextureURL(name).thenAccept(textureUrl -> {
                    setSkullProfile(textureUrl, name, headBlock);
                }).exceptionally(throwable -> {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load TextureURL for Name: " + name, throwable);
                    return null;
                });
            }).exceptionally(throwable -> {
                plugin.getLogger().log(Level.SEVERE, "Failed to check hasPlayerSkull for Name: " + name, throwable);
                return null;
            });
            return;
        }
        String questionName = "?";
        hasPlayerSkull(headBlock, questionName).thenAccept(hasPlayerSkull -> {
            if (hasPlayerSkull) {
                return;
            }
            String url = "http://textures.minecraft.net/texture/badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a";
            setSkullProfile(url, questionName, headBlock);
        }).exceptionally(throwable -> {
            plugin.getLogger().log(Level.SEVERE, "Failed to check hasPlayerSkull for Name: " + questionName, throwable);
            return null;
        });
    }

    private void setSkullProfile(String url, String name, Block block) {
        reflectionUtil.getPlayerProfile(url, name).thenAccept(playerProfile -> {
            bukkitScheduler.runTask(plugin, () -> {
                BlockState blockState = block.getState();
                if (!(blockState instanceof Skull)) {
                    return;
                }
                Skull skull = (Skull) blockState;
                skull.setOwnerProfile(playerProfile);
                skull.update(true);
            });
        }).exceptionally(throwable -> {
            plugin.getLogger().log(Level.SEVERE, "Failed to load PlayerProfile for URL: " + url, throwable);
            return null;
        });
    }

    private CompletableFuture<Boolean> hasPlayerSkull(Block block, String name) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        bukkitScheduler.runTask(plugin, () -> {
            PlayerProfile playerProfile = ((Skull) block.getState()).getOwnerProfile();
            completableFuture.complete(playerProfile != null && playerProfile.getName() != null && playerProfile.getName().equals(name));
        });
        return completableFuture;
    }

    private String getPlace(int place) {
        String path = "STATS_WALL.RANK_PREFIX.";
        if (place >= 1 && place <= 3) {
            path = path + place;
        } else {
            path = path + "DEFAULT";
        }
        return languageConfig.getTranslatedLanguage(path).replace("%number%", languageConfig.formatNumber((long) place));
    }

    private String getPlaceName(LinkedHashMap<String, Long> sortedData, int index) {
        List<Map.Entry<String, Long>> entryList = new ArrayList<>(sortedData.entrySet());
        if (index >= 0 && index < entryList.size()) {
            return entryList.get(index).getKey();
        }
        return "?";
    }

    public LinkedHashMap<String, Long> getSortedAlltimeData() {
        return sortedAlltimeData;
    }

    public LinkedHashMap<String, Long> getSortedTimedData() {
        return sortedTimedData;
    }

    public LinkedHashMap<String, Long> getCachedTimedData() {
        return cachedTimedData;
    }
}
