package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class PlayerPlaceTNT implements Listener {

    @EventHandler
    public void onInteract(BlockPlaceEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null)
            return;

        if (e.getBlock().getType() != Material.TNT)
            return;

        Location blockLocation = e.getBlock().getLocation();
        TNTPrimed tnt = blockLocation.getWorld().spawn(blockLocation.clone().add(0.5D, 0.0D, 0.5D), TNTPrimed.class);
        tnt.setVelocity(new Vector(0, 0, 0));
        tnt.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        tnt.setMetadata("Owner", new FixedMetadataValue(BedWarsPlugin.getInstance(), p.getName()));
        tnt.setFuseTicks(40);
        e.setCancelled(true);
        Utils.decreaseItemAmountFromHand(p);
        match.getMatchEntity().add(tnt);
    }

}
