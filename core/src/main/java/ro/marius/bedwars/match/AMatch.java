package ro.marius.bedwars.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Items;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.mechanics.Event;
import ro.marius.bedwars.game.mechanics.PlayerDamageCause;
import ro.marius.bedwars.game.mechanics.PlayerInvisibility;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.ScoreboardManager;
import ro.marius.bedwars.scoreboard.ScoreboardAPI;
import ro.marius.bedwars.tasks.RespawnTask;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.TeamBed;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public abstract class AMatch {

    final String ALIVE = Utils.translate("&a✔");
    final String DEAD = Utils.translate("&c✗");
    private Game game;
    private Event event;
    private boolean isStarting, forceStart;
    private int startingTime;
    private BukkitTask startingTask;
    private MatchState matchState = MatchState.IN_WAITING;
    private Set<Player> players = new HashSet<>();
    private Set<Player> spectators = new HashSet<>();
    private final Map<UUID, Team> playerTeam = new HashMap<>();
    private final Map<Player, PlayerDamageCause> damageCause = new WeakHashMap<>();
    private final Map<Player, PlayerInvisibility> invisibility = new WeakHashMap<>();
    private final Map<Player, RespawnTask> respawnTask = new HashMap<>();
    private final Map<UUID, BukkitTask> preventTrap = new HashMap<>();
    private final List<BukkitTask> tasks = new ArrayList<>();
    private final List<Team> teams = new ArrayList<>();
    private final Set<Team> eliminatedTeams = new HashSet<>();
    private final Map<UUID, Team> rejoinMap = new HashMap<>();
    private final List<DiamondGenerator> diamondGenerators = new ArrayList<>();
    private final List<EmeraldGenerator> emeraldGenerators = new ArrayList<>();
    private final List<Entity> matchEntity = new ArrayList<>();
    private final List<Block> placedBlocks = new ArrayList<>();
    private final Map<Team, TeamBed> placedBeds = new HashMap<>();
    private final Map<String, MatchData> matchData = new HashMap<>();
    private final List<MatchSpectator> permanentSpectators = new ArrayList<>();

    public AMatch(Game game) {
        this.game = game;
        this.event = new Event(this);
        this.startingTime = game.getArenaStartingTime().get(0);
    }

    public abstract void addPlayer(Player player);

    public abstract void removePlayer(Player player);

    public abstract void addToSpectatorTask(Player player);

    public abstract void addToSpectator(Player player);

    public abstract void startGame();

    public abstract void endGame(String cause);

    public void spawnAirGenerators() {

        new BukkitRunnable() {

            @Override
            public void run() {

                for (DiamondGenerator gen : diamondGenerators) {
                    gen.spawn();
                    gen.start();
                }

                for (EmeraldGenerator gen : emeraldGenerators) {
                    gen.spawn();
                    gen.start();
                }

                AMatch.this.startRotatingHead();
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20);

    }

    public void startRotatingHead() {
        this.tasks.add(new GeneratorRotatingTask(this).runTaskTimer(BedWarsPlugin.getInstance(), 0, 3));
    }

    public void setupTeams() {

        UpgradePath upgradePath = this.game.getUpgradePath();

        if (upgradePath == null) {
            Bukkit.broadcastMessage("NULL UPGRADE PATH FOR ARENA " + this.getGame().getName());
            return;
        }

        int ironAmount = upgradePath.getIronAmount();
        int ironTime = upgradePath.getIronTime();

        int goldAmount = upgradePath.getGoldAmount();
        int goldTime = upgradePath.getGoldTime();

        for (Team team : new HashSet<>(this.playerTeam.values())) {
            team.getGoldGenerator().reloadLocation();
            team.getIronGenerator().reloadLocation();

            FloorGenerator ironGenerator = team.getIronFloorGenerator(this);
            ironGenerator.setAmount(ironAmount);
            ironGenerator.setTimeTask(ironTime);
            ironGenerator.setLocation(team.getIronGenerator());
            ironGenerator.start();

            FloorGenerator goldGenerator = team.getGoldFloorGenerator(this);
            goldGenerator.setAmount(goldAmount);
            goldGenerator.setTimeTask(goldTime);
            goldGenerator.setLocation(team.getGoldGenerator());
            goldGenerator.start();

            team.setupPlayers();

            TeamBed teamBed = new TeamBed(team);
            teamBed.placeBed();
            placedBeds.put(team, teamBed);
        }

    }

    public void spawnNPC() {

        List<String> shopHologram = this.game.getArenaOptions().getStringList("ShopHologram.Lines");
        List<String> upgradeHologram = this.game.getArenaOptions().getStringList("UpgradeHologram.Lines");

        int spawnDelay = this.game.getArenaOptions().getPathInt("NPCsSpawnDelay");
        boolean emptyTeams = this.game.getArenaOptions().getPathBoolean("SpawnNPCsOnEmptyTeams");
        spawnDelay = (spawnDelay <= 0) ? 25 : spawnDelay;

        Collection<? extends Team> list = emptyTeams ? this.getTeams() : this.playerTeam.values();

        Set<Team> teams = new HashSet<>(list);

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Team team : teams) {

                    AMatch.this.matchEntity.addAll(team.spawnNPC());
                    String teamName = team.getName();
                    String teamChatColor = team.getTeamColor().getChatColor();
                    Location shopLocation = team.getShopLocation().getLocation().clone().add(0,
                            1.85 + (shopHologram.size() * 0.12), 0);
                    spawnNPCHologram(teamName, teamChatColor, shopLocation, shopHologram);
                    Location upgLocation = team.getUpgradeLocation().getLocation().clone().add(0,
                            1.85 + (upgradeHologram.size() * 0.12), 0);
                    spawnNPCHologram(teamName, teamChatColor, upgLocation, upgradeHologram);
                }

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), spawnDelay);

    }

    private void spawnNPCHologram(String teamName, String teamChatColor, Location location, List<String> textList) {

        for (String value : textList) {
            String s = Utils.translate(value).replace("<teamColor>", teamChatColor).replace("<teamName>",
                    teamName);
            ArmorStand stand = Utils.getSpawnedArmorStand(location, s);
            location.subtract(0, 0.25, 0);
            AMatch.this.matchEntity.add(stand);
        }

    }

    public void setupPlayers() {

        for (Player p : this.players) {
            p.getEnderChest().clear();
            this.matchData.put(p.getName(), new MatchData());
            Utils.resetPlayer(p, true, true);
            ManagerHandler.getScoreboardManager().setScoreboardGame(p, false);
        }


    }

    public Team getTeamAlive(String teamName) {

        for (Team team : this.playerTeam.values()) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        return null;
    }

    public Team getTeam(String teamName) {

        for (Team team : this.getTeams()) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }

        return null;
    }


    public void findTeamPlayers(List<Player> players, int playersPerTeam) {


        if (players.isEmpty())
            return;

        if (playersPerTeam <= 0)
            return;

        Team team = findTeamByEmptySize(playersPerTeam);

        if (team == null) {
            findTeamPlayers(players, --playersPerTeam);
            return;
        }

        int remainingTeamPlayers = playersPerTeam - team.getPlayers().size();
        List<Player> subList = players.subList(0, Math.min(remainingTeamPlayers, players.size()));
        team.getPlayers().addAll(subList);
        subList.forEach(partyPlayer -> getPlayerTeam().put(partyPlayer.getUniqueId(), team));
        subList.clear();
        isRequiredStarting();
        findTeamPlayers(players, playersPerTeam);
    }

    public int getRemainingPlayers() {
        return game.getMaxPlayers() - getPlayers().size();
    }

    public void isRequiredStarting() {

        boolean isRequired = new HashSet<>(this.getPlayerTeam().values()).size() == this.getGame().getMinTeamsToStart();

        if (isRequired && !this.isStarting()) {
            this.startGame();
        }

    }

    public void isRequiredEnding() {

        if (this.getMatchState() != MatchState.IN_GAME) {
            return;
        }

        Set<Team> teams = new HashSet<>(this.getPlayerTeam().values());

        if (teams.size() <= 1) {
            this.endGame("NORMAL");
            return;
        }

        if (this.getPlayers().isEmpty()) {
            this.endGame("NO-PLAYERS");
        }

        // daca este mai mult de 1 echipa
        // si daca sunt jucatori in arena


        // daca this.getPlayers().size() <= this.getGame().getPlayersPerTeam()
        // se poate ajunge cand exista doua echipe cu cate un jucator si se incheie meciul


//        if ((this.getPlayers().size() <= this.getGame().getPlayersPerTeam()) && (this.getRejoinMap().size() <= 1)) {
//
//            Iterator<Entry<UUID, Team>> it = this.getRejoinMap().entrySet().iterator();
//
//            if (it.hasNext()) {
//                Entry<UUID, Team> entry = it.next();
//                this.getPlayerTeam().remove(entry.getKey());
//                this.getEliminatedTeams().add(entry.getValue());
//            }
//
//            this.endGame("NORMAL");
//        }

    }

    public void setCancelledTask() {

        if ((new HashSet<>(this.getPlayerTeam().values()).size() < this.game.getMinTeamsToStart())
                && (!this.isForceStart() || this.getPlayers().isEmpty()) && this.isStarting()
                && (this.getMatchState() == MatchState.IN_WAITING)) {
            this.cancelTask(this.getStartingTask());
            this.setStartingTime(this.getGame().getArenaStartingTime().get(0));
            this.sendMessage(Lang.START_CANCELLED.getString());
            this.setStarting(false);
            this.setForceStart(false);
        }
    }

    public void setStartingTime() {

        int players = this.getPlayers().size();
        Integer time = this.getGame().getArenaStartingTime().get(players);

        if (time == null) {
            return;
        }
        if (((time > this.getStartingTime()) && this.isStarting)) {
            return;
        }

        this.setStartingTime(time);
    }

    public void checkEmptyTeam(Team team) {

        if (!team.getPlayers().isEmpty()) {
            return;
        }

        this.sendMessage(Lang.TEAM_ELIMINATED.getString().replace("<teamColor>", team.getColorName()).replace("<team>",
                team.getName()));
        this.destroyBed(team);
        this.getPlacedBeds().remove(team);
        this.getEliminatedTeams().add(team);
        this.getPlayerTeam().values().removeAll(Collections.singletonList(team));
        this.isRequiredEnding();

    }

    public Team findTeamByEmptySize(int emptySize) {

        for (Team team : this.teams) {

            if (team.getPlayers().size() + emptySize > getGame().getPlayersPerTeam()) {
                continue;
            }

            return team;
        }

        return null;

    }

    public Team findAvailableTeam() {

        return findTeamByEmptySize(1);
    }

    public Team findEmptyTeam() {

        for (Team team : this.teams) {

            if (!team.getPlayers().isEmpty()) {
                continue;
            }

            return team;
        }

        return null;
    }

    public void removeFromTeam(Player p) {
        Team team = this.playerTeam.get(p.getUniqueId());

        if (team == null) {
            return;
        }

        team.getPlayers().remove(p);
        this.playerTeam.remove(p.getUniqueId());

        if (this.getMatchState() == MatchState.IN_WAITING) {
            return;
        }
        if (!team.getPlayers().isEmpty()) {
            return;
        }
        if (this.getRejoinMap().containsValue(team)) {
            return;
        }

        this.sendMessage(Lang.TEAM_ELIMINATED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                .replace("<team>", team.getName()));
        this.destroyBed(team);
        this.getPlacedBeds().remove(team);
        this.getEliminatedTeams().add(team);
    }

    public void destroyBed(Team team) {
        TeamBed teamBed = placedBeds.get(team);

        if (teamBed == null)
            return;

        teamBed.destroyBed();
    }

    public void sendMessage(String message) {
        String finalMessage = Utils.translate(message);

        Set<Player> playersToSend = new HashSet<>();
        playersToSend.addAll(this.getPlayers());
        playersToSend.addAll(this.getSpectators());

        playersToSend.forEach(p -> p.sendMessage(finalMessage));
    }

    public void sendMessage(List<String> list) {

        for (Player player : this.getPlayers()) {
            for (String string : list) {

                if (string.contains("<center>")) {
                    Utils.sendCenteredMessage(player, string.replace("<center>", ""));
                    continue;
                }

                player.sendMessage(Utils.translate(string));
            }
        }

    }

    public void addItemsWaiting(Player p) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if (Items.GAME_LEAVE.isEnabled()) {
                    p.getInventory().setItem(Items.GAME_LEAVE.getSlot(), Items.GAME_LEAVE.toItemStack());
                }

                if (Items.TEAM_SELECTOR.isEnabled()) {
                    p.getInventory().setItem(Items.TEAM_SELECTOR.getSlot(), Items.TEAM_SELECTOR.toItemStack());
                }


            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20);

    }

    public void doMethodForBedBroken(Player p) {
        Team team = this.getPlayerTeam().get(p.getUniqueId());

        if (!team.isBedBroken()) {
            this.addToSpectatorTask(p);
            return;
        }

        ScoreboardManager scManager = ManagerHandler.getScoreboardManager();

        for (Player player : this.getPlayers()) {
            ScoreboardAPI sc = scManager.scoreboard.get(player.getUniqueId());

            if (sc == null) {
                continue;
            }

            sc.removeFromTeam(p, team);
        }

        team.getPlayers().remove(p);
        this.getPlayerTeam().remove(p.getUniqueId());
        this.addToSpectator(p);

        if (!team.getPlayers().isEmpty()) {
            return;
        }

        this.sendMessage(Lang.TEAM_ELIMINATED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                .replace("<team>", team.getName()));
        this.getPlayerTeam().values().removeAll(Collections.singletonList(team));
        this.getEliminatedTeams().add(team);
        this.isRequiredEnding();

    }

    public String isAliveTeam(Team team) {

        return this.playerTeam.containsValue(team) ? (team.isBedBroken() ? (team.getPlayers().size() + "") : ALIVE) : DEAD;
    }

    public MatchData getMatchData(Player p) {

        MatchData matchData = this.matchData.get(p.getName());

        if (matchData == null) {
            matchData = new MatchData();
            this.matchData.put(p.getName(), matchData);
        }

        return matchData;

    }

    private void cancelTask(BukkitTask task) {

        if (task == null) {
            return;
        }

        task.cancel();
    }

    public void sendCenterMessage(String message) {
        this.getPlayers().forEach(p -> Utils.sendCenteredMessage(p, message));
        this.getSpectators().forEach(p -> Utils.sendCenteredMessage(p, message));
    }

    public boolean isDenyPlacingBlock(Location blockLocation) {

        ArenaOptions arenaOptions = this.getGame().getArenaOptions();

        double spawnRadius = arenaOptions.getDouble("BlockPlaceProtection.SpawnLocation.ProtectionRadius");
        boolean spawnProtection = arenaOptions.getBoolean("BlockPlaceProtection.SpawnLocation.Enabled");

        double upgradeRadius = arenaOptions.getDouble("BlockPlaceProtection.UpgradeLocation.ProtectionRadius");
        boolean upgradeProtection = arenaOptions.getBoolean("BlockPlaceProtection.UpgradeLocation.Enabled");

        double shopRadius = arenaOptions.getDouble("BlockPlaceProtection.UpgradeLocation.ProtectionRadius");
        boolean shopProtection = arenaOptions.getBoolean("BlockPlaceProtection.ShopLocation.Enabled");

        if (!(spawnProtection || upgradeProtection || shopProtection)) {
            return false;
        }

        Set<Team> allTeams = new HashSet<>();
        allTeams.addAll(this.getPlayerTeam().values());
        allTeams.addAll(this.getEliminatedTeams());

        for (Team team : allTeams) {

            Location spawnLocation = team.getSpawnLocation().getLocation();
            Location upgradeLocation = team.getUpgradeLocation().getLocation();
            Location shopLocation = team.getShopLocation().getLocation();

            if (spawnProtection && (blockLocation.distance(spawnLocation) <= spawnRadius)) {
                return true;
            }

            if (upgradeProtection && (blockLocation.distance(upgradeLocation) <= upgradeRadius)) {
                return true;
            }

            if (shopProtection && (blockLocation.distance(shopLocation) <= shopRadius)) {
                return true;
            }

        }

        return false;
    }

    public void addToPreventMap(UUID uuid) {

        BukkitTask task = this.preventTrap.get(uuid);

        if (task != null) {
            task.cancel();
        } else {
            task = new BukkitRunnable() {

                @Override
                public void run() {

                    AMatch.this.preventTrap.remove(uuid);

                }
            }.runTaskLater(BedWarsPlugin.getInstance(), 20 * 60);

            this.preventTrap.put(uuid, task);
        }

    }

    public void removeRespawnTask(Player p) {

        RespawnTask respawnTask = this.respawnTask.get(p);

        if (respawnTask == null) {
            return;
        }

        respawnTask.cancelTask();
    }

    public boolean isFull() {
        return this.getPlayers().size() >= this.getGame().getMaxPlayers();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isStarting() {
        return this.isStarting;
    }

    public void setStarting(boolean isStarting) {
        this.isStarting = isStarting;
    }

    public boolean isForceStart() {
        return this.forceStart;
    }

    public void setForceStart(boolean forceStart) {
        this.forceStart = forceStart;
    }

    public int getStartingTime() {
        return this.startingTime;
    }

    public void setStartingTime(int startingTime) {
        this.startingTime = startingTime;
    }

    public BukkitTask getStartingTask() {
        return this.startingTask;
    }

    public void setStartingTask(BukkitTask startingTask) {
        this.startingTask = startingTask;
    }

    public MatchState getMatchState() {
        return this.matchState;
    }

    public void setMatchState(MatchState matchState) {
        this.matchState = matchState;
        this.getGame().notifyObservers();
    }

    public Map<UUID, Team> getPlayerTeam() {
        return this.playerTeam;
    }

    public Map<Player, PlayerDamageCause> getDamageCause() {
        return this.damageCause;
    }

    public Map<Player, PlayerInvisibility> getInvisibility() {
        return this.invisibility;
    }

    public Map<Player, RespawnTask> getRespawnTask() {
        return this.respawnTask;
    }

    public Map<UUID, BukkitTask> getPreventTrap() {
        return this.preventTrap;
    }

    public List<BukkitTask> getTasks() {
        return this.tasks;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public Set<Team> getEliminatedTeams() {
        return this.eliminatedTeams;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public Set<Player> getSpectators() {
        return this.spectators;
    }

    public void setSpectators(Set<Player> spectators) {
        this.spectators = spectators;
    }

    public Map<UUID, Team> getRejoinMap() {
        return this.rejoinMap;
    }

    public List<DiamondGenerator> getDiamondGenerators() {
        return this.diamondGenerators;
    }

    public List<EmeraldGenerator> getEmeraldGenerators() {
        return this.emeraldGenerators;
    }

    public List<Entity> getMatchEntity() {
        return this.matchEntity;
    }

    public List<Block> getPlacedBlocks() {
        return this.placedBlocks;
    }

    public Map<String, MatchData> getMatchData() {
        return this.matchData;
    }

    public Map<Team, TeamBed> getPlacedBeds() {
        return placedBeds;
    }

    public List<MatchSpectator> getPermanentSpectators() {
        return this.permanentSpectators;
    }

}
