package ro.marius.bedwars.listeners.bungeecord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class BungeeQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        if (!BedWarsPlugin.getInstance().isBungeeCord()) {
            return;
        }
        if (ManagerHandler.getGameManager().getGames().isEmpty()) {
            return;
        }

        Game game = ManagerHandler.getGameManager().getGames().get(0);

        if (game == null) {
            return;
        }

        AMatch match = game.getMatch();

        if (match.getMatchState() == MatchState.IN_GAME) {
            match.removePlayer(e.getPlayer());
        }

    }

}
