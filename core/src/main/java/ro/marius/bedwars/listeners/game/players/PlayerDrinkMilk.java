package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class PlayerDrinkMilk implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {

        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (item.getType() != Material.MILK_BUCKET) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        match.addToPreventMap(p.getUniqueId());
        Utils.decreaseItemAmountFromHand(p);
        e.setItem(null);
        e.setCancelled(true);
    }

}
