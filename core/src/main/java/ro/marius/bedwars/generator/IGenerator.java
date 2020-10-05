package ro.marius.bedwars.generator;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public interface IGenerator {

    void spawn();

    void start();

    void cancelTasks();

    void removeGenerator();

    List<ArmorStand> getTextStand();

    ArmorStand getSupportStand();

    Location getLocation();

    List<BukkitTask> getTasks();

    int getTier();

    int getTime();

    void setTime(int time);
}
