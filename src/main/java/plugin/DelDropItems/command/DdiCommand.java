package plugin.DelDropItems.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import plugin.DelDropItems.Main;
import plugin.DelDropItems.cleanup.ItemCleaner;
import plugin.DelDropItems.cleanup.RemovalLogger;
import plugin.DelDropItems.config.Settings;
import plugin.DelDropItems.message.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DdiCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = Arrays.asList("help", "reload", "clear", "view-logs");

    private final Main plugin;
    private final Settings settings;
    private final Messages messages;
    private final ItemCleaner cleaner;
    private final RemovalLogger logger;

    public DdiCommand(Main plugin, Settings settings, Messages messages, ItemCleaner cleaner, RemovalLogger logger) {
        this.plugin = plugin;
        this.settings = settings;
        this.messages = messages;
        this.cleaner = cleaner;
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);

        if (!SUBCOMMANDS.contains(sub)) {
            sender.sendMessage(messages.prefixed("unknown-command", "§cUnknown subcommand. Use /ddi help."));
            return true;
        }
        if (!sender.hasPermission("delDropItems." + sub)) {
            sender.sendMessage(messages.prefixed("no-permission", "§cYou do not have permission for this command."));
            return true;
        }

        switch (sub) {
            case "reload":
                plugin.reloadAll();
                if (settings.reloadMessageAllow()) {
                    sender.sendMessage(messages.get("reload-message", "[§aDelDropItems§f] §eConfiguration reloaded!"));
                }
                break;
            case "clear":
                int removed = cleaner.sweep(true);
                sender.sendMessage(messages.prefix() + messages.get("clear-message", "§eRemoved items: §f%count%")
                        .replace("%count%", String.valueOf(removed)));
                break;
            case "view-logs":
                sendLogs(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendLogs(CommandSender sender) {
        List<String> lines = logger.readLast(10);
        if (lines.isEmpty()) {
            sender.sendMessage(messages.prefixed("logs-empty", "§eThe log is empty."));
            return;
        }
        sender.sendMessage(messages.get("logs-header", "§6=== Last removals ==="));
        for (String line : lines) {
            sender.sendMessage("§7" + line);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(messages.get("help-header", "§6=== DelDropItems Commands ==="));
        if (sender.hasPermission("delDropItems.reload")) {
            sender.sendMessage(messages.get("help-reload", "§a/ddi reload - Reload the plugin configuration."));
        }
        if (sender.hasPermission("delDropItems.clear")) {
            sender.sendMessage(messages.get("help-clear", "§a/ddi clear - Remove all matching dropped items right now."));
        }
        if (sender.hasPermission("delDropItems.view-logs")) {
            sender.sendMessage(messages.get("help-logs", "§a/ddi view-logs - Show the last removals."));
        }
        sender.sendMessage(messages.get("help-help", "§a/ddi help - Show this help."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        String typed = args[0].toLowerCase(Locale.ROOT);
        for (String sub : SUBCOMMANDS) {
            if (sub.startsWith(typed) && sender.hasPermission("delDropItems." + sub)) {
                result.add(sub);
            }
        }
        return result;
    }
}
