package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchSpectator;

public class SpectatorToggleFly implements Listener {

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        MatchSpectator spectator = this.getPermanentSpectator(match, p.getName());

        if (spectator == null) {
            return;
        }

        e.setCancelled(spectator.isFly());

    }

    public MatchSpectator getPermanentSpectator(AMatch match, String pName) {

        for (MatchSpectator spectator : match.getPermanentSpectators()) {

            if (!spectator.getSpectator().getName().equals(pName)) {
                continue;
            }

            return spectator;

        }

        return null;

    }

}
