package ro.marius.bedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.SQLData;
import ro.marius.bedwars.utils.PlayerHologram;

public class RestartListener implements Listener {

    private boolean isSaved = false;

    @EventHandler
    public void onCommand(ServerCommandEvent e) {

        String command = e.getCommand();

        if (this.isSaved) {
            return;
        }

        if ("restart".equalsIgnoreCase(command) || "rl".equalsIgnoreCase(command) || "reload".equalsIgnoreCase(command)
                || "stop".equalsIgnoreCase(command) || "reboot".equalsIgnoreCase(command)) {
            e.getSender().sendMessage(
                    "[Bedwars] Command has been cancelled. It will get executed when the bedwars data will be saved");
            this.isSaved = true;

            for (APlayerData data : ManagerHandler.getGameManager().getPlayerData().values()) {

                if (data instanceof SQLData) {
                    SQLData sqlData = (SQLData) data;
                    sqlData.saveQuickBuy();
                }

            }

            ManagerHandler.getWorldManager().copyWorldFiles();
            this.removeHologram();

            Bukkit.dispatchCommand(e.getSender(), command);

            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onPreprocess(PlayerCommandPreprocessEvent e) {

        Player p = e.getPlayer();
        String command = e.getMessage().replace("/", "");

        if (this.isSaved) {
            return;
        }

        if ("stop".equalsIgnoreCase(command) && p.hasPermission("minecraft.command.stop")) {
            e.setCancelled(true);
            p.sendMessage(
                    "[Bedwars] Command has been cancelled. It will get executed when the bedwars data will be saved");
            this.isSaved = true;

            for (APlayerData data : ManagerHandler.getGameManager().getPlayerData().values()) {

                if (data instanceof SQLData) {
                    SQLData sqlData = (SQLData) data;
                    sqlData.saveQuickBuy();
                }

            }

            ManagerHandler.getWorldManager().copyWorldFiles();
            this.removeHologram();

            p.performCommand("stop");
            return;
        }

        if ("rl".equalsIgnoreCase(command) && p.hasPermission("bukkit.command.reload")) {
            e.setCancelled(true);
            p.sendMessage(
                    "[Bedwars] Command has been cancelled. It will get executed when the bedwars data will be saved");
            this.isSaved = true;

            for (APlayerData data : ManagerHandler.getGameManager().getPlayerData().values()) {

                if (data instanceof SQLData) {
                    SQLData sqlData = (SQLData) data;
                    sqlData.saveQuickBuy();
                }

            }

            ManagerHandler.getWorldManager().copyWorldFiles();
            this.removeHologram();

            p.performCommand("rl");
            return;
        }

        if ("reload".equalsIgnoreCase(command) && p.hasPermission("bukkit.command.reload")) {
            e.setCancelled(true);
            p.sendMessage(
                    "[Bedwars] Command has been cancelled. It will get executed when the bedwars data will be saved");
            this.isSaved = true;

            for (APlayerData data : ManagerHandler.getGameManager().getPlayerData().values()) {

                if (data instanceof SQLData) {
                    SQLData sqlData = (SQLData) data;
                    sqlData.saveQuickBuy();
                }

            }

            ManagerHandler.getWorldManager().copyWorldFiles();
            this.removeHologram();

            p.performCommand("reload");
            return;
        }

        if ("restart".equalsIgnoreCase(command) && p.hasPermission("bukkit.command.restart")) {
            e.setCancelled(true);
            p.sendMessage(
                    "[Bedwars] Command has been cancelled. It will get executed when the bedwars data will be saved");
            this.isSaved = true;

            for (APlayerData data : ManagerHandler.getGameManager().getPlayerData().values()) {

                if (data instanceof SQLData) {
                    SQLData sqlData = (SQLData) data;
                    sqlData.saveQuickBuy();
                }

            }

            ManagerHandler.getWorldManager().copyWorldFiles();
            this.removeHologram();

            p.performCommand("restart");
            return;
        }

    }

    public void removeHologram() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram().get(p);
            playerHologram.removeHologram();
        }
    }

}
