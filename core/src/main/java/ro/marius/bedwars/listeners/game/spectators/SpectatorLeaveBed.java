package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.event.Listener;

public class SpectatorLeaveBed implements Listener {

//	@SuppressWarnings("deprecation")
//	@EventHandler
//	public void onPlayerInteractSpectator(PlayerInteractEvent e) {
//		Player p = e.getPlayer();
//		if (!GameManager.getManager().getSpectators().containsKey(p))
//			return;
//		if (p.getItemInHand() == null)
//			return;
//		if (p.getItemInHand().getType() != Material.getMaterial(Items.SPECTATOR_LEAVE.getMaterial()))
//			return;
//		if (!p.getItemInHand().hasItemMeta())
//			return;
//		if (!p.getItemInHand().getItemMeta().getDisplayName().equals(Utils.translate(Items.SPECTATOR_LEAVE.getName())))
//			return;
//		GameManager.getManager().removeSpectator(p);
//	}

}
