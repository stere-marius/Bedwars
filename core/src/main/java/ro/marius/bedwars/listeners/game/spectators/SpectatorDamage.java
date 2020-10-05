package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        e.setCancelled(true);
    }


    @EventHandler
    public void onEntityDamageArrow(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!(e.getDamager() instanceof Arrow)) {
            return;
        }
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) ((Arrow) e.getDamager()).getShooter();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onSpectatorDamageByOther(EntityDamageEvent e) {
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
