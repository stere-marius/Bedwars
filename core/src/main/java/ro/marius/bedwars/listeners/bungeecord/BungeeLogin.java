package ro.marius.bedwars.listeners.bungeecord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class BungeeLogin implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (!BedWarsPlugin.getInstance().isBungeeCord()) {
            return;
        }
        if (ManagerHandler.getGameManager().getGames().isEmpty()) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getGames().get(0).getMatch();

        if (match == null) {
            return;
        }

        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

//		if (GameManager.getManager().getRejoin().containsKey(e.getPlayer().getName()))
//			return;

        e.setResult(Result.KICK_FULL);
        e.setKickMessage(Lang.ALREADY_IN_GAME.getString());

    }

}
