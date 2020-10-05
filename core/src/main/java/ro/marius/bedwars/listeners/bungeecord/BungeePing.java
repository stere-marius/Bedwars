package ro.marius.bedwars.listeners.bungeecord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.utils.Utils;

public class BungeePing implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent e) {
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
        org.bukkit.configuration.file.FileConfiguration config = BedWarsPlugin.getInstance().getConfig();

        String motd = config.getString("BungeeCord.MOTD.Display");
        String inGame = config.getString("BungeeCord.MOTD.InGameState");
        String inWaiting = config.getString("BungeeCord.MOTD.InWaitingState");
        String gameState = (match.getMatchState() == MatchState.IN_GAME) ? inGame : inWaiting;
        e.setMaxPlayers(game.getMaxPlayers());
        e.setMotd(Utils.translate(motd.replace("<gameState>", gameState).replace("<max>", game.getMaxPlayers() + "")
                .replace("<inGame>", match.getPlayers().size() + "")));
    }

}
