package ro.marius.bedwars.npc.bedwars;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginEnableEvent;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.NPCPlayer;
import ro.marius.bedwars.NPCSkin;
import ro.marius.bedwars.manager.factory.BedwarsNPCFactory;
import ro.marius.bedwars.npc.BedwarsJoinNPC;
import ro.marius.bedwars.npc.PlayerInteractJoinNpcEvent;
import ro.marius.bedwars.npc.SkinFetcher;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.*;
import java.util.stream.Stream;

public class BedwarsNPC extends BedwarsJoinNPC {

    private final BiMap<Integer, NPCPlayer> spawnedNpcID = HashBiMap.create();
    private final Map<Player, PacketInjector> playerPacketInjector = new HashMap<>();

    public void spawnNPC(int id, Location location, String skinName) {
        SkinFetcher.getSkinFromName(skinName,
                npcSkin -> Bukkit.getScheduler().runTask(BedWarsPlugin.getInstance(), () -> spawnNPC(id, location, npcSkin)));
    }

    public void spawnNPC(int id, Location location, NPCSkin npcSkin) {
        NPCPlayer joinNPC = spawnedNpcID.getOrDefault(id, BedwarsNPCFactory.getNpc());
        joinNPC.setSkin(npcSkin);
        joinNPC.createNPC(location, npcSkin);
        Bukkit.getOnlinePlayers().forEach(p -> sendNpcSpawnPackets(joinNPC, p));
        spawnedNpcID.put(id, joinNPC);
    }

    public void sendNpcSpawnPackets(NPCPlayer npcPlayer, Player player) {
        npcPlayer.sendSpawnPackets(new HashSet<>(Collections.singletonList(player)));
        npcPlayer.sendPacketsHideName(player);
        npcPlayer.getViewers().add(player);

        Bukkit.getScheduler().runTaskLater(BedWarsPlugin.getInstance(), () -> npcPlayer.sendPacketsRemoveTablist(player), 60L);
    }

    @Override
    public void updateSkin(int id, NPCSkin npcSkin) {
        if (spawnedNpcID.get(id) == null) return;

        NPCPlayer joinNPC = spawnedNpcID.get(id);
        joinNPC.updateSkin(npcSkin);
    }

    public void removeNPC(int id) {
        NPCPlayer spawnedNPC = spawnedNpcID.get(id);
        spawnedNPC.remove();
        spawnedNPC.getViewers().clear();
        spawnedNpcID.remove(id);
    }

    public void injectPacketListener(Player player) {
        PacketInjector packetInjector = new PacketInjector();
        packetInjector.injectPlayer(player, new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
                    int id = (Integer) ReflectionUtils.getField("a", packet);
                    String action = ReflectionUtils.getField("action", packet).toString();
                    if (action.equalsIgnoreCase("INTERACT_AT")) {
                        Stream<NPCPlayer> stream = spawnedNpcID.values().stream();
                        Optional<NPCPlayer> npcPlayerOptional = stream.filter(npc -> npc.getBukkitEntity().getEntityId() == id).findFirst();
                        npcPlayerOptional.ifPresent(npcPlayer -> Bukkit.getScheduler().scheduleSyncDelayedTask(BedWarsPlugin.getInstance(), () -> {
                            PlayerInteractJoinNpcEvent event = new PlayerInteractJoinNpcEvent(player, action, id, spawnedNpcID.inverse().get(npcPlayer));
                            Bukkit.getServer().getPluginManager().callEvent(event);
                        }));
                    }
                    ;

                }
                super.channelRead(ctx, packet);
            }
        });
        playerPacketInjector.put(player, packetInjector);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        spawnedNpcID.values().forEach(npc -> sendNpcSpawnPackets(npc, e.getPlayer()));
        injectPacketListener(e.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        String worldName = e.getPlayer().getLocation().getWorld().getName();
        Collection<NPCPlayer> values = spawnedNpcID.values();
        values.stream().filter(npcPlayer -> npcPlayer.getBukkitEntity().getLocation().getWorld().getName().equals(worldName))
                .forEach(npc -> sendNpcSpawnPackets(npc, e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (playerPacketInjector.containsKey(e.getPlayer())) {
            playerPacketInjector.get(e.getPlayer()).removePlayer(e.getPlayer());
        }
        spawnedNpcID.values().forEach(npc -> npc.getViewers().remove(e.getPlayer()));
    }

    @EventHandler
    public void onPluginLoad(PluginEnableEvent e) {
        Bukkit.getOnlinePlayers().forEach(this::injectPacketListener);
    }

    public BiMap<Integer, NPCPlayer> getSpawnedNpcID() {
        return spawnedNpcID;
    }
}
