package ro.marius.bedwars.listeners.game.players;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class PlayerPlaceBucket implements Listener {

    private final BlockFace[] FACES = new BlockFace[]{

            BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.NORTH,
            BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST

    };

    @EventHandler
    public void onPlaceWater(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlockClicked().getRelative(e.getBlockFace());
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (match.getMatchState() == MatchState.IN_WAITING) {
            return;
        }

        for (BlockFace face : this.FACES) {
            for (int i = 0; i < 3; i++) {
                Block block = b.getRelative(face, i);
                if (block.isLiquid()) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        match.getPlacedBlocks().add(b);
    }

//	@EventHandler
//	public void onFillWater(PlayerBucketFillEvent e) {
//		Player p = e.getPlayer();
//		Block b = e.getBlockClicked();
//		Game game = GameManager.getManager().getPlayers().get(p);
//
//		if (game == null)
//			return;
//		if (game.getGameState() == GameState.IN_WAITING)
//			return;
//
//		if (game.getBuckets().contains(b)) {
//			game.getBuckets().remove(b);
//		}
//	}

}
