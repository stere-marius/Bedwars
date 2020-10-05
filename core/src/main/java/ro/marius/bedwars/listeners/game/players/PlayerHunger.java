package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import ro.marius.bedwars.manager.ManagerHandler;

public class PlayerHunger implements Listener {

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent e) {

        Player p = (Player) e.getEntity();

        if (!ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId())) {
            return;
        }

        e.setCancelled(true);
    }

}
