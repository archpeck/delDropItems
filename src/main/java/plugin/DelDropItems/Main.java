package plugin.DelDropItems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;




public class Main extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> itemSpawnTime = new HashMap<>();
    private final HashMap<UUID, String> itemOwners = new HashMap<>();
    private final HashMap<String, Integer> playerItemCount = new HashMap<>(); // Счетчик выброшенных предметов
    private long removalTime;
    private String dropMessage;
    private String reloadMessage;
    private boolean dropMessageAllow;
    private boolean reloadMessageAllow;
    private long broadcastInterval;
    private String broadcastMessage;
    private boolean broadcastMessageAllow;
    private String languageConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        loadMessages();

        File messagesFolder = new File(getDataFolder(), "messages");
        if (!messagesFolder.exists()) {
            messagesFolder.mkdir();
        }


        getLogger().info(ChatColor.translateAlternateColorCodes('&', "=== DelDropItems Plugin Enabled ==="));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Version: &f1.0"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Author: &f"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Initialization completed!"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "=== Available Commands ==="));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "/ddi reload - Reload the plugin configuration."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "/ddi help - Show the list of commands.\n\n"));


        File logDir = new File(getDataFolder(), "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                HashMap<UUID, String> itemsToRemove = new HashMap<>();

                for (HashMap.Entry<UUID, Long> entry : itemSpawnTime.entrySet()) {
                    UUID itemId = entry.getKey();
                    Item item = (Item) Bukkit.getEntity(itemId);
                    if (item != null && currentTime - entry.getValue() >= removalTime * 1000) {
                        String playerName = itemOwners.get(itemId);
                        String itemName = item.getItemStack().getType().name();
                        item.remove();
                        logItemRemoval(playerName, itemName); // Логируем удаление
                        itemsToRemove.put(itemId, playerName);
                    }
                }

                for (UUID itemId : itemsToRemove.keySet()) {
                    itemSpawnTime.remove(itemId);
                    itemOwners.remove(itemId);
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (broadcastMessageAllow && broadcastMessage != null && !broadcastMessage.isEmpty()) {
                    Bukkit.broadcastMessage(broadcastMessage);
                }
            }
        }.runTaskTimer(this, 0L, broadcastInterval * 20L);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        removalTime = config.getLong("item-removal-time");
        dropMessageAllow = config.getBoolean("drop-message-allow");
        broadcastInterval = config.getLong("broadcast-interval");
        broadcastMessageAllow = config.getBoolean("broadcast-message-allow");
        reloadMessageAllow = config.getBoolean("reload-message-allow");
        languageConfig = config.getString("language");
    }

    private void loadMessages() {
        String language = getConfig().getString("language", "en");
        File messagesFolder = new File(getDataFolder(), "messages");
        File messageFile = new File(messagesFolder, "messages_" + language + ".yml");

        // Проверка и создание папки, если она отсутствует
        if (!messagesFolder.exists()) {
            messagesFolder.mkdirs(); // Создание папки
        }

        // Проверка и создание файла, если он отсутствует
        if (!messageFile.exists()) {
            try (InputStream input = getResource("messages_" + language + ".yml")) {
                if (input != null) {
                    Files.copy(input, messageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    getLogger().severe("Default message file not found in resources!");
                }
            } catch (IOException e) {
                getLogger().severe("Could not create message file: " + e.getMessage());
            }
        }

        // Загрузка конфигурации сообщений
        YamlConfiguration messages = YamlConfiguration.loadConfiguration(messageFile);
        broadcastMessage = messages.getString("broadcast-message", "[§aDelDropItems§f] §cItem removal will occur in §l5 minutes§r!");
        reloadMessage = messages.getString("reload-message", "[§aDelDropItems§f] §ePlugin configuration has been successfully reloaded!");
        dropMessage = messages.getString("drop-message", "[§aDelDropItems§f] §eYou dropped: %item_name%!");
    }



    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (dropMessageAllow) {
            Item droppedItem = event.getItemDrop();
            UUID itemId = droppedItem.getUniqueId();
            String playerName = event.getPlayer().getName();

            itemSpawnTime.put(itemId, System.currentTimeMillis());
            itemOwners.put(itemId, playerName);

            // Увеличиваем счётчик для игрока
            playerItemCount.put(playerName, playerItemCount.getOrDefault(playerName, 0) + 1);

            String message = dropMessage.replace("%item_name%", droppedItem.getItemStack().getType().name());
            event.getPlayer().sendMessage(message);
        }
    }

    private void logItemRemoval(String playerName, String itemName) {
        int count = playerItemCount.getOrDefault(playerName, 0);
        String logMessage = String.format("[%s] Item: %s x%d, dropped by player: %s has been removed.",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                itemName,
                count,
                playerName
                );

        File logFile = new File(getDataFolder() + "/logs", "item_removal_log.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Сбрасываем счётчик для игрока
        playerItemCount.put(playerName, 0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ddi")) {
            if (args.length == 0) {
                // Вывод списка подкоманд
                sender.sendMessage("§6=== Available Commands ===");
                sender.sendMessage("§a/ddi reload - Reload the plugin configuration.");
                sender.sendMessage("§a/ddi help - Show the list of commands.");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("delDropItems.reload")) {
                    sender.sendMessage("§cYou do not have permission for this command.");
                    return true;
                }
                reloadConfig();
                loadConfigValues();
                sender.sendMessage(reloadMessage);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("delDropItems.help")) {
                    sender.sendMessage("§cYou do not have permission for this command.");
                    return true;
                }
                // Display help
                sender.sendMessage("§6=== DelDropItems Commands Help ===");
                sender.sendMessage("§a/ddi reload - Reload the plugin configuration.");
                return true;
            }
        }
        return false;
    }


    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6=== DelDropItems Plugin Commands ===");
        sender.sendMessage("§a/ddi reload - Reload the plugin configuration.");
        sender.sendMessage("§a/ddi help - Show this help.");
        sender.sendMessage("§6===============================");
    }

}