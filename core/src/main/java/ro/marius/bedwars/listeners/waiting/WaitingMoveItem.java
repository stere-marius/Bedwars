package ro.marius.bedwars.listeners.waiting;

import org.bukkit.event.Listener;

public class WaitingMoveItem implements Listener {

//	@EventHandler
//	public void onClick(InventoryClickEvent e) {
//		Player p = (Player) e.getWhoClicked();
//		AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
//		ItemStack item = e.getCurrentItem();
//		
//		if (match == null)
//			return;
//		if (match.getMatchState() == MatchState.IN_GAME)
//			return;
//		
//		if (e.getClick() == ClickType.NUMBER_KEY) {
//			e.setCancelled(true);
//			return;
//		}
//		
//		if (item == null)
//			return;
//		if (item.getType() == Material.AIR)
//			return;
//		if (!item.hasItemMeta())
//			return;
//		if (!item.getItemMeta().hasDisplayName())
//			return;
//		if (!item.getItemMeta().hasLore())
//			return;
//
//		ItemMeta meta = item.getItemMeta();
//		String displayName = meta.getDisplayName();
//		List<String> lore = meta.getLore();
//		boolean hasName = displayName.equals(Utils.translate(Items.GAME_LEAVE.getName()))
//				|| displayName.equals(Utils.translate(Items.TEAM_SELECTOR.getName()));
//		boolean hasLore = lore.equals(Items.TEAM_SELECTOR.getLore()) || lore.equals(Items.GAME_LEAVE.getLore());
//		e.setCancelled(hasName && hasLore);
//	}

}
