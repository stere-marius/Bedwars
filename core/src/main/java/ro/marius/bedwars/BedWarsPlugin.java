package ro.marius.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import ro.marius.bedwars.commands.CommandManager;
import ro.marius.bedwars.configuration.*;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.ListenerManager;
import ro.marius.bedwars.manager.type.VersionManager;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.mysql.MySQL;
import ro.marius.bedwars.party.BedwarsPartyHandler;
import ro.marius.bedwars.party.PartyHandler;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.FileData;
import ro.marius.bedwars.playerdata.SQLData;
import ro.marius.bedwars.socketclient.ClientSocket;
import ro.marius.bedwars.utils.FileUtils;
import ro.marius.bedwars.utils.PAPIExtension;
import ro.marius.bedwars.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class BedWarsPlugin extends JavaPlugin {

    public static final int DATABASE_HEALTH_CHECK_RATE = 60 * 20; // ~1 minute

    private final File dataDirectory = new File(this.getDataFolder(), "data");
    private final File upgradeDirectory = new File(this.getDataFolder(), "upgrade");
    private final File shopDirectory = new File(this.getDataFolder(), "shop");

    public static BedWarsPlugin instance;
    public MySQL sql;
    private boolean fawe = false;

    private static PartyHandler partyHandler = new BedwarsPartyHandler();

    public static BedWarsPlugin getInstance() {
        return instance;
    }


    @Override
    public void onLoad() {
        ManagerHandler.setVersionManager(new VersionManager());
    }

    @Override
    public void onEnable() {
        instance = this;

        // create data directories and save default configurations
        this.dataDirectory.mkdirs();
        this.upgradeDirectory.mkdirs();
        this.shopDirectory.mkdirs();
        this.saveDefaultConfiguration();

        // load configs
        Lang.loadLang(this);
        Items.loadItems(this);
        GUIStructure.loadConfiguration();

        // setup multiple weird things
        // FIXME: this part needs to be more ENTERPRISE
        new ArenaOptions().loadDefaultConfiguration();
        new ListenerManager().registerEvents(this);
        new ManagerHandler(this);
        new DefaultConfig().loadConfiguration();
        new TeamSelectorConfiguration().loadConfiguration();
        new CommandManager().registerCommands();

        // wait until server loads with these things
        this.getServer().getScheduler().runTask(this, () -> {
            BedWarsPlugin.this.setupFAWE();
            ManagerHandler.getHologramManager().spawnPlayersHologram();
            ManagerHandler.getGameManager().loadGames(() -> ManagerHandler.getNPCManager().loadNPCGameObservers());
        });

        // load arena types
        ManagerHandler.getGameManager().loadArenaTypes();

        // setup mysql if used
        this.setupDatabase();

        // load players
        for (Player player : Bukkit.getOnlinePlayers()) {
            APlayerData playerData = this.isSQLEnabled()
                    ? new SQLData(player)
                    : new FileData(player);
            playerData.loadData();
            ManagerHandler.getGameManager().getPlayerData().put(player.getUniqueId(), playerData);
        }

        // register outgoing plugin message channels
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // placeholderapi support
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIExtension().register();
        }
    }

    public void saveDefaultConfiguration() {
        FileUtils.saveDefaultResource(new File(this.upgradeDirectory, "default.yml"), this.getResource("upgrades.yml"));
        FileUtils.saveDefaultResource(new File(this.shopDirectory, "default.yml"), this.getResource("default.yml"));
    }

    @Override
    public void onDisable() {

        if (this.isBungeeCord()) {
            ClientSocket.runThread = false;
        }

        ManagerHandler.getHologramManager().removePlayersHologram();
        ManagerHandler.getWorldManager().copyWorldFiles();
        ManagerHandler.onDisable();

        for (Player player : Bukkit.getOnlinePlayers()) {

            InventoryView inv = player.getOpenInventory();

            if ((inv != null) && (inv.getTopInventory().getHolder() != null) && (inv.getTopInventory().getHolder() instanceof ExtraInventory)) {
                player.closeInventory();
            }

            World mainWorld = Bukkit.getWorlds().get(0);
            if (player.getWorld().getName().equals(mainWorld.getName())) {
                continue;
            }

            Utils.teleportToLobby(player, this);
        }

        if (this.sql != null) {
            this.sql.closeConnection();
        }
    }

    public void setupDatabase() {

        if (!this.isSQLEnabled()) {
            return;
        }

        System.out.println("[Bedwars] Trying to establish the connection with MySQL database.");
        this.sql = new MySQL(this.getConfig().getString("MySQL.Host"), this.getConfig().getInt("MySQL.Port"),
                this.getConfig().getString("MySQL.Database"), this.getConfig().getString("MySQL.Username"),
                this.getConfig().getString("MySQL.Password"));

        this.sql.createDatabase();
        this.sql.createTables();
        this.setupDatabaseHealthCheck();
    }

    // TODO: Shuffle teams formula: players / teams
    // daca players / playersPerTeam
    // 3v3: 4 -> 4/2 -> 2v2
    // 3v3v3: 5 -> 5/3 ->

    public void setupDatabaseHealthCheck() {
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Connection connection = BedWarsPlugin.this.sql.getConnection();
            try {
                if ((connection != null) && !connection.isClosed()) {
                    connection.createStatement().execute("SELECT 1");
                }
            } catch (SQLException e) {
                BedWarsPlugin.this.sql.getNewConnection();
            }
        }, DATABASE_HEALTH_CHECK_RATE, DATABASE_HEALTH_CHECK_RATE);
    }

    public void setupFAWE() {

        int versionID = ManagerHandler.getVersionManager().getServerVersion().getID();

        if (!this.getConfig().getBoolean("FastAsyncWorldEdit")) {
            return;
        }

        if (versionID < 7) {
            Bukkit.getConsoleSender().sendMessage(Utils.translate("&c[Bedwars] Support for FastAsyncWorldEdit is just for 1.13+"));
            return;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            this.fawe = true;
            Bukkit.getConsoleSender().sendMessage(Utils.translate("&a[Bedwars] Found FastAsyncWorldEdit in your plugins."));
            File schematicsDirectory = new File(this.getDataFolder(), "schematics");
            schematicsDirectory.mkdir();
            return;
        }

        Bukkit.getConsoleSender().sendMessage(Utils.translate("&a[Bedwars] &cCouldn't find AsyncWorldEdit in your plugins."));
    }

    public static PartyHandler getPartyHandler() {
        return partyHandler;
    }

    public void readPartyHandler() {

        String adapter = getConfig().getString("PartyAdapter", "BEDWARS_ADAPTER");

        if (adapter.equalsIgnoreCase("PARTY_AND_FRIENDS_ADAPTER")) {
            partyHandler = new BedwarsPartyHandler();//TODO
        }

    }

    public static void setPartyHandler(PartyHandler partyHandler) {
        BedWarsPlugin.partyHandler = partyHandler;
    }

    public boolean isFAWE() {
        return this.fawe;
    }

    public boolean isBungeeCord() {
        return this.getConfig().getBoolean("BungeeCord.Enabled");
    }

    public boolean isSQLEnabled() {
        return this.getConfig().getBoolean("MySQL.Enabled");
    }
}
