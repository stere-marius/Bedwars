package ro.marius.bedwars.match;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.ScoreboardManager;
import ro.marius.bedwars.manager.type.VersionManager;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.TeamBed;
import ro.marius.bedwars.utils.PlayerHologram;
import ro.marius.bedwars.utils.Utils;

import java.util.*;
import java.util.Map.Entry;

public class MatchEnd {

    private final AMatch match;
    private final String cause;

    public MatchEnd(AMatch match, String cause) {
        this.match = match;
        this.cause = cause;
    }

    public void onReset() {

        long start = System.nanoTime();

        ScoreboardManager scoreboardManager = ManagerHandler.getScoreboardManager();
        VersionManager versionManager = ManagerHandler.getVersionManager();
        String arenaType = this.match.getGame().getArenaType();
        Set<Player> players = this.match.getPlayers();
        Set<Player> spectators = this.match.getSpectators();
        boolean isRestart = "RESTART".equalsIgnoreCase(this.cause) || "RELOAD".equalsIgnoreCase(this.cause);

        for (Player p : players) {

            spectators.forEach(sp -> versionManager.getVersionWrapper().showPlayer(p, sp, BedWarsPlugin.getInstance()));

            if (!isRestart) {
                updatePlayerData(arenaType, p, true);
                this.updateHologram(p);
            }

            Utils.resetPlayer(p, true, true);
            p.getEnderChest().clear();
            ManagerHandler.getGameManager().givePlayerContents(p);
            Utils.teleportToLobby(p, BedWarsPlugin.getInstance());

            p.setFlying(false);
            p.setAllowFlight(false);
            versionManager.getVersionWrapper().setCollidable(p, true);
            scoreboardManager.toggleScoreboard(p);

            ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());
        }

        for (Player p : spectators) {

            spectators.forEach(sp -> {
                versionManager.getVersionWrapper().showPlayer(sp, p, BedWarsPlugin.getInstance());
                versionManager.getVersionWrapper().showPlayer(p, sp, BedWarsPlugin.getInstance());
            });

            if (!isRestart) {
                updatePlayerData(arenaType, p, false);
                this.updateHologram(p);
            }

            p.getEnderChest().clear();
            scoreboardManager.toggleScoreboard(p);
            p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            Utils.resetPlayer(p, true, true);
            ManagerHandler.getGameManager().givePlayerContents(p);
            Utils.teleportToLobby(p, BedWarsPlugin.getInstance());

            p.setFlying(false);
            p.setAllowFlight(false);
            versionManager.getVersionWrapper().setCollidable(p, true);

            ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());

            if (!isRestart) {
                ManagerHandler.getVersionManager().getVersionWrapper().sendPlayerInfoPackets(p, BedWarsPlugin.getInstance());
            }

        }

        if (isRestart) {
            this.match.getMatchEntity().forEach(Entity::remove);
            this.match.getDiamondGenerators().forEach(DiamondGenerator::removeGenerator);
            this.match.getEmeraldGenerators().forEach(EmeraldGenerator::removeGenerator);
            this.match.getPlacedBeds().values().forEach(TeamBed::removeMetadata);
        }

        if (!isRestart) {
            clearMatchData();
        }

        long end = System.nanoTime() - start;

        Bukkit.getLogger().info("Took " + end + " to call the onReset method");

    }

    private void clearCollections() {
        this.match.getPermanentSpectators().clear();
        this.match.getEliminatedTeams().clear();
        this.match.getPlacedBlocks().clear();
        this.match.getMatchEntity().clear();
        this.match.getDamageCause().clear();
        this.match.getPreventTrap().clear();
        this.match.getPlayerTeam().clear();
        this.match.getSpectators().clear();
        this.match.getPlacedBeds().clear();
        this.match.getMatchData().clear();
        this.match.getRejoinMap().clear();
        this.match.getPlayers().clear();
    }

    private void clearMatchData() {
        this.match.getDiamondGenerators().forEach(DiamondGenerator::removeGenerator);
        this.match.getEmeraldGenerators().forEach(EmeraldGenerator::removeGenerator);
        this.match.getPlacedBeds().values().forEach(TeamBed::removeMetadata);
        this.match.getMatchEntity().forEach(Entity::remove);
        this.clearCollections();
    }

    private void updatePlayerData(String arenaType, Player p, boolean isWinner) {
        APlayerData data = ManagerHandler.getGameManager().getData(p);
        MatchData matchData = this.match.getMatchData(p);

        data.addDeaths(arenaType, matchData.getDeaths());
        data.addKills(arenaType, matchData.getKills());
        data.addFinalKills(arenaType, matchData.getFinalKills());
        data.addBedsBroken(arenaType, matchData.getBedBroken());
        data.addGamePlayed(arenaType);

        if (isWinner) {
            data.addWin(arenaType);
            return;
        }

        data.addBedLost(arenaType);
        data.addDefeat(arenaType);
    }

    public void sendMessageEnd() {

        if (this.match.getPlayerTeam().values().size() <= 0) {
            return;
        }

        ArenaOptions arenaOptions = this.match.getGame().getArenaOptions();
        Team winnerTeam = (Team) this.match.getPlayerTeam().values().toArray()[0];
        boolean send = arenaOptions.getBoolean("WinTitle.Enabled");
        int fadeIn = arenaOptions.getInt("WinTitle.FadeIn");
        int fadeOut = arenaOptions.getInt("WinTitle.FadeOut");
        int stay = arenaOptions.getInt("WinTitle.Stay");
        String title = arenaOptions.getString("WinTitle.Title");
        String subTitle = arenaOptions.getString("WinTitle.SubTitle");
        StringJoiner winnerMessage = new StringJoiner(",");
        VersionWrapper version = ManagerHandler.getVersionManager().getVersionWrapper();

        for (Player p : winnerTeam.getPlayers()) {
            version.sendTitle(p, fadeIn, stay, fadeOut, title, subTitle, send, true);
            arenaOptions.performCommands("WinnerCommands", p);
            winnerMessage.add(p.getName());
        }

        TreeMap<Integer, String> top = new TreeMap<>(Collections.reverseOrder());

        for (Entry<String, MatchData> entry : this.match.getMatchData().entrySet()) {
            top.put(entry.getValue().getKills(), entry.getKey());
        }

        if (top.isEmpty()) {
            return;
        }

        int mapSize = top.size();
        String topOneName = (String) top.values().toArray()[0];
        String topTwoName = (mapSize >= 2) ? (String) top.values().toArray()[1] : "In waiting";
        String topThreeName = (mapSize >= 3) ? (String) top.values().toArray()[2] : "In waiting";
        String topOneKills = top.keySet().toArray()[0] + "";
        String topTwoKills = (mapSize >= 2) ? (top.keySet().toArray()[1] + "") : "";
        String topThreeKills = (mapSize >= 3) ? (top.keySet().toArray()[2] + "") : "";

        for (String s : Lang.END_MESSAGE.getList()) {
            String message = s.replace("<winner>", winnerMessage.toString()).replace("<topOneName>", topOneName)
                    .replace("<topTwoName>", topTwoName).replace("<topThreeName>", topThreeName)
                    .replace("<topOneKills>", topOneKills + "").replace("<topTwoKills>", topTwoKills + "")
                    .replace("<topThreeKills>", topThreeKills + "").replace("<arena>", this.match.getGame().getName())
                    .replace("<winnerTeam>", winnerTeam.getName())
                    .replace("<winnerTeamColor>", winnerTeam.getTeamColor().getChatColor());

            if (message.contains("<center>")) {
                this.match.sendCenterMessage(message.replace("<center>", ""));
                continue;
            }

            this.match.sendMessage(message);
        }

    }

    public void updateHologram(Player p) {

        Map<Player, PlayerHologram> map = ManagerHandler.getHologramManager().getPlayerHologram();

        PlayerHologram playerHologram = map.get(p);

        if (playerHologram == null) {
            return;
        }

        playerHologram.updateHologram();

    }

    public AMatch getMatch() {
        return this.match;
    }

    public String getCause() {
        return this.cause;
    }
}
