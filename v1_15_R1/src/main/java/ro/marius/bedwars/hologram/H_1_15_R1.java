package ro.marius.bedwars.hologram;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ro.marius.bedwars.NMSHologramWrapper;
import ro.marius.bedwars.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class H_1_15_R1 implements NMSHologramWrapper {

    private EntityArmorStand armorStand;
    private String text;
    private final Set<Player> players = new HashSet<>();

    @Override
    public void spawn(Location location, String text) {
        this.text = text;
        this.armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(),
                location.getY(), location.getZ());
        this.armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
        this.armorStand.setCustomName(new ChatComponentText(Utils.translate(text)));
        this.armorStand.setNoGravity(false);
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setInvisible(true);
    }

    @Override
    public void sendTo(Player... players) {

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.armorStand);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.armorStand.getId(),
                this.armorStand.getDataWatcher(), true);

        for (Player p : players) {
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
            connection.sendPacket(packet);
            connection.sendPacket(metadataPacket);
            this.players.add(p);
        }

    }

    @Override
    public void remove(Player... players) {

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.armorStand.getId());

        for (Player p : players) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            this.players.remove(p);
        }
    }

    @Override
    public void setArmorStandText(String text) {
        this.armorStand.setCustomName(new ChatComponentText(Utils.translate(text)));
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(this.armorStand.getId(),
                this.armorStand.getDataWatcher(), true);

        for (Player p : this.players) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(metadataPacket);
        }
    }

    @Override
    public String getText() {
        return this.text;
    }

}
