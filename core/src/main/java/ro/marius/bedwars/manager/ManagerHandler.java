package ro.marius.bedwars.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.type.*;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.mysql.SQLManager;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.utils.InventoryRestore;

import java.util.HashMap;

public class ManagerHandler {
    private static GeneratorManager generatorManager;
    private static GameManager gameManager;
    private static ScoreboardManager scoreboardManager;
    private static VersionManager versionManager;
    private static SQLManager SQLManager;
    private static WorldManager worldManager;
    private static HologramManager hologramManager;
    private static SocketManager socketManager;
    private static NPCManager npcManager;
    private static final HashMap<Player, InventoryRestore> inventorySave = new HashMap<>();

    public ManagerHandler(BedWarsPlugin plugin) {
        generatorManager = new GeneratorManager(plugin);
        scoreboardManager = new ScoreboardManager();
        versionManager = new VersionManager();
        SQLManager = new SQLManager();
        gameManager = new GameManager();
        worldManager = new WorldManager();
        hologramManager = new HologramManager();
        socketManager = new SocketManager();
        npcManager = new NPCManager();
    }

    public static void onDisable() {
        if (npcManager != null) {
            npcManager.deleteNPC();
        }

        for (Game game : gameManager.getGames()) {
            AMatch match = game.getMatch();
            if (match.getMatchState() == MatchState.WAITING) {
                match.getPlayers().forEach(match::removePlayer);
            } else {
                match.endGame("RESTART");
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            APlayerData data = gameManager.getData(player);
            data.saveData();
        }
    }


    public static HologramManager getHologramManager() {
        return hologramManager;
    }

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static HashMap<Player, InventoryRestore> getInventorySave() {
        return inventorySave;
    }

    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public static VersionManager getVersionManager() {
        return versionManager;
    }

    public static void setVersionManager(VersionManager v) {
        versionManager = v;
    }

    public static SQLManager getSQLManager() {
        return SQLManager;
    }

    public static SocketManager getSocketManager() {
        return socketManager;
    }

    public static NPCManager getNPCManager() {
        return npcManager;
    }

}
