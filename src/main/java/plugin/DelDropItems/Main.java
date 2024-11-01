package plugin.DelDropItems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

        getLogger().info(ChatColor.translateAlternateColorCodes('&', "\n\n=== Плагин DelDropItems запущен ==="));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Версия: &f1.0"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Автор: &f"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "Инициализация завершена!"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "=== Доступные команды ==="));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "/ddi reload - Перезагрузить конфигурацию плагина."));
        getLogger().info(ChatColor.translateAlternateColorCodes('&', "/ddi help - Показать список команд.\n\n"));

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
                if(reloadMessageAllow) {
                    Bukkit.broadcastMessage(broadcastMessage);
                }

            }
        }.runTaskTimer(this, 0L, broadcastInterval * 20L);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        removalTime = config.getLong("item-removal-time");
        dropMessage = config.getString("drop-message");
        reloadMessage = config.getString("reload-message");
        dropMessageAllow = config.getBoolean("drop-message-allow");
        broadcastInterval = config.getLong("broadcast-interval");
        broadcastMessage = config.getString("broadcast-message");
        broadcastMessageAllow = config.getBoolean("broadcast-message-allow");
        reloadMessageAllow = config.getBoolean("reload-message-allow");
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
        String logMessage = String.format("[%s] Предмет: %s x%d, выброшенный игроком: %s был удалён.",
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
                sender.sendMessage("§6=== Доступные команды ===");
                sender.sendMessage("§a/ddi reload - Перезагрузить конфигурацию плагина.");
                sender.sendMessage("§a/ddi help - Показать список команд.");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("delDropItems.reload")) {
                    sender.sendMessage("§cУ вас нет разрешения на эту команду.");
                    return true;
                }
                reloadConfig();
                loadConfigValues();
                sender.sendMessage(reloadMessage);
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("delDropItems.help")) {
                    sender.sendMessage("§cУ вас нет разрешения на эту команду.");
                    return true;
                }
                // Вывод помощи
                sender.sendMessage("§6=== Помощь по командам ===");
                sender.sendMessage("§a/ddi reload - Перезагрузить конфигурацию плагина.");
                return true;
            }
        }
        return false;
    }


    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6=== Команды плагина DelDropItems ===");
        sender.sendMessage("§a/ddi reload - Перезагрузить конфигурацию плагина.");
        sender.sendMessage("§a/ddi help - Показать этот список команд.");
        sender.sendMessage("§6===============================");
    }

}
