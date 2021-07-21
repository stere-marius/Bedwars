package ro.marius.bedwars;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public interface BedwarsNPC {

    void spawn(Location location, String texture, String signature);

    void sendNPCSpawnPacket(Player... players);

    void sendNPCRemovePacket();

    void sendHideNamePacket(Player... player);

    void hideFromTablist(Player... player);

    Set<Player> getViewers();

    int getNPCID();
}
