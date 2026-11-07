package plugin.DelDropItems.cleanup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import plugin.DelDropItems.config.Settings;
import plugin.DelDropItems.message.Messages;

public class CleanupScheduler {
    private final JavaPlugin plugin;
    private final Settings settings;
    private final Messages messages;
    private final ItemCleaner cleaner;

    private BukkitTask sweepTask;
    private BukkitTask broadcastTask;

    public CleanupScheduler(JavaPlugin plugin, Settings settings, Messages messages, ItemCleaner cleaner) {
        this.plugin = plugin;
        this.settings = settings;
        this.messages = messages;
        this.cleaner = cleaner;
    }

    public void restart() {
        stop();

        sweepTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> cleaner.sweep(false), 20L, 20L);

        if (settings.broadcastMessageAllow() && settings.broadcastInterval() > 0) {
            long period = settings.broadcastInterval() * 20L;
            broadcastTask = Bukkit.getScheduler().runTaskTimer(plugin, this::broadcast, period, period);
        }
    }

    public void stop() {
        if (sweepTask != null) {
            sweepTask.cancel();
            sweepTask = null;
        }
        if (broadcastTask != null) {
            broadcastTask.cancel();
            broadcastTask = null;
        }
    }

    private void broadcast() {
        String text = messages.get("broadcast-message", "");
        if (text.isEmpty()) {
            return;
        }
        String message = messages.applyTime(text);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!settings.isWorldDisabled(player.getWorld().getName())) {
                player.sendMessage(message);
            }
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
