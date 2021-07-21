package ro.marius.bedwars.game.mechanics.worldadapter;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.FileUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.VoidGenerator;
import ro.marius.bedwars.WorldCallback;

import java.io.File;

public class SimpleWorldAdapter extends WorldAdapter {

    private final String FOLDER_PATH = BedWarsPlugin.instance.getDataFolder().getPath();
    private final VoidGenerator VOID_GENERATOR = new VoidGenerator();
    private final String FIRST_WORLD = Bukkit.getServer().getWorlds().get(0).getName();

    @Override
    public void loadWorld(String worldName, WorldCallback callback) {

        World w = Bukkit.getWorld(worldName);

        if (w != null) {
            this.regenerateWorld(worldName, callback);
            return;
        }

        File worldSave = new File(this.FOLDER_PATH + "/WorldSaves/" + worldName);

        if (!worldSave.exists()) {
            callback.onError(new String[]{"[BEDWARS-ERROR]", Utils.translate("&cAn error has occurred during the world loading"),
                    Utils.translate("&cCouldn't find the world " + worldName + "'s saved file on WorldSaves folder.")});
            return;
        }

        File toLocation = new File(Bukkit.getWorldContainer(), worldName);

        FileUtils.deleteFolder(toLocation);
        this.copyWorldFolder(worldSave, toLocation);

        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(this.VOID_GENERATOR);
        worldCreator.generateStructures(false);
        World world = Bukkit.createWorld(worldCreator);
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.save();

        Bukkit.getWorlds().add(world);

        new BukkitRunnable() {

            @Override
            public void run() {
                callback.onComplete(world, new String[]{});

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 4);

    }

    @Override
    public void cloneWorld(String worldName, String newWorld, WorldCallback callback) {
        World clonedWorld = Bukkit.getWorld(worldName);

        if (clonedWorld == null) {
            callback.onError(new String[]{"&aThe world " + worldName + " does not exist."});
            return;
        }

        this.copyWorldFolder(clonedWorld.getWorldFolder(), new File(clonedWorld.getWorldFolder().getParentFile(), newWorld));

        ManagerHandler.getGameManager().getGameNames().add(newWorld);
        WorldCreator worldCreator = new WorldCreator(newWorld);
        worldCreator.generator(this.VOID_GENERATOR);
        worldCreator.generateStructures(false);
        World world = Bukkit.createWorld(worldCreator);
        world.setAutoSave(false);
        world.setKeepSpawnInMemory(false);
        world.setSpawnLocation(0, 61, 0);
        world.getBlockAt(0, 60, 0).setType(Material.STONE);
        new Location(world, 0, 60, 0).getBlock().setType(Material.STONE);
        world.save();

        File clonedFile = new File(this.FOLDER_PATH + "/WorldSaves/" + newWorld);

        if (clonedFile.exists()) {
            FileUtils.deleteFolder(clonedFile);
        }

        this.copyWorldFolder(world.getWorldFolder(), clonedFile);

        callback.onComplete(world, new String[]{"&aThe world " + newWorld + " has been loaded successfully."});

    }

    @Override
    public void saveWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return;
        }

        world.getEntities().forEach(e -> {

            if (!(e instanceof Player)) {
                e.remove();
            }

        });

        world.save();

        new BukkitRunnable() {

            @Override
            public void run() {
                File locationFile = new File(SimpleWorldAdapter.this.FOLDER_PATH + "/WorldSaves/" + world.getName());
                File worldFile = world.getWorldFolder();

                if (locationFile.exists()) {
                    FileUtils.deleteFolder(locationFile);
                }

                SimpleWorldAdapter.this.copyWorldFolder(worldFile, locationFile);

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 25);

    }

    @Override
    public void saveWorldFile(String worldName) {
        File locationFile = new File(this.FOLDER_PATH + "/WorldSaves/" + worldName);

        if (locationFile.exists()) {
            FileUtils.deleteFolder(locationFile);
        }

        this.copyWorldFolder(Bukkit.getWorld(worldName).getWorldFolder(), locationFile);

    }

    @Override
    public void copyWorld(String worldName) {
        File toLocation = new File(Bukkit.getWorldContainer(), worldName);
        File worldFile = new File(this.FOLDER_PATH + "/WorldSaves/" + worldName);

        this.copyWorldFolder(toLocation, worldFile);
        FileUtils.deleteFolder(toLocation);
    }

    @Override
    public void regenerateWorld(String worldName) {

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            Bukkit.getConsoleSender()
                    .sendMessage("&cCould not regenerate the world " + worldName + " because it does not exist.");
            return;
        }

        File file = world.getWorldFolder();
        File worldSaved = new File(this.FOLDER_PATH + "/WorldSaves/" + world.getName());

        if (!file.exists()) {
            return;
        }

        if (world.getName().equals(this.FIRST_WORLD)) {
            Bukkit.getConsoleSender().sendMessage(
                    "&cCould not regenerate the world " + world + " because it's the main world of the server");
            return;
        }

        if (!worldSaved.exists()) {
            Bukkit.getConsoleSender()
                    .sendMessage(new String[]{"[BEDWARS-ERROR]", "&cAn error has occur during the world regeneration",
                            "&cCouldn't find the world " + world.getName() + "'s saved file on WorldSaves folder."});

            return;
        }

        this.unloadWorld(world, false, false);
        FileUtils.deleteFolder(file);
        this.copyWorldFolder(worldSaved, file);

        new BukkitRunnable() {

            @Override
            public void run() {
                WorldCreator worldCreator = new WorldCreator(worldName);
                worldCreator.seed(world.getSeed());
                worldCreator.generateStructures(false);
                worldCreator.generator(SimpleWorldAdapter.this.VOID_GENERATOR);
                World world = worldCreator.createWorld();
                world.setKeepSpawnInMemory(false);
                world.setSpawnFlags(true, false);
                world.setAutoSave(false);
                world.setTime(0L);

                Bukkit.getWorlds().add(world);

            }
        }.runTask(BedWarsPlugin.instance);

    }

    @Override
    public void regenerateWorld(String worldName, WorldCallback callback) {
        World world = Bukkit.getWorld(worldName);

        File file = world.getWorldFolder();
        File worldSaved = new File(this.FOLDER_PATH + "/WorldSaves/" + world.getName());

        if (!file.exists()) {
            callback.onError(new String[]{ChatColor.RED + "[BEDWARS-ERROR]",
                    ChatColor.RED + "An error has occur during the world regeneration",
                    ChatColor.RED + "Couldn't find the world " + world.getName() + "'s file."});
            return;
        }

        if (worldName.equals(this.FIRST_WORLD)) {
            callback.onError(new String[]{ChatColor.RED + "Could not regenerate the world " + world
                    + " because it's the main world of the server"});
            return;
        }

        this.unloadWorld(world, false, false);

        if (!worldSaved.exists()) {
            callback.onError(new String[]{"[BEDWARS-ERROR]",
                    ChatColor.RED + "An error has occur during the world regeneration", ChatColor.RED
                    + "Couldn't find the world " + world.getName() + "'s saved file on WorldSaves folder."});
            return;
        }

        FileUtils.deleteFolder(file);
        this.copyWorldFolder(worldSaved, file);

        new BukkitRunnable() {

            @Override
            public void run() {
                WorldCreator worldCreator = new WorldCreator(worldName);
                worldCreator.seed(world.getSeed());
                worldCreator.generateStructures(false);
                worldCreator.generator(SimpleWorldAdapter.this.VOID_GENERATOR);
                World world = worldCreator.createWorld();
                world.setKeepSpawnInMemory(false);
                world.setAutoSave(false);
                world.setTime(0L);

                Bukkit.getWorlds().add(world);
                callback.onComplete(world, new String[]{"The world " + worldName + " has loaded successfully"});

            }
        }.runTask(BedWarsPlugin.instance);

    }

    public boolean isRegenerable(String s) {

        return s.equals(this.FIRST_WORLD) || "world_nether".equals(s) || "world_end".equals(s);
    }


    @Override
    public void respawn(Player p) {
        try {
            p.spigot().respawn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteWorld(String worldName) {

        File savedWorldFile = new File(this.FOLDER_PATH + "/WorldSaves/" + worldName);
        FileUtils.deleteFolder(savedWorldFile);

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return;
        }

        this.unloadWorld(world, false, true);
        FileUtils.deleteFolder(world.getWorldFolder());

    }

    @Override
    public void copyWorldFiles() {

        File worldFile = Bukkit.getWorlds().get(0).getWorldFolder().getParentFile();

        for (Game game : ManagerHandler.getGameManager().getGames()) {

            File gameFile = new File(this.FOLDER_PATH + "/WorldSaves/" + game.getName());

            if (!gameFile.exists()) {
                continue;
            }
            if (!gameFile.isDirectory()) {
                continue;
            }

            File file = new File(worldFile, game.getName());

            this.copyWorldFolder(gameFile, file);

        }

    }

    public VoidGenerator getVOID_GENERATOR() {
        return this.VOID_GENERATOR;
    }
}
