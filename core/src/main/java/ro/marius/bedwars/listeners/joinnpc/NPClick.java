package ro.marius.bedwars.listeners.joinnpc;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ro.marius.bedwars.menu.extra.JoinInventory;

public class NPClick implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {

        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (!entity.hasMetadata("BedwarsNPC")) {
            return;
        }

        String arenaType = entity.getMetadata("BedwarsNPC").get(0).asString();
        p.openInventory(new JoinInventory(arenaType).getInventory());

    }

//	@EventHandler
//	public void onRemove(NPCRemoveEvent e) {
//
//	}

}
