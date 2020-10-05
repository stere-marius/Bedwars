package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import ro.marius.bedwars.match.AMatch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TNTExplode implements Listener {

    @EventHandler
    public void onFireballExplode(EntityExplodeEvent e) {

        Entity entity = e.getEntity();

        if (entity.getType() != EntityType.PRIMED_TNT) {
            return;
        }
        if (!entity.hasMetadata("Match")) {
            return;
        }

        AMatch match = (AMatch) e.getEntity().getMetadata("Match").get(0).value();
        List<Block> blocks = e.blockList();

        if (blocks.isEmpty()) {
            return;
        }

        Iterator<Block> it = blocks.iterator();
        Set<Block> placedTeamBeds = new HashSet<>();
        match.getPlacedBeds().values().forEach(teamBed -> placedTeamBeds.addAll(teamBed.getBedBlocks()));

        blocks.removeIf(b ->
                (!match.getPlacedBlocks().contains(b))
                        || placedTeamBeds.contains(b)
                        || b.getType().name().contains("GLASS")
                        || b.getRelative(BlockFace.UP).getType().name().contains("GLASS"));

    }


}
