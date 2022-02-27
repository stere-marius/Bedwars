package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;
import ro.marius.bedwars.game.mechanics.Cause;
import ro.marius.bedwars.game.mechanics.PlayerDamageCause;
import ro.marius.bedwars.game.mechanics.PlayerInvisibility;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;

public class PlayerDamageGame implements Listener {

    @EventHandler
    public void onPlayerDamageByTNT(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (!(entity instanceof Player)) {
            return;
        }

        Player p = (Player) entity;
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        if (damager instanceof Arrow) {

            Arrow arrow = (Arrow) e.getDamager();

            if (!(arrow.getShooter() instanceof Player)) {
                return;
            }

            Player dmg = (Player) arrow.getShooter();

            if (match.getPlayerTeam().get(p.getUniqueId()).getPlayers().contains(dmg)) {
                return;
            }
            if (p.getUniqueId().equals(dmg.getUniqueId())) {
                return;
            }

            match.getDamageCause().put(p, new PlayerDamageCause(System.nanoTime(), dmg, Cause.ARCHER));

            return;
        }

        if (damager instanceof TNTPrimed) {

            TNTPrimed tnt = (TNTPrimed) e.getDamager();

            if (!tnt.hasMetadata("Owner"))
                return;


            String owner = tnt.getMetadata("Owner").get(0).asString();

            if (owner.equals(p.getName()))
                return;


            match.getDamageCause().put(p, new PlayerDamageCause(System.nanoTime(), null, Cause.TNT));

            return;
        }

        if (damager instanceof Fireball) {

            Fireball fireball = (Fireball) damager;

            if (!(fireball.getShooter() instanceof Player))
                return;

            Player playerShooter = (Player) fireball.getShooter();

            if (p.getUniqueId().equals(playerShooter.getUniqueId()))
                return;

            match.getDamageCause().put(p, new PlayerDamageCause(System.nanoTime(), playerShooter, Cause.FIREBALL));
        }

    }

    @EventHandler
    public void onDamageGolem(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (e.getCause() != DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(entity.getUniqueId());
        EntityType type = damager.getType();

        if (match == null) {
            return;
        }
        if ((type != EntityType.IRON_GOLEM) && (type != EntityType.SILVERFISH)) {
            return;
        }
        if (!damager.hasMetadata("Team")) {
            return;
        }
        if (!damager.hasMetadata("Match")) {
            return;
        }

        Cause cause = (type == EntityType.IRON_GOLEM) ? Cause.GOLEM : Cause.SILVERFISH;
        Player p = (Player) e.getEntity();
        match.getDamageCause().put(p, new PlayerDamageCause(System.nanoTime(), p, cause));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (!(damager instanceof Player)) {
            return;
        }
        if (!entity.hasMetadata("Team")) {
            return;
        }

        Player p = (Player) e.getDamager();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Team eTeam = (Team) entity.getMetadata("Team").get(0).value();
        Team pTeam = match.getPlayerTeam().get(p.getUniqueId());

        if (eTeam.getName().equals(pTeam.getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamager(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();
        Entity dmger = e.getDamager();

        if (!(dmger instanceof Player)) {
            return;
        }
        if (!(entity instanceof Player)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(dmger.getUniqueId());

        if (match == null) {
            return;
        }

        Team pTeam = match.getPlayerTeam().get(entity.getUniqueId());

        if (pTeam == null) {
            return;
        }
        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

        Player damager = (Player) e.getDamager();
        Player p = (Player) e.getEntity();

        if (!match.getPlayerTeam().containsKey(damager.getUniqueId())) {
            return;
        }

        if (!match.getInvisibility().containsKey(p)) {
            return;
        }

        if (pTeam.getPlayers().contains(damager)) {
            return;
        }

        PlayerInvisibility map = match.getInvisibility().get(p);
        map.undoInvisibility();
        map.cancelTask();
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        match.getInvisibility().remove(p);
    }

    @EventHandler
    public void onDamageNPC(EntityDamageByEntityEvent e) {

        Entity damager = e.getDamager();

        if (damager instanceof Player) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(damager.getUniqueId());

            if (match == null) {
                return;
            }

        }

        if (e.getEntity().hasMetadata("MatchShop")) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity().hasMetadata("MatchUpgrade")) {
            e.setCancelled(true);
            return;
        }

    }

//	@EventHandler
//	public void onDamage(EntityDamageByEntityEvent e) {
//		if (!(e.getDamager() instanceof Player))
//			return;
//		if (!GameManager.getManager().getPlayers().containsKey(e.getDamager()))
//			return;
//
//		if (e.getEntity().hasMetadata("Shop")) {
//			e.setCancelled(true);
//			return;
//		}
//		if (e.getEntity().hasMetadata("Upgrade")) {
//			e.setCancelled(true);
//			return;
//		}
//
//	}

}
