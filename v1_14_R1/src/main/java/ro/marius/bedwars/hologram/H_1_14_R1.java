package ro.marius.bedwars.hologram;

import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ro.marius.bedwars.NMSHologramWrapper;
import ro.marius.bedwars.utils.Utils;

public class H_1_14_R1 implements NMSHologramWrapper {

    private EntityArmorStand armorStand;
    private String text;

    @Override
    public void spawn(Location location, String text) {
        this.text = text;
        this.armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(),
                location.getY(), location.getZ());
        this.armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
                location.getPitch());
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setNoGravity(false);
        this.armorStand.setCustomName(new ChatComponentText(Utils.translate(text)));
        this.armorStand.setInvisible(true);
    }

    @Override
    public void sendTo(Player... players) {

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.armorStand);

        for (Player p : players) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void remove(Player... players) {

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.armorStand.getId());

        for (Player p : players) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    @Override
    public void setArmorStandText(String text) {
        this.armorStand.setCustomName(new ChatComponentText(Utils.translate(text)));
    }

    @Override
    public String getText() {
        return this.text;
    }

}
