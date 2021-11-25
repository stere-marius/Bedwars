package ro.marius.bedwars.game.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.utils.CuboidSelection;

import java.util.concurrent.TimeUnit;

public class LobbyRemovalTask extends BukkitRunnable {

    private final Game game;
    private int blockSize;
    private int startIndex;

    private int currentIndex = 0;
    private Material material;
    private final long start;

    public LobbyRemovalTask(Game game, Material material) {
        this.game = game;
        this.blockSize = game.getWaitingLobbySelection().getBlocks().size();
        this.material = material;
        this.start = System.nanoTime();
    }

    public LobbyRemovalTask(Game game) {
        this.game = game;
        this.blockSize = game.getWaitingLobbySelection().getBlocks().size();
        this.start = System.nanoTime();
    }

    @Override
    public void run() {
        if (blockSize <= 0) {
            Bukkit.broadcastMessage("It took " + (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - this.start) + " seconds!");
            cancel();
            Bukkit.broadcastMessage("blockSize <= 0");
            return;
        }

        if (game.getMatch().getMatchState() != MatchState.IN_GAME) {
            cancel();
            return;
        }

        int blocksPerIteration = 30;
        if (currentIndex * blocksPerIteration >= game.getWaitingLobbySelection().getBlocks().size()) {
            game.getWaitingLobbySelection().getPositionOne().getChunk().unload();
            game.getWaitingLobbySelection().getPositionTwo().getChunk().unload();
            game.getWaitingLobbySelection().getPositionOne().getChunk().load();
            game.getWaitingLobbySelection().getPositionOne().getChunk().load();
            cancel();
            Bukkit.broadcastMessage("It took " + (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - this.start) + " seconds!");
            Bukkit.broadcastMessage("blockSize <= 0");
            return;
        }

        int startIndex = currentIndex * blocksPerIteration;
        int endIndex = Math.min(currentIndex * blocksPerIteration + blocksPerIteration, game.getWaitingLobbySelection().getBlocks().size());

        for (int i = startIndex; i < endIndex; i++) {
            Block block = game.getWaitingLobbySelection().getBlocks().get(i);
            block.setType(material == null ? Material.AIR : material);
            block.getState().update();
        }

        currentIndex++;
        // 110

        // 0 20 0
        // 20 40 1
        // 40 60 2
        // 60 80 3
        // 80 100 4
        // 100 110 5
    }
}
