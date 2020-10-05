package ro.marius.bedwars.listeners.playerdata;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.SQLData;

public class PlayerDataQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        new BukkitRunnable() {

            @Override
            public void run() {

                APlayerData data = ManagerHandler.getGameManager().getPlayerData().get(e.getPlayer().getUniqueId());

                if (data == null) {
                    this.cancel();
                    return;
                }

                if (data instanceof SQLData) {
                    ((SQLData) data).saveQuickBuy();
                }

                data.saveData();

            }
        }.runTaskLaterAsynchronously(BedWarsPlugin.getInstance(), 20);

        BedWarsPlugin.getPartyHandler().leave(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerKickEvent e) {

        APlayerData data = ManagerHandler.getGameManager().getPlayerData().get(e.getPlayer().getUniqueId());

        if (data == null) {
            return;
        }

        if (data instanceof SQLData) {
            ((SQLData) data).saveQuickBuy();
        }

        data.saveData();
        BedWarsPlugin.getPartyHandler().leave(e.getPlayer());

    }

}
