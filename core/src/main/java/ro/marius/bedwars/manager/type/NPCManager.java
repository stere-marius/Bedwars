package ro.marius.bedwars.manager.type;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.gameobserver.NPCObserver;
import ro.marius.bedwars.game.mechanics.NPCArena;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class NPCManager {

    public final static MetadataValue METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(), "BedwarsStand");
    private final Map<String, List<NPCArena>> npc = new HashMap<>();
    private final File file = new File(BedWarsPlugin.getInstance().getDataFolder(), "npc.yml");
    private YamlConfiguration config = YamlConfiguration.loadConfiguration(this.file);

    public NPCManager() {
        this.loadNPC();
        this.loadNPCGameObservers();
    }

    public void loadNPCGameObservers() {

        ManagerHandler
                .getGameManager()
                .getGames()
                .forEach(game -> game.registerObserver(new NPCObserver(game)));

    }

    public void spawnNPC(Location location, String skinName, String arenaType, List<String> lines, boolean save) {

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        npc.spawn(location);
        Entity npcEntity = npc.getEntity();
        npcEntity.setMetadata("NPCPlayer", new FixedMetadataValue(BedWarsPlugin.getInstance(), arenaType));

        if (npcEntity instanceof SkinnableEntity) {
            ((SkinnableEntity) npcEntity).setSkinName(skinName);
        }

        List<ArmorStand> standList = new ArrayList<>();
        Location clonedLocation = location.clone().add(0, 1.805, 0);
        final double distance = 0.40;
        int playersPlaying = ManagerHandler.getGameManager().getPlayersPlaying(arenaType);

        for (String line : lines) {
            ArmorStand stand = Utils.getSpawnedArmorStand(clonedLocation,
                    Utils.translate(line).replace("<playersPlaying>", playersPlaying + ""));
            stand.setRemoveWhenFarAway(false);
            clonedLocation = clonedLocation.add(0, distance, 0);
            stand.setMetadata("BedwarsStand", METADATA);
            standList.add(stand);
        }

        List<NPCArena> npcList = this.npc.computeIfAbsent(arenaType, k -> new ArrayList<>());

        NPCArena npcArena = new NPCArena(npc, location, arenaType, standList, lines);
        npcList.add(npcArena);

        if (save) {
            String path = "NPC." + npc.getId();
            this.config.set(path + ".Location", Utils.convertingString(location));
            this.config.set(path + ".SkinName", skinName);
            this.config.set(path + ".ArenaType", arenaType);
            this.config.set(path + ".Lines", lines);
            this.saveFile();
        }


    }

    public void deleteNPC() {

        for (Entry<String, List<NPCArena>> entry : this.npc.entrySet()) {

            List<NPCArena> list = entry.getValue();
            list.forEach(obj -> {
                obj.despawnStandList();
                obj.getNpc().despawn();
            });

        }

    }

    public void removeNPC(int id) {

        this.config.set("NPC." + id, null);
        this.saveFile();

    }

//	/bedwars joinNPC spawn SOLO rmellis &e<playersPlaying> Players;&bSolo &7[v1.6];&eCLICK TO PLAY

    public void loadNPC(Location location, String skinName, String arenaType, List<String> lines, int id) {
        NPC npc = CitizensAPI.getNPCRegistry().getById(id);

        if (npc == null) {
            npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        }

        if (!npc.isSpawned()) {
            npc.spawn(location);
        }

        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        Entity npcEntity = npc.getEntity();

        if (npcEntity == null) {
            System.out.println("npcEntity == null");
            return;
        }

        npcEntity.setMetadata("NPCPlayer", new FixedMetadataValue(BedWarsPlugin.getInstance(), arenaType));

        if (npcEntity instanceof SkinnableEntity) {
            ((SkinnableEntity) npcEntity).setSkinName(skinName);
        }

        List<ArmorStand> standList = new ArrayList<>();
        Location clonedLocation = location.clone().add(0, 1.805, 0);
        int size = lines.size();
        final double distance = 0.40;
        int playersPlaying = ManagerHandler.getGameManager().getPlayersPlaying(arenaType);

        for (String line : lines) {
            ArmorStand stand = Utils.getSpawnedArmorStand(clonedLocation, Utils.translate(line).replace("<playersPlaying>", playersPlaying + ""));
            stand.setRemoveWhenFarAway(false);
            stand.setMetadata("BedwarsStand", METADATA);
            clonedLocation = clonedLocation.add(0, distance, 0);
            standList.add(stand);
        }

        List<NPCArena> npcList = this.npc.computeIfAbsent(arenaType, k -> new ArrayList<>());

        NPCArena npcArena = new NPCArena(npc, location, arenaType, standList, lines);
        npcList.add(npcArena);
    }

    public void loadNPC() {

        if (this.config == null) {
            return;
        }
        if (this.config.getConfigurationSection("NPC") == null) {
            return;
        }

        for (String strID : this.config.getConfigurationSection("NPC").getKeys(false)) {

            String path = "NPC." + strID;

            int id = Integer.parseInt(strID);
            Location location = Utils.convertingLocation(this.config.getString(path + ".Location"));
            String skinName = this.config.getString(path + ".SkinName");
            String arenaType = this.config.getString(path + ".ArenaType");
            List<String> lines = this.config.getStringList(path + ".Lines");

            this.loadNPC(location, skinName, arenaType, lines, id);

        }

    }

    public List<NPCArena> getNPCList(Chunk chunk) {

        List<NPCArena> list = new ArrayList<>();

        for (List<NPCArena> npcArena : this.npc.values()) {

            for (NPCArena npc : npcArena) {

                if (!npc.getLocation().getChunk().equals(chunk)) {
                    continue;
                }

                list.add(npc);
            }

        }

        return list;
    }

    public NPCArena getNPCByUUID(UUID uuid) {

        for (List<NPCArena> npcArena : this.npc.values()) {

            for (NPCArena npc : npcArena) {

                if (!npc.getUuid().equals(uuid)) {
                    continue;
                }

                return npc;
            }

        }

        return null;

    }

    public void saveFile() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<NPCArena>> getNpc() {
        return this.npc;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
