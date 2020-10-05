package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorLoseHunger implements Listener {

    @EventHandler
    public void onLoseHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }
        e.setCancelled(true);
    }

}
