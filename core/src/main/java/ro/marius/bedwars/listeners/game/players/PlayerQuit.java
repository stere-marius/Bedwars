package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.ScoreboardManager;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.scoreboard.ScoreboardAPI;
import ro.marius.bedwars.team.Team;

import java.util.UUID;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuitRejoin(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        if (match.getMatchState() == MatchState.IN_WAITING) {
            return;
        }

        ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());
        match.getPlayers().forEach(pl -> {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, pl, BedWarsPlugin.getInstance());
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(pl, p, BedWarsPlugin.getInstance());
        });

        match.getSpectators().forEach(sp -> {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, sp, BedWarsPlugin.getInstance());
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(sp, p, BedWarsPlugin.getInstance());
        });

        Team team = match.getPlayerTeam().get(uuid);

        if (team == null) {
            return;
        }
        if (!team.getPlayers().contains(p)) {
            return;
        }

        Game game = match.getGame();

        ArenaOptions arenaOptions = game.getArenaOptions();

        e.setQuitMessage(null);

        if (!arenaOptions.getBoolean("Rejoin")) {
            onMatchQuit(p, uuid, match, team);
            ManagerHandler.getScoreboardManager().toggleScoreboard(p);
            return;
        }

        if (team.isBedBroken()) {
            onMatchQuit(p, uuid, match, team);
            ManagerHandler.getScoreboardManager().toggleScoreboard(p);
            return;
        }

        team.getPlayers().remove(p);
        match.getPlayers().remove(p);
        match.getRejoinMap().put(uuid, team);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        ManagerHandler.getGameManager().givePlayerContents(p);
        match.sendMessage(
                Lang.PLAYER_DISCONNECTED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                        .replace("<player>", p.getName()).replace("<team>", team.getName()));
        match.isRequiredEnding();
        ManagerHandler.getScoreboardManager().toggleScoreboard(p);
    }

    private void onMatchQuit(Player p, UUID uuid, AMatch match, Team team) {
        ScoreboardManager scManager = ManagerHandler.getScoreboardManager();

        match.getPlayers().remove(p);
        team.getPlayers().remove(p);
        match.sendMessage(
                Lang.PLAYER_DISCONNECTED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                        .replace("<player>", p.getName()).replace("<team>", team.getName()));
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        ManagerHandler.getGameManager().givePlayerContents(p);

        for (Player player : match.getPlayers()) {

            ScoreboardAPI sc = scManager.scoreboard.get(player.getUniqueId());

            if (sc == null) {
                continue;
            }

            sc.removeFromTeam(p, team);
        }

        match.getPlayerTeam().remove(uuid);
        ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());

        if (!team.getPlayers().isEmpty()) {
            return;
        }

        match.destroyBed(team);
        match.getEliminatedTeams().add(team);
        match.isRequiredEnding();

        return;
    }


    @EventHandler
    public void onPlayerQuitWaiting(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        if (match.getMatchState() != MatchState.IN_WAITING) {
            return;
        }

        match.getPlayers().forEach(pl -> {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, pl, BedWarsPlugin.getInstance());
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(pl, p, BedWarsPlugin.getInstance());
        });

        match.getSpectators().forEach(sp -> {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, sp, BedWarsPlugin.getInstance());
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(sp, p, BedWarsPlugin.getInstance());
        });

        match.removePlayer(p);
        e.setQuitMessage(null);
    }

}
