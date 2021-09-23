package ro.marius.bedwars.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.NPC;
import org.bukkit.event.Listener;
import ro.marius.bedwars.NPCSkin;

public abstract class BedwarsJoinNPC implements Listener {

    public abstract void spawnNPC(int id, Location location, String skinName);

    public abstract void spawnNPC(int id, Location location, NPCSkin npcSkin);

    public abstract void updateSkin(int id, NPCSkin npcSkin);

    public abstract void removeNPC(int id);

}
