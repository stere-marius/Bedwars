package ro.marius.bedwars.game.mechanics;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.NPCManager;
import ro.marius.bedwars.utils.Utils;

import java.util.List;
import java.util.UUID;

public class NPCArena {

    private final UUID uuid;
    private final NPC npc;
    private final Location location;
    private final List<String> text;
    private final List<ArmorStand> standList;
    private final String arenaType;

    public NPCArena(NPC npc, Location location, String arenaType, List<ArmorStand> stand,
                    List<String> text) {
        this.npc = npc;
        this.location = location;
        this.standList = stand;
        this.arenaType = arenaType;
        this.text = text;
        this.uuid = UUID.randomUUID();
    }

    public void update(int players) {

        int size = this.text.size();

        for (int i = 0; i < size; i++) {
            ArmorStand stand = this.standList.get(i);
            String txt = Utils.translate(this.text.get(i));
            stand.setCustomName(txt.replace("<playersPlaying>", players + ""));
        }

    }

    public void despawnStandList() {
        this.standList.forEach(Entity::remove);
        this.standList.clear();
    }

    public void remove() {
        this.despawnStandList();

        if (this.npc != null) {
            this.npc.despawn();
            CitizensAPI.getNPCRegistry().deregister(this.npc);
            ManagerHandler.getNPCManager().removeNPC(this.npc.getId());
            ManagerHandler.getNPCManager().getNpc().get(this.arenaType).remove(this);
        }

    }

    public void respawnStandList(int players) {

        Location clonedLocation = this.location.clone().add(0, 1.805, 0);
        final double DISTANCE_BETWEEN_STAND = 0.40;

        for (int i = 0; i < this.text.size(); i++) {
            ArmorStand stand = Utils.getSpawnedArmorStand(clonedLocation, Utils.translate(this.text.get(i)).replace("<playersPlaying>", players + ""));
            stand.setRemoveWhenFarAway(false);
            stand.setMetadata("BedwarsStand", NPCManager.METADATA);
            clonedLocation = clonedLocation.add(0, DISTANCE_BETWEEN_STAND, 0);
            this.standList.set(i, stand);
        }

    }

    public NPC getNpc() {
        return this.npc;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<String> getText() {
        return this.text;
    }

    public List<ArmorStand> getStandList() {
        return this.standList;
    }

    public String getArenaType() {
        return this.arenaType;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}