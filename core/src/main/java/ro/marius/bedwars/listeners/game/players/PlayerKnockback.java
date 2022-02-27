package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ro.marius.bedwars.game.mechanics.Cause;
import ro.marius.bedwars.game.mechanics.PlayerDamageCause;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;

public class PlayerKnockback implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(e.getEntity().getUniqueId());

        if (match == null) {
            return;
        }

        Player p = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();

        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

        Team pTeam = match.getPlayerTeam().get(p.getUniqueId());

        if (pTeam == null) {
            return;
        }
        if (!match.getPlayerTeam().containsKey(damager.getUniqueId())) {
            return;
        }
        if (pTeam.getPlayers().contains(damager)) {
            return;
        }

        match.getDamageCause().put(p, new PlayerDamageCause(System.nanoTime(), damager, Cause.KNOCKBACK));
    }

}
