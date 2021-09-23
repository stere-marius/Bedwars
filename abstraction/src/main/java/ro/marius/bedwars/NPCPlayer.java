package ro.marius.bedwars;

import com.mojang.authlib.properties.Property;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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

    public abstract void createNPC(Location location, NPCSkin skin);

    public abstract void sendSpawnPackets(Set<Player> players);

    public abstract void updateSkin(NPCSkin skin);

    public abstract void sendPacketsRemoveTablist(Player player);

    public abstract void sendPacketsHideName(Player player);

    public abstract void remove();

    public abstract Entity getBukkitEntity();

    public void setSkin(NPCSkin skin) {
        gameProfile.getProperties().get("textures").clear();
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
    }

    public Set<Player> getViewers() {
        return viewers;
    }

}
