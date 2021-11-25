package ro.marius.bedwars.game;

import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.game.mechanics.TeamSetup;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.FAWEManager;
import ro.marius.bedwars.match.NormalMatch;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.utils.CuboidSelection;
import ro.marius.bedwars.utils.TextComponentBuilder;
import ro.marius.bedwars.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class GameSetup {

    private final Player player;
    private String scoreboardPath;
    private String upgradePath;
    private String shopPath;
    private final String name;
    private final String arenaType;
    private final int playersPerTeam;
    private CuboidSelection gameCuboid;
    private CuboidSelection lobbyCuboid;
    private int minimumTeams;
    private int steps;
    private Location spectateLocation;
    private Location inWaiting;
    private Location positionOne, positionTwo;
    private final List<Location> diamondGenerator = new ArrayList<>();
    private final List<Location> emeraldGenerator = new ArrayList<>();
    private final List<TeamSetup> teams = new ArrayList<>();

    public GameSetup(Player player, String name, String arenaType, int playersPerTeam, int minimumTeams) {
        this.player = player;
        this.name = name;
        this.arenaType = arenaType;
        this.playersPerTeam = playersPerTeam;
        this.minimumTeams = minimumTeams;
    }

    public void sendAvailableCommands() {

        this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));
        this.player.sendMessage(" ");

        if (this.steps == 1) {
            player.sendMessage(Utils.translate("&e⇨ Select the waiting lobby to be removal when the arena starts."));
            player.sendMessage(Utils.translate("&c⇨ WARNING: The lobby must be in the same world with the arena's world in order to reset."));
            Utils.sendSuggestCommand(this.player, "/bedwars nextStep", "&e⇨ Skip this step &e⇨/bedwars nextStep",
                    "&aClick me to write the command in chat!");
            this.player.sendMessage(" ");
            this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));
            return;
        }

        if (this.steps == 2) {
            Utils.sendSuggestCommand(this.player, "/bedwars createTeam teamName color",
                    "&e⇨ Create " + (this.teams.isEmpty() ? "a" : "another") + " team, click the message.",
                    "&aClick me to write the command in chat!");
            Utils.sendSuggestCommand(this.player, "/bedwars nextStep", "&e⇨ Go to next step &e⇨/bedwars nextStep",
                    "&aClick me to write the command in chat!");
            this.player.sendMessage(" ");
            this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));
            return;
        }


        if (this.steps == 3) {

            if (this.inWaiting == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setWaitingLocation", "&e⇨ /bedwars setWaitingLocation",
                        "&aClick me to set the waiting location at your current location!");
            }

            if (this.spectateLocation == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setSpectateLocation", "&e⇨ /bedwars setSpectateLocation",
                        "&aClick me to set the spectate location at your current location!");
            }

            Utils.sendPerformCommand(this.player, "/bedwars addDiamondGenerator",
                    "&e⇨ /bedwars addDiamondGenerator &d(" + this.diamondGenerator.size() + ")",
                    "&aClick me to set the generator location at your current location!");
            Utils.sendPerformCommand(this.player, "/bedwars addEmeraldGenerator",
                    "&e⇨ /bedwars addEmeraldGenerator &d(" + this.emeraldGenerator.size() + ")",
                    "&aClick me to set the generator location at your current location!");

            Utils.sendSuggestCommand(this.player, "/bedwars nextStep", "&e⇨ /bedwars nextStep",
                    "&aClick me to write the command in chat!");
            this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));
            this.player.sendMessage(" ");

            return;
        }

        for (TeamSetup team : this.teams) {

            boolean needsBreak = false;
            String teamName = team.getTeamName();

            if (team.getSpawnLocation() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamSpawn " + teamName,
                        "&e⇨ /bedwars setTeamSpawn " + teamName,
                        "&aClick me to set the spawn at your current location!");
                needsBreak = true;
            }

            if (team.getBedLocation() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamBed " + teamName,
                        "&e⇨ /bedwars setTeamBed " + teamName, "&aClick me to set the bed at your current location!");
                needsBreak = true;
            }

            if (team.getIronGenerator() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamIronGenerator " + teamName,
                        "&e⇨ /bedwars setTeamIronGenerator " + teamName,
                        "&aClick me to set the spawn location at your current location!");
                needsBreak = true;
            }

            if (team.getGoldGenerator() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamGoldGenerator " + teamName,
                        "&e⇨ /bedwars setTeamGoldGenerator " + teamName,
                        "&aClick me to set the generator at your current location!");
                needsBreak = true;
            }

            if (team.getEmeraldGenerator() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamEmeraldGenerator " + teamName,
                        "&e⇨ /bedwars setTeamEmeraldGenerator " + teamName,
                        "&aClick me to set the generator at your current location!");
                needsBreak = true;
            }

            if (team.getShop() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamShop " + teamName,
                        "&e⇨ /bedwars setTeamShop " + teamName, "&aClick me to set the shop at your current location!");
                needsBreak = true;
            }

            if (team.getUpgrade() == null) {
                Utils.sendPerformCommand(this.player, "/bedwars setTeamUpgrade " + teamName,
                        "&e⇨ /bedwars setTeamUpgrade " + teamName,
                        "&aClick me to set the upgrade at your current location!");
                needsBreak = true;
            }

            if (needsBreak) {
                break;
            }

        }

        boolean finish = true;

        for (TeamSetup team : this.teams) {

            if (team.isReady()) {
                continue;
            }

            finish = false;
        }

        if (finish) {
            this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));
            this.player.sendMessage(" ");
            Utils.sendSuggestCommand(this.player, "/bedwars finish ", "&e⇨ /bedwars finish ",
                    "&aClick me to set the bed at your current location!");
        }

        this.player.sendMessage(" ");
        this.player.sendMessage(Utils.translate("&e-----------------------------------------------"));

    }

    public void finish() {

        List<Team> teams = new ArrayList<>();

        List<String> letters = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

        String upgradePath = (this.upgradePath != null) ? this.upgradePath : "DEFAULT";
        String shopPath = (this.shopPath != null) ? this.shopPath : "DEFAULT";

        for (int i = 0; i < this.teams.size(); i++) {

            TeamSetup teamName = this.teams.get(i);

            Team team = new Team(teamName.getTeamName(), (i > (letters.size() - 1)) ? letters.get(0) : letters.get(i),
                    teamName.getTeamColor(), teamName.getBedFace(), GameLocation.convertLocation(teamName.getBedLocation()),
                    GameLocation.convertLocation(teamName.getSpawnLocation()),
                    GameLocation.convertLocation(teamName.getGoldGenerator()),
                    GameLocation.convertLocation(teamName.getIronGenerator()),
                    GameLocation.convertLocation(teamName.getEmeraldGenerator()), GameLocation.convertLocation(teamName.getShop()),
                    GameLocation.convertLocation(teamName.getUpgrade()));

            teams.add(team);

        }

//		public Game(String name, String scoreboardPath, String arenaType, GameLocation spectateLocation, GameLocation inWaiting,
//				CuboidSelection gameCuboid, int playersPerTeam, int minTeams, int maxPlayers, List<Team> teams,
//				List<GameLocation> diamondGenerator, List<GameLocation> emeraldGenerator)

        this.scoreboardPath = (this.scoreboardPath == null) ? this.arenaType : this.scoreboardPath;

        Game game = new Game(this.name, this.scoreboardPath, this.arenaType, GameLocation.convertLocation(this.spectateLocation),
                GameLocation.convertLocation(this.inWaiting), gameCuboid, this.playersPerTeam, this.minimumTeams,
                this.getTeams().size() * this.playersPerTeam, teams, GameLocation.getConvertedLocation(this.diamondGenerator),
                GameLocation.getConvertedLocation(this.emeraldGenerator));

        game.setShopPathName(shopPath);
        game.setUpgradePathName(upgradePath);

        ManagerHandler.getGameManager().loadUpgrade(game);
        ManagerHandler.getGameManager().loadShop(game);

        teams.forEach(t -> t.setUpgrades(game.getUpgradePath().getUpgrades()));

        String path = "Games." + this.getName() + ".";
        FileConfiguration config = ManagerHandler.getGameManager().game;

        config.set("Games." + this.getName(), null);
        config.set(path + ".ArenaType", this.arenaType);
        config.set(path + ".UpgradePath", upgradePath);
        config.set(path + ".ShopPath", shopPath);
        config.set(path + ".ScoreboardPath", this.scoreboardPath);
        config.set(path + ".ArenaOptionsPath", "DEFAULT");
        config.set(path + ".DiamondGenerators", Utils.setConvertingLocations(this.getDiamondGenerator()));
        config.set(path + ".EmeraldGenerators", Utils.setConvertingLocations(this.getEmeraldGenerator()));
        config.set(path + ".Spectate", Utils.convertingString(this.getSpectateLocation()));
        config.set(path + ".PositionOne", Utils.convertingString(this.gameCuboid.getPositionOne()));
        config.set(path + ".PositionTwo", Utils.convertingString(this.gameCuboid.getPositionTwo()));
        config.set(path + ".MinimumTeams", this.getMinimumTeams());
        config.set(path + ".Waiting", Utils.convertingString(this.getInWaiting()));
        config.set(path + ".MaxPlayers", this.getTeams().size() * this.playersPerTeam);
        config.set(path + ".PerTeamPlayers", this.playersPerTeam);

        if (lobbyCuboid != null) {
            config.set(path + ".LobbyPositionOne", Utils.convertingString(this.lobbyCuboid.getPositionOne()));
            config.set(path + ".LobbyPositionTwo", Utils.convertingString(this.lobbyCuboid.getPositionTwo()));
            game.setWaitingLobbySelection(this.lobbyCuboid);
        }

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

        this.player.sendMessage(Utils.translate("&a⇨ Arena " + game.getName() + " has been created."));
        ManagerHandler.getGameManager().getGames().add(game);
        ManagerHandler.getGameManager().saveGameFile();

        NormalMatch normalMatch = new NormalMatch(game);
        normalMatch.getTeams().addAll(teams);
        ManagerHandler.getGameManager().loadMatchGenerators(normalMatch);
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

        if (BedWarsPlugin.getInstance().isSQLEnabled()) {

            new BukkitRunnable() {

                @Override
                public void run() {
                    BedWarsPlugin.getInstance().sql.execute("CREATE TABLE IF NOT EXISTS `" + GameSetup.this.arenaType.toUpperCase()
                            + "` (UUID VARCHAR(37) PRIMARY KEY, " + "GamesPlayed INT(8) DEFAULT 0, "
                            + "BedsBroken INT(8) DEFAULT 0, " + "BedsLost INT(8) DEFAULT 0,"
                            + "Kills INT(8) DEFAULT 0, " + "Deaths INT(8) DEFAULT 0, " + "FinalKills INT(8) DEFAULT 0, "
                            + "FinalDeaths INT(8) DEFAULT 0, " + "Wins INT(8) DEFAULT 0, " + "Defeats INT(8) DEFAULT 0,"
                            + "QuickBuy VARCHAR(10000) DEFAULT '')");

                    for (Player player : Bukkit.getOnlinePlayers()) {

                        APlayerData playerData = ManagerHandler.getGameManager().getData(player);
                        playerData.loadData(GameSetup.this.arenaType);

                    }

                }
            }.runTaskAsynchronously(BedWarsPlugin.getInstance());
        }

        World world = Bukkit.getWorld(this.getName());

        for (Entity entity : world.getEntities()) {

            if (entity instanceof Player) {
                continue;
            }

            if (entity instanceof ArmorStand) {
                continue;
            }

            entity.remove();

        }

        world.save();

        new BukkitRunnable() {

            @Override
            public void run() {
                ManagerHandler.getWorldManager().saveWorldFile(GameSetup.this.getName());

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 25);

        ManagerHandler.getScoreboardManager().createPathScoreboard(game);

        if (BedWarsPlugin.getInstance().isFAWE()) {
            FAWEManager.saveSchematic(world, this.positionOne, this.positionTwo, this.name);
            FAWEManager.loadSchematic(game);
        }

        if (!ManagerHandler.getGameManager().getArenaOptions().containsKey(this.arenaType)) {
            TextComponent message = new TextComponentBuilder(
                    "&7>> Do you want to create the arena settings for arena type " + this.arenaType + " ? ").build();
            TextComponent generate = new TextComponentBuilder("&aCLICK HERE TO CREATE IT")
                    .withClickEvent(Action.RUN_COMMAND, "/bedwars generateArenaOptions " + this.name).build();
            this.player.spigot().sendMessage(message, generate);
        }

    }

    public void addStep() {

        if (this.steps == 2) {

            if (this.getTeams().size() < 2) {
                this.player.sendMessage(Utils.translate("&c⇨ You must create at least two teams."));
                return;
            }
        }

        if (this.steps == 3) {

            if (this.inWaiting == null) {
                this.player.sendMessage(Utils.translate("&c⇨ You must set the waiting location."));
                return;
            }

            if (this.spectateLocation == null) {
                this.player.sendMessage(Utils.translate("&c⇨ You must set the spectate location."));
                return;
            }

            if (this.getEmeraldGenerator().isEmpty()) {
                this.player.sendMessage(Utils.translate("&c⇨ You must have at least one emerald generator."));
                return;
            }

            if (this.getDiamondGenerator().isEmpty()) {
                this.player.sendMessage(Utils.translate("&c⇨ You must have at least one diamond generator."));
                return;
            }

        }

        this.steps++;
    }

    public void performCuboidSelection() {
        if (this.steps == 0) {
            gameCuboid = new CuboidSelection(positionOne, positionTwo);
            gameCuboid.assignValues();
        }

        if (this.steps == 1) {
            lobbyCuboid = new CuboidSelection(positionOne, positionTwo);
            lobbyCuboid.assignValues();
            lobbyCuboid.select();
            player.setItemInHand(null);
        }

        positionOne = positionTwo = null;
        addStep();
        sendAvailableCommands();
    }

    public boolean containsTeam(String teamName) {
        return teams.stream().anyMatch(team -> team.getTeamName().equals(teamName));
    }

    public TeamSetup getTeamSetup(String teamName) {
        return teams.stream().filter(team -> team.getTeamName().equals(teamName)).findFirst().get();
    }

//    87 103 93 - -87 44 -86


//    13.709 115 12.639 - -12.566 126 -12.715

    public int getTeamsReady() {
        return this.teams.stream().filter(TeamSetup::isReady).collect(Collectors.toSet()).size();
    }

    public String getName() {
        return this.name;
    }

    public int getMinimumTeams() {
        return this.minimumTeams;
    }

    public void setMinimumTeams(int minimumTeams) {
        this.minimumTeams = minimumTeams;
    }

    public int getSteps() {
        return this.steps;
    }

    public Location getSpectateLocation() {
        return this.spectateLocation;
    }

    public void setSpectateLocation(Location spectateLocation) {
        this.spectateLocation = spectateLocation;
    }

    public Location getInWaiting() {
        return this.inWaiting;
    }

    public void setInWaiting(Location inWaiting) {
        this.inWaiting = inWaiting;
    }

    public Location getPositionOne() {
        return this.positionOne;
    }

    public void setPositionOne(Location positionOne) {
        this.positionOne = positionOne;
    }

    public Location getPositionTwo() {
        return this.positionTwo;
    }

    public void setPositionTwo(Location positionTwo) {
        this.positionTwo = positionTwo;
    }

    public List<Location> getDiamondGenerator() {
        return this.diamondGenerator;
    }

    public List<Location> getEmeraldGenerator() {
        return this.emeraldGenerator;
    }

    public List<TeamSetup> getTeams() {
        return this.teams;
    }


    public CuboidSelection getGameCuboid() {

        if (gameCuboid == null) {
            gameCuboid = new CuboidSelection(this.positionOne, this.positionTwo);
            gameCuboid.assignValues();
        }

        return gameCuboid;
    }
}
