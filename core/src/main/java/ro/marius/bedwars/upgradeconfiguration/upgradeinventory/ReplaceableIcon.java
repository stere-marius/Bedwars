package ro.marius.bedwars.upgradeconfiguration.upgradeinventory;

import org.bukkit.Material;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

class ReplaceableIcon extends InventoryIcon {

    private final String replaceWith;

    public ReplaceableIcon(String path, String replaceWith, ItemBuilder itemBuilder) {
        super(path, itemBuilder);
        this.replaceWith = replaceWith.toUpperCase();
    }

    @Override
    public ItemBuilder getItemBuilder() {

        if (this.replaceWith == null) {
            return super.getItemBuilder();
        }

        if (this.replaceWith.isEmpty()) {
            return super.getItemBuilder();
        }
//		
//		
//		if(getTeamAlive() == null) {
//			Bukkit.broadcastMessage("getTeamAlive() == null");
//		}
//		
//		if(getTeamAlive().getGameUpgrades() == null) {
//			Bukkit.broadcastMessage("getTeamAlive().getGameUpgrades() == null");
//		}

        if (this.replaceWith.startsWith("ENEMYTRAP")) {

            int index = 0;

            if (this.replaceWith.contains("_") && (this.replaceWith.split("_").length >= 3)) {

                index = Integer.parseInt(this.replaceWith.split("_")[2]) - 1;

            }

            UpgradeTier tier = this.getTeam().getTrapTier(Math.max(index, 0));

            if (tier != null) {
                return tier.getItemBuilder().removeFromLore("<hasEnoughResources>");
            } else {
                return super.getItemBuilder();
            }

        }

        if (this.getTeam().getGameUpgrades().get(this.replaceWith) == null) {
            return new ItemBuilder(Material.STONE)
                    .setDisplayName("COULDN'T FIND UPGRADE NAME " + this.replaceWith + " FOR REPLACING");
        }

        return super.getItemBuilder();
    }

    @Override
    public InventoryIcon clone() {

        ReplaceableIcon ic = new ReplaceableIcon(this.getPath(), this.replaceWith, new ItemBuilder(super.getItemBuilder()));
        ic.setClickAction(this.getClickAction());
        ic.setRequirement(this.getRequirement());

        return ic;
    }

    public String getReplaceWith() {
        return this.replaceWith;
    }
}
