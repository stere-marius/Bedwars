package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorDropItem implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null)
            return;
        if (!match.getSpectators().contains(p))
            return;

        e.setCancelled(true);
    }

}
