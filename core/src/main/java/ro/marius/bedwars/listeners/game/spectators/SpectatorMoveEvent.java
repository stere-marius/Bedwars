package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class SpectatorMoveEvent implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null)
            return;


        if (!match.getSpectators().contains(p))
            return;

        Location spectateLocation = match.getGame().getSpectateLocation().getLocation();

        if (!match.getGame().getGameCuboid().isInsideCuboidSelection(spectateLocation))
            return;
        if (match.getGame().getGameCuboid().isInsideCuboidSelection(p.getLocation()))
            return;

        p.teleport(spectateLocation);
        e.setFrom(spectateLocation);
        p.setFallDistance(0.0F);
        Bukkit.getLogger().info("The player " + p.getName() + " is out of arena.");
    }

}
