package plugin.DelDropItems.cleanup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import plugin.DelDropItems.message.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class RemovalLogger {
    private final JavaPlugin plugin;
    private final Messages messages;
    private final Object lock = new Object();

    public RemovalLogger(JavaPlugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    private File file() {
        return new File(new File(plugin.getDataFolder(), "logs"), "item_removal_log.txt");
    }

    public void write(List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }
        final File file = file();
        Runnable job = () -> {
            synchronized (lock) {
                try {
                    File dir = file.getParentFile();
                    if (!dir.exists() && !dir.mkdirs()) {
                        return;
                    }
                    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                } catch (IOException e) {
                    plugin.getLogger().warning(messages.console("console-log-error",
                            "Could not write the removal log: %error%", "%error%", String.valueOf(e.getMessage())));
                }
            }
        };

        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, job);
        } else {
            job.run();
        }
    }

    public List<String> readLast(int count) {
        File file = file();
        if (!file.exists()) {
            return Collections.emptyList();
        }
        synchronized (lock) {
            try {
                List<String> all = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                return all.subList(Math.max(0, all.size() - count), all.size());
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }
}
