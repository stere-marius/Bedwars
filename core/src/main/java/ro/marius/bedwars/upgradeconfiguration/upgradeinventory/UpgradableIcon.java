package ro.marius.bedwars.upgradeconfiguration.upgradeinventory;

import org.bukkit.Material;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

class UpgradableIcon extends InventoryIcon {

    private final String optionalUpgrade;
    private final String CLICK_TO_PURCHASE = Lang.CLICK_TO_PURCHASE.getString();
    private final String NOT_ENOUGH_RESOURCES = Lang.NOT_ENOUGH_RESOURCES.getString();

    public UpgradableIcon(String path, String optionalUpgrade) {
        super(path, new ItemBuilder(Material.STONE));
        this.optionalUpgrade = optionalUpgrade;

    }

    @Override
    public ItemBuilder getItemBuilder() {

        if (this.getTeam() == null) {
            return super.getItemBuilder();
        }

        if (this.getPlayer() == null) {
            return super.getItemBuilder();
        }

        TeamUpgrade upg = this.getTeam().getGameUpgrades().get(this.optionalUpgrade);

        if (upg == null) {
            return super.getItemBuilder();
        }

        UpgradeTier upgradeTier = upg.getCurrentTier();
        ItemBuilder uBuilder = upgradeTier.getItemBuilder();

        int price = upgradeTier.getPrice();

        uBuilder.replaceInLore("<hasEnoughResources>",
                this.getPlayer().getInventory().containsAtLeast(upgradeTier.getPriceItemStack(), upgradeTier.getPrice())
                        ? this.CLICK_TO_PURCHASE
                        : this.NOT_ENOUGH_RESOURCES);
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
