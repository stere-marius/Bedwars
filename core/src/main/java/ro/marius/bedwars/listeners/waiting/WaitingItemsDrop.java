package ro.marius.bedwars.listeners.waiting;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.configuration.Items;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class WaitingItemsDrop implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = e.getItemDrop().getItemStack();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
		
        if (match == null)
            return;
        if (match.getMatchState() == MatchState.IN_GAME)
            return;

        e.setCancelled(itemStack.isSimilar(Items.GAME_LEAVE.toItemStack()));
    }

}
