package ro.marius.bedwars.listeners.game.players;

import org.bukkit.event.Listener;

public class PlayerBlockPlaceSpectator implements Listener {

//	@EventHandler
//	public void onBuild(BlockCanBuildEvent e) {
//
//		Player p = e.getPlayer();
//		AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
//
//		if (e.isBuildable())
//			return;
//
//		if (match == null)
//			return;
//
//		if (match.getMatchState() != MatchState.IN_GAME)
//			return;
//
//		if (match.getSpectators().size() == 0)
//			return;
//
//		if (match.getSpectators().contains(p))
//			return;
//
//		Block b = e.getBlock();
//		Location blockLocation = b.getLocation();
//		int blockX = blockLocation.getBlockX();
//		int blockY = blockLocation.getBlockY();
//		int blockZ = blockLocation.getBlockZ();
//		String blockWorld = blockLocation.getWorld().getName();
//
//		for (Player sp : match.getSpectators()) {
//
//			Location loc = sp.getLocation();
//			int x = loc.getBlockX();
//			int y = loc.getBlockY();
//			int z = loc.getBlockZ();
//			String world = loc.getWorld().getName();
//
//			Bukkit.broadcastMessage("Player " + sp.getName() + " location is  " + sp.getLocation().getBlockX() + " ; "
//					+ sp.getLocation().getBlockY() + " ; " + sp.getLocation().getBlockZ());
//
//			if (!world.equals(blockWorld))
//				continue;
//			if (blockX != x)
//				continue;
//			if (blockY != y)
//				continue;
//			if (blockZ != z)
//				continue;
//
//			Bukkit.broadcastMessage("Player is in the same location as the block");
//
//			sp.teleport(sp.getLocation().add(0, 1, 0));
//			e.setBuildable(true);
//
//		}
//
//		Bukkit.broadcastMessage("Block location is " + e.getBlock().getLocation().getBlockX() + " ; "
//				+ e.getBlock().getLocation().getBlockY() + " ; " + e.getBlock().getLocation().getBlockZ());
//
//	}

}
