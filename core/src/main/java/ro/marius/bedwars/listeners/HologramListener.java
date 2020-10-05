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
        if (hManager.getConfig().getStringList("StatisticsHologram.Text").isEmpty()) {
            return;
        }

        Player p = e.getPlayer();
        PlayerHologram playerHologram = new PlayerHologram(p, hManager.getLocationHolograms(),
                hManager.getStatsHologramText());

        new BukkitRunnable() {

            @Override
            public void run() {
                playerHologram.spawnAllStatsHologram();
                playerHologram.updateHologram();

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20);

        hManager.getPlayerHologram().put(p, playerHologram);

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {

        Player player = e.getPlayer();
        World world = e.getPlayer().getLocation().getWorld();
        PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram().get(player);

        if (playerHologram == null) {
            HologramManager hManager = ManagerHandler.getHologramManager();
            playerHologram = new PlayerHologram(player, hManager.getLocationHolograms(),
                    hManager.getStatsHologramText());
            hManager.getPlayerHologram().put(player, playerHologram);
        }

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
