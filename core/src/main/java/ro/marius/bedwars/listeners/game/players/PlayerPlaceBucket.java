package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.utils.Utils;

public class PlayerPlaceBucket implements Listener {

    @EventHandler
    public void onPlaceWater(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlockClicked().getRelative(e.getBlockFace());
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }

        Bukkit.getScheduler().runTask(BedWarsPlugin.getInstance(), () -> Utils.decreaseItemAmountFromHand(p, e.getItemStack()));
        match.getPlacedBlocks().add(b);
    }

}
