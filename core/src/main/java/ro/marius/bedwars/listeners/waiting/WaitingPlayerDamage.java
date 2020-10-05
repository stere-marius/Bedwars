package ro.marius.bedwars.listeners.waiting;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class WaitingPlayerDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!ManagerHandler.getGameManager().getPlayerMatch().containsKey(e.getDamager().getUniqueId())) {
            return;
        }
        if (ManagerHandler.getGameManager().getPlayerMatch().get(e.getDamager().getUniqueId()).getMatchState() == MatchState.IN_GAME) {
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

        Player p = (Player) e.getEntity();
        Arrow aw = (Arrow) e.getDamager();

        if (!(aw.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) aw.getShooter();

        AMatch shMatch = ManagerHandler.getGameManager().getPlayerMatch().get(shooter.getUniqueId());

        if (shMatch == null) {
            return;
        }

        AMatch pMatch = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (pMatch == null) {
            return;
        }

        if (pMatch.getMatchState() == MatchState.IN_GAME) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onSpectatorDamageByOther(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!ManagerHandler.getGameManager().getPlayerMatch().containsKey(e.getEntity().getUniqueId())) {
            return;
        }
        if (ManagerHandler.getGameManager().getPlayerMatch().get(e.getEntity().getUniqueId()).getMatchState() == MatchState.IN_GAME) {
            return;
        }
        Player p = (Player) e.getEntity();
        Game game = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId()).getGame();
        if (e.getCause() == DamageCause.VOID) {
            p.teleport(game.getWaitingLocation().getLocation());
        }
        e.setCancelled(true);
    }

}
