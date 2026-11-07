package plugin.DelDropItems.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import plugin.DelDropItems.config.Settings;
import plugin.DelDropItems.message.Messages;

public class DropListener implements Listener {
    private final Settings settings;
    private final Messages messages;

    public DropListener(Settings settings, Messages messages) {
        this.settings = settings;
        this.messages = messages;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!settings.dropMessageAllow()) {
            return;
        }
        String name = event.getItemDrop().getItemStack().getType().name();
        event.getPlayer().sendMessage(messages.get("drop-message", "").replace("%item_name%", name));
    }
}
