package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.PlayerInventory;
import ro.marius.bedwars.manager.ManagerHandler;

public class PlayerDamageArmor implements Listener {


    @EventHandler
    public void onDamageArmor(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player))
            return;

        Player p = (Player) e.getEntity();

        if (!ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId()))
            return;

        PlayerInventory playerInventory = p.getInventory();
        playerInventory.getHelmet().setDurability((short) 0);
        playerInventory.getChestplate().setDurability((short) 0);
        playerInventory.getLeggings().setDurability((short) 0);
        playerInventory.getBoots().setDurability((short) 0);
    }

}
