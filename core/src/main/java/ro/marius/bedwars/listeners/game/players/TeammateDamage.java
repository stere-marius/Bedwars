package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;

public class TeammateDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity dmg = e.getDamager();

        if (!(dmg instanceof Player)) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }

        AMatch pMatch = ManagerHandler.getGameManager().getPlayerMatch().get(entity.getUniqueId());
        AMatch dMatch = ManagerHandler.getGameManager().getPlayerMatch().get(dmg.getUniqueId());

        if ((pMatch == null) || (dMatch == null)) {
            return;
        }
        if (!pMatch.getGame().getName().equals(dMatch.getGame().getName())) {
            return;
        }
        if (dMatch.getMatchState() == MatchState.IN_WAITING) {
            return;
        }

        Player damager = (Player) dmg;
        Player p = (Player) entity;

        Team dTeam = pMatch.getPlayerTeam().get(damager.getUniqueId());

        if (dTeam == null) {
            return;
        }

        if (!dTeam.getPlayers().contains(p)) {
            return;
        }

        e.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamageArrow(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (!(damager instanceof Arrow)) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(entity.getUniqueId());

        if (match == null) {
            return;
        }

        Arrow arrow = (Arrow) damager;
        ProjectileSource shooter = arrow.getShooter();

        if (!(shooter instanceof Player)) {
            return;
        }

        Player p = (Player) entity;

        Team pTeam = match.getPlayerTeam().get(p.getUniqueId());

        if (!pTeam.getPlayers().contains(shooter)) {
            return;
        }

        e.setCancelled(true);

    }

}
