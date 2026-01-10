package de.zillolp.cookieclicker.manager;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.config.customconfigs.LocationConfig;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CookieClickerManager {
    public final long adventureClickDelay = 100;
    private final Logger logger;
    private final LocationConfig locationConfig;
    private final LinkedHashMap<Location, String> statsWallLocationPaths = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Location[]> timeLocations = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, Location[]> alltimeLocations = new LinkedHashMap<>();
    private ArrayList<Location> clickerLocations;

    public CookieClickerManager(CookieClicker plugin) {
        logger = plugin.getLogger();
        locationConfig = plugin.getLocationConfig();
        loadLocations();
    }

    public void loadLocations() {
        clickerLocations = locationConfig.getLocations("CookieClicker");
        loadStatsWallLocations();
    }

    public void loadStatsWallLocations() {
        statsWallLocationPaths.clear();
        alltimeLocations.clear();
        timeLocations.clear();
        loadLocations("StatsWalls.Alltime", alltimeLocations);
        loadLocations("StatsWalls.Time", timeLocations);
    }

    private void loadLocations(String section, LinkedHashMap<Integer, Location[]> locations) {
        for (String place : locationConfig.getConfigurationSection("locations." + section)) {
            int placeNumber;
            try {
                placeNumber = Integer.parseInt(place);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid place number in configuration section '" + section + "': " + place + " - Skipping this entry", e);
                continue;
            }

            String path = section + "." + place;
            Location headLocation = locationConfig.getLocation(path + ".head");
            Location signLocation = locationConfig.getLocation(path + ".sign");
            locations.put(placeNumber, new Location[]{headLocation, signLocation});
            statsWallLocationPaths.put(headLocation, path);
            statsWallLocationPaths.put(signLocation, path);
        }
    }

    public ArrayList<Location> getClickerLocations() {
        return clickerLocations;
    }

    public LinkedHashMap<Location, String> getStatsWallHeadsLocations() {
        return statsWallLocationPaths;
    }

    public LinkedHashMap<Integer, Location[]> getTimeLocations() {
        return timeLocations;
    }

    public LinkedHashMap<Integer, Location[]> getAlltimeLocations() {
        return alltimeLocations;
    }
}
