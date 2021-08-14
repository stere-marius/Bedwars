package ro.marius.bedwars.utils.itembuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;
import ro.marius.bedwars.utils.ServerVersion;
import ro.marius.bedwars.utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PotionBuilder extends ItemBuilder {

    public static final ServerVersion SERVER_VERSION;

    static {
        SERVER_VERSION = ServerVersion.valueOf("v" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1));
    }

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

    public PotionBuilder setPotionBaseType(PotionType potionType) {

        if(!Utils.SERVER_VERSION.isGreaterThan(ServerVersion.v1_8_R3)){
            Class<?> potionClass;
            try {
                potionClass = Class.forName("org.bukkit.potion.Potion");
                Constructor<?> constructor = potionClass.getConstructor(PotionType.class, int.class);
                Object potion = constructor.newInstance(potionType, 1);
                itemStack = (ItemStack) potionClass.getMethod("toItemStack", int.class).invoke(potion, itemStack.getAmount());
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return this;
        }

        PotionMeta potionMeta = (PotionMeta) this.getItemStack().getItemMeta();

        if(!potionMeta.getCustomEffects().isEmpty()){

        }

        this.getItemStack().setItemMeta(potionMeta);
        return this;
    }


}
