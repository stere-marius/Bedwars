package ro.marius.bedwars.team.upgrade.team;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.upgrade.EnchantUpgrade;
import ro.marius.bedwars.team.upgrade.IUpgrade;

import java.util.Map;

public class TeamEnchantment extends EnchantUpgrade implements IUpgrade {

    private final Map<String, Map<Enchantment, Integer>> enchants;

    public TeamEnchantment(Map<String, Map<Enchantment, Integer>> enchants) {
        this.enchants = enchants;
    }

    @Override
    public void onActivation(AMatch match, Player p) {

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        if (this.enchants.containsKey("SWORD")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("SWORD");

            for (Player player : team.getPlayers()) {

                PlayerInventory pi = player.getInventory();

                for (int i = 0; i < pi.getSize(); i++) {
                    ItemStack item = pi.getItem(i);

                    if (item == null) {
                        continue;
                    }

                    if (!item.getType().name().endsWith("_SWORD")) {
                        continue;
                    }

                    item.addEnchantments(enchants);

                }
            }
        }

        if (this.enchants.containsKey("BOOTS")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("BOOTS");

            for (Player player : team.getPlayers()) {

                ItemStack boots = player.getInventory().getBoots();

                if (boots == null) {
                    return;
                }

                boots.addEnchantments(enchants);
            }

        }

        if (this.enchants.containsKey("LEGGINGS")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("LEGGINGS");

            for (Player player : team.getPlayers()) {

                ItemStack leggings = player.getInventory().getLeggings();

                if (leggings == null) {
                    return;
                }

                leggings.addEnchantments(enchants);

                player.getInventory().setLeggings(leggings);

            }

        }

        if (this.enchants.containsKey("CHESTPLATE")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("CHESTPLATE");

            for (Player player : team.getPlayers()) {

                ItemStack chestplate = player.getInventory().getChestplate();

                if (chestplate == null) {
                    return;
                }

                chestplate.addEnchantments(enchants);

            }

        }

        if (this.enchants.containsKey("HELMET")) {

            Map<Enchantment, Integer> enchants = this.enchants.get("HELMET");

            for (Player player : team.getPlayers()) {

                ItemStack helmet = player.getInventory().getHelmet();

                if (helmet == null) {
                    return;
                }

                helmet.addEnchantments(enchants);

            }

        }

    }

    @Override
    public void cancelTask() {

    }

    @Override
    public IUpgrade clone() {

        return new TeamEnchantment(this.enchants);
    }

    @Override
    public void onActivation(String part, Player p) {

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        if ("SWORD".equalsIgnoreCase(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("SWORD");

            if (enchants == null) {
                return;
            }

            for (Player player : team.getPlayers()) {

                PlayerInventory pi = player.getInventory();

                for (int i = 0; i < pi.getSize(); i++) {
                    ItemStack item = pi.getItem(i);

                    if (item == null) {
                        continue;
                    }

                    if (!item.getType().name().endsWith("_SWORD")) {
                        continue;
                    }

                    item.addEnchantments(enchants);

                }
            }

            return;
        }

        if ("BOOTS".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("BOOTS");

            if (enchants == null) {
                return;
            }

            for (Player player : team.getPlayers()) {

                ItemStack boots = player.getInventory().getBoots();

                if (boots == null) {
                    return;
                }

                boots.addEnchantments(enchants);
            }

            return;

        }

        if ("LEGGINGS".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("LEGGINGS");

            if (enchants == null) {
                return;
            }

            for (Player player : team.getPlayers()) {

                ItemStack leggings = player.getInventory().getLeggings();

                if (leggings == null) {
                    return;
                }

                leggings.addEnchantments(enchants);

            }

            return;
        }

        if ("CHESTPLATE".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("CHESTPLATE");

            if (enchants == null) {
                return;
            }

            for (Player player : team.getPlayers()) {

                ItemStack chestplate = player.getInventory().getChestplate();

                if (chestplate == null) {
                    return;
                }

                chestplate.addEnchantments(enchants);

            }

            return;
        }

        if ("HELMET".equals(part)) {

            Map<Enchantment, Integer> enchants = this.enchants.get("HELMET");

            if (enchants == null) {
                return;
            }

            for (Player player : team.getPlayers()) {

                ItemStack helmet = player.getInventory().getHelmet();

                if (helmet == null) {
                    return;
                }

                helmet.addEnchantments(enchants);

            }

        }

    }

}
