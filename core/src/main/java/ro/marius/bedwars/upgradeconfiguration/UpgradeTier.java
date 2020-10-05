package ro.marius.bedwars.upgradeconfiguration;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.menu.action.RewardItem;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.team.PermanentItemList;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.upgrade.IUpgrade;
import ro.marius.bedwars.team.upgrade.QueuedTrap;
import ro.marius.bedwars.team.upgrade.enemy.EnemyTrapEffect;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class UpgradeTier {

    private Game game;

    private List<IUpgrade> upgrades;
    private boolean isUnlocked;
    private boolean isPermanent;

    // TODO Glowing, Enchants
    private boolean glowing;
    private ItemStack priceItemStack;

    private String upgradeName;
    private Material displayMaterial;
    private int materialData;
    private int amount;

    private Material priceMaterial;
    private int price;

    private boolean decreaseTierOnDeath = false;
    private boolean respawnItems = false;

    private String name;
    private List<String> lore;
    private List<String> increaseTier;
    private List<String> decreaseTier;
    private List<RewardItem> rewardItem;

    private List<Requirement> requirements = new ArrayList<>();

    public UpgradeTier(Game game, String upgradeName, List<String> increaseTier, List<String> decreaseTier,
                       List<IUpgrade> upgrades, List<RewardItem> rewardItem, Material displayMaterial, int materialData,
                       int amount, Material priceMaterial, int price, String name, List<String> lore, boolean glowing) {
        this.game = game;
        this.rewardItem = rewardItem;
        this.increaseTier = increaseTier;
        this.decreaseTier = decreaseTier;
        this.upgradeName = upgradeName;
        this.upgrades = upgrades;
        this.displayMaterial = displayMaterial;
        this.materialData = materialData;
        this.priceMaterial = priceMaterial;
        this.amount = amount;
        this.price = price;
        this.name = name;
        this.lore = lore;
        this.glowing = glowing;
        this.priceItemStack = new ItemStack(priceMaterial);
    }

    @Override
    public UpgradeTier clone() {

        List<IUpgrade> upgrades = new ArrayList<>();

        for (IUpgrade upg : this.upgrades) {
            upgrades.add(upg.clone());
        }

        List<Requirement> requirements = new ArrayList<>();

        for (Requirement req : this.requirements) {
            requirements.add(req.clone());
        }

        List<String> lore = new ArrayList<>(this.lore);
        lore.replaceAll(s -> s.replaceAll("§", "&"));

        UpgradeTier upgradeTier = new UpgradeTier(this.game, this.upgradeName, this.increaseTier, this.decreaseTier, upgrades, this.rewardItem,
                this.displayMaterial, this.materialData, this.amount, this.priceMaterial, this.price, this.name, lore, this.glowing);
        upgradeTier.setDecreaseTierOnDeath(this.decreaseTierOnDeath);
        upgradeTier.setRespawnItems(this.respawnItems);
        upgradeTier.setRequirements(requirements);

        return upgradeTier;
    }

//	public void applyRequiements(Team team, Player p) {
//
//		for (Requirement r : requirements) {
//
//			r.readRequirement(team, p);
//			
//			p.sendMessage("Requirement " + r.getDescription() + " " + r.isActivated());
//
//			if (!r.isActivated())
//				continue;
//
//			r.getRequirementBuilder().apply(this);
//
//		}
//
//	}

    public boolean onPurchase(Player p, boolean isShop, boolean isPlayerUpgrade) {

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return false;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return false;
        }

//		applyRequiements(team, p);

        if (this.isUnlocked) {
            p.sendMessage(Lang.ALREADY_BOUGHT_THE_UPGRADE.getString());
            Utils.playEndermanSound(p);
            p.closeInventory();
            return false;
        }

//		p.sendMessage("Has item " + priceMaterial.name() + " : " + price);

        if (!p.getInventory().containsAtLeast(new ItemStack(this.priceMaterial), this.price)) {
            p.sendMessage(Lang.NOT_ENOUGH_RESOURCES.getString());
            Utils.playEndermanSound(p);
            p.closeInventory();
            return false;
        }

        if (!isPlayerUpgrade) {
            team.sendMessage(Lang.UPGRADE_BOUGHT.getString().replace("<upgradeName>", Utils.translate(this.name)).replace("<player>", p.getName()));
        } else {
            p.sendMessage(Lang.PLAYER_UPGRADE_BOUGHT.getString().replace("<upgradeName>", Utils.translate(this.name)));
        }

        if (this.isPermanent) {
            team.getPermanentUpgrades().put(this.upgradeName, this.upgrades);
        }

//		p.sendMessage(Utils.translate(this.getPurchaseMessage()).replace("<item>", getDisplayName()));

        for (IUpgrade u : this.upgrades) {

            if (u instanceof EnemyTrapEffect) {

                if (team.getFirstTrap() == null) {
                    u.onActivation(match, p);
                    team.getTraps().add(new QueuedTrap(this, (EnemyTrapEffect) u));
                    continue;
                }

                team.getTraps().add(new QueuedTrap(this, (EnemyTrapEffect) u));
                continue;
            }

            u.onActivation(match, p);
        }

        for (String s : this.increaseTier) {

            TeamUpgrade teamUpgrade = isPlayerUpgrade ? team.getPlayerShopUpgrades().get(p.getUniqueId()).get(s)
                    : (isShop ? team.getShopUpgrades().get(s) : team.getGameUpgrades().get(s));

            if (teamUpgrade == null) {
                continue;
            }

            teamUpgrade.increaseTier();

        }

        for (String s : this.decreaseTier) {

            TeamUpgrade teamUpgrade = isPlayerUpgrade ? team.getPlayerShopUpgrades().get(p.getUniqueId()).get(s)
                    : (isShop ? team.getShopUpgrades().get(s) : team.getGameUpgrades().get(s));

            if (teamUpgrade == null) {
                continue;
            }

            teamUpgrade.decreaseTier();

        }

        giveReward(p, team);
        Utils.removeItem(p, this.priceMaterial, this.price);
        this.isUnlocked = true;
        Utils.playSoundBuy(p);

        return true;
    }

    public void giveReward(Player p, Team team) {

        PermanentItemList permanentItems = team.getPermanentItems(p);

        for (RewardItem rewardItem : this.rewardItem) {

            ItemStack finalItem = rewardItem.getReward().build();
            Material material = finalItem.getType();
            String mName = material.name();

            if (rewardItem.isPermanent()) {
                permanentItems.add(rewardItem);
            }

            if (mName.endsWith("HELMET")) {
                p.getInventory().setHelmet(finalItem);
                team.applyEnchant("HELMET", p);
                continue;
            }

            if (mName.endsWith("CHESTPLATE")) {
                p.getInventory().setChestplate(finalItem);
                team.applyEnchant("CHESTPLATE", p);
                continue;
            }

            if (mName.endsWith("LEGGINGS")) {
                p.getInventory().setLeggings(finalItem);
                team.applyEnchant("LEGGINGS", p);
                continue;
            }

            if (mName.endsWith("BOOTS")) {
                p.getInventory().setBoots(finalItem);
                team.applyEnchant("BOOTS", p);
                continue;
            }

            if (mName.endsWith("SWORD")) {
                team.applyEnchant("SWORD", p);
                p.getInventory().addItem(finalItem);
                Utils.removeItem(p, "_SWORD");
                continue;
            }

            if (mName.endsWith("PICKAXE")) {
                Utils.removeItem(p, "PICKAXE");
            }

            if (mName.endsWith("_AXE")) {
                Utils.removeItem(p, "_AXE");
            }

            if (mName.endsWith("WOOL")) {
                finalItem = team.getTeamColor().getBuildMaterial();
            }

            p.getInventory().addItem(finalItem);

        }
    }

    public ItemBuilder getItemBuilder() {

        return new ItemBuilder(this.displayMaterial, this.amount, this.materialData).setDisplayName(this.name).setLore(this.lore)
                .glowingItem(ManagerHandler.getVersionManager().getVersionWrapper(), this.glowing);
    }

    public ItemStack priceItemStack() {

        return this.priceItemStack;
    }

    public void reset() {
        this.upgrades.forEach(IUpgrade::cancelTask);
        this.isUnlocked = false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (IUpgrade upg : this.upgrades) {

            if (upg instanceof EnemyTrapEffect) {

                EnemyTrapEffect eff = (EnemyTrapEffect) upg;
                builder.append(eff.getActivationRange());
                builder.append(eff.getDecreaseTier());

            }

        }

        return "UpgradeTier [upgrades=" + (builder.toString().isEmpty() ? this.upgrades.toString() : builder.toString())
                + ", isUnlocked=" + this.isUnlocked + ", isPermanent=" + this.isPermanent + ", priceItemStack=" + this.priceItemStack
                + ", upgradeName=" + this.upgradeName + ", displayMaterial=" + this.displayMaterial + ", materialData="
                + this.materialData + ", amount=" + this.amount + ", priceMaterial=" + this.priceMaterial + ", price=" + this.price
                + ", name=" + this.name + ", lore=" + this.lore + ", increseTier=" + this.increaseTier + ", decreaseTier="
                + this.decreaseTier + "]";
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<IUpgrade> getUpgrades() {
        return this.upgrades;
    }

    public boolean isUnlocked() {
        return this.isUnlocked;
    }

    public void setUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public boolean isPermanent() {
        return this.isPermanent;
    }

    public void setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public ItemStack getPriceItemStack() {
        return this.priceItemStack;
    }

    public Material getDisplayMaterial() {
        return this.displayMaterial;
    }

    public int getMaterialData() {
        return this.materialData;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Material getPriceMaterial() {
        return this.priceMaterial;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isDecreaseTierOnDeath() {
        return this.decreaseTierOnDeath;
    }

    public void setDecreaseTierOnDeath(boolean decreaseTierOnDeath) {
        this.decreaseTierOnDeath = decreaseTierOnDeath;
    }

    public boolean isRespawnItems() {
        return this.respawnItems;
    }

    public void setRespawnItems(boolean respawnItems) {
        this.respawnItems = respawnItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<String> getIncreaseTier() {
        return this.increaseTier;
    }

    public void setIncreaseTier(List<String> increaseTier) {
        this.increaseTier = increaseTier;
    }

    public List<String> getDecreaseTier() {
        return this.decreaseTier;
    }

    public void setDecreaseTier(List<String> decreaseTier) {
        this.decreaseTier = decreaseTier;
    }

    public List<RewardItem> getRewardItem() {
        return this.rewardItem;
    }

    public List<Requirement> getRequirements() {
        return this.requirements;
    }

    public void setRequirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }
}
