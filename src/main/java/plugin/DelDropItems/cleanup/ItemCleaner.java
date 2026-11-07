package plugin.DelDropItems.cleanup;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import plugin.DelDropItems.config.Settings;
import plugin.DelDropItems.message.Messages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemCleaner {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Settings settings;
    private final RemovalLogger logger;
    private final Messages messages;

    public ItemCleaner(Settings settings, RemovalLogger logger, Messages messages) {
        this.settings = settings;
        this.logger = logger;
        this.messages = messages;
    }

    public int sweep(boolean force) {
        final long limitTicks = settings.removalTime() * 20L;
        final double maxDist = settings.distancePlayerToObject();
        final double maxDistSq = maxDist * maxDist;
        final List<String> logLines = new ArrayList<>();
        int removed = 0;

        for (World world : Bukkit.getWorlds()) {
            if (settings.isWorldDisabled(world.getName())) {
                continue;
            }
            List<Player> players = world.getPlayers();

            for (Item item : world.getEntitiesByClass(Item.class)) {
                if (!item.isValid()) {
                    continue;
                }
                if (!force && item.getTicksLived() < limitTicks) {
                    continue;
                }
                if (!isRemovable(item)) {
                    continue;
                }
                if (!force && maxDist > 0 && playerNearby(players, item, maxDistSq)) {
                    continue;
                }

                if (settings.logRemovals()) {
                    logLines.add(describe(item));
                }
                item.remove();
                removed++;
            }
        }

        logger.write(logLines);
        return removed;
    }

    private boolean isRemovable(Item item) {
        ItemStack stack = item.getItemStack();
        if (settings.ignoredMaterials().contains(stack.getType())) {
            return false;
        }
        if (settings.ignoreNamedItems() && stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return false;
            }
        }
        return !settings.onlyPlayerDropped() || item.getThrower() != null;
    }

    private boolean playerNearby(List<Player> players, Item item, double maxDistSq) {
        for (Player player : players) {
            if (player.getLocation().distanceSquared(item.getLocation()) <= maxDistSq) {
                return true;
            }
        }
        return false;
    }

    private String describe(Item item) {
        ItemStack stack = item.getItemStack();
        UUID thrower = item.getThrower();
        String who = messages.console("log-unknown-player", "unknown");
        if (thrower != null) {
            Player online = Bukkit.getPlayer(thrower);
            who = online != null ? online.getName() : thrower.toString();
        }
        return messages.console("log-removal",
                "[%time%] Item: %item% x%amount%, dropped by: %player%, world: %world% (%x%, %y%, %z%) has been removed.",
                "%time%", LocalDateTime.now().format(TIME_FORMAT),
                "%item%", stack.getType().name(),
                "%amount%", String.valueOf(stack.getAmount()),
                "%player%", who,
                "%world%", item.getWorld().getName(),
                "%x%", String.valueOf(item.getLocation().getBlockX()),
                "%y%", String.valueOf(item.getLocation().getBlockY()),
                "%z%", String.valueOf(item.getLocation().getBlockZ()));
    }
}
