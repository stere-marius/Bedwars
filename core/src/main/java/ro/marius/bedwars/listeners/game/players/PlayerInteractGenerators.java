package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class PlayerInteractGenerators implements Listener {

    @EventHandler
    public void onInteractPlayer(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        ArmorStand stand = (ArmorStand) e.getRightClicked();
        if (match == null) {
            return;
        }
        if (stand.isVisible()) {
            return;
        }

        e.setCancelled(true);
    }

}
