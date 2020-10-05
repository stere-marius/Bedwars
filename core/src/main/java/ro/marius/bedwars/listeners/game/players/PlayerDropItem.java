package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.XMaterial;

public class PlayerDropItem implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Item item = e.getItemDrop();
        Material material = item.getItemStack().getType();
        Material woodenSword = XMaterial.WOODEN_SWORD.parseMaterial();

        if (material == woodenSword) {
            e.setCancelled(true);
            return;
        }

        if (material.name().endsWith("_SWORD") && !p.getInventory().contains(woodenSword)) {
            ItemStack itemStack = new ItemStack(woodenSword);
            ItemMeta meta = itemStack.getItemMeta();
            this.setUnbreakable(meta, true);
            itemStack.setItemMeta(meta);
            p.getInventory().addItem(itemStack);
        }

        match.getMatchEntity().add(item);
    }

    @SuppressWarnings("deprecation")
    public void setUnbreakable(ItemMeta meta, boolean value) {
        try {
            meta.setUnbreakable(value);
        } catch (NoSuchMethodError e) {
//            meta.spigot().setUnbreakable(true);
//            TODO: Fix
        }
    }

}
