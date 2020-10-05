package ro.marius.bedwars.shopconfiguration.shopinventory;

import org.bukkit.Material;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.Map;

public class UpgradableIcon extends InventoryIcon {

    private final String optionalUpgrade;
    private static final String CLICK_TO_PURCHASE = Lang.CLICK_TO_PURCHASE.getString();
    private static final String NOT_ENOUGH_RESOURCES = Lang.NOT_ENOUGH_RESOURCES.getString();

    public UpgradableIcon(String path, String optionalUpgrade) {
        super(path, new ItemBuilder(Material.STONE));
        this.optionalUpgrade = optionalUpgrade;
    }

    @Override
    public ItemBuilder getItemBuilder() {

        if (this.getPlayer() == null) {
            return new ItemBuilder(Material.STONE).setDisplayName("NULL PLAYER");
        }

        if (this.getTeam() == null) {
            return new ItemBuilder(Material.STONE).setDisplayName("NULL TEAM");
        }

        if (this.getTeam().getPlayerShopUpgrades() == null) {
            return new ItemBuilder(Material.STONE).setDisplayName("NULL getTeamAlive().getPlayerShopUpgrades()");
        }

        Map<String, TeamUpgrade> teamUpgrade = this.getTeam().getPlayerShopUpgrades().get(this.getPlayer().getUniqueId());

        TeamUpgrade upg = teamUpgrade.get(this.optionalUpgrade);

        if (upg == null) {
            return super.getItemBuilder();
        }

        UpgradeTier upgradeTier = upg.getCurrentTier();
        ItemBuilder uBuilder = upgradeTier.getItemBuilder();
        int price = upgradeTier.getPrice();

        uBuilder.replaceInLore("<hasEnoughResources>",
                this.getPlayer().getInventory().containsAtLeast(upgradeTier.getPriceItemStack(), upgradeTier.getPrice())
                        ? CLICK_TO_PURCHASE
                        : NOT_ENOUGH_RESOURCES);
        uBuilder.replaceInLore("<cost>", price + "");

        return uBuilder;
    }

    @Override
    public InventoryIcon clone() {

        UpgradableIcon ic = new UpgradableIcon(super.getPath(), this.optionalUpgrade);
        ic.setItemBuilder(new ItemBuilder(Material.STONE));
        ic.setClickAction(this.getClickAction());
        ic.setRequirement(this.getRequirement());

        return ic;
    }

}
