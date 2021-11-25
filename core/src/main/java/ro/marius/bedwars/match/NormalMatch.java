package ro.marius.bedwars.match;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Items;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.tasks.RespawnTask;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NormalMatch extends AMatch {

    public NormalMatch(Game game) {
        super(game);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addPlayer(Player p) {

        if (ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId())) {
            p.sendMessage(Lang.ALREADY_IN_GAME.getString());
            return;
        }

        if (this.getMatchState() == MatchState.CLOSED) {
            p.sendMessage(Utils.translate("&eThe arena state is PAUSED"));
            return;
        }

        if (this.getPlayers().contains(p)) {
            p.sendMessage(Lang.ALREADY_IN_GAME.getString());
            return;
        }

        if (this.getPlayers().size() >= this.getGame().getMaxPlayers()) {
            p.sendMessage(Lang.GAME_FULL.getString());
            return;
        }

        if (this.getMatchState() != MatchState.IN_WAITING) {
            p.sendMessage(Lang.GAME_IS_STARTED.getString());
            return;
        }

        if (BedWarsPlugin.getPartyHandler().hasParty(p) && BedWarsPlugin.getPartyHandler().getLeader(p).getName().equals(p.getName())) {
            addParty(p);
            return;
        }

        Team team = super.findAvailableTeam();

        if (team == null) {
            p.sendMessage(Utils.translate("&cCouldn't find any empty team."));
            return;
        }

        ManagerHandler.getGameManager().savePlayerContents(p);
        p.spigot().setCollidesWithEntities(true);
        p.setAllowFlight(false);
        p.setFlying(false);
        this.getPlayers().forEach(player -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(player, p, BedWarsPlugin.getInstance()));
        this.getPlayers().forEach(player -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, player, BedWarsPlugin.getInstance()));
        p.teleport(this.getGame().getWaitingLocation().getLocation());
        Utils.resetPlayer(p, true, true);
        this.getPlayers().add(p);
        team.getPlayers().add(p);
        this.getPlayerTeam().put(p.getUniqueId(), team);
        super.addItemsWaiting(p);
        super.setStartingTime();
        super.isRequiredStarting();
        ManagerHandler.getGameManager().getPlayerMatch().put(p.getUniqueId(), this);
        ManagerHandler.getScoreboardManager().setScoreboreboardLobby(p);
        this.getGame().notifyObservers();
        this.sendMessage(Lang.PLAYER_JOINED_IN_GAME.getString().replace("<player>", p.getName())
                .replace("<max>", this.getGame().getMaxPlayers() + "").replace("<inGame>", this.getPlayers().size() + ""));
    }

    public void addParty(Player player) {

        List<Player> availablePartyPlayers = new ArrayList<>(BedWarsPlugin.getPartyHandler().getMembers(player));
        availablePartyPlayers.removeIf(p -> ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId()));
        availablePartyPlayers = availablePartyPlayers.subList(0, Math.min(getRemainingPlayers(), availablePartyPlayers.size()));
        findTeamPlayers(new ArrayList<>(availablePartyPlayers), getGame().getPlayersPerTeam());

        for (Player partyPlayer : availablePartyPlayers) {
            ManagerHandler.getGameManager().savePlayerContents(partyPlayer);
            partyPlayer.spigot().setCollidesWithEntities(true);
            partyPlayer.setAllowFlight(false);
            partyPlayer.setFlying(false);
            this.getPlayers().forEach(matchPlayer -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(matchPlayer, partyPlayer, BedWarsPlugin.getInstance()));
            this.getPlayers().forEach(matchPlayer -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(partyPlayer, matchPlayer, BedWarsPlugin.getInstance()));
            partyPlayer.teleport(this.getGame().getWaitingLocation().getLocation());
            Utils.resetPlayer(partyPlayer, true, true);
            this.getPlayers().add(partyPlayer);
            super.addItemsWaiting(partyPlayer);
            super.setStartingTime();
            ManagerHandler.getGameManager().getPlayerMatch().put(partyPlayer.getUniqueId(), this);
            ManagerHandler.getScoreboardManager().setScoreboreboardLobby(partyPlayer);
            this.sendMessage(Lang.PLAYER_JOINED_IN_GAME.getString().replace("<player>", partyPlayer.getName())
                    .replace("<max>", this.getGame().getMaxPlayers() + "").replace("<inGame>", this.getPlayers().size() + ""));
        }

        this.getGame().notifyObservers();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removePlayer(Player p) {

        if (!this.getPlayers().contains(p)) {
            p.sendMessage(Lang.NOT_IN_GAME.getString());
            return;
        }

        p.getInventory().clear();
        p.spigot().setCollidesWithEntities(true);
        p.updateInventory();
        super.removeRespawnTask(p);
        super.removeFromTeam(p);
        super.getPlayers().remove(p);
        super.sendMessage(Lang.PLAYER_LEFT_IN_GAME.getString().replace("<player>", p.getName())
                .replace("<gameName>", this.getGame().getName()).replace("<inGame>", this.getPlayers().size() + "")
                .replace("<max>", this.getGame().getMaxPlayers() + ""));
        Utils.teleportToLobby(p, BedWarsPlugin.getInstance());
        Utils.resetPlayer(p, true, true);
        ManagerHandler.getScoreboardManager().toggleScoreboard(p);
        this.getPlayers().forEach(p1 -> this.getPlayers().forEach(p2 -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p1, p2, BedWarsPlugin.getInstance())));
        this.getSpectators().forEach(sp -> ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(p, sp, BedWarsPlugin.getInstance()));
        ManagerHandler.getGameManager().givePlayerContents(p);
        super.isRequiredEnding();
        super.setCancelledTask();
        ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());
        this.getPlayers().remove(p);
        this.getGame().notifyObservers();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addToSpectatorTask(Player p) {

        if (this.getSpectators().contains(p)) {
            p.sendMessage(Utils.translate("&cYou're already spectator."));
            return;
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        Utils.clearPotionEffects(p);
        p.setHealth(20.0);
        p.teleport(this.getGame().getSpectateLocation().getLocation());
        this.getSpectators().add(p);
        p.setGameMode(GameMode.SURVIVAL);
        p.setAllowFlight(true);
        p.setFlying(true);

        for (Player matchPlayer : this.getPlayers()) {
            ManagerHandler.getVersionManager().getVersionWrapper().hidePlayer(matchPlayer, p, BedWarsPlugin.getInstance());
        }

        p.spigot().setCollidesWithEntities(false);
        RespawnTask respawnTask = new RespawnTask(p, this);
        respawnTask.startTask();
        this.getRespawnTask().put(p, respawnTask);

    }

    @Override
    public void addToSpectator(Player p) {

        Game game = this.getGame();
        MatchData data = this.getMatchData(p);
        data.addBedLost();
        data.addFinalDeath();
        game.getArenaOptions().performCommands("LoserCommands", p);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.setHealth(20.0);
        p.teleport(game.getSpectateLocation().getLocation());
        this.getSpectators().add(p);
        this.getPermanentSpectators().add(new MatchSpectator(this, p));
        this.getPlayers().remove(p);
        this.getPlayers().forEach(players -> ManagerHandler.getVersionManager().getVersionWrapper().hidePlayer(players, p, BedWarsPlugin.getInstance()));
        p.setAllowFlight(true);
        p.setFlying(true);
        ManagerHandler.getVersionManager().getVersionWrapper().setCollidable(p, false);

        new BukkitRunnable() {

            @Override
            public void run() {
                p.getInventory().setItem(Items.TELEPORTER.getSlot(), Items.TELEPORTER.toItemStack());
                p.getInventory().setItem(Items.SPECTATOR_LEAVE.getSlot(), Items.SPECTATOR_LEAVE.toItemStack());
                p.getInventory().setItem(Items.SPECTATOR_SETTINGS.getSlot(), Items.SPECTATOR_SETTINGS.toItemStack());

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20);

    }

    @Override
    public void startGame() {

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                NormalMatch.this.setStartingTime(NormalMatch.this.getStartingTime() - 1);

                if (NormalMatch.this.getStartingTime() == 10) {
                    NormalMatch.this.sendMessage(Lang.GAME_STARTING_IN.getString().replace("<seconds>", NormalMatch.this.getStartingTime() + ""));
                }

                if ((NormalMatch.this.getStartingTime() <= 5) && (NormalMatch.this.getStartingTime() > 0)) {
                    NormalMatch.this.sendMessage(Lang.GAME_STARTING_IN.getString().replace("<seconds>", NormalMatch.this.getStartingTime() + ""));
                }

                if (NormalMatch.this.getStartingTime() == 0) {
                    this.cancel();
                    NormalMatch.this.setMatchState(MatchState.IN_GAME);
                    NormalMatch.this.setForceStart(false);
                    NormalMatch.this.setupPlayers();
                    NormalMatch.this.setupTeams();
                    NormalMatch.this.spawnNPC();
                    NormalMatch.this.spawnAirGenerators();
                    NormalMatch.this.sendMessage(Lang.START_MESSAGE.getList());
                    NormalMatch.this.getEvent().startTask();
                    Bukkit.getScheduler().runTaskLater(BedWarsPlugin.getInstance(), () -> getGame().startLobbyRemovalTask(), 10L);
                    return;
                }

                if (NormalMatch.this.getStartingTime() < 0) {
                    this.cancel();
                    return;
                }

                NormalMatch.this.setStarting(true);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);

        this.setStartingTask(task);
    }

    @Override
    public void endGame(String cause) {

        MatchEnd matchEnd = new MatchEnd(this, cause);

        if (cause.contains("RESTART") || cause.contains("RELOAD")) {
            matchEnd.onReset();
            this.getDiamondGenerators().forEach(DiamondGenerator::removeGenerator);
            this.getEmeraldGenerators().forEach(EmeraldGenerator::removeGenerator);
            this.getPlayerTeam().values().forEach(Team::reset);
            this.getEliminatedTeams().forEach(Team::reset);
            return;
        }

        int seconds = BedWarsPlugin.getInstance().isBungeeCord() ? (20 * 6) : (20 * 3);

        this.setMatchState(MatchState.RESTARTING);

        this.getRespawnTask().values().forEach(RespawnTask::cancelTask);
        this.getPreventTrap().values().forEach(BukkitTask::cancel);
        this.getTasks().forEach(BukkitTask::cancel);
        matchEnd.sendMessageEnd();

        new BukkitRunnable() {

            @Override
            public void run() {

                matchEnd.onReset();
                NormalMatch.this.getGame().notifyObservers();

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), seconds);

        new BukkitRunnable() {

            @Override
            public void run() {
                ManagerHandler.getGameManager().getArenaReset().resetArena(getGame());
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), seconds + 20);

        this.getRejoinMap().clear();
        this.getEvent().reset();
        this.getEvent().cancelTask();
        this.getPlayerTeam().values().forEach(Team::reset);
        this.getEliminatedTeams().forEach(Team::reset);
        this.getInvisibility().values().forEach(inv -> {
            inv.undoInvisibility();
            inv.cancelTask();
        });

        this.getInvisibility().clear();
        this.setStarting(false);
        this.setStartingTime(this.getGame().getArenaStartingTime().get(0));
        this.getRespawnTask().clear();
        this.getPreventTrap().clear();
        this.getTasks().clear();
        Utils.doCommandBungeeCord(BedWarsPlugin.getInstance());

    }

}
