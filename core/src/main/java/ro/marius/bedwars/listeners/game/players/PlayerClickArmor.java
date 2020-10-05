package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class PlayerClickArmor implements Listener {

    @EventHandler
    public void onArmorSlot(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        if (match == null) {
            return;
        }
        if (e.getSlotType() != SlotType.ARMOR) {
            return;
        }

        e.setCancelled(true);
    }

}
