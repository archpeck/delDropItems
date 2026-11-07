package plugin.DelDropItems.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Settings {
    private long removalTime;
    private boolean dropMessageAllow;
    private boolean reloadMessageAllow;
    private boolean broadcastMessageAllow;
    private long broadcastInterval;
    private double distancePlayerToObject;
    private boolean onlyPlayerDropped;
    private boolean ignoreNamedItems;
    private boolean logRemovals;
    private String language;
    private final Set<Material> ignoredMaterials = new HashSet<>();
    private final Set<String> disabledWorlds = new HashSet<>();
    private final List<String> unknownMaterials = new ArrayList<>();

    public void load(FileConfiguration config) {
        removalTime = Math.max(1L, config.getLong("item-removal-time", 300L));
        dropMessageAllow = config.getBoolean("drop-message-allow", true);
        reloadMessageAllow = config.getBoolean("reload-message-allow", true);
        broadcastMessageAllow = config.getBoolean("broadcast-message-allow", true);
        broadcastInterval = config.getLong("broadcast-interval", 300L);
        distancePlayerToObject = config.getDouble("distance-player-to-object", 0D);
        onlyPlayerDropped = config.getBoolean("only-player-dropped", true);
        ignoreNamedItems = config.getBoolean("ignore-named-items", false);
        logRemovals = config.getBoolean("log-removals", true);
        language = config.getString("language", "en");

        ignoredMaterials.clear();
        unknownMaterials.clear();
        for (String name : config.getStringList("ignored-materials")) {
            Material material = Material.matchMaterial(name.trim());
            if (material == null) {
                unknownMaterials.add(name);
            } else {
                ignoredMaterials.add(material);
            }
        }

        disabledWorlds.clear();
        disabledWorlds.addAll(config.getStringList("disabled-worlds"));
    }

    public long removalTime() { return removalTime; }
    public boolean dropMessageAllow() { return dropMessageAllow; }
    public boolean reloadMessageAllow() { return reloadMessageAllow; }
    public boolean broadcastMessageAllow() { return broadcastMessageAllow; }

    public long broadcastInterval() { return broadcastInterval; }

    public double distancePlayerToObject() { return distancePlayerToObject; }
    public boolean onlyPlayerDropped() { return onlyPlayerDropped; }
    public boolean ignoreNamedItems() { return ignoreNamedItems; }
    public boolean logRemovals() { return logRemovals; }
    public String language() { return language; }
    public Set<Material> ignoredMaterials() { return Collections.unmodifiableSet(ignoredMaterials); }
    public List<String> unknownMaterials() { return Collections.unmodifiableList(unknownMaterials); }
    public boolean isWorldDisabled(String worldName) { return disabledWorlds.contains(worldName); }
}
