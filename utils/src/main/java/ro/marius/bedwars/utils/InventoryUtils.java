package ro.marius.bedwars.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

public class InventoryUtils {


    public static void fillEmptySlotsWithGlass(Inventory inventory) {

        ItemStack blackGlassItem = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                .setDisplayName(" ")
                .build();

        for (int i = 0; i < inventory.getSize(); i++) {

            if (inventory.getItem(i) != null)
                continue;

            inventory.setItem(i, blackGlassItem);
        }

    }
}
