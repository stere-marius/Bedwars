package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class PlayerBlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Block b = e.getBlock();
        Location blockLocation = b.getLocation();

        if (match.getMatchState() != MatchState.IN_GAME) {
            e.setCancelled(true);
            return;
        }

        if (match.getSpectators().contains(p)) {
            e.setCancelled(true);
            return;
        }

        if (!match.getGame().getGameCuboid().isInsideCuboidSelection(blockLocation)) {
            p.sendMessage(Lang.OUTSIDE_OF_ARENA.getString());
            e.setCancelled(true);
            return;
        }
        if (match.getGame().isNearAirGenerators(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        if (match.isDenyPlacingBlock(blockLocation)) {
            e.setCancelled(true);
            return;
        }

        match.getPlacedBlocks().add(e.getBlock());
    }

}
