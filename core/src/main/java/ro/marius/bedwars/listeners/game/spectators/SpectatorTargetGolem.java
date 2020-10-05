package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorTargetGolem implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {

        Entity entity = e.getEntity();
        Entity target = e.getTarget();

        if (!(entity instanceof IronGolem)) {
            return;
        }
        if (!(target instanceof Player)) {
            return;
        }
        if (!entity.hasMetadata("Match")) {
            return;
        }
        if (!entity.hasMetadata("Team")) {
            return;
        }

        Player p = (Player) e.getTarget();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        e.setCancelled(true);
        e.setTarget(null);

    }

}
