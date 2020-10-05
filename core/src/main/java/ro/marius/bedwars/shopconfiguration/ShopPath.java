package ro.marius.bedwars.shopconfiguration;

import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.shopconfiguration.shopinventory.QuickBuyIcon;
import ro.marius.bedwars.shopconfiguration.shopinventory.ShopInventory;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopPath {

    private Map<String, ShopInventory> inventory;
    private final Map<String, TeamUpgrade> playerUpgrade;

    public ShopPath(Map<String, ShopInventory> inventory, Map<String, TeamUpgrade> playerUpgrade) {
        this.inventory = inventory;
        this.playerUpgrade = playerUpgrade;
    }

    public List<QuickBuyIcon> getQuickBuySlots() {

        List<QuickBuyIcon> icons = new ArrayList<>();

        for (ShopInventory inv : this.inventory.values()) {

            for (InventoryIcon i : inv.getItems().values()) {

                if (i instanceof QuickBuyIcon) {

                    icons.add((QuickBuyIcon) i);

                }

            }
        }

        return icons;
    }

    public InventoryIcon getItem(String path) {

        for (ShopInventory inv : this.inventory.values()) {

            for (InventoryIcon i : inv.getItems().values()) {

                if (i.getPath().equals(path)) {

                    return i;
                }

            }
        }

        return null;
    }

    public Map<String, ShopInventory> getInventory() {
        return this.inventory;
    }

    public void setInventory(Map<String, ShopInventory> inventory) {
        this.inventory = inventory;
    }

    public Map<String, TeamUpgrade> getPlayerUpgrade() {
        return this.playerUpgrade;
    }

}
