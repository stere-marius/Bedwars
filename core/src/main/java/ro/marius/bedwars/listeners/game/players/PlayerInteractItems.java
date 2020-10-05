package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.configuration.Items;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.menu.extra.TeamSelectorInventory;

public class PlayerInteractItems implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        if (match == null) {
            return;
        }

        ItemStack handItem = e.getItem();

        if (handItem == null) {
            return;
        }

        if (handItem.isSimilar(Items.GAME_LEAVE.toItemStack())) {
            match.removePlayer(p);
            e.setCancelled(true);
        }

        if (handItem.isSimilar(Items.TEAM_SELECTOR.toItemStack())) {
            p.openInventory(new TeamSelectorInventory(match).getInventory());
            e.setCancelled(true);
        }

    }

}
