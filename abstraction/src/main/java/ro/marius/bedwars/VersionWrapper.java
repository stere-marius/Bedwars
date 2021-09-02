package ro.marius.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface VersionWrapper {

    default void deleteItemInHand(Player p, ItemStack itemStack) {

        if (p.getInventory().getItemInMainHand().isSimilar(itemStack)) {
            p.getInventory().setItemInMainHand(null);
            return;
        }

        if (p.getInventory().getItemInOffHand().isSimilar(itemStack)) {
            p.getInventory().setItemInOffHand(null);
        }

    }

    default void setCollidable(Player p, boolean value) {
        p.setCollidable(value);
    }

    default void hidePlayer(Player p, Player playerToBeHidden, JavaPlugin javaPlugin) {
        p.hidePlayer(javaPlugin, playerToBeHidden);
    }

    default void showPlayer(Player p, Player toShow, JavaPlugin javaPlugin) {
        p.showPlayer(javaPlugin, toShow);
    }

    default void setOwner(SkullMeta skullMeta, UUID uuid) {
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    default void setUnbreakable(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
    }

    void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle,
                   boolean send, boolean sendAgainTitle);

    Set<Block> getPlacedBedBlocks(BlockFace bedFace, Location loc, Material bedMaterial);

    void hidePlayer(Player p);

    void sendPlayerInfoPackets(Player p, JavaPlugin javaPlugin);

    BlockFace getBedFace(Location location);

    Location getBedHead(Location location);

    int getPing(Player p);

    void freezeEntity(Entity en);

    ItemStack addGlow(ItemStack item);

    ItemStack setNBTTag(ItemStack item, String tag, String value);

    boolean containsNBTTag(ItemStack item, String tag);

    String getNBTTag(ItemStack item, String tag);

    void sendHideEquipmentPacket(Player player, Set<Player> playersToSendPacket);

    void sendShowEquipmentPacket(Player player, Set<Player> playersToSendPacket);

    Villager spawnVillager(Location location);

    Blaze spawnBlaze(Location location);

    Creeper spawnCreeper(Location location);

    Skeleton spawnSkeleton(Location location);

    IronGolem spawnGolem(Location location);

    Zombie spawnZombie(Location location);

    PigZombie spawnPigZombie(Location location);

}
