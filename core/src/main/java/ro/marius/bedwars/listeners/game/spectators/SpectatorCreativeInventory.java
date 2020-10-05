package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorCreativeInventory implements Listener {


    @EventHandler(priority = EventPriority.NORMAL)
    public void onCreative(InventoryCreativeEvent e) {
        Player p = (Player) e.getWhoClicked();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }
    }

}
