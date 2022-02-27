package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

public class IceFishEvent implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        if (!e.getEntity().hasMetadata("Team")) {
            return;
        }
        if (!e.getEntity().hasMetadata("Match")) {
            return;
        }
        if (!(e.getEntity() instanceof Silverfish)) {
            return;
        }

        Silverfish silverFish = (Silverfish) e.getEntity();
        AMatch match = (AMatch) silverFish.getMetadata("Match").get(0).value();

        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

        Team team = (Team) silverFish.getMetadata("Team").get(0).value();

        if (team == null) {
            return;
        }
        if (!team.getSilverFish().contains(silverFish)) {
            return;
        }

        team.getSilverFish().remove(silverFish);
    }

    @EventHandler
    public void onSpawn(ProjectileHitEvent e) {

        Projectile entity = e.getEntity();
//		Entity shooter = entity.getShooter()

        if (!entity.hasMetadata("Icefish")) {
            return;
        }
        if (!entity.hasMetadata("Match")) {
            return;
        }
        if (!(entity instanceof Snowball)) {
            return;
        }

        AMatch match = (AMatch) entity.getMetadata("Match").get(0).value();
        Team team = (Team) entity.getMetadata("Icefish").get(0).value();

        Silverfish fish = entity.getLocation().getWorld().spawn(entity.getLocation(), Silverfish.class);
        fish.setCustomName(Utils.translate(match.getGame().getArenaOptions().getString("IceFishName"))
                .replace("<teamColor>", team.getTeamColor().getChatColor()).replace("<teamName>", team.getName()));
        fish.setMetadata("Team", new FixedMetadataValue(BedWarsPlugin.getInstance(), team));
        fish.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        fish.setCustomNameVisible(true);
        team.getSilverFish().add(fish);

    }

    @EventHandler
    public void onIceFish(EntityTargetEvent e) {

        Entity entity = e.getEntity();
        Entity target = e.getTarget();

        if (!(entity instanceof Silverfish)) {
            return;
        }
        if (!entity.hasMetadata("Team")) {
            return;
        }
        if (target == null) {
            return;
        }
        if (!(target instanceof Player)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(target.getUniqueId());

        if (match == null) {
            return;
        }

        Player p = (Player) target;
        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }
        if (!((Team) entity.getMetadata("Team").get(0).value()).getName().equals(team.getName())) {
            return;
        }

        e.setCancelled(true);
        e.setTarget(null);
    }

}
