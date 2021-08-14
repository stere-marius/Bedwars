package ro.marius.bedwars;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public abstract class PotionWrapper {

    protected ItemStack itemStack;

    public PotionWrapper(String potionMaterialType){
        this.itemStack = new ItemStack(Material.valueOf(potionMaterialType));
    }

    // new ItemBuilder(PotionWrapper.toItemStack())

    public void addEffect(PotionEffect effect) {
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.addCustomEffect(effect, true);
        itemStack.setItemMeta(potionMeta);
    }

    public void addEffectType(PotionEffectType type, int duration, int amplifier) {
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
        itemStack.setItemMeta(potionMeta);
    }

    public void setBasePotionData(PotionType potionType){
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(potionType));
        itemStack.setItemMeta(potionMeta);
    }

    public ItemStack toItemStack() {
        return itemStack;
    }


}
