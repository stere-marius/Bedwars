package ro.marius.bedwars.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class ExtraInventory implements InventoryHolder {


    @Override
    public abstract Inventory getInventory();

    public void onClick(InventoryClickEvent e) {

    }

    public void onClose(InventoryCloseEvent e) {

    }


}
