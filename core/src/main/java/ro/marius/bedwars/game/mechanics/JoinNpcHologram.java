package ro.marius.bedwars.game.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import ro.marius.bedwars.manager.type.NPCManager;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class JoinNpcHologram {

    private final Location location;
    private final List<String> text;
    private final List<ArmorStand> holograms = new ArrayList<>();

    public JoinNpcHologram(Location location, List<String> text) {
        this.location = location;
        this.text = text;
    }

    public void update(int players) {
        for (int i = 0; i < this.text.size(); i++) {
            ArmorStand stand = this.holograms.get(i);
            String txt = Utils.translate(this.text.get(i));
            stand.setCustomName(txt.replace("<playersPlaying>", players + ""));
        }
    }

    public void despawnHolograms() {
        this.holograms.forEach(Entity::remove);
        this.holograms.clear();
    }

    public void spawnHolograms(int players) {

        Location clonedLocation = this.location.clone().add(0, 1.805, 0);
        final double DISTANCE_BETWEEN_STAND = 0.40;

        for (int i = 0; i < this.text.size(); i++) {
            ArmorStand stand = Utils.getSpawnedArmorStand(clonedLocation, Utils.translate(this.text.get(i)).replace("<playersPlaying>", players + ""));
            stand.setRemoveWhenFarAway(false);
            stand.setMetadata("BedwarsStand", NPCManager.METADATA);
            clonedLocation = clonedLocation.add(0, DISTANCE_BETWEEN_STAND, 0);
            this.holograms.add(stand);
        }

    }

    public Location getLocation() {
        return location;
    }

    public List<ArmorStand> getHologramList() {
        return holograms;
    }
}
