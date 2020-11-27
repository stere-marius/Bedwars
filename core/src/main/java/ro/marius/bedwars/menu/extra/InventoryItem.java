package ro.marius.bedwars.menu.extra;

import org.bukkit.inventory.ItemStack;

public class InventoryItem {

    private final int slot;
    private final ItemStack itemStack;

    public InventoryItem(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "slot=" + slot +
                ", itemStack=" + (itemStack.getItemMeta() == null ? "NULL-ITEM-META" : itemStack.getItemMeta().getDisplayName()) +
                '}';
    }
}
