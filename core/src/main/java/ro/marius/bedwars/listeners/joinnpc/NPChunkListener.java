package ro.marius.bedwars.listeners.joinnpc;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import ro.marius.bedwars.game.mechanics.NPCArena;
import ro.marius.bedwars.manager.ManagerHandler;

public class NPChunkListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {

        Chunk ch = e.getChunk();
        Entity[] entities = ch.getEntities();

        if (entities.length == 0) {
            return;
        }

        for (Entity entity : entities) {

            if (!entity.hasMetadata("BedwarsStand")) {
                continue;
            }

            entity.remove();
        }

    }

    @EventHandler
    public void onChunkUnload(ChunkLoadEvent e) {

        Chunk ch = e.getChunk();

        for (NPCArena npcArena : ManagerHandler.getNPCManager().getNPCList(ch)) {
            npcArena.respawnStandList(ManagerHandler.getGameManager().getPlayersPlaying(npcArena.getArenaType()));
        }

    }

}
