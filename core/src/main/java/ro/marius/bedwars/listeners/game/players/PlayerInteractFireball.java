package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.ServerVersion;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;

public class PlayerInteractFireball implements Listener {

    private final Material FIREBALL_MATERIAL;

    public PlayerInteractFireball() {
        this.FIREBALL_MATERIAL = XMaterial.FIRE_CHARGE.parseMaterial();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType() != this.FIREBALL_MATERIAL) {
            return;
        }


        Location eyeLocation = p.getEyeLocation();
        Location loc = eyeLocation.add(eyeLocation.getDirection().multiply(1.2));
        Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
        fireball.setVelocity(loc.getDirection().normalize().multiply(2));
        fireball.setYield(1.5f);
//        fireball.setIsIncendiary(false);
        fireball.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        fireball.setMetadata("Owner", new FixedMetadataValue(BedWarsPlugin.getInstance(), p.getName()));
        fireball.setShooter(p);
        match.getMatchEntity().add(fireball);

        int amount = e.getItem().getAmount();

        if (amount > 1) {
            e.getItem().setAmount(amount - 1);
            e.setCancelled(true);
            return;
        }

        Utils.decreaseItemAmountFromHand(p, e.getItem());
        e.setCancelled(true);
    }

}
