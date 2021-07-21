package ro.marius.bedwars;


import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.*;

public class NPC_V1_8_R3 implements BedwarsNPC {

    private EntityPlayer npc;
    private GameProfile gameProfile;
    private final UUID uuid = UUID.randomUUID();
    private final Set<Player> viewers = new HashSet<>();

    @Override
    public void spawn(Location location, String texture, String signature){
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        gameProfile = new GameProfile(uuid, uuid.toString().substring(0, 16));
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public void sendNPCSpawnPacket(Player... players){
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc);
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
        PacketPlayOutEntityHeadRotation playOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360));

        for(Player player: players) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(packetPlayOutPlayerInfo);
            connection.sendPacket(packetPlayOutNamedEntitySpawn);
            connection.sendPacket(playOutEntityHeadRotation);
            viewers.add(player);
        }
    }

    @Override
    public void sendNPCRemovePacket(){
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(npc.getId());
        viewers.forEach(viewer -> ((CraftPlayer) viewer).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy));
    }

    @Override
    public void sendHideNamePacket(Player... players){
        Collection<String> gameProfile = Collections.singletonList(this.gameProfile.getName());

        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

        // TODO: Add "npcVisibility" + uuid
        String uuidSubstring = uuid.toString().substring(0, 14);
        ReflectionUtils.setFieldValue("a", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, "n_" + uuidSubstring);
        ReflectionUtils.setFieldValue("b", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, "d_" + uuidSubstring);
        ReflectionUtils.setFieldValue("e", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, ScoreboardTeamBase.EnumNameTagVisibility.NEVER.e);
        ReflectionUtils.setFieldValue("g", PacketPlayOutScoreboardTeam.class, packetPlayOutScoreboardTeam, gameProfile);

        for(Player player: players){
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutScoreboardTeam);
        }
    }

    @Override
    public void hideFromTablist(Player... players) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc);
        for(Player player: players){
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetPlayOutPlayerInfo);
        }
    }

    @Override
    public Set<Player> getViewers() {
        return viewers;
    }

    @Override
    public int getNPCID(){
        return npc.getId();
    }

}
