package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.mechanics.PlayerInvisibility;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class PlayerDrinkPotions implements Listener {

//	private final PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 45, 0);

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent e) {

        ItemStack item = e.getItem();
        Player p = e.getPlayer();

//		if (!item.hasItemMeta())
//			return;
//		if (!item.getItemMeta().hasDisplayName())
//			return;

        if (!(item.getItemMeta() instanceof PotionMeta)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        e.setCancelled(true);
        ManagerHandler.getVersionManager().getVersionWrapper().deleteItemInHand(p, item);

        PotionMeta meta = (PotionMeta) item.getItemMeta();

        PotionEffect invisibility = null;

        for (PotionEffect eff : meta.getCustomEffects()) {

            if (!"INVISIBILITY".equals(eff.getType().getName())) {
                continue;
            }

            invisibility = eff;
        }

        if (invisibility == null) {
            meta.getCustomEffects().forEach(p::addPotionEffect);
            return;
        }


        p.addPotionEffect(invisibility);

        Bukkit.getScheduler().scheduleSyncDelayedTask(BedWarsPlugin.getInstance(), () -> {
            PlayerInvisibility playerInv = new PlayerInvisibility(match, p);
            playerInv.putInvisibility();
            playerInv.runTaskRemove();
            match.getInvisibility().put(p, playerInv);
        });

    }

}
