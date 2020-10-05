package ro.marius.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!BedWarsPlugin.getInstance().getConfig().getBoolean("onJoin.TeleportToLobbyLocation"))
            return;
        if (ManagerHandler.getGameManager().getRejoin(e.getPlayer()) != null)
            return;

        Utils.teleportToLobby(e.getPlayer(), BedWarsPlugin.getInstance());
    }
}
