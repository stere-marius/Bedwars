package ro.marius.bedwars.listeners.bungeecord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class BungeeJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!BedWarsPlugin.getInstance().isBungeeCord()) {
            return;
        }
        if (ManagerHandler.getGameManager().getGames().isEmpty()) {
            return;
        }
//		if (GameManager.getManager().getRejoin().containsKey(e.getPlayer()))
//			return;

        e.setJoinMessage(null);
        AMatch match = ManagerHandler.getGameManager().getGames().get(0).getMatch();
        match.addPlayer(e.getPlayer());
    }

}
