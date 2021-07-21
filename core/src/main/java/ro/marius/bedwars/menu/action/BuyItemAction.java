package ro.marius.bedwars.menu.action;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.team.PermanentItemList;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class BuyItemAction implements IconAction {

    private Material material = XMaterial.STONE.parseMaterial();
    private Material priceMaterial = XMaterial.IRON_INGOT.parseMaterial();
    private List<RewardItem> reward = new ArrayList<>();
    private List<String> upgrades = new ArrayList<>();
    private int price = -1;

    private ItemStack priceItemStack;

    @Override
    public void onClick(Player p, Team team, Game game) {

        ItemStack itemPrice = new ItemStack(this.priceMaterial);

        if (!p.getInventory().containsAtLeast(itemPrice, this.price)) {
            p.sendMessage(Lang.NOT_ENOUGH_RESOURCES.getString());
            return;
        }

        PermanentItemList permanentItems = team.getPermanentItems(p);

        byte colorData = team.getTeamColor().getData();

        for (RewardItem rewardItem : this.reward) {

            ItemStack finalItem = rewardItem.getReward().build();
            Material material = finalItem.getType();
            String mName = material.name();

            if (rewardItem.isPermanent()) {
                permanentItems.add(rewardItem);
            }

            if (mName.endsWith("CHESTPLATE")) {
                p.getInventory().setChestplate(finalItem);
                team.applyEnchant("CHESTPLATE", p);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("HELMET")) {
                team.applyEnchant("HELMET", p);
                p.getInventory().setHelmet(finalItem);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("LEGGINGS")) {
                p.getInventory().setLeggings(finalItem);
                team.applyEnchant("LEGGINGS", p);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("BOOTS")) {
                p.getInventory().setBoots(finalItem);
                team.applyEnchant("BOOTS", p);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("SWORD")) {
                this.removeWoodenSword(p);
                finalItem = new ItemBuilder(finalItem).setUnbreakable(ManagerHandler.getVersionManager().getVersionWrapper()).build();

                if (p.getInventory().getItem(0) == null) {
                    p.getInventory().setItem(0, finalItem);
                } else {
                    p.getInventory().addItem(finalItem);
                }

                team.applyEnchant("SWORD", p);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("WOOL")) {
                ItemStack wool = team.getTeamColor().getBuildMaterial();
                wool.setItemMeta(finalItem.getItemMeta());
                wool.setAmount(finalItem.getAmount());
                p.getInventory().addItem(wool);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("_GLASS_PANE")) {
                XMaterial stainedGlassPane = XMaterial.matchXMaterial("STAINED_GLASS_PANE", colorData);
                ItemStack item = stainedGlassPane.parseItem(true);
                item.setItemMeta(finalItem.getItemMeta());
                item.setAmount(finalItem.getAmount());
                p.getInventory().addItem(item);
                Utils.playSoundBuy(p);
                continue;
            }

            if (mName.endsWith("GLASS")) {
                XMaterial stainedGlass = XMaterial.matchXMaterial("STAINED_GLASS", colorData);
                ItemStack item = stainedGlass.parseItem(true);
                item.setItemMeta(finalItem.getItemMeta());
                item.setAmount(finalItem.getAmount());
                p.getInventory().addItem(item);
                Utils.playSoundBuy(p);
                continue;
            }

            if ("HARD_CLAY".equals(mName) || "TERRACOTTA".equals(mName)) {
                XMaterial clay = XMaterial.matchXMaterial("STAINED_CLAY", colorData);
                ItemStack item = clay.parseItem(true);
                item.setItemMeta(finalItem.getItemMeta());
                item.setAmount(finalItem.getAmount());
                p.getInventory().addItem(item);
                Utils.playSoundBuy(p);
                continue;
            }

            p.getInventory().addItem(finalItem);
            Utils.playSoundBuy(p);
        }

        Utils.removeItem(p, this.priceMaterial, this.price);

    }

    public ItemStack getPriceItemStack() {

        return this.priceItemStack == null ?
                this.priceItemStack = new ItemStack(this.priceMaterial) :
                this.priceItemStack;
    }

    public void removeWoodenSword(Player p) {

        PlayerInventory pi = p.getInventory();
        Material sword = XMaterial.WOODEN_SWORD.parseMaterial();

        for (int i = 0; i < 36; i++) {

            ItemStack item = pi.getItem(i);

            if (item == null) {
                continue;
            }
            if (item.getType() != sword) {
                continue;
            }

            pi.clear(i);
        }

    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setPriceMaterial(Material priceMaterial) {
        this.priceMaterial = priceMaterial;
    }

    public void setReward(List<RewardItem> reward) {
        this.reward = reward;
    }

    public List<String> getUpgrades() {
        return this.upgrades;
    }

    public void setUpgrades(List<String> upgrades) {
        this.upgrades = upgrades;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
