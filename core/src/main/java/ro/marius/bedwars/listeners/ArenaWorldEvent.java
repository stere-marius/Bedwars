package ro.marius.bedwars.listeners;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import ro.marius.bedwars.manager.ManagerHandler;

import java.util.Set;

public class ArenaWorldEvent implements Listener {

    @EventHandler
    public void onLoad(WorldInitEvent e) {
        World world = e.getWorld();
        String name = world.getName();
        Set<String> games = ManagerHandler.getGameManager().getGameNames();

        if (!games.contains(name)) {
            return;
        }

        world.setKeepSpawnInMemory(false);
        world.setAutoSave(false);

        for (Chunk chunk : world.getLoadedChunks()) {
            chunk.unload();
        }

    }

}
