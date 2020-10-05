package ro.marius.bedwars;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NMSHologramWrapper {


    void spawn(Location location, String text);

    void sendTo(Player... players);

    void remove(Player... players);

    void setArmorStandText(String text);

    String getText();

}
