package ro.marius.bedwars.requirements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.action.BuyItemAction;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class RequirementBuilder {

    private String displayName;
    private Material displayMaterial;
    private int displayAmount;
    private int displayData;
    private boolean displayGlowing;
    private Material priceMaterial;
    private int price;
    private List<String> displayLore = new ArrayList<>();

    public RequirementBuilder() {

    }

    @Override
    public RequirementBuilder clone() {

        RequirementBuilder reqBuilder = new RequirementBuilder();

        if (this.displayName != null) {
            reqBuilder.withDisplayName(this.displayName);
        }

        if (this.displayMaterial != null) {
            reqBuilder.withDisplayMaterial(this.displayMaterial);
        }

        if (this.displayAmount != 0) {
            reqBuilder.withDisplayAmount(this.displayAmount);
        }

        if (this.displayData != 0) {
            reqBuilder.withDisplayData(this.displayData);
        }

        if (this.displayGlowing) {
            reqBuilder.glowing();
        }

        if (this.priceMaterial != null) {
            reqBuilder.withPriceMaterial(this.priceMaterial);
        }

        if (this.price > 0) {
            reqBuilder.withPrice(this.price);
        }

        if ((this.displayLore != null) && !this.displayLore.isEmpty()) {
            reqBuilder.withDisplayLore(new ArrayList<String>(this.displayLore));
        }

        return reqBuilder;
    }

    public RequirementBuilder withDisplayName(String displayName) {

        this.displayName = displayName;

        return this;
    }

    public RequirementBuilder withDisplayMaterial(Material displayMaterial) {

        this.displayMaterial = displayMaterial;

        return this;
    }

    public RequirementBuilder withDisplayAmount(int amount) {

        this.displayAmount = amount;

        return this;
    }

    public RequirementBuilder withDisplayData(int data) {

        this.displayData = data;

        return this;
    }

    public RequirementBuilder glowing() {

        this.displayGlowing = true;

        return this;
    }

    public RequirementBuilder withPriceMaterial(Material material) {

        this.priceMaterial = material;

        return this;
    }

    public RequirementBuilder withPrice(int price) {

        this.price = price;

        return this;
    }

    public RequirementBuilder withDisplayLore(List<String> lore) {

        this.displayLore = lore;

        return this;
    }

    public void apply(UpgradeTier upgradeTier) {

        ItemBuilder builder = upgradeTier.getItemBuilder();
        ItemStack itemStack = builder.getItemStack();

        if (this.displayName != null) {
            builder.setDisplayName(this.displayName);
        }

        if (this.displayMaterial != null) {
            itemStack.setType(this.displayMaterial);
        }

        if (this.displayAmount > 0) {
            itemStack.setAmount(this.displayAmount);
        }

        if (this.displayGlowing) {
            builder.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
        }

        if (!this.displayLore.isEmpty()) {
            builder.setLore(this.displayLore);
        }
    }

    @SuppressWarnings("deprecation")
    public void apply(InventoryIcon inventoryIcon) {

        ItemBuilder builder = inventoryIcon.getItemBuilder();
        ItemStack itemStack = builder.getItemStack();

        if (this.displayName != null) {
            builder.setDisplayName(this.displayName);
        }

        if (this.displayMaterial != null) {
            itemStack.setType(this.displayMaterial);
        }

        if (this.displayAmount > 0) {
            itemStack.setAmount(this.displayAmount);
        }

        if (this.displayData >= 0) {
            itemStack.setDurability((short) this.displayData);
        }

        if (this.displayGlowing) {
            builder.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
        }

        if (!this.displayLore.isEmpty()) {
            builder.setLore(this.displayLore);
        }

        if (inventoryIcon.getClickAction().isEmpty()) {
            return;
        }

        IconAction clickAction = inventoryIcon.getClickAction().get(0);

        if (!(clickAction instanceof BuyItemAction)) {
            return;
        }

        BuyItemAction buyAction = (BuyItemAction) clickAction;

        if (this.priceMaterial != null) {
            buyAction.setPriceMaterial(this.priceMaterial);
        }

        if (this.price > 0) {
            buyAction.setPrice(this.price);
        }

    }

}
