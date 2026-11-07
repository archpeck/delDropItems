package plugin.DelDropItems.message;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import plugin.DelDropItems.config.Settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Messages {
    private final JavaPlugin plugin;
    private final Settings settings;
    private YamlConfiguration messages;
    private YamlConfiguration fallback;

    public Messages(JavaPlugin plugin, Settings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void load() {
        String language = settings.language();
        messages = null;
        fallback = loadBundled("messages_en.yml");

        if (plugin.getResource("messages_" + language + ".yml") == null) {
            plugin.getLogger().warning(console("console-language-missing",
                    "Language '%language%' not found, using English.", "%language%", language));
            language = "en";
        }

        fallback = loadBundled("messages_" + language + ".yml");

        File folder = new File(plugin.getDataFolder(), "messages");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning(console("console-folder-error", "Could not create the messages folder."));
        }

        File file = new File(folder, "messages_" + language + ".yml");
        if (!file.exists()) {
            try (InputStream in = plugin.getResource("messages_" + language + ".yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                plugin.getLogger().severe(console("console-file-error",
                        "Could not create the message file: %error%", "%error%", String.valueOf(e.getMessage())));
            }
        }

        messages = YamlConfiguration.loadConfiguration(file);
    }

    private YamlConfiguration loadBundled(String resource) {
        InputStream stream = plugin.getResource(resource);
        if (stream == null) {
            return null;
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            return null;
        }
    }

    private String raw(String key, String def) {
        String value = messages != null ? messages.getString(key) : null;
        if (value == null && fallback != null) {
            value = fallback.getString(key);
        }
        return value != null ? value : def;
    }

    public String get(String key, String def) {
        return ChatColor.translateAlternateColorCodes('&', raw(key, def));
    }

    public String prefix() {
        return get("prefix", "[§aDelDropItems§f] ");
    }

    public String prefixed(String key, String def) {
        return prefix() + get(key, def);
    }

    public String console(String key, String def, String... replacements) {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', fill(raw(key, def), replacements)));
    }

    public static String fill(String text, String... replacements) {
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return text;
    }

    public String applyTime(String text) {
        long seconds = settings.removalTime();
        return fill(text,
                "%seconds%", String.valueOf(seconds),
                "%minutes%", String.valueOf(Math.max(1L, seconds / 60L)));
    }
}
