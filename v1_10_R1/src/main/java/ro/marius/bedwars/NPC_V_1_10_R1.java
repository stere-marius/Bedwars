package ro.marius.bedwars;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class NPC_V_1_10_R1 extends NPCPlayer {

    private EntityPlayer entityPlayer;

    @Override
    public void createNPC(Location location, NPCSkin skin) {
        gameProfile.getProperties().get("textures").clear();
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        entityPlayer = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        entityPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void updateSkin(NPCSkin skin) {
        this.gameProfile.getProperties().get("textures").clear();
        this.gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfoRemove =
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy =
                new PacketPlayOutEntityDestroy(entityPlayer.getId());

        for (Player player : getViewers()) {
            PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
            playerConnection.sendPacket(packetPlayOutPlayerInfoRemove);
            playerConnection.sendPacket(packetPlayOutEntityDestroy);
        }

        sendSpawnPackets(getViewers());
    }

    @Override
    public void sendSpawnPackets(Set<Player> players) {

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfoAdd =
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);

        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn =
                new PacketPlayOutNamedEntitySpawn(entityPlayer);

        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation =
                new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.yaw * 256 / 360));

        for (Player player : players) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(packetPlayOutPlayerInfoAdd);
            connection.sendPacket(packetPlayOutNamedEntitySpawn);
            connection.sendPacket(packetPlayOutEntityHeadRotation);
        }

    }

    @Override
    public void sendPacketsRemoveTablist(Player player) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo =
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(packetPlayOutPlayerInfo);

    }

    @Override
    public void sendPacketsHideName(Player player) {
        Collection<String> gameProfile = Collections.singletonList(this.gameProfile.getName());

        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

        ReflectionUtils.setFieldValue("a", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, this.gameProfile.getName());
        ReflectionUtils.setFieldValue("b", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, "displayName");
        ReflectionUtils.setFieldValue("c", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, "prefix");
        ReflectionUtils.setFieldValue("d", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, "suffix");
        ReflectionUtils.setFieldValue("e", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, ScoreboardTeamBase.EnumNameTagVisibility.NEVER.e);
        ReflectionUtils.setFieldValue("h", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, gameProfile);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutScoreboardTeam);
    }

    @Override
    public void remove() {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy =
                new PacketPlayOutEntityDestroy(entityPlayer.getId());
        for (Player player : getViewers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
        }
    }

    @Override
    public Entity getBukkitEntity() {
        return entityPlayer.getBukkitEntity();
    }
}
