package ro.marius.bedwars;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

public abstract class NPCPlayer {

    private final Set<Player> viewers = new HashSet<>();
    protected final UUID uuid = UUID.randomUUID();
    protected final String name = uuid.toString().replace("-", "").substring(0, 10);
    protected GameProfile gameProfile = new GameProfile(uuid, name);

    public abstract void spawnNPC(Location location, NPCSkin skin);

    public abstract void spawnNPC(Location location);

    public abstract void updateSkin(NPCSkin skin);

    public abstract void sendSpawnPackets(Set<Player> players);

    public abstract void removeFromTablist();

    public abstract void hideName();

    public abstract int getEntityID();

    public Set<Player> getViewers() {
        return viewers;
    }

}
