package ro.marius.bedwars.team.upgrade;

import org.bukkit.Material;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

public enum PermanentArmor {

    DEFAULT(0, new ItemBuilder(Material.LEATHER_LEGGINGS), new ItemBuilder(Material.LEATHER_BOOTS)),
    CHAIN(1, new ItemBuilder(Material.CHAINMAIL_LEGGINGS), new ItemBuilder(Material.CHAINMAIL_BOOTS)),
    IRON(2, new ItemBuilder(Material.IRON_LEGGINGS), new ItemBuilder(Material.IRON_BOOTS)),
    DIAMOND(3, new ItemBuilder(Material.DIAMOND_LEGGINGS), new ItemBuilder(Material.DIAMOND_BOOTS));

    private int ID;
    private ItemBuilder leggings;
    private ItemBuilder boots;

    PermanentArmor(int ID, ItemBuilder leggings, ItemBuilder boots) {
        this.ID = ID;
        this.leggings = leggings;
        this.boots = boots;
    }

    public ItemBuilder getLeggings() {
        return this.leggings;
    }

    public ItemBuilder getBoots() {
        return this.boots;
    }

}
