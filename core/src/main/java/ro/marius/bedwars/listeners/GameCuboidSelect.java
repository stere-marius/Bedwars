package ro.marius.bedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.GameSetup;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;

public class GameCuboidSelect implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

        if (gameSetup == null) {
            return;
        }

        ItemStack handItem = p.getItemInHand();

        if (handItem == null) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        if (!handItem.hasItemMeta()) {
            return;
        }
        if (!handItem.getItemMeta().getDisplayName().equals(Utils.translate("&aArena selector"))) {
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = e.getClickedBlock().getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY() - 1;
            int z = loc.getBlockZ();
            gameSetup.setPositionTwo(loc);
            p.sendMessage(Utils.translate("&eYou have set the &d#2 &ecorner at &a" + x + " , " + y + " , " + z));
            e.setCancelled(true);
        }

        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location loc = e.getClickedBlock().getLocation();
            int x = loc.getBlockX();
            int y = loc.getBlockY() - 1;
            int z = loc.getBlockZ();
            gameSetup.setPositionOne(loc);
            p.sendMessage(Utils.translate("&eYou have set the &d#1 &ecorner at &a" + x + " , " + y + " , " + z));
            e.setCancelled(true);
        }

        Bukkit.getScheduler().runTaskLater(BedWarsPlugin.getInstance(), () -> {
            if (gameSetup.getPositionOne() != null && gameSetup.getPositionTwo() != null) {
                gameSetup.performCuboidSelection();
            }
        }, 10L);

    }

}
