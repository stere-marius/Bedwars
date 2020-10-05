package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

public class PlayerRejoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getRejoin(p);

        if (match == null) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        ManagerHandler.getGameManager().savePlayerContents(p);
        match.getPlayers().add(p);
        team.getPlayers().add(p);

        for (Player pl : match.getPlayers()) {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, pl, BedWarsPlugin.getInstance());
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(pl, p, BedWarsPlugin.getInstance());
        }

        match.sendMessage(Lang.PLAYER_REJOIN.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                .replace("<team>", team.getName()).replace("<player>", p.getName()));
        ManagerHandler.getGameManager().getPlayerMatch().put(p.getUniqueId(), match);
        ManagerHandler.getScoreboardManager().setScoreboardGame(p, true);
        match.addToSpectatorTask(p);
        match.getRejoinMap().remove(p.getUniqueId());
        e.setJoinMessage(null);
    }

}
