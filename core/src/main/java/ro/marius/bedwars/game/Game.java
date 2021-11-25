package ro.marius.bedwars.game;

import org.bukkit.Location;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.game.gameobserver.GameObserver;
import ro.marius.bedwars.game.gameobserver.SignObserver;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.game.mechanics.LobbyRemovalTask;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.shopconfiguration.ShopPath;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.utils.CuboidSelection;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public class Game {

    private String name;
    private String scoreboardPath;
    private String upgradePathName = "DEFAULT";
    private String shopPathName = "DEFAULT";
    private String arenaOptionName = "DEFAULT";
    private String arenaType;
    private AMatch match;
    private GameLocation spectateLocation;
    private GameLocation waitingLocation;
    private final CuboidSelection gameCuboid;
    private CuboidSelection waitingLobbySelection;
    private int playersPerTeam;
    private int minTeamsToStart;
    private int maxPlayers;
    private final Set<Location> gameSigns = new HashSet<>();
    private final List<Team> teams;
    private final List<GameLocation> diamondGenerator;
    private final List<GameLocation> emeraldGenerator;

    private final Map<Integer, Integer> arenaStartingTime = new HashMap<>();

    private final List<Location> diamondGeneratorLocation = new ArrayList<>();
    private final List<Location> emeraldGeneratorLocation = new ArrayList<>();
    private final List<GameObserver> gameObservers = new ArrayList<>();

    private UpgradePath upgradePath;
    private ShopPath shopPath;
    private ArenaOptions arenaOptions;

//	private Map<>

    // private boolean kitSelector; TODO
    // private boolean isAutoTeam; TODO

    public Game(String name, String scoreboardPath, String arenaType, GameLocation spectateLocation,
                GameLocation inWaiting, CuboidSelection gameCuboid, int playersPerTeam, int minTeams, int maxPlayers,
                List<Team> teams, List<GameLocation> diamondGenerator, List<GameLocation> emeraldGenerator) {
        this.name = name;
        this.scoreboardPath = scoreboardPath;
        this.arenaType = arenaType;
        this.spectateLocation = spectateLocation;
        this.waitingLocation = inWaiting;
        this.playersPerTeam = playersPerTeam;
        this.minTeamsToStart = minTeams;
        this.maxPlayers = maxPlayers;
        this.teams = teams;
        this.diamondGenerator = diamondGenerator;
        this.emeraldGenerator = emeraldGenerator;
        this.gameCuboid = gameCuboid;

        diamondGenerator.forEach(d -> this.diamondGeneratorLocation.add(d.getLocation()));
        emeraldGenerator.forEach(e -> this.emeraldGeneratorLocation.add(e.getLocation()));

        this.arenaOptions = new ArenaOptions(this.arenaOptionName, "default.yml");
        // 0 means DEFAULT starting time, 15 means starting time
        this.arenaStartingTime.put(0, 15);
        this.registerObserver(new SignObserver(this));
    }

    public void registerObserver(GameObserver gameObserver) {
        this.gameObservers.add(gameObserver);
    }

    public void removeObserver(GameObserver gameObserver) {
        this.gameObservers.remove(gameObserver);
    }

    public void notifyObservers() {
        this.gameObservers.forEach(GameObserver::update);
    }

    public String getMod() {
        return (this.getMatch().getMatchState() == MatchState.IN_WAITING)
                ? Utils.translate(this.getArenaOptions().getString("ArenaSign.InWaitingDisplay"))
                : Utils.translate(this.getArenaOptions().getString("ArenaSign.InGameDisplay"));

    }

    public boolean isNearAirGenerators(Location location) {

        if (!this.getArenaOptions().getBoolean("BlockPlaceProtection.AirGenerators.Enabled")) {
            return false;
        }

        Set<Location> locations = new HashSet<>();
        locations.addAll(this.diamondGeneratorLocation);
        locations.addAll(this.emeraldGeneratorLocation);
        double radius = this.arenaOptions.getDouble("BlockPlaceProtection.AirGenerators.ProtectionRadius");
        for (Location genLoc : locations) {
            if (genLoc.distance(location) <= radius) {
                return true;
            }
        }
        return false;
    }

    public void startLobbyRemovalTask() {

        if (waitingLobbySelection == null) return;

        LobbyRemovalTask lobbyRemovalTask = new LobbyRemovalTask(this);
        lobbyRemovalTask.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);
    }

    @Override
    public String toString() {
        return "Game [name=" + this.name + ", spectateLocation=" + this.spectateLocation + ", waitingLocation=" + this.waitingLocation
                + ", gameCuboid=" + this.gameCuboid + ", teams=" + Arrays.toString(this.teams.toArray())
                + ", diamondGeneratorLocation=" + this.diamondGeneratorLocation + ", emeraldGeneratorLocation="
                + this.emeraldGeneratorLocation + "]";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScoreboardPath() {
        return this.scoreboardPath;
    }

    public void setScoreboardPath(String scoreboardPath) {
        this.scoreboardPath = scoreboardPath;
    }

    public String getUpgradePathName() {
        return this.upgradePathName;
    }

    public void setUpgradePathName(String upgradePathName) {
        this.upgradePathName = upgradePathName;
    }

    public String getShopPathName() {
        return this.shopPathName;
    }

    public void setShopPathName(String shopPathName) {
        this.shopPathName = shopPathName;
    }

    public String getArenaOptionName() {
        return this.arenaOptionName;
    }

    public void setArenaOptionName(String arenaOptionName) {
        this.arenaOptionName = arenaOptionName;
    }

    public String getArenaType() {
        return this.arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }

    public GameLocation getSpectateLocation() {
        return this.spectateLocation;
    }

    public GameLocation getWaitingLocation() {
        return this.waitingLocation;
    }

    public CuboidSelection getGameCuboid() {
        return this.gameCuboid;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }

    public int getMinTeamsToStart() {
        return this.minTeamsToStart;
    }

    public void setMinTeamsToStart(int minTeamsToStart) {
        this.minTeamsToStart = minTeamsToStart;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    public Set<Location> getGameSigns() {
        return this.gameSigns;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public List<GameLocation> getDiamondGenerator() {
        return this.diamondGenerator;
    }

    public List<GameLocation> getEmeraldGenerator() {
        return this.emeraldGenerator;
    }

    public Map<Integer, Integer> getArenaStartingTime() {
        return this.arenaStartingTime;
    }

    public List<Location> getDiamondGeneratorLocation() {
        return this.diamondGeneratorLocation;
    }

    public List<Location> getEmeraldGeneratorLocation() {
        return this.emeraldGeneratorLocation;
    }

    public UpgradePath getUpgradePath() {
        return this.upgradePath;
    }

    public void setUpgradePath(UpgradePath upgradePath) {
        this.upgradePath = upgradePath;
    }

    public ShopPath getShopPath() {
        return this.shopPath;
    }

    public void setShopPath(ShopPath shopPath) {
        this.shopPath = shopPath;
    }

    public ArenaOptions getArenaOptions() {
        return this.arenaOptions;
    }

    public void setArenaOptions(ArenaOptions arenaOptions) {
        this.arenaOptions = arenaOptions;
    }

    public void setSpectateLocation(GameLocation spectateLocation) {
        this.spectateLocation = spectateLocation;
    }

    public void setWaitingLocation(GameLocation waitingLocation) {
        this.waitingLocation = waitingLocation;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<GameObserver> getGameObservers() {
        return gameObservers;
    }

    public void setWaitingLobbySelection(CuboidSelection waitingLobbySelection) {
        this.waitingLobbySelection = waitingLobbySelection;
    }

    public CuboidSelection getWaitingLobbySelection() {
        return waitingLobbySelection;
    }


}
