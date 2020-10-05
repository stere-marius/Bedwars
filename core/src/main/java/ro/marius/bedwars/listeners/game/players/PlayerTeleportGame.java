package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class PlayerTeleportGame implements Listener {

    @EventHandler
    public void onChangeWorld(PlayerTeleportEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            Game gameWorld = ManagerHandler.getGameManager().getGame(e.getTo().getWorld().getName());

            if ((gameWorld != null) && (gameWorld.getMatch().getMatchState() == MatchState.RESTARTING)) {
                e.setCancelled(true);
                return;
            }

            return;
        }

        if (match.getMatchState() != MatchState.IN_GAME) {
            return;
        }

        TeleportCause cause = e.getCause();

        if (cause == TeleportCause.ENDER_PEARL) {
            p.teleport(e.getTo());
            e.setCancelled(true);
            return;
        }

        if (cause == TeleportCause.PLUGIN) {
            return;
        }

        Game game = match.getGame();
        boolean isOP = p.isOp();

        if (!game.getArenaOptions().getBoolean("DuringGame.AllowTeleporting.OP") && isOP) {
            e.setCancelled(true);
            return;
        }

        if (!game.getArenaOptions().getBoolean("DuringGame.AllowTeleporting.Players") && !isOP) {
            e.setCancelled(true);
            return;
        }

    }

}
