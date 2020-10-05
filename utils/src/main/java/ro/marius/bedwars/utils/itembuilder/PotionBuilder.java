package ro.marius.bedwars.utils.itembuilder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionBuilder extends ItemBuilder {

    public PotionBuilder(int amount) {
        super(new ItemStack(Material.POTION), amount);
    }

    public PotionBuilder setPotionMeta() {
        PotionMeta potionMeta = (PotionMeta) this.getItemStack().getItemMeta();
        this.getItemStack().setItemMeta(potionMeta);

        return this;
    }

    public PotionBuilder addEffect(PotionEffect effect) {
        PotionMeta potionMeta = (PotionMeta) this.getItemStack().getItemMeta();
        potionMeta.addCustomEffect(effect, true);
        this.getItemStack().setItemMeta(potionMeta);

        return this;
    }

    public PotionBuilder addEffectType(PotionEffectType type, int duration, int amplifier) {

        PotionMeta potionMeta = (PotionMeta) this.getItemStack().getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
        this.getItemStack().setItemMeta(potionMeta);

        return this;
    }


}
