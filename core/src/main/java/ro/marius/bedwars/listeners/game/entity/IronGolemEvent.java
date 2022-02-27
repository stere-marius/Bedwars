package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;

public class IronGolemEvent implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        Entity entity = e.getEntity();

        if (!entity.hasMetadata("Team")) {
            return;
        }
        if (!entity.hasMetadata("Match")) {
            return;
        }
        if (!(entity instanceof IronGolem)) {
            return;
        }

        IronGolem golem = (IronGolem) entity;
        AMatch match = (AMatch) golem.getMetadata("Match").get(0).value();

        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

        Team team = (Team) golem.getMetadata("Team").get(0).value();

        if ((team == null) || !team.getGolems().contains(golem)) {
            return;
        }

        team.getGolems().remove(golem);
    }

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

        Player p = (Player) target;

        AMatch match = (AMatch) entity.getMetadata("Match").get(0).value();
        Team golemTeam = (Team) entity.getMetadata("Team").get(0).value();
        Team playerTeam = match.getPlayerTeam().get(p.getUniqueId());

        if ((golemTeam == null) || (playerTeam == null)) {
            return;
        }

        if (golemTeam.getName().equals(playerTeam.getName())) {
            e.setTarget(null);
            e.setCancelled(true);
        }
    }

}
