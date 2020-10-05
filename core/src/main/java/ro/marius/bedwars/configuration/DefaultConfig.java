package ro.marius.bedwars.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import ro.marius.bedwars.BedWarsPlugin;

import java.util.Arrays;

public class DefaultConfig {

    public DefaultConfig() {

    }

    public void loadConfiguration() {
        FileConfiguration config = BedWarsPlugin.getInstance().getConfig();
        config.options().header("Official Documentation https://bitbucket.org/STRMarius/bedwarswiki/wiki/Home");
        config.options().copyHeader(true);
        config.addDefault("WorldAdapter", "NORMAL_ADAPTER");
        config.addDefault("FastAsyncWorldEdit", false);
        config.addDefault("onJoin.TeleportToLobbyLocation", false);
        config.addDefault("ShoutCommand.Name", "shout");
        config.addDefault("MySQL.Enabled", false);
        config.addDefault("MySQL.Host", "localhost");
        config.addDefault("MySQL.Database", "TestDatabase");
        config.addDefault("MySQL.Username", "root");
        config.addDefault("MySQL.Password", "");
        config.addDefault("MySQL.Port", 3306);
        config.addDefault("BungeeCord.Enabled", false);
        config.addDefault("BungeeCord.RandomLobbyServer.Enabled", false);
        config.addDefault("BungeeCord.RandomLobbyServer.Servers", Arrays.asList("Hub", "Hub-1", "Hub-2"));
        config.addDefault("BungeeCord.LobbySocket.Enabled", false);
        config.addDefault("BungeeCord.LobbySocket.IP", "localhost");
        config.addDefault("BungeeCord.LobbySocket.Port", 9999);
        config.addDefault("BungeeCord.LobbyServer", "Hub");
        config.addDefault("BungeeCord.RestartCommand", "restart");
        config.addDefault("BungeeCord.RestartMessage", "&aThe server is going to restart in 10 seconds.");
        config.addDefault("BungeeCord.MOTD.Display", "<gameState>,&a<inGame>/<max>");
        config.addDefault("BungeeCord.MOTD.InGameState", "&cIN_GAME");
        config.addDefault("BungeeCord.MOTD.InWaitingState", "&aIN_WAITING");
        config.options().copyDefaults(true);
        BedWarsPlugin.getInstance().saveConfig();
    }

}
