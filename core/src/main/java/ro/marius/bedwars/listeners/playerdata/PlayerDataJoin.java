package ro.marius.bedwars.listeners.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.FileData;
import ro.marius.bedwars.playerdata.SQLData;

import java.util.UUID;

public class PlayerDataJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        new BukkitRunnable() {

            @Override
            public void run() {

                APlayerData playerData = ManagerHandler.getGameManager().getPlayerData().get(uuid);

                if (playerData == null) {
                    playerData = BedWarsPlugin.getInstance().isSQLEnabled() ? new SQLData(p) : new FileData(p);
                }

                playerData.loadData();
                ManagerHandler.getGameManager().getPlayerData().put(uuid, playerData);

            }
        }.runTaskLaterAsynchronously(BedWarsPlugin.getInstance(), 1);

    }

}
