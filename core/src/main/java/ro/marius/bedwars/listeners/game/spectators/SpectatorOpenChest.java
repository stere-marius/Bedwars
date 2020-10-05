package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorOpenChest implements Listener {

    @EventHandler
    public void onOpen(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        if (e.getClickedBlock().getType() == Material.AIR) {
            return;
        }
        if (!e.getClickedBlock().getType().name().contains("CHEST")) {
            return;
        }
        e.setCancelled(true);
    }

}
