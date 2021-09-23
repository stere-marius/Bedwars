package ro.marius.bedwars.npc.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.NPCSkin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.npc.BedwarsJoinNPC;
import ro.marius.bedwars.npc.PlayerInteractJoinNpcEvent;
import ro.marius.bedwars.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class CitizensNPC extends BedwarsJoinNPC {

    private final Map<Integer, NPC> spawnedNpcID = new HashMap<>();

    public void spawnNPC(int id, Location location, String skinName) {
        if (spawnedNpcID.get(id) != null && spawnedNpcID.get(id).isSpawned())
            return;

        NPC npc = createNPC(id);

        if (!npc.isSpawned()) {
            npc.spawn(location);
        }

        if (npc.getEntity() instanceof SkinnableEntity) {
            ((SkinnableEntity) npc.getEntity()).setSkinName(skinName);
        }

        npc.getEntity().setMetadata("NPCPlayer", new FixedMetadataValue(BedWarsPlugin.getInstance(), id));
        spawnedNpcID.put(id, npc);
    }

    public void spawnNPC(int id, Location location, NPCSkin npcSkin) {
        if (spawnedNpcID.get(id) != null && spawnedNpcID.get(id).isSpawned())
            return;

        NPC npc = createNPC(id);

        if (!npc.isSpawned()) {
            npc.spawn(location);
        }

        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, npcSkin.getSignature());
        npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, npcSkin.getValue());
        npc.getEntity().setMetadata("NPCPlayer", new FixedMetadataValue(BedWarsPlugin.getInstance(), id));
        spawnedNpcID.put(id, npc);
    }

    public NPC createNPC(int id) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(id);

        if (npc == null) {
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        }

        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        return npc;
    }

    @Override
    public void updateSkin(int id, NPCSkin npcSkin) {
        if (spawnedNpcID.get(id) == null)
            return;

        NPC spawnedNPC = spawnedNpcID.get(id);
        spawnedNPC.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, npcSkin.getSignature());
        spawnedNPC.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, npcSkin.getValue());
    }

    public void removeNPC(int id) {
        if (spawnedNpcID.get(id) == null) return;
        NPC spawnedNPC = spawnedNpcID.get(id);
        spawnedNPC.despawn();
        CitizensAPI.getNPCRegistry().deregister(spawnedNPC);
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent e) {

        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();

        if (!entity.hasMetadata("NPCPlayer")) {
            return;
        }

        int npcID = entity.getMetadata("NPCPlayer").get(0).asInt();
        PlayerInteractJoinNpcEvent event = new PlayerInteractJoinNpcEvent(p, "INTERACT_AT", entity.getEntityId(), npcID);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onCitizensLoad(CitizensEnableEvent e){
        ManagerHandler.getNPCManager().loadNPC();
    }

}
