package ro.marius.bedwars.game.mechanics;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.List;
import java.util.UUID;

public class NPCArena {

    private final UUID uuid;
    private final int index;
    private final String arenaType;
    private final JoinNpcHologram npcHologram;

    public NPCArena(int index, Location location, String arenaType, List<String> text) {
        this.index = index;
        this.arenaType = arenaType;
        this.uuid = UUID.randomUUID();
        this.npcHologram = new JoinNpcHologram(location, text);
    }

    public void remove() {
        npcHologram.despawnHolograms();
    }

    public int getIndex() {
        return index;
    }

    public String getArenaType() {
        return this.arenaType;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public JoinNpcHologram getNpcHologram() {
        return npcHologram;
    }
}