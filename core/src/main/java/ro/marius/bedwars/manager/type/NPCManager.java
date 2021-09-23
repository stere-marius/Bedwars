package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.NPCSkin;
import ro.marius.bedwars.game.gameobserver.NPCObserver;
import ro.marius.bedwars.game.mechanics.NPCArena;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.npc.BedwarsJoinNPC;
import ro.marius.bedwars.npc.SkinFetcher;
import ro.marius.bedwars.npc.bedwars.BedwarsNPC;
import ro.marius.bedwars.npc.citizens.CitizensNPC;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class NPCManager {

    public final static MetadataValue METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(), "BedwarsStand");
    private final Map<String, List<NPCArena>> arenaTypeNpc = new HashMap<>();
    private final Map<Integer, String> npcIdArenaType = new HashMap<>();
    private final File file = new File(BedWarsPlugin.getInstance().getDataFolder(), "npc.yml");
    private YamlConfiguration config = YamlConfiguration.loadConfiguration(this.file);

    private BedwarsJoinNPC joinNPC;

    public NPCManager() {
        this.loadNpcAdapter();
    }

    public void loadNPCGameObservers() {
        ManagerHandler
                .getGameManager()
                .getGames()
                .forEach(game -> game.registerObserver(new NPCObserver(game)));
    }


    public void loadNpcAdapter() {

        String joinNPCAdapter = BedWarsPlugin.getInstance().getConfig().getString("JoinArenaNpcAdapter", "BEDWARS");
        NPCManager.this.createJoinNPC(joinNPCAdapter);
        Bukkit.getServer().getPluginManager().registerEvents(joinNPC, BedWarsPlugin.getInstance());

        // For Citizens adapter the load NPC logic will be handled when the Citizens plugin gets loaded
        if (!joinNPCAdapter.equalsIgnoreCase("CITIZENS")) {
            loadNPC();
        }
    }

    public void createJoinNPC(String joinNpcAdapter) {
        if (this.joinNPC != null) return;
        this.joinNPC = "CITIZENS".equalsIgnoreCase(joinNpcAdapter) ? new CitizensNPC() : new BedwarsNPC();
    }

    public void spawnNPC(int index, Location location, String skinName, String arenaType, List<String> lines) {
        joinNPC.spawnNPC(index, location, skinName);
        setupNPCArena(index, location, arenaType, lines);
    }

    public void spawnNPC(int index, Location location, NPCSkin npcSkin, String arenaType, List<String> lines) {
        joinNPC.spawnNPC(index, location, npcSkin);
        setupNPCArena(index, location, arenaType, lines);
    }

    private void setupNPCArena(int index, Location location, String arenaType, List<String> lines) {
        List<NPCArena> npcList = this.arenaTypeNpc.computeIfAbsent(arenaType, k -> new ArrayList<>());
        NPCArena npcArena = new NPCArena(index, location, arenaType, lines);
        npcArena.getNpcHologram().spawnHolograms(ManagerHandler.getGameManager().getPlayersPlaying(arenaType));
        npcList.add(npcArena);
    }


    public void deleteNPC() {

        for (Entry<String, List<NPCArena>> entry : this.arenaTypeNpc.entrySet()) {

            List<NPCArena> list = entry.getValue();
            list.forEach(obj -> {
                obj.getNpcHologram().despawnHolograms();
                joinNPC.removeNPC(obj.getIndex());
            });

        }

    }

    private String getNpcConfigPath(int id) {
        return "NPC." + id;
    }

    private void saveNPC(Location location, int id, String arenaType, List<String> lines) {
        String path = getNpcConfigPath(id);
        this.config.set(path + ".Location", Utils.convertingString(location));
        this.config.set(path + ".ArenaType", arenaType);
        this.config.set(path + ".Lines", lines);
    }

    public void saveNPC(Location location, int id, String skinName, String arenaType, List<String> lines) {
        saveNPC(location, id, arenaType, lines);
        this.config.set(getNpcConfigPath(id) + ".SkinName", skinName);
        this.saveFile();
    }

    public void saveNPC(Location location, int id, NPCSkin npcSkin, String arenaType, List<String> lines) {
        saveNPC(location, id, arenaType, lines);
        this.config.set(getNpcConfigPath(id) + ".Skin.Value", npcSkin.getValue());
        this.config.set(getNpcConfigPath(id) + ".Skin.Signature", npcSkin.getSignature());
        this.saveFile();
    }

    public void removeNPC(int id) {
        this.config.set(getNpcConfigPath(id), null);
        this.saveFile();
    }

    public void despawnNPC(int id) {
        joinNPC.removeNPC(id);
    }

    public void setSkin(int id, NPCSkin npcSkin) {
        joinNPC.updateSkin(id, npcSkin);
        this.config.set(getNpcConfigPath(id) + ".Skin.Value", npcSkin.getValue());
        this.config.set(getNpcConfigPath(id) + ".Skin.Signature", npcSkin.getSignature());
        this.saveFile();
    }

    public int getNewNpcID() {
        ConfigurationSection npcConfigurationSection = this.config.getConfigurationSection("NPC");
        return npcConfigurationSection == null ? 0 : npcConfigurationSection.getKeys(false).size();
    }

//	/bedwars joinNPC spawn SOLO rmellis &e<playersPlaying> Players;&bSolo &7[v1.6];&eCLICK TO PLAY

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
            String skinValue = this.config.getString(path + ".Skin.Value");
            String skinSignature = this.config.getString(path + ".Skin.Signature");
            String arenaType = this.config.getString(path + ".ArenaType");
            List<String> lines = this.config.getStringList(path + ".Lines");
            npcIdArenaType.put(id, arenaType);

            if (skinValue != null && skinSignature != null) {
                spawnNPC(id, location, new NPCSkin(skinValue, skinSignature), arenaType, lines);
                continue;
            }

            if (skinName != null) {
                spawnNPC(id, location, skinName, arenaType, lines);
                continue;
            }

            spawnNPC(id, location, SkinFetcher.DEFAULT_NPC_SKIN, arenaType, lines);
        }

    }

    public List<NPCArena> getNPCList(Chunk chunk) {

        List<NPCArena> list = new ArrayList<>();

        for (List<NPCArena> npcArena : this.arenaTypeNpc.values()) {

            for (NPCArena npc : npcArena) {

                if (!npc.getNpcHologram().getLocation().getChunk().equals(chunk)) {
                    continue;
                }

                list.add(npc);
            }

        }

        return list;
    }

    public NPCArena getNPCByUUID(UUID uuid) {

        for (List<NPCArena> npcArena : this.arenaTypeNpc.values()) {

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

    public BedwarsJoinNPC getJoinNPC() {
        return joinNPC;
    }

    public Map<String, List<NPCArena>> getArenaTypeNpc() {
        return this.arenaTypeNpc;
    }

    public Map<Integer, String> getNpcIdArenaType() {
        return npcIdArenaType;
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
