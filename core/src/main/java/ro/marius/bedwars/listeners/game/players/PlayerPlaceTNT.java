package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class PlayerPlaceTNT implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(BlockPlaceEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (p.getItemInHand() == null) {
            return;
        }
        if (p.getItemInHand().getType() != Material.TNT) {
            return;
        }

        TNTPrimed tnt = e.getBlock().getLocation().getWorld().spawn(e.getBlock().getLocation().add(0.5D, 0, 0.5D), TNTPrimed.class);
        tnt.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        tnt.setMetadata("Owner", new FixedMetadataValue(BedWarsPlugin.getInstance(), p.getName()));
        tnt.setFuseTicks(40);
        e.setCancelled(true);
        Utils.removeItemInHand(p);

        match.getMatchEntity().add(tnt);
    }

}
