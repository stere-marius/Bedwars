package ro.marius.bedwars.game.mechanics.worldadapter;

import org.bukkit.*;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.FileUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.VoidGenerator;
import ro.marius.bedwars.WorldCallback;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class WorldAdapter {

    private static final VoidGenerator VOID_GENERATOR = new VoidGenerator();
    private static final Set<String> EXCLUDE_FILES = new HashSet<>(
            Arrays.asList("session.lock", "stats", "playerdata", "uid.dat"));


    public abstract void loadWorld(String worldName, WorldCallback callback);

    public abstract void cloneWorld(String worldName, String newWorld, WorldCallback callback);

    public abstract void saveWorld(String worldName);

    public abstract void deleteWorld(String worldName);

    public abstract void saveWorldFile(String worldName);

    public abstract void copyWorld(String worldName);

    public abstract void copyWorldFiles();

    public abstract void regenerateWorld(String worldName);

    public abstract void regenerateWorld(String worldName, WorldCallback callback);

    public void createNormalWorld(String worldName, WorldCallback callback) {

        World world = Bukkit.getWorld(worldName);

        if (world == null) {
//			ManagerHandler.getGameManager().getGameNames().add(worldName);
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.generator(VOID_GENERATOR);
            worldCreator.generateStructures(false);
            world = Bukkit.createWorld(worldCreator);
            world.setAutoSave(false);
            world.setKeepSpawnInMemory(false);
            world.setSpawnLocation(0, 61, 0);
            world.getBlockAt(0, 60, 0).setType(Material.STONE);
            new Location(world, 0, 60, 0).getBlock().setType(Material.STONE);
            world.save();
        }

        callback.onComplete(world, new String[]{"The world has been loaded successfully."});

    }

    public void unloadWorld(String worldName, boolean save, boolean delete) {

        World world = Bukkit.getWorld(worldName);

        if (world != null) {

            for (Player p : world.getPlayers()) {

                if (p.isDead()) {
                    this.respawn(p);
                }

                Utils.teleportToLobby(p, BedWarsPlugin.getInstance());
                // TODO: Teleport players
            }

            Bukkit.unloadWorld(world, save);

            if (delete) {
                Bukkit.getWorlds().remove(world);
            }

        }

    }

    public void unloadWorld(World world, boolean save, boolean delete) {

        for (Player p : world.getPlayers()) {

            if (p.isDead()) {
                this.respawn(p);
            }

            Utils.teleportToLobby(p, BedWarsPlugin.getInstance());
            // TODO: Teleport players
        }

        Bukkit.unloadWorld(world, save);

        if (delete) {
            Bukkit.getWorlds().remove(world);
        }

    }

    public void respawn(Player p) {
        try {
            p.spigot().respawn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyWorldFolder(File from, File to) {

        if (!to.exists()) {
            to.mkdirs();
        }

        for (String fileName : from.list()) {

            if (EXCLUDE_FILES.contains(fileName)) {
                continue;
            }

            File file = new File(from, fileName);

            FileUtils.move(file, to);

        }

    }

}
