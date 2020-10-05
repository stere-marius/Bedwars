package ro.marius.bedwars;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface ICustomGolem {

    void spawn(Location location);

    Entity getCustomEntity();

    void setCustomName(String s);

}
