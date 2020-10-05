package ro.marius.bedwars.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryRestore {

    private final Player player;
    private final ItemStack[] inventoryContents;
    private final ItemStack[] enderchestItems;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final int totalExp;
    private final int level;
    private final float exp;
    private final GameMode gameMode;

    public InventoryRestore(Player player) {
        this.player = player;
        this.inventoryContents = player.getInventory().getContents();
        this.helmet = player.getInventory().getHelmet();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();
        this.totalExp = player.getTotalExperience();
        this.exp = player.getExp();
        this.level = player.getLevel();
        this.enderchestItems = player.getEnderChest().getContents();
        this.gameMode = player.getGameMode();
    }

    public void restore() {
        player.getInventory().setContents(this.inventoryContents);
        player.getInventory().setHelmet(this.helmet);
        player.getInventory().setChestplate(this.chestplate);
        player.getInventory().setLeggings(this.leggings);
        player.getInventory().setBoots(this.boots);
        player.setTotalExperience(this.totalExp);
        player.setExp(this.exp);
        player.setLevel(this.level);
        player.getEnderChest().setContents(this.enderchestItems);
        player.setGameMode(this.gameMode);
    }
    
}