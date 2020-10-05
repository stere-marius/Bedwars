package ro.marius.bedwars.menu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.utils.Utils;

import java.util.Map;

public abstract class CustomInventory implements InventoryHolder {

    private final String inventoryName;
    private final int size;
    private Map<Integer, InventoryIcon> items;


    public CustomInventory(String inventoryName, int size, Map<Integer, InventoryIcon> items) {
        this.inventoryName = Utils.translate(inventoryName);
        this.size = size;
        this.items = items;
    }

    public void onClick(InventoryClickEvent e) {

    }

    public String getInventoryName() {
        return this.inventoryName;
    }

    public int getSize() {
        return this.size;
    }

    public Map<Integer, InventoryIcon> getItems() {
        return this.items;
    }

    public void setItems(Map<Integer, InventoryIcon> items) {
        this.items = items;
    }
}
