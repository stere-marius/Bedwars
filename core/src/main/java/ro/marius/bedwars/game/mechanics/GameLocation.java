package ro.marius.bedwars.game.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GameLocation {

    private String string;
    private String worldName;
    private Location location;

    public GameLocation(String string, Location location, String worldName) {
        this.string = string;
        this.location = location;
        this.worldName = worldName;
    }

    public static GameLocation convertGameLocation(String string) {
        return new GameLocation(string, Utils.convertingLocation(string), string.split(",")[0]);
    }

    public static GameLocation convertLocation(Location location) {
        return new GameLocation(Utils.convertingString(location), location, location.getWorld().getName());
    }

    public static List<GameLocation> getConvertedLocations(List<String> stringList) {
        List<GameLocation> list = new ArrayList<>();
        stringList.forEach(string -> list.add(convertGameLocation(string)));
        return list;
    }

    public static List<GameLocation> getConvertedLocation(List<Location> locations) {
        List<GameLocation> list = new ArrayList<>();
        locations.forEach(l -> list.add(new GameLocation(Utils.convertingString(l), l, l.getWorld().getName())));
        return list;
    }

    @Override
    public GameLocation clone() {
        return new GameLocation(this.string, this.location, this.worldName);
    }

    public void reloadLocation() {
        this.location.setWorld(Bukkit.getWorld(this.worldName));
    }

    public void setWorld(World world) {
        this.location.setWorld(world);
    }

    @Override
    public String toString() {

        return this.location.toString();
    }

    public String getString() {
        return this.string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public GameLocation setWorldName(String worldName) {
        this.worldName = worldName;
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
