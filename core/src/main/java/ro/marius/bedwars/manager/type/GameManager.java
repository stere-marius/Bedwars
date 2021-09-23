package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.game.GameSetup;
import ro.marius.bedwars.game.arenareset.ArenaReset;
import ro.marius.bedwars.game.arenareset.WorldReset;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.match.NormalMatch;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.FileData;
import ro.marius.bedwars.playerdata.SQLData;
import ro.marius.bedwars.shopconfiguration.ShopPath;
import ro.marius.bedwars.shopconfiguration.shopinventory.ConfigurationHelper;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.upgradeconfiguration.upgradeinventory.UpgradeConfiguration;
import ro.marius.bedwars.utils.CuboidSelection;
import ro.marius.bedwars.utils.InventoryRestore;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.WorldCallback;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class GameManager {

    public final File gameFile = new File(BedWarsPlugin.getInstance().getDataFolder(), "arenas.yml");
    private final List<Game> games = new ArrayList<>();
    private final Set<String> arenaType = new HashSet<>();
    private final Map<UUID, AMatch> playerMatch = new HashMap<>();
    private final Map<Player, GameSetup> gameSetup = new HashMap<>();
    private final Map<UUID, GameEdit> gameEdit = new HashMap<>();
    private final Map<UUID, APlayerData> playerData = new HashMap<>();
    private final Map<String, UpgradePath> upgradePath = new HashMap<>();
    private final Map<String, ShopPath> shopPath = new HashMap<>();
    private final UpgradeConfiguration upgradeConfiguration = new UpgradeConfiguration();
    private final ConfigurationHelper shopConfiguration = new ConfigurationHelper();
    private final Map<String, ArenaOptions> arenaOptions = new HashMap<>();
    private final Map<UUID, InventoryRestore> playerContents = new HashMap<>();
    public FileConfiguration game = YamlConfiguration.loadConfiguration(this.gameFile);
    private final Set<String> gameNames = new HashSet<>();

    private ArenaReset arenaReset = new WorldReset();

    public GameManager() {
        this.loadArenaOptions();
        // loadUpgrades();
        // loadShop();
        // loadTeams();
    }

    public AMatch getFreeMatch() {

        for (Game game : this.getGames()) {

            if (game.getMatch().getPlayers().size() >= 2) {
                continue;
            }

            return game.getMatch();
        }

        return null;
    }

    public AMatch getMatchByArenaType(String arenaType/* , List<Player> players */) {

        List<Game> list = new ArrayList<>(this.getGames());
        list.sort((o1, o2) -> {

            if (o1.getMatch().getPlayers().size() > o2.getMatch().getPlayers().size()) {
                return 1;
            }

            return 0;
        });

        for (Game game : list) {

            AMatch match = game.getMatch();

            if (!game.getArenaType().equals(arenaType)) {
                continue;
            }
            if (match.isFull()) {
                continue;
            }
            if (match.getMatchState() != MatchState.IN_WAITING) {
                continue;
            }
            if (match.findAvailableTeam() == null) {
                continue;
            }

            return match;

        }

        return null;

    }

    public AMatch getEmptyMatch() {

        List<Game> list = new ArrayList<>(this.getGames());
        list.sort((o1, o2) -> {

            if (o1.getMatch().getPlayers().size() > o2.getMatch().getPlayers().size()) {
                return 1;
            }

            return 0;
        });

        for (Game game : list) {

            AMatch match = game.getMatch();

            if (match.isFull()) {
                continue;
            }
            if (match.getMatchState() != MatchState.IN_WAITING) {
                continue;
            }
            if (match.findAvailableTeam() == null) {
                continue;
            }

            return match;

        }

        return null;

    }

    public Game getGame(String name) {

        for (Game game : this.games) {

            if (!game.getName().equals(name)) {
                continue;
            }

            return game;
        }

        return null;
    }

    public boolean containsGameName(String name) {

        for (Game game : this.games) {

            if (!game.getName().equals(name)) {
                continue;
            }

            return true;
        }

        return false;

    }

    public AMatch getRejoin(Player p) {

        for (Game game : this.games) {

            if (game.getMatch().getRejoinMap().containsKey(p.getUniqueId())) {
                return game.getMatch();
            }

        }

        return null;

    }

    public void loadUpgrade(Game game) {

        if (this.upgradePath.containsKey(game.getUpgradePathName())) {
            game.setUpgradePath(this.upgradePath.get(game.getUpgradePathName()));
            return;
        }

        try {
            this.upgradeConfiguration.loadUpgrades(game);
            this.upgradePath.put(game.getUpgradePathName(), game.getUpgradePath());
        } catch (Exception e) {
            this.upgradeConfiguration.loadDefaultConfiguration(game);
            this.upgradePath.put("DEFAULT", game.getUpgradePath());
            e.printStackTrace();
        }

    }

    public void loadShop(Game game) {

        if (this.shopPath.containsKey(game.getShopPathName())) {
            game.setShopPath(this.shopPath.get(game.getShopPathName()));
            return;
        }

        try {

            this.shopConfiguration.loadShopConfiguration(game);

            this.shopPath.put(game.getShopPathName(), game.getShopPath());
        } catch (Exception e) {
            this.shopConfiguration.loadDefaultConfiguration(game);
            this.shopPath.put("DEFAULT", game.getShopPath());
            e.printStackTrace();
        }

    }

    public void loadShop() {

        ConfigurationHelper shopConfig = new ConfigurationHelper();

        for (Game game : this.games) {
            shopConfig.loadShopConfiguration(game);
            this.shopPath.put(game.getShopPathName(), game.getShopPath());
        }
    }

    public void loadArenaOptions() {

        new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings").mkdirs();

        this.arenaOptions.putAll(ArenaOptions.loadConfiguration());

    }

    public void loadSign(Game game) {

        List<String> list = this.game.getStringList("Signs." + game.getName());
        if (list.isEmpty()) {
            return;
        }

        for (String s : list) {
            game.getGameSigns().add(Utils.convertingLocation(s));
        }
    }

    public void loadArenaTypes() {

        if (!this.gameFile.exists()) {
            return;
        }

        ConfigurationSection section = this.game.getConfigurationSection("Games");
        if (section == null) {
            return;
        }

        for (String name : section.getKeys(false)) {
            String type = this.game.getString("Games." + name + ".ArenaType");
            type = (type == null) ? "DEFAULT" : this.game.getString("Games." + name + ".ArenaType");
            this.arenaType.add(type);
        }
    }

    public void loadGames(Runnable callbackLoadGames) {

        StringBuilder builder = new StringBuilder(ChatColor.GREEN + "Loaded games: ");

        if (!this.gameFile.exists()) {
            return;
        }

        Set<String> configGameSet = this.game.getConfigurationSection("Games").getKeys(false);

        for (String name : configGameSet) {

            this.gameNames.add(name);

            ManagerHandler.getWorldManager().loadWorld(name, new WorldCallback() {

                @Override
                public void onComplete(World result, String[] mesage) {

                    List<GameLocation> diamondGenerators = GameLocation
                            .getConvertedLocations(GameManager.this.game.getStringList("Games." + name + ".DiamondGenerators"));
                    List<GameLocation> emeraldGenerators = GameLocation
                            .getConvertedLocations(GameManager.this.game.getStringList("Games." + name + ".EmeraldGenerators"));
                    String type = GameManager.this.game.getString("Games." + name + ".ArenaType");
                    type = (type == null) ? "DEFAULT" : GameManager.this.game.getString("Games." + name + ".ArenaType");
                    GameLocation spectate = GameLocation
                            .convertGameLocation(GameManager.this.game.getString("Games." + name + ".Spectate"));
                    GameLocation waiting = GameLocation.convertGameLocation(GameManager.this.game.getString("Games." + name + ".Waiting"));
                    int playersPerTeam = GameManager.this.game.getInt("Games." + name + ".PerTeamPlayers");

                    GameLocation positionOne = GameLocation
                            .convertGameLocation(GameManager.this.game.getString("Games." + name + ".PositionOne"));
                    GameLocation positionTwo = GameLocation
                            .convertGameLocation(GameManager.this.game.getString("Games." + name + ".PositionTwo"));
//										Object timeStart = game.get("Games." + name + ".StartingTime");

//										String lobbyOne = game.getString("Games." + name + ".LobbyPositionOne");
//										String lobbyTwo = game.getString("Games." + name + ".LobbyPositionTwo");
                    String scPath = GameManager.this.game.getString("Games." + name + ".ScoreboardPath");
                    String upgPath = GameManager.this.game.getString("Games." + name + ".UpgradePath");
                    String optionsPath = GameManager.this.game.getString("Games." + name + ".ArenaOptionsPath");
                    String shPath = GameManager.this.game.getString("Games." + name + ".ShopPath");

                    if (upgPath == null) {
                        upgPath = "DEFAULT";
                    }

                    if (shPath == null) {
                        shPath = "DEFAULT";
                    }

                    if (optionsPath == null) {
                        optionsPath = "DEFAULT";
                    }

                    if (scPath == null) {
                        scPath = "DEFAULT";
                    }

                    int minPlayers = GameManager.this.game.getInt("Games." + name + ".MinimumTeams");
                    int maxPlayers = GameManager.this.game.getInt("Games." + name + ".MaxPlayers");

                    List<Team> teams = new ArrayList<>();
                    List<String> letters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

                    int i = 0;

                    for (String teamName : GameManager.this.game.getConfigurationSection("Games." + name + ".Team").getKeys(false)) {
                        String color = GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".Color");
                        String face = GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".BedFace");

                        GameLocation bedLocation = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".BedLocation"));
                        GameLocation spawnLocation = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".SpawnLocation"));
                        GameLocation ironGenerator = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".IronGeneratorLocation"));
                        GameLocation goldGenerator = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".GoldGeneratorLocation"));
                        GameLocation emeraldGenerator = GameLocation.convertGameLocation(GameManager.this.game
                                .getString("Games." + name + ".Team." + teamName + ".EmeraldGeneratorLocation"));
                        GameLocation shopLocation = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".ShopLocation"));
                        GameLocation upgradeLocation = GameLocation.convertGameLocation(
                                GameManager.this.game.getString("Games." + name + ".Team." + teamName + ".UpgradeLocation"));

//											config.set(path + ".Team." + name + ".BedFace", team.getBedFace().name());

                        teams.add(new Team(teamName, letters.get(i), color,
                                (Utils.getBlockFace(face) == null) ? BlockFace.EAST : BlockFace.valueOf(face),
                                bedLocation, spawnLocation, goldGenerator, ironGenerator, emeraldGenerator,
                                shopLocation, upgradeLocation));

                        i++;

                        if (i > (letters.size() - 1)) {
                            i = 0;
                        }
                    }

                    CuboidSelection gameCuboid = new CuboidSelection(positionOne.getLocation(), positionTwo.getLocation());
                    gameCuboid.assignValues();

                    Game game = new Game(name, scPath, type, spectate, waiting, gameCuboid, playersPerTeam,
                            minPlayers, maxPlayers, teams, diamondGenerators, emeraldGenerators);
                    game.setUpgradePathName(upgPath);
                    game.setShopPathName(shPath);
                    game.setArenaOptionName(optionsPath);
                    game.setScoreboardPath(scPath);
                    game.setUpgradePath(GameManager.this.upgradePath.get(upgPath));
                    game.setShopPath(GameManager.this.shopPath.get(shPath));
                    game.setArenaOptions(GameManager.this.arenaOptions.get(optionsPath));
                    GameManager.this.loadStartingTime(game);

                    NormalMatch match = new NormalMatch(game);
                    loadMatchGenerators(match);
                    match.getTeams().addAll(teams);

                    game.setMatch(match);

                    ManagerHandler.getScoreboardManager().createPathScoreboard(game);
                    builder.append(game.getName()).append(" ");

                    if (BedWarsPlugin.getInstance().isFAWE() && !FAWEManager.loadSchematic(game)) {

                        Bukkit.getConsoleSender()
                                .sendMessage(Utils.translate("&c[Bedwars] Could not load the schematic for arena "
                                        + name
                                        + " . The schematic file might not exist. &aTrying to create it... "));

                        if (!FAWEManager.saveSchematic(result, positionOne.getLocation(), positionTwo.getLocation(),
                                name)) {
                            Bukkit.getConsoleSender().sendMessage(
                                    Utils.translate("&c[Bedwars] Could not save the schematic for arena " + name));
                        } else {
                            Bukkit.getConsoleSender().sendMessage(
                                    Utils.translate("&c[Bedwars] The schematic has been saved for arena " + name));
                        }

                    }

                    GameManager.this.games.add(game);

                    GameManager.this.loadShop(game);
                    GameManager.this.loadUpgrade(game);
                    GameManager.this.loadSign(game);

                    if (game.getShopPath() == null) {
                        GameManager.this.shopConfiguration.loadDefaultConfiguration(game);
                        Bukkit.getConsoleSender()
                                .sendMessage(Utils.translate("&c[BEDWARS] Couldn't load the shop path " + upgPath
                                        + " for game " + name + " .Setting it as being the default one."));
                    }

                    if (game.getUpgradePath() == null) {
                        GameManager.this.upgradeConfiguration.loadDefaultConfiguration(game);
                        Bukkit.getConsoleSender()
                                .sendMessage(Utils.translate("&c[BEDWARS] Couldn't load the upgrade path " + upgPath
                                        + " for game " + name + " .Setting it as being the default one."));
                    }

                    if (games.size() == configGameSet.size()) {
                        callbackLoadGames.run();
                        Bukkit.getConsoleSender().sendMessage(Utils.translate(builder.toString()));
                    }

                    for (Team t : game.getMatch().getTeams()) {
                        t.setIronFloorGenerator(new FloorGenerator(match, FloorGeneratorType.IRON,
                                game.getUpgradePath().getIronAmount(), game.getUpgradePath().getIronTime(),
                                t.getIronGenerator()));
                        t.setGoldFloorGenerator(new FloorGenerator(match, FloorGeneratorType.GOLD,
                                game.getUpgradePath().getGoldAmount(), game.getUpgradePath().getGoldTime(),
                                t.getGoldGenerator()));
                        Map<String, TeamUpgrade> upgradeMap = game.getUpgradePath().getUpgrades();
                        t.getUpgrades().putAll(upgradeMap);
                        t.getGameUpgrades().putAll(upgradeMap);
                        t.getShopUpgrades().putAll(game.getShopPath().getPlayerUpgrade());
                    }

                    game.notifyObservers();
                }

                @Override
                public void onError(String[] message) {
                    Bukkit.getConsoleSender().sendMessage(message);
                    GameManager.this.gameNames.remove(name);

                }
            });
        }

    }

    public void cloneGame(Game clonedGame, String newGameGame, CommandSender player) {

        ManagerHandler.getWorldManager().cloneWorld(clonedGame.getName(), newGameGame, new WorldCallback() {

            @Override
            public void onComplete(World result, String[] message) {
                List<Team> teams = new ArrayList<>();

                List<String> letters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

                String upgradePath = clonedGame.getUpgradePathName();
                String shopPath = clonedGame.getShopPathName();

                for (int i = 0; i < clonedGame.getTeams().size(); i++) {

                    Team teamName = clonedGame.getTeams().get(i);

                    GameLocation bed = teamName.getBedLocation().clone();
                    bed.getLocation().setWorld(result);
                    bed.setWorldName(newGameGame);
                    GameLocation spawn = teamName.getSpawnLocation().clone();
                    spawn.getLocation().setWorld(result);
                    spawn.setWorldName(newGameGame);
                    GameLocation ironGenerator = teamName.getIronGenerator().clone();
                    ironGenerator.getLocation().setWorld(result);
                    ironGenerator.setWorldName(newGameGame);
                    GameLocation goldGenerator = teamName.getGoldGenerator().clone();
                    goldGenerator.getLocation().setWorld(result);
                    goldGenerator.setWorldName(newGameGame);
                    GameLocation emeraldGenerator = teamName.getEmeraldGenerator().clone();
                    emeraldGenerator.getLocation().setWorld(result);
                    emeraldGenerator.setWorldName(newGameGame);
                    GameLocation shopLocation = teamName.getShopLocation().clone();
                    shopLocation.getLocation().setWorld(result);
                    shopLocation.setWorldName(newGameGame);
                    GameLocation upgradeLocation = teamName.getUpgradeLocation().clone();
                    upgradeLocation.getLocation().setWorld(result);
                    upgradeLocation.setWorldName(newGameGame);

                    Team team = new Team(teamName.getName(), (i > (letters.size() - 1)) ? letters.get(0) : letters.get(i),
                            teamName.getTeamColor().name(), teamName.getBedFace(), bed, spawn, goldGenerator,
                            ironGenerator, emeraldGenerator, shopLocation, upgradeLocation);

                    teams.add(team);

                }

                CuboidSelection selection = clonedGame.getGameCuboid().clone();
                selection.getPositionOne().setWorld(result);
                selection.getPositionTwo().setWorld(result);
                selection.assignValues();

                GameLocation spectateLocation = clonedGame.getSpectateLocation().clone();
                spectateLocation.getLocation().setWorld(result);
                spectateLocation.setWorldName(newGameGame);
                GameLocation waitingLocation = clonedGame.getWaitingLocation().clone();
                waitingLocation.getLocation().setWorld(result);
                waitingLocation.setWorldName(newGameGame);

                List<GameLocation> diamondGenerator = new ArrayList<>();
                List<GameLocation> emeraldGenerator = new ArrayList<>();

                for (GameLocation gameLocation : clonedGame.getDiamondGenerator()) {
                    GameLocation clonedGeneratorLocation = gameLocation.clone();
                    clonedGeneratorLocation.setWorld(result);
                    clonedGeneratorLocation.setWorldName(result.getName());
                    diamondGenerator.add(clonedGeneratorLocation);
                }

                for (GameLocation gameLocation : clonedGame.getEmeraldGenerator()) {
                    GameLocation clonedGeneratorLocation = gameLocation.clone();
                    clonedGeneratorLocation.setWorld(result);
                    clonedGeneratorLocation.setWorldName(result.getName());
                    emeraldGenerator.add(clonedGeneratorLocation);
                }

                Game game = new Game(newGameGame, clonedGame.getScoreboardPath(), clonedGame.getArenaType(), spectateLocation,
                        waitingLocation, selection, clonedGame.getPlayersPerTeam(), clonedGame.getMinTeamsToStart(), clonedGame.getMaxPlayers(), teams,
                        diamondGenerator, emeraldGenerator);

                game.setShopPathName(shopPath);
                game.setUpgradePathName(upgradePath);

                GameManager.this.loadUpgrade(game);
                GameManager.this.loadShop(game);

                teams.forEach(t -> t.setUpgrades(game.getUpgradePath().getUpgrades()));

                String path = "Games." + newGameGame + ".";
                FileConfiguration config = ManagerHandler.getGameManager().game;

                config.set("Games." + newGameGame, null);
                config.set(path + ".ArenaType", clonedGame.getArenaType());
                config.set(path + ".UpgradePath", upgradePath);
                config.set(path + ".ShopPath", shopPath);
                config.set(path + ".ScoreboardPath", game.getScoreboardPath());
                config.set(path + ".OptionsPath", "DEFAULT");
                config.set(path + ".DiamondGenerators",
                        Utils.setConvertingLocations(game.getDiamondGeneratorLocation()));
                config.set(path + ".EmeraldGenerators",
                        Utils.setConvertingLocations(game.getEmeraldGeneratorLocation()));
                config.set(path + ".Spectate", Utils.convertingString(game.getSpectateLocation().getLocation()));
                config.set(path + ".PositionOne", Utils.convertingString(game.getGameCuboid().getPositionOne()));
                config.set(path + ".PositionTwo", Utils.convertingString(game.getGameCuboid().getPositionTwo()));
                config.set(path + ".MinimumTeams", game.getMinTeamsToStart());
                config.set(path + ".Waiting", Utils.convertingString(game.getWaitingLocation().getLocation()));
                config.set(path + ".MaxPlayers", game.getMaxPlayers());
                config.set(path + ".PerTeamPlayers", game.getPlayersPerTeam());

                for (Team team : teams) {
                    String name = team.getName();
                    config.set(path + ".Team." + name + ".Color", team.getTeamColor().name());
                    config.set(path + ".Team." + name + ".BedFace", team.getBedFace().name());
                    config.set(path + ".Team." + name + ".BedLocation",
                            Utils.convertingString(team.getBedLocation().getLocation()));
                    config.set(path + ".Team." + name + ".SpawnLocation",
                            Utils.convertingString(team.getSpawnLocation().getLocation()));
                    config.set(path + ".Team." + name + ".ShopLocation",
                            Utils.convertingString(team.getShopLocation().getLocation()));
                    config.set(path + ".Team." + name + ".UpgradeLocation",
                            Utils.convertingString(team.getUpgradeLocation().getLocation()));
                    config.set(path + ".Team." + name + ".IronGeneratorLocation",
                            Utils.convertingString(team.getIronGenerator().getLocation()));
                    config.set(path + ".Team." + name + ".GoldGeneratorLocation",
                            Utils.convertingString(team.getGoldGenerator().getLocation()));
                    config.set(path + ".Team." + name + ".EmeraldGeneratorLocation",
                            Utils.convertingString(team.getEmeraldGenerator().getLocation()));
                }

                player.sendMessage(Utils.translate("&a⇨ Arena " + game.getName() + " has been created."));
                ManagerHandler.getGameManager().getGames().add(game);
                ManagerHandler.getGameManager().saveGameFile();

                NormalMatch normalMatch = new NormalMatch(game);
                normalMatch.getTeams().addAll(teams);
                game.getTeams().addAll(teams);
                loadMatchGenerators(normalMatch);
                game.setMatch(normalMatch);

                int ironTime = game.getUpgradePath().getIronTime();
                int goldTime = game.getUpgradePath().getGoldTime();
                int ironAmount = game.getUpgradePath().getIronAmount();
                int goldAmount = game.getUpgradePath().getGoldAmount();

                for (Team team : normalMatch.getTeams()) {
                    team.setIronFloorGenerator(new FloorGenerator(normalMatch, FloorGeneratorType.IRON, ironAmount,
                            ironTime, team.getIronGenerator()));
                    team.setGoldFloorGenerator(new FloorGenerator(normalMatch, FloorGeneratorType.GOLD, goldAmount,
                            goldTime, team.getGoldGenerator()));
                    Map<String, TeamUpgrade> upgradeMap = game.getUpgradePath().getUpgrades();
                    team.getUpgrades().putAll(upgradeMap);
                    team.getGameUpgrades().putAll(upgradeMap);
                    team.getShopUpgrades().putAll(game.getShopPath().getPlayerUpgrade());
                }

                game.notifyObservers();
            }

            @Override
            public void onError(String[] message) {
                player.sendMessage(message);

            }
        });

    }

//	public void verificaShopSiUpgradePath TODO

    @NotNull
    public APlayerData getData(Player p) {

        APlayerData data = this.playerData.get(p.getUniqueId());

        if (data == null) {

            boolean isSQL = BedWarsPlugin.getInstance().isSQLEnabled();

            APlayerData pData = isSQL ? new SQLData(p) : new FileData(p);
            pData.loadData();

            this.playerData.put(p.getUniqueId(), pData);

            return pData;
        }

        return data;

    }

    public void saveGameFile() {
        try {
            this.game.save(this.gameFile);
        } catch (IOException ignored) {

        }

    }

    public Set<String> getGameKeys() {

        Set<String> games = new HashSet<>();

        if (this.game == null) {
            return games;
        }

        ConfigurationSection set = this.game.getConfigurationSection("Games");

        if (set == null) {
            return games;
        }

        return set.getKeys(false);
    }

    public void saveStartingTime(Game game) {

        String path = "Games." + game.getName() + ".";

        for (Entry<Integer, Integer> entry : game.getArenaStartingTime().entrySet()) {
            this.game.set(path + ".StartingTime." + entry.getKey() + ".Time", entry.getValue());
        }

        this.saveGameFile();
    }

    public void loadStartingTime(Game game) {
        Map<Integer, Integer> arenaStartingTime = new HashMap<>();
        String sectionPath = "Games." + game.getName() + ".StartingTime";
        ConfigurationSection configurationSection = this.game.getConfigurationSection(sectionPath);

        if (configurationSection == null) {
            return;
        }

        for (String playerSize : configurationSection.getKeys(false)) {

            if (!Utils.isInteger(playerSize)) {
                continue;
            }

            int time = this.game.getInt(sectionPath + "." + playerSize + ".Time");

            if (time <= 0) {
                continue;
            }

            arenaStartingTime.put(Utils.getInteger(playerSize), time);
        }

        game.getArenaStartingTime().putAll(arenaStartingTime);
    }

    public int getPlayersPlaying(String arenaType) {

        int players = 0;

        for (Game game : this.games) {

            if (!game.getArenaType().equals(arenaType)) {
                continue;
            }

            players += game.getMatch().getPlayers().size() + game.getMatch().getSpectators().size();
        }

        return players;
    }

    public void loadMatchGenerators(AMatch match) {

        for (Location diamondGeneratorLocation : match.getGame().getDiamondGeneratorLocation()) {
            DiamondGenerator generator = new DiamondGenerator(diamondGeneratorLocation, match);
            match.getDiamondGenerators().add(generator);
        }

        for (Location emeraldGeneratorLocation : match.getGame().getEmeraldGeneratorLocation()) {
            EmeraldGenerator generator = new EmeraldGenerator(emeraldGeneratorLocation, match);
            match.getEmeraldGenerators().add(generator);
        }
    }

    public void savePlayerContents(Player p) {
        this.playerContents.put(p.getUniqueId(), new InventoryRestore(p));
    }

    public void givePlayerContents(Player p) {

        InventoryRestore inv = this.playerContents.get(p.getUniqueId());

        if (inv == null) {
            return;
        }

        inv.restore();
        this.playerContents.remove(p.getUniqueId());
    }

    public List<Game> getGames() {
        return this.games;
    }

    public Set<String> getArenaType() {
        return this.arenaType;
    }

    public Map<UUID, AMatch> getPlayerMatch() {
        return playerMatch;
    }

    public Map<Player, GameSetup> getGameSetup() {
        return this.gameSetup;
    }

    public Map<UUID, GameEdit> getGameEdit() {
        return this.gameEdit;
    }

    public Map<UUID, APlayerData> getPlayerData() {
        return this.playerData;
    }

    public Map<String, UpgradePath> getUpgradePath() {
        return this.upgradePath;
    }

    public Map<String, ShopPath> getShopPath() {
        return this.shopPath;
    }

    public Map<String, ArenaOptions> getArenaOptions() {
        return this.arenaOptions;
    }

    public FileConfiguration getGame() {
        return this.game;
    }

    public Set<String> getGameNames() {
        return this.gameNames;
    }

    public ArenaReset getArenaReset() {
        return arenaReset;
    }

    public void setArenaReset(ArenaReset arenaReset) {
        this.arenaReset = arenaReset;
    }
}
