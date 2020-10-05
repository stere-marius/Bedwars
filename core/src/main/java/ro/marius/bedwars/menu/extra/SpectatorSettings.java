package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchSpectator;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

public class SpectatorSettings extends ExtraInventory {

    private Player player;
    private AMatch match;
    private MatchSpectator matchSpectator;
    private Inventory inventory;

    public SpectatorSettings(Player player, AMatch match, MatchSpectator matchSpectator) {
        this.player = player;
        this.match = match;
        this.matchSpectator = matchSpectator;
    }

    @Override
    public Inventory getInventory() {

        if (this.inventory != null) {
            return this.inventory;
        }

        Inventory inv = Bukkit.createInventory(this, 36, Utils.translate("&7Spectator Settings"));

        ItemBuilder leatherBoots = new ItemBuilder(XMaterial.LEATHER_BOOTS.parseMaterial())
                .setDisplayName("&aNo Speed");
        ItemBuilder chainBoots = new ItemBuilder(XMaterial.CHAINMAIL_BOOTS.parseMaterial()).setDisplayName("&aSpeed I");
        ItemBuilder ironBoots = new ItemBuilder(XMaterial.IRON_BOOTS.parseMaterial()).setDisplayName("&aSpeed II");
        ItemBuilder goldBoots = new ItemBuilder(XMaterial.GOLDEN_BOOTS.parseMaterial()).setDisplayName("&aSpeed III");
        ItemBuilder diamondBoots = new ItemBuilder(XMaterial.DIAMOND_BOOTS.parseMaterial())
                .setDisplayName("&aSpeed IV");

        inv.setItem(11, leatherBoots.build());
        inv.setItem(12, chainBoots.build());
        inv.setItem(13, ironBoots.build());
        inv.setItem(14, goldBoots.build());
        inv.setItem(15, diamondBoots.build());

        ItemBuilder autoTeleport = new ItemBuilder(XMaterial.COMPASS.parseMaterial())
                .setDisplayName("&aEnable Auto Teleport").setLore("&7Click to enable auto teleport!");
        ItemBuilder nightVision = new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial())
                .setDisplayName("&aEnable night vision").setLore("&7Click to enable night vision!");
        ItemBuilder alwaysFlying = new ItemBuilder(XMaterial.FEATHER.parseMaterial())
                .setDisplayName("&aEnable always flying").setLore("&7Click to enable always flying.");

        inv.setItem(21, autoTeleport.build());
        inv.setItem(22, nightVision.build());
        inv.setItem(23, alwaysFlying.build());

        this.inventory = inv;

        return inv;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        e.setCancelled(true);

        if (slot == 11) {
            p.removePotionEffect(PotionEffectType.SPEED);
            return;
        }

        if (slot == 12) {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0));
            return;
        }

        if (slot == 13) {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1));
            return;
        }

        if (slot == 14) {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2));
            return;
        }

        if (slot == 15) {
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3));
            return;
        }

        if (slot == 21) {

            this.matchSpectator.setAutoTeleport(!this.matchSpectator.isAutoTeleport());
            ItemBuilder autoTeleport = new ItemBuilder(XMaterial.COMPASS.parseMaterial())
                    .setDisplayName(this.matchSpectator.isAutoTeleport() ? "&cDisable" : ("&aEnable" + " Auto Teleport"))
                    .setLore("&7Click to " + (this.matchSpectator.isAutoTeleport() ? "disable" : "enable")
                            + " auto teleport!");
            this.getInventory().setItem(21, autoTeleport.build());

            return;
        }

        if (slot == 22) {

            this.matchSpectator.setNightVision(!this.matchSpectator.isNightVision());
            ItemBuilder nightVision = new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial())
                    .setDisplayName(this.matchSpectator.isNightVision() ? "&cDisable" : ("&aEnable" + " night vision"))
                    .setLore(
                            "&7Click to " + (this.matchSpectator.isNightVision() ? "disable" : "enable") + " night vision!");
            this.getInventory().setItem(22, nightVision.build());

            return;
        }

        if (slot == 23) {

            this.matchSpectator.setFly(!this.matchSpectator.isFly());
            ItemBuilder alwaysFlying = new ItemBuilder(XMaterial.FEATHER.parseMaterial())
                    .setDisplayName(this.matchSpectator.isNightVision() ? "&cDisable" : ("&aEnable" + " always flying"))
                    .setLore(
                            "&7Click to " + (this.matchSpectator.isNightVision() ? "disable" : "enable") + " always flying!");
            this.getInventory().setItem(23, alwaysFlying.build());

        }

    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

}
