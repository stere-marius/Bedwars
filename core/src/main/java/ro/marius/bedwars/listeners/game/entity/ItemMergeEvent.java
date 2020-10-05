package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemMergeEvent implements Listener {

    @EventHandler
    public void onMerge(org.bukkit.event.entity.ItemMergeEvent event) {

        Item item = event.getEntity();

        if (item.hasMetadata("EmeraldMatch")) {
            event.setCancelled(true);
            return;
        }

        if (item.hasMetadata("DiamondMatch")) {
            event.setCancelled(true);
            return;
        }

    }

}
