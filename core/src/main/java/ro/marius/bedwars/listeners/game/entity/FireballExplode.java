package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import ro.marius.bedwars.match.AMatch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FireballExplode implements Listener {

    @EventHandler
    public void onFireballExplode(EntityExplodeEvent e) {

        if (e.getEntity().getType() != EntityType.FIREBALL) {
            return;
        }
        if (!e.getEntity().hasMetadata("Match")) {
            return;
        }

//        Fireball fireball = (Fireball) e.getEntity();
//        fireball.setIsIncendiary(false);
        AMatch match = (AMatch) e.getEntity().getMetadata("Match").get(0).value();
        List<Block> blocks = e.blockList();
        Set<Block> placedTeamBeds = new HashSet<>();
        match.getPlacedBeds().values().forEach(teamBed -> placedTeamBeds.addAll(teamBed.getBedBlocks()));

        blocks.removeIf(b ->
                (!match.getPlacedBlocks().contains(b))
                        || placedTeamBeds.contains(b)
                        || b.getType().name().contains("GLASS"));

        for (Block block : blocks) {
            block.breakNaturally();
            match.getPlacedBlocks().remove(block);
        }

        e.setCancelled(true);
    }

}
