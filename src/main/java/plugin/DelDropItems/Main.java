package plugin.DelDropItems;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import plugin.DelDropItems.cleanup.CleanupScheduler;
import plugin.DelDropItems.cleanup.ItemCleaner;
import plugin.DelDropItems.cleanup.RemovalLogger;
import plugin.DelDropItems.command.DdiCommand;
import plugin.DelDropItems.config.Settings;
import plugin.DelDropItems.listener.DropListener;
import plugin.DelDropItems.message.Messages;

public class Main extends JavaPlugin {
    private Settings settings;
    private Messages messages;
    private CleanupScheduler scheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        settings = new Settings();
        messages = new Messages(this, settings);
        RemovalLogger removalLogger = new RemovalLogger(this, messages);
        ItemCleaner cleaner = new ItemCleaner(settings, removalLogger, messages);
        scheduler = new CleanupScheduler(this, settings, messages, cleaner);

        reloadAll();

        Bukkit.getPluginManager().registerEvents(new DropListener(settings, messages), this);

        PluginCommand command = getCommand("ddi");
        if (command != null) {
            DdiCommand handler = new DdiCommand(this, settings, messages, cleaner, removalLogger);
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }

        getLogger().info(messages.console("console-enabled", "DelDropItems v%version% enabled. Use /ddi help.",
                "%version%", getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        if (scheduler != null) {
            scheduler.stop();
        }
    }

    public void reloadAll() {
        reloadConfig();
        settings.load(getConfig());
        messages.load();
        for (String name : settings.unknownMaterials()) {
            getLogger().warning(messages.console("console-unknown-material",
                    "ignored-materials: unknown material '%name%'", "%name%", name));
        }
        scheduler.restart();
    }
}
