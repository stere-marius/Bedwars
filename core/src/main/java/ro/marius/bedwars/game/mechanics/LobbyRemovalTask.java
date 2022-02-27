package ro.marius.bedwars.game.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
            cancel();
            return;
        }

        if (game.getMatch().getMatchState() != MatchState.IN_GAME) {
            cancel();
            return;
        }

        int blocksPerIteration = game.getWaitingLobbySelection().getBlocks().size() / 4;

        if (currentIndex * blocksPerIteration >= game.getWaitingLobbySelection().getBlocks().size()) {
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
            block.getState().update(true);
        }

        currentIndex++;
    }

}
