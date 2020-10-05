package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.mechanics.worldadapter.SimpleWorldAdapter;
import ro.marius.bedwars.game.mechanics.worldadapter.SlimeWorldAdapter;
import ro.marius.bedwars.game.mechanics.worldadapter.WorldAdapter;
import ro.marius.bedwars.WorldCallback;

public class WorldManager {

    private WorldAdapter worldAdapter = new SimpleWorldAdapter();

    public WorldManager() {
        this.initializeWorldAdapter();
    }

    public void initializeWorldAdapter() {

        String adapter = BedWarsPlugin.getInstance().getConfig().getString("WorldAdapter");
        boolean isSlime = "SLIME_WORLD_ADAPTER".equalsIgnoreCase(adapter);
        boolean containsPlugin = BedWarsPlugin.getInstance().getServer().getPluginManager()
                .getPlugin("SlimeWorldManager") != null;

        if (isSlime && containsPlugin) {

            try {
                this.worldAdapter = new SlimeWorldAdapter();
            } catch (Exception ex) {
                this.worldAdapter = new SimpleWorldAdapter();
                Bukkit.getConsoleSender().sendMessage(
                        ChatColor.RED + "[Bedwars] Could not load the slime world adapter due to an error");
                ex.printStackTrace();
            }
        }

    }

    public void cloneWorld(String name, String newWorld, WorldCallback callback) {
        this.worldAdapter.cloneWorld(name, newWorld, callback);
    }

    public void saveWorld(World world) {
        this.worldAdapter.saveWorld(world.getName());
    }

    public void createWorld(String name, WorldCallback callback) {
        this.worldAdapter.createNormalWorld(name, callback);
    }

    public void saveWorldFile(String worldName) {
        this.worldAdapter.saveWorldFile(worldName);
    }

    public void loadWorld(String name, WorldCallback callback) {
        this.worldAdapter.loadWorld(name, callback);
    }

    public void saveWorld(String name) {
        this.saveWorld(Bukkit.getWorld(name));
    }

    public void copyWorld(String worldName) {
        this.worldAdapter.copyWorld(worldName);
    }

    public void copyWorldFiles() {
        this.worldAdapter.copyWorldFiles();
    }

    public void regenerateWorld(String world) {
        this.regenerateWorld(Bukkit.getWorld(world));
    }

    public void regenerateWorld(World world) {
        this.worldAdapter.regenerateWorld(world.getName());
    }

    public void regenerateWorld(World world, WorldCallback callback) {
        this.worldAdapter.regenerateWorld(world.getName(), callback);
    }

    public void deleteArenaWorld(String arenaName) {
        this.worldAdapter.deleteWorld(arenaName);
    }

    public WorldAdapter getWorldAdapter() {
        return this.worldAdapter;
    }
}
