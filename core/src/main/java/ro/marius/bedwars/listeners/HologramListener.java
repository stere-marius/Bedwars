package ro.marius.bedwars.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.HologramManager;
import ro.marius.bedwars.utils.PlayerHologram;

public class HologramListener implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        HologramManager hManager = ManagerHandler.getHologramManager();

        if (hManager.getConfig() == null) {
            return;
        }
        if (hManager.getStatsHologramText().isEmpty()) {
            return;
        }

        Player p = e.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram(p);
                playerHologram.removeHologram();
                playerHologram.spawnWorldHolograms(p.getWorld());
                playerHologram.updateHologram();
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20);

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World world = player.getLocation().getWorld();
        PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram(player);
        playerHologram.removeHologram();
        playerHologram.spawnWorldHolograms(world);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram().get(player);

        if (playerHologram == null) {
            return;
        }

        playerHologram.removeHologram();
        ManagerHandler.getHologramManager().getPlayerHologram().remove(player);
    }

}
