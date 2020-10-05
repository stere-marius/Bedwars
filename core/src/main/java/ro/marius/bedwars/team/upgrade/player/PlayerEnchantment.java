package ro.marius.bedwars.team.upgrade.player;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.upgrade.EnchantUpgrade;
import ro.marius.bedwars.team.upgrade.IUpgrade;

import java.util.Map;

public class PlayerEnchantment extends EnchantUpgrade implements IUpgrade {

    private final Map<String, Map<Enchantment, Integer>> enchants;

    public PlayerEnchantment(Map<String, Map<Enchantment, Integer>> enchants) {
        this.enchants = enchants;
    }

    @Override
    public void onActivation(AMatch match, Player p) {

        if (this.enchants.containsKey("SWORD")) {

//			Map<Enchantment, Integer> enchants = this.enchants.get("SWORD");

            PlayerInventory pi = p.getInventory();

            for (int i = 0; i < 36; i++) {

                ItemStack item = pi.getItem(i);

                if (item == null) {
                    continue;
                }
                if (!item.getType().name().contains("SWORD")) {
                    continue;
                }

                item.addEnchantment(Enchantment.DAMAGE_ALL, 0);
//				item.addEnchantments(enchants);
                
            }

        }

        if (this.enchants.containsKey("ARMOR")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("ARMOR");

            PlayerInventory pi = p.getInventory();
            ItemStack helmet = pi.getHelmet();
            ItemStack chestplate = pi.getChestplate();
            ItemStack leggings = pi.getLeggings();
            ItemStack boots = pi.getBoots();

            if (helmet != null) {
                helmet.addEnchantments(enchants);
            }
            if (chestplate != null) {
                chestplate.addEnchantments(enchants);
            }
            if (leggings != null) {
                leggings.addEnchantments(enchants);
            }
            if (boots != null) {
                boots.addEnchantments(enchants);
            }

            return;

        }

        if (this.enchants.containsKey("BOOTS")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("BOOTS");

            ItemStack boots = p.getInventory().getBoots();

            if (boots == null) {
                return;
            }

            boots.addEnchantments(enchants);

        }

        if (this.enchants.containsKey("LEGGINGS")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("LEGGINGS");

            ItemStack leggings = p.getInventory().getLeggings();

            if (leggings == null) {
                return;
            }

            leggings.addEnchantments(enchants);

        }

        if (this.enchants.containsKey("CHESTPLATE")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("CHESTPLATE");

            ItemStack chestplate = p.getInventory().getChestplate();

            if (chestplate == null) {
                return;
            }

            chestplate.addEnchantments(enchants);

        }

        if (this.enchants.containsKey("HELMET")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("HELMET");

            ItemStack helmet = p.getInventory().getHelmet();

            if (helmet == null) {
                return;
            }

            helmet.addEnchantments(enchants);

        }

    }

    @Override
    public void onActivation(String part, Player p) {

        if ("SWORD".equalsIgnoreCase(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("SWORD");

            if (enchants == null) {
                return;
            }

            PlayerInventory pi = p.getInventory();

            for (int i = 0; i < pi.getSize(); i++) {
                ItemStack item = pi.getItem(i);
                if (item == null) {
                    continue;
                }

                if (!item.getType().name().endsWith("_SWORD")) {
                    continue;
                }

                item.addEnchantment(Enchantment.DAMAGE_ALL, 1);

            }

            return;
        }

        if ("BOOTS".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("BOOTS");

            if (enchants == null) {
                return;
            }

            ItemStack boots = p.getInventory().getBoots();

            if (boots == null) {
                return;
            }

            boots.addEnchantments(enchants);

            return;

        }

        if ("LEGGINGS".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("LEGGINGS");

            if (enchants == null) {
                return;
            }

            ItemStack leggings = p.getInventory().getLeggings();

            if (leggings == null) {
                return;
            }

            leggings.addEnchantments(enchants);

            return;
        }

        if ("CHESTPLATE".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("CHESTPLATE");

            if (enchants == null) {
                return;
            }

            ItemStack chestplate = p.getInventory().getChestplate();

            if (chestplate == null) {
                return;
            }

            chestplate.addEnchantments(enchants);

            return;
        }

        if ("HELMET".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("HELMET");

            if (enchants == null) {
                return;
            }

            ItemStack helmet = p.getInventory().getHelmet();

            if (helmet == null) {
                return;
            }

            helmet.addEnchantments(enchants);

        }

    }

    @Override
    public void cancelTask() {

    }

    @Override
    public IUpgrade clone() {

        return new PlayerEnchantment(this.enchants);
    }

}
