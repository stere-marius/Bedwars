package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.gameobserver.SocketObserver;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.socketclient.ClientSocket;

import java.util.UUID;

public class SocketManager {

    private final UUID UUID = java.util.UUID.randomUUID();
    private ClientSocket socket;
    private final String serverName = "";

    public SocketManager() {
        this.setupSocket();
        this.registerGameSocketObservers();
    }

    public void registerGameSocketObservers() {
        ManagerHandler
                .getGameManager()
                .getGames()
                .forEach(game -> game.registerObserver(new SocketObserver(game)));
    }

    public void unregisterGameSocketObservers() {
        ManagerHandler
                .getGameManager()
                .getGames()
                .forEach(game -> game.removeObserver(new SocketObserver(game)));
    }

    public void setupSocket() {

        if (!BedWarsPlugin.getInstance().getConfig().getBoolean("BungeeCord.Enabled")) {
            return;
        }
        if (!BedWarsPlugin.getInstance().getConfig().getBoolean("BungeeCord.LobbySocket.Enabled")) {
            return;
        }

        String ip = BedWarsPlugin.getInstance().getConfig().getString("BungeeCord.LobbySocket.IP");
        int port = BedWarsPlugin.getInstance().getConfig().getInt("BungeeCord.LobbySocket.Port");

        this.socket = new ClientSocket(ip, port);

        new BukkitRunnable() {

            int i = 0;

            @Override
            public void run() {

                if (this.i == 10) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED
                            + "[Bedwars] Could not establish the connection with the lobby socket. Your lobby server might be closed.");
                    SocketManager.this.socket = null;
                    this.cancel();
                    return;
                }

                if (!SocketManager.this.socket.establishConnection()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED
                            + "[Bedwars] Could not establish a connection with the lobby socket. Trying again in 10 seconds.");
                    this.i++;
                    return;
                }

                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.GREEN + "[Bedwars] The connection with the lobby socket has been established.");
                SocketManager.this.socket.start();
                ManagerHandler.getGameManager().getGames().forEach(Game::notifyObservers);
                this.cancel();

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 120, 120);

    }

    public ClientSocket getSocket() {
        return this.socket;
    }

    public UUID getUUID() {
        return this.UUID;
    }
}
