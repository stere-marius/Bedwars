package ro.marius.bedwars.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.WorldCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameEdit {


    private final Game game;
    private final String currentGameName;
    private final String currentArenaType;

    private Conversation conversation;
    private BukkitTask bukkitTask;

    public GameEdit(Game game) {
        this.game = game;
        this.currentGameName = game.getName();
        this.currentArenaType = game.getArenaType();
    }

    public Game getGame() {
        return game;
    }


    public void saveEditedGame() {

        if (!currentGameName.equals(game.getName())) {
            World world = Bukkit.getWorld(currentGameName);
            world.save();
            File worldFile = world.getWorldFolder();
            File newWorldFile = new File(worldFile.getParentFile(), game.getName());
            ManagerHandler.getWorldManager().getWorldAdapter().copyWorldFolder(worldFile, newWorldFile);

            // TODO: Send message if he wants to delete the world
            // TODO: Send message if there is already an world loded

            new BukkitRunnable() {
                @Override
                public void run() {
                    ManagerHandler.getWorldManager().createWorld(game.getName(), new WorldCallback() {
                        @Override
                        public void onComplete(World result, String[] message) {
                            Bukkit.getConsoleSender().sendMessage(message);
                            changeLocationsWorld(result);
                            ManagerHandler.getWorldManager().saveWorldFile(result.getName());
                            ManagerHandler.getGameManager().getGame().set("Games." + currentGameName, null);
                            saveEditedGameToFile();
                            saveArenaTypeToDatabase();
                        }

                        @Override
                        public void onError(String[] message) {
                            Bukkit.getConsoleSender().sendMessage(message);
                        }
                    });
                }
            }.runTaskLater(BedWarsPlugin.getInstance(), 20L);


            if (!worldFile.delete()) {
                Bukkit.getConsoleSender().sendMessage(Utils.translate("[Bedwars] The file " + worldFile.getAbsolutePath() + " could not be deleted. Try again."));
            }

            return;
        }

        ManagerHandler.getGameManager().getGame().set("Games." + game.getName(), null);
        saveEditedGameToFile();
        saveArenaTypeToDatabase();


        System.out.println("The game " + game.getName() + " has been saved");

    }

    public void saveArenaTypeToDatabase() {
        if (!currentArenaType.equals(game.getArenaType()) && BedWarsPlugin.getInstance().isSQLEnabled()) {

            new BukkitRunnable() {

                @Override
                public void run() {
                    BedWarsPlugin.getInstance().sql.execute("CREATE TABLE IF NOT EXISTS `" + game.getArenaType().toUpperCase()
                            + "` (UUID VARCHAR(37) PRIMARY KEY, " + "GamesPlayed INT(8) DEFAULT 0, "
                            + "BedsBroken INT(8) DEFAULT 0, " + "BedsLost INT(8) DEFAULT 0,"
                            + "Kills INT(8) DEFAULT 0, " + "Deaths INT(8) DEFAULT 0, " + "FinalKills INT(8) DEFAULT 0, "
                            + "FinalDeaths INT(8) DEFAULT 0, " + "Wins INT(8) DEFAULT 0, " + "Defeats INT(8) DEFAULT 0,"
                            + "QuickBuy VARCHAR(10000) DEFAULT '')");

                    for (Player player : Bukkit.getOnlinePlayers()) {

                        APlayerData playerData = ManagerHandler.getGameManager().getData(player);
                        playerData.loadData(game.getArenaType());

                    }

                }
            }.runTaskAsynchronously(BedWarsPlugin.getInstance());
        }
    }

    private void saveEditedGameToFile() {

        String path = "Games." + game.getName() + ".";
        FileConfiguration config = ManagerHandler.getGameManager().game;

        config.set(path + ".ArenaType", game.getArenaType());
        config.set(path + ".UpgradePath", game.getUpgradePathName());
        config.set(path + ".ShopPath", game.getShopPathName());
        config.set(path + ".ScoreboardPath", game.getScoreboardPath());
        config.set(path + ".ArenaOptionsPath", "DEFAULT");
        config.set(path + ".DiamondGenerators", Utils.setConvertingLocations(game.getDiamondGeneratorLocation()));
        config.set(path + ".EmeraldGenerators", Utils.setConvertingLocations(game.getEmeraldGeneratorLocation()));
        config.set(path + ".Spectate", Utils.convertingString(game.getSpectateLocation().getLocation()));
        config.set(path + ".PositionOne", Utils.convertingString(game.getGameCuboid().getPositionOne()));
        config.set(path + ".PositionTwo", Utils.convertingString(game.getGameCuboid().getPositionTwo()));
        config.set(path + ".MinimumTeams", game.getMinTeamsToStart());
        config.set(path + ".Waiting", Utils.convertingString(game.getWaitingLocation().getLocation()));
        config.set(path + ".MaxPlayers", game.getTeams().size() * game.getPlayersPerTeam());
        config.set(path + ".PerTeamPlayers", game.getPlayersPerTeam());

        for (Team team : game.getTeams()) {
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

        ManagerHandler.getGameManager().saveGameFile();

        AMatch match = game.getMatch();
        match.getTeams().clear();
        match.getTeams().addAll(game.getTeams());
        ManagerHandler.getGameManager().loadMatchGenerators(match);

        int ironTime = game.getUpgradePath().getIronTime();
        int goldTime = game.getUpgradePath().getGoldTime();
        int ironAmount = game.getUpgradePath().getIronAmount();
        int goldAmount = game.getUpgradePath().getGoldAmount();

        for (Team team : match.getTeams()) {
            team.setIronFloorGenerator(new FloorGenerator(match, FloorGeneratorType.IRON, ironAmount,
                    ironTime, team.getIronGenerator()));
            team.setGoldFloorGenerator(new FloorGenerator(match, FloorGeneratorType.GOLD, goldAmount,
                    goldTime, team.getGoldGenerator()));
            Map<String, TeamUpgrade> upgradeMap = game.getUpgradePath().getUpgrades();
            team.getUpgrades().putAll(upgradeMap);
            team.getGameUpgrades().putAll(upgradeMap);
            team.getShopUpgrades().putAll(game.getShopPath().getPlayerUpgrade());
        }
    }

    private void changeLocationsWorld(World world) {
        Bukkit.getConsoleSender().sendMessage(Utils.translate("&a&lChanging the locations to " + world.getName()));
        List<Location> locations = new ArrayList<>();
        locations.addAll(game.getDiamondGeneratorLocation());
        locations.addAll(game.getEmeraldGeneratorLocation());
        game.getEmeraldGenerator().forEach(e -> locations.add(e.getLocation()));
        game.getDiamondGenerator().forEach(d -> locations.add(d.getLocation()));
        locations.add(game.getSpectateLocation().getLocation());
        locations.add(game.getWaitingLocation().getLocation());
        locations.add(game.getGameCuboid().getPositionOne());
        locations.add(game.getGameCuboid().getPositionTwo());

        for (Team team : game.getTeams()) {
            locations.add(team.getBedLocation().getLocation());
            locations.add(team.getSpawnLocation().getLocation());
            locations.add(team.getIronGenerator().getLocation());
            locations.add(team.getGoldGenerator().getLocation());
            locations.add(team.getEmeraldGenerator().getLocation());
            locations.add(team.getShopLocation().getLocation());
            locations.add(team.getUpgradeLocation().getLocation());
        }

        for (Location location : locations) {

            if (!location.getWorld().getName().equals(currentGameName))
                continue;

            location.setWorld(world);
        }

    }

    public void setCurrentConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setCurrentTask(Player player) {

        if (bukkitTask != null)
            bukkitTask.cancel();

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                System.out.println(conversation.getState());

                if (conversation.getState() != Conversation.ConversationState.STARTED)
                    return;

                player.sendRawMessage(Utils.translate("&e&lConversation timed out."));
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 10 * 20);
    }
}
