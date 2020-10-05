package ro.marius.bedwars;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface IShopKeepers {

    void spawn(Location location);

    Entity getCustomEntity();

}
