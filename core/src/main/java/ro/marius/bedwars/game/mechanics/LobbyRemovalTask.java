package ro.marius.bedwars.game.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.utils.CuboidSelection;

public class LobbyRemovalTask extends BukkitRunnable {

    private static final int MAX_MILLIS_PER_PASTE_TICK = 5;

    private final CuboidSelection lobbySelection;
    private int currentIndex;

    public LobbyRemovalTask(CuboidSelection lobbySelection) {
        this.lobbySelection = lobbySelection;
    }


    @Override
    public void run() {

        long start = System.currentTimeMillis();

        

        this.cancel();
        Bukkit.broadcastMessage("Lobby Removal Task completed!");

    }

    private void removeBlock() {

        Block block = lobbySelection.getBlocks().get(currentIndex);

        if (block.getType() == Material.AIR)
            return;

        block.setType(Material.AIR);
        currentIndex++;
    }

}
