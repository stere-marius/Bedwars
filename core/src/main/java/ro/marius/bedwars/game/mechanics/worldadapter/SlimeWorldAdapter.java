package ro.marius.bedwars.game.mechanics.worldadapter;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.mechanics.SimpleCallback;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.FileUtils;
import ro.marius.bedwars.WorldCallback;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SlimeWorldAdapter extends WorldAdapter {

    private final SlimeLoader bedwarsLoader;
    private final SlimePlugin slimePlugin;
    private final File worldDirectory = new File(BedWarsPlugin.getInstance().getDataFolder(), "/slime_worlds");

//	private static Location spawnLocation;

    public SlimeWorldAdapter() {
        SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        plugin.registerLoader("BWLoader", new BWSlimeLoader());
        this.slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        this.bedwarsLoader = this.slimePlugin.getLoader("BWLoader");
        this.convertWorlds();
    }

    public void convertWorlds() {

        File file = new File(BedWarsPlugin.getInstance().getDataFolder() + "/WorldSaves");

        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }

        Set<String> keys = ManagerHandler.getGameManager().getGameKeys();

        for (File worldFile : file.listFiles()) {
            String worldName = worldFile.getName();

            if (!keys.contains(worldName)) {
                continue;
            }
            if (this.worldExists(worldName)) {
                continue;
            }

            World world = Bukkit.getWorld(worldName);
            boolean isNull = world == null;

            if (!isNull) {
                this.unloadWorld(world, false, true);
            }

            if (!isNull && !world.getWorldFolder().exists()) {
                FileUtils.copyDirectory(worldFile, new File(Bukkit.getWorldContainer(), worldFile.getName()));
            }

            this.importWorld(worldName);
            Bukkit.getConsoleSender().sendMessage("&a[Bedwars] Converting " + worldName + " to slime format.");
        }

    }

    @Override
    public void loadWorld(String worldName, WorldCallback callback) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            this.unloadWorld(world, false, true);
        }

        SlimePropertyMap map = new SlimePropertyMap();
        map.setString(SlimeProperties.DIFFICULTY, "EASY");
        map.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        map.setBoolean(SlimeProperties.ALLOW_MONSTERS, true);
        map.setBoolean(SlimeProperties.PVP, true);
        map.setInt(SlimeProperties.SPAWN_X, 1);
        map.setInt(SlimeProperties.SPAWN_Y, 100);
        map.setInt(SlimeProperties.SPAWN_Z, 1);

        Bukkit.getLogger()
                .info(ChatColor.GRAY + "Loading world " + ChatColor.YELLOW + worldName + ChatColor.GRAY + "...");


        Bukkit.getScheduler().runTaskAsynchronously(BedWarsPlugin.instance, () -> {

            try {
                long start = System.currentTimeMillis();

                SlimeWorld slimeWorld = this.slimePlugin.loadWorld(this.bedwarsLoader, worldName, true, map);

                Bukkit.getScheduler().runTask(BedWarsPlugin.instance, () -> {
                    try {
                        this.slimePlugin.generateWorld(slimeWorld);
                        callback.onComplete(Bukkit.getWorld(worldName),
                                new String[]{"&aThe world " + worldName + " has been loaded successfully."});
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                        callback.onError(new String[]{
                                ChatColor.RED + "Failed to load world " + worldName + ": " + ex.getMessage() + "."});
                        return;
                    }

                    Bukkit.getLogger().info(ChatColor.GREEN + "World " + ChatColor.YELLOW + worldName + ChatColor.GREEN
                            + " loaded and generated in " + (System.currentTimeMillis() - start) + "ms!");
                });
            } catch (CorruptedWorldException ex) {

                Bukkit.getLogger()
                        .info(ChatColor.RED + "Failed to load world " + worldName + ": world seems to be corrupted.");

                ex.printStackTrace();
            } catch (NewerFormatException ex) {
                Bukkit.getLogger()
                        .info(ChatColor.RED + "Failed to load world " + worldName + ": this world"
                                + " was serialized with a newer version of the Slime Format (" + ex.getMessage()
                                + ") that SWM cannot understand.");
            } catch (UnknownWorldException e) {
                Bukkit.getLogger().info(ChatColor.RED + "Failed to load world " + worldName
                        + ": world could not be found (using data source '" + "').");
            } catch (WorldInUseException e) {
                Bukkit.getLogger().info(ChatColor.RED + "Failed to load world " + worldName
                        + ": world is already in use. If you are sure this is a mistake, run the command /swm unlock "
                        + worldName);
            } catch (IOException ex) {

                Bukkit.getLogger().info(ChatColor.RED + "Failed to load world " + worldName
                        + ". Take a look at the server console for more information.");

                Bukkit.getLogger().info("Failed to load world " + worldName + ":");
                ex.printStackTrace();
            }

        });

    }

    @Override
    public void cloneWorld(String worldName, String newWorld, WorldCallback callback) {

        File worldFile = new File(this.worldDirectory, worldName + ".slime");

        if (!worldFile.exists()) {
            callback.onError(new String[]{"&cCould not find the world " + worldName});
            return;
        }

        FileUtils.copyFiles(worldFile, new File(this.worldDirectory, newWorld + ".slime"));

        this.loadWorld(newWorld, callback);
    }

    @Override
    public void saveWorld(String worldName) {

    }

    @Override
    public void saveWorldFile(String worldName) {

        this.unloadWorld(worldName, true, true);

        this.importWorld(worldName, () -> this.loadWorld(worldName, new WorldCallback() {

            @Override
            public void onError(String[] message) {
                Bukkit.getConsoleSender().sendMessage(message);

            }

            @Override
            public void onComplete(World result, String[] message) {
                Bukkit.getConsoleSender().sendMessage(message);

            }
        }));

    }

    public void importWorld(String worldName, SimpleCallback callback) {

        new BukkitRunnable() {

            @Override
            public void run() {

                File worldDir = new File(Bukkit.getWorldContainer(), worldName);

                try {
                    SlimeWorldAdapter.this.slimePlugin.importWorld(worldDir, worldName, SlimeWorldAdapter.this.bedwarsLoader);
                    callback.onCallback();
                } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException
                        | WorldTooBigException | IOException ex) {
                    ex.printStackTrace();
                }

            }
        }.runTaskAsynchronously(BedWarsPlugin.instance);

    }

    public void importWorld(String worldName) {

        new BukkitRunnable() {

            @Override
            public void run() {

                File worldDir = new File(Bukkit.getWorldContainer(), worldName);

                try {
                    SlimeWorldAdapter.this.slimePlugin.importWorld(worldDir, worldName, SlimeWorldAdapter.this.bedwarsLoader);
                } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException
                        | WorldTooBigException | IOException ex) {
                    ex.printStackTrace();
                }

            }
        }.runTaskAsynchronously(BedWarsPlugin.instance);

    }

    @Override
    public void copyWorld(String worldName) {

    }

    @Override
    public void regenerateWorld(String worldName) {

    }

    @Override
    public void regenerateWorld(String worldName, WorldCallback callback) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            this.unloadWorld(world, false, true);
        }

        ManagerHandler.getGameManager().getGameNames().add(worldName);

        SlimePropertyMap map = new SlimePropertyMap();
        map.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
        map.setBoolean(SlimeProperties.ALLOW_MONSTERS, true);
        map.setBoolean(SlimeProperties.PVP, true);
        map.setString(SlimeProperties.DIFFICULTY, "EASY");
        map.setInt(SlimeProperties.SPAWN_X, 1);
        map.setInt(SlimeProperties.SPAWN_Y, 100);
        map.setInt(SlimeProperties.SPAWN_Z, 1);

        Bukkit.getLogger()
                .info(ChatColor.GRAY + "[Bedwars-Reset] Loading world " + ChatColor.YELLOW + worldName + ChatColor.GRAY + "...");

        Bukkit.getScheduler().runTaskAsynchronously(BedWarsPlugin.instance, () -> {

            try {
                long start = System.currentTimeMillis();

                SlimeWorld slimeWorld = this.slimePlugin.loadWorld(this.bedwarsLoader, worldName, true, map);

                Bukkit.getScheduler().runTask(BedWarsPlugin.instance, () -> {
                    try {
                        this.slimePlugin.generateWorld(slimeWorld);
                        callback.onComplete(Bukkit.getWorld(worldName),
                                new String[]{"&a[Bedwars-Reset] The world " + worldName + " has been loaded successfully."});
                    } catch (IllegalArgumentException ex) {
                        callback.onError(new String[]{
                                ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName + ": " + ex.getMessage() + "."});
                        return;
                    }

                    Bukkit.getLogger().info(ChatColor.GREEN + "World " + ChatColor.YELLOW + worldName + ChatColor.GREEN
                            + " loaded and generated in " + (System.currentTimeMillis() - start) + "ms!");
                });
            } catch (CorruptedWorldException ex) {

                callback.onError(new String[]{
                        ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName + ": world seems to be corrupted."});

                ex.printStackTrace();
            } catch (NewerFormatException ex) {
                callback.onError(new String[]{ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName + ": this world"
                        + " was serialized with a newer version of the Slime Format (" + ex.getMessage()
                        + ") that SWM cannot understand."});
            } catch (UnknownWorldException e) {
                callback.onError(new String[]{ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName
                        + ": world could not be found (using data source '" + "')."});
            } catch (WorldInUseException e) {
                callback.onError(new String[]{ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName
                        + ": world is already in use. If you are sure this is a mistake, run the command /swm unlock "
                        + worldName});
            } catch (IOException ex) {

                callback.onError(new String[]{ChatColor.RED + "[Bedwars-Reset] Failed to load world " + worldName
                        + ". Take a look at the server console for more information."});
                ex.printStackTrace();
            }

        });

    }

    @Override
    public void loadGameWorld(String worldName, WorldCallback callback) {

    }

    public boolean worldExists(String worldName) {
        try {
            return this.bedwarsLoader.worldExists(worldName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void deleteWorld(String worldName) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {
            this.unloadWorld(world, false, true);
        }

        try {
            this.bedwarsLoader.deleteWorld(worldName);
        } catch (UnknownWorldException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void copyWorldFiles() {

    }

}
