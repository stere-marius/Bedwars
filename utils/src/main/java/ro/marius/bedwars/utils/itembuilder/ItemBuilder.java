package ro.marius.bedwars.utils.itembuilder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemBuilder implements Cloneable {

    protected ItemStack itemStack;

    @SuppressWarnings("deprecation")
    public ItemBuilder(Material m, int amount, int data) {

        if (amount <= 0) {
            amount = 1;
        }

        this.itemStack = XMaterial.isNewVersion() ? new ItemStack(m, amount) : new ItemStack(m, amount, (byte) data);

    }

    public ItemBuilder(XMaterial material, int amount) {

        this.itemStack = material.parseItem(amount);

    }

    public ItemBuilder(Material material) {

        this.itemStack = new ItemStack(material, 1);

    }

    public ItemBuilder(ItemStack m, int amount) {

        this.itemStack = m;
        this.itemStack.setAmount(amount);

    }

    public ItemBuilder(ItemBuilder builder) {

        this.itemStack = builder.getItemStack().clone();

    }

    public ItemBuilder(ItemStack parseItem) {

        this.itemStack = parseItem;

    }

    public ItemBuilder addEnchant(Enchantment enchant, int value) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.addEnchant(enchant, value, true);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int value, boolean add) {
        if (add) {
            ItemMeta meta = this.itemStack.getItemMeta();
            meta.addEnchant(enchant, value, true);
            this.itemStack.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> map) {

        ItemMeta meta = this.itemStack.getItemMeta();

        for (Entry<Enchantment, Integer> entry : map.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        this.itemStack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder replaceInLore(String replaced, String replaceWith) {

        if (!this.itemStack.hasItemMeta()) {
            return this;
        }

        if (!this.itemStack.getItemMeta().hasLore()) {
            return this;
        }

        List<String> newLore = new ArrayList<>();

        for (String s : this.itemStack.getItemMeta().getLore()) {
            s = s.replace(replaced, replaceWith);
            newLore.add(s);
        }

        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(newLore);
        this.itemStack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder replaceInLore1(String replaced, String replaceWith) {

        if (!this.itemStack.hasItemMeta()) {
            return this;
        }
        if (!this.itemStack.getItemMeta().hasLore()) {
            return this;
        }
        if (this.itemStack.getItemMeta().getLore().isEmpty()) {
            return this;
        }

        List<String> newLore = new ArrayList<>();

        for (String s : this.itemStack.getItemMeta().getLore()) {

            s = s.replace(replaced, replaceWith);
            newLore.add(s);
        }

        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(newLore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeFromLore(String stringToRemove) {

        if (this.itemStack == null) {
            return this;
        }

        if (!this.itemStack.hasItemMeta()) {
            return this;
        }

        if (!this.itemStack.getItemMeta().hasLore()) {
            return this;
        }

        List<String> newLore = new ArrayList<>();

        for (String s : this.itemStack.getItemMeta().getLore()) {

            if (s.equalsIgnoreCase(stringToRemove)) {
                continue;
            }

            s = s.replace(stringToRemove, "");

            newLore.add(s);
        }

        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setLore(newLore);
        this.itemStack.setItemMeta(meta);

        return this;
    }

    public ItemBuilder addEnchantLeather(Enchantment enchant, int value, boolean add) {
        if (add) {
            LeatherArmorMeta lh = (LeatherArmorMeta) this.itemStack.getItemMeta();
            lh.addEnchant(enchant, value, true);
            this.itemStack.setItemMeta(lh);
        }
        return this;
    }

    public ItemBuilder setColorLeather(Color color) {
        LeatherArmorMeta lh = (LeatherArmorMeta) this.itemStack.getItemMeta();
        lh.setColor(color);
        this.itemStack.setItemMeta(lh);
        return this;
    }

    public ItemBuilder addUnsafeEnchant(Enchantment enchant, int value) {
        this.itemStack.addUnsafeEnchantment(enchant, value);
        return this;
    }

    public ItemBuilder setNBTTag(VersionWrapper nmsVersion, String tag, String value) {
        this.itemStack = nmsVersion.setNBTTag(this.itemStack, tag, value);
        return this;
    }

    public ItemBuilder setLore(String... strings) {
        if (strings.length == 0) {
            return this;
        }

        List<String> lore = Arrays.asList(strings);

        ItemMeta meta = this.itemStack.getItemMeta();
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i).replace("&", "§");
            lore.set(i, s);
        }

        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder glowingItem(VersionWrapper nmsVersion) {

        this.itemStack = nmsVersion.addGlow(this.itemStack);

        return this;
    }

    public ItemBuilder glowingItem(VersionWrapper nmsVersion, boolean b) {

        if (!b) {
            return this;
        }

        this.itemStack = nmsVersion.addGlow(this.itemStack);

        return this;
    }

    public ItemBuilder setLore(List<String> strings) {
        if (strings.isEmpty()) {
            return this;
        }

        ItemMeta meta = this.itemStack.getItemMeta();

        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i).replace("&", "§");
            strings.set(i, s);
        }

        meta.setLore(strings);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder replaceItemStack(ItemStack itemStack) {

        itemStack.setItemMeta(this.itemStack.getItemMeta());
        this.itemStack = itemStack;

        return this;
    }

    @Override
    public ItemBuilder clone() {

        return new ItemBuilder(this);
    }

    public String getDisplayName() {

        return this.itemStack.getItemMeta().getDisplayName();
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(Utils.translate(name));
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setUnbreakable(VersionWrapper versionWrapper) {
        versionWrapper.setUnbreakable(this.itemStack);
        return this;
    }

    public ItemBuilder withAmount(int amount){
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

}
