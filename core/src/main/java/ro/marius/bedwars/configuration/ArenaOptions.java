package ro.marius.bedwars.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.generator.GeneratorTier;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaOptions {

    private String arenaType = "DEFAULT";

    private File defaultFile = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings",
            "default.yml");
    private FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(this.defaultFile);

    private File file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings", "default.yml");
    private FileConfiguration config = YamlConfiguration.loadConfiguration(this.file);

    public ArenaOptions(String arenaType, String file) {
        File f = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings", file);
        this.file = f;
        this.config = YamlConfiguration.loadConfiguration(f);
        this.arenaType = arenaType;
    }

    public ArenaOptions() {

    }

    public static Map<String, ArenaOptions> loadConfiguration() {

        Map<String, ArenaOptions> map = new HashMap<>();

        File folder = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings");
        File[] folderFiles = folder.listFiles();

        if (folderFiles == null) {
            return Collections.unmodifiableMap(map);
        }

        for (File file : folderFiles) {

            if (file.isDirectory()) {
                continue;
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (config.getConfigurationSection("ArenaType") == null) {
                continue;
            }

            for (String s : config.getConfigurationSection("ArenaType").getKeys(false)) {
                map.put(s, new ArenaOptions(s, file.getName()));
            }

        }

        return map;
    }

    public void generateOptions(String arenaType, boolean separateFile) {

        if (separateFile) {
            String fileName = arenaType.toLowerCase() + ".yml";
            String parent = BedWarsPlugin.getInstance().getDataFolder() + File.separator + "ArenaSettings";
            File file = this.file = new File(parent, fileName);
            FileConfiguration config = this.config = YamlConfiguration.loadConfiguration(file);
            this.loadConfiguration(file, config, arenaType);
            this.arenaType = arenaType;
            return;
        }

        this.loadConfiguration(this.defaultFile, this.defaultConfig, arenaType);
        this.arenaType = arenaType;
    }

    public void performCommands(String path, Player p) {
        if (!this.getPathBoolean(path + ".Enabled")) {
            return;
        }
        if (this.getPathStringList(path + ".Commands").isEmpty()) {
            return;
        }

        List<String> commands = this.getPathStringList(path + ".Commands");
        commands.forEach(command -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("<player>", p.getName())));
    }

    public void performCommands(String path, Player p, boolean perform) {

        if (!perform) {
            return;
        }

        performCommands(path, p);
    }

    public int getPathInt(String path) {

        return this.config.getInt("ArenaType." + this.arenaType + "." + path);
    }

    public String getPathString(String path) {

        return this.config.getString("ArenaType." + this.arenaType + "." + path);
    }

    public List<String> getPathStringList(String path) {

        return this.config.getStringList("ArenaType." + this.arenaType + "." + path);
    }

    public boolean getPathBoolean(String path) {
        return this.config.getBoolean("ArenaType." + this.arenaType + "." + path);
    }

    public int getInt(String path) {

        Object obj = this.config.get("ArenaType." + this.arenaType + "." + path);

        if (obj == null) {
            return this.defaultConfig.getInt("ArenaType.DEFAULT." + path);
        }

        return (int) obj;
    }

    public String getString(String path) {

        String obj = this.config.getString("ArenaType." + this.arenaType + "." + path);

        if (obj == null) {
            return this.defaultConfig.getString("ArenaType.DEFAULT." + path);
        }

        return obj;
    }

    public List<String> getStringList(String path) {

        List<String> obj = this.config.getStringList("ArenaType." + this.arenaType + "." + path);

        if (obj.isEmpty()) {
            return this.defaultConfig.getStringList("ArenaType.DEFAULT." + path);
        }

        return obj;
    }

    public double getDouble(String path) {

        Object obj = this.config.get("ArenaType." + this.arenaType + "." + path);

        if (obj == null) {
            return this.defaultConfig.getDouble("ArenaType.DEFAULT." + path);
        }

        return (double) obj;
    }

    public boolean getBoolean(String path) {

        Object obj = this.config.get("ArenaType." + this.arenaType + "." + path);

        if (obj == null) {
            return this.defaultConfig.getBoolean("ArenaType.DEFAULT." + path);
        }

        return (boolean) obj;
    }

    public void loadConfiguration(File file, FileConfiguration config, String arenaType) {

        String path = "ArenaType." + arenaType + ".";
        String diamondGen = path + ".DiamondGenerator";
        String emeraldGen = path + ".EmeraldGenerator";

        config.addDefault(path + ".Rejoin", true);
        config.addDefault(path + ".TablistTeams", true);
        config.addDefault(path + ".SpawnNPCsOnEmptyTeams", false);
        config.addDefault(path + ".NPCsSpawnDelay", 25);
        config.addDefault(path + ".BlockedCommands.EnableForOp", false);
        config.addDefault(path + ".BlockedCommands.List", Arrays.asList("say", "gamemode", "mute", "spawn"));
        config.addDefault(path + ".DuringGame.AllowTeleporting.OP", true);
        config.addDefault(path + ".DuringGame.AllowTeleporting.Players", false);
        config.addDefault(path + ".DuringGame.AllowChangingWorld.OP", true);
        config.addDefault(path + ".DuringGame.AllowChangingWorld.Players", false);

        for (FloorGeneratorType type : FloorGeneratorType.values()) {
            config.addDefault(path + "." + type.getIsLimitPath(), true);
            config.addDefault(path + "." + type.getLimitAmountPath(), 68);
        }

        config.addDefault(path + ".EventDisplay.EmeraldUpgrade", "Emerald Upgrade");
        config.addDefault(path + ".EventDisplay.DiamondUpgrade", "Diamond Upgrade");
        config.addDefault(path + ".EventDisplay.BedGone", "Bed Gone");
        config.addDefault(path + ".EventDisplay.TimeLeft", "Time Left");

        config.addDefault(path + ".BlockPlaceProtection.AirGenerators.Enabled", true);
        config.addDefault(path + ".BlockPlaceProtection.AirGenerators.ProtectionRadius", 2.0);
        config.addDefault(path + ".BlockPlaceProtection.ShopLocation.Enabled", true);
        config.addDefault(path + ".BlockPlaceProtection.ShopLocation.ProtectionRadius", 3.0);
        config.addDefault(path + ".BlockPlaceProtection.UpgradeLocation.Enabled", true);
        config.addDefault(path + ".BlockPlaceProtection.UpgradeLocation.ProtectionRadius", 3.0);
        config.addDefault(path + ".BlockPlaceProtection.SpawnLocation.Enabled", true);
        config.addDefault(path + ".BlockPlaceProtection.SpawnLocation.ProtectionRadius", 3.0);

        config.addDefault(path + ".ShopHologram.Lines", Arrays.asList("&bITEM SHOP", "&e&lRIGHT CLICK"));
        config.addDefault(path + ".UpgradeHologram.Lines", Arrays.asList("&bSOLO", "&bUPGRADES", "&e&lRIGHT CLICK"));

        config.addDefault(diamondGen + ".TextLines",
                Arrays.asList("&eTier &c<tier>", "&b&lDiamond", "&eSpawns in &c<spawnsIn> &eseconds"));

        config.addDefault(diamondGen + ".Tier.I.SpawnTime", 46);
        config.addDefault(diamondGen + ".Tier.I.SpawnAmount", 1);
        config.addDefault(diamondGen + ".Tier.I.Limit.Enabled", true);
        config.addDefault(diamondGen + ".Tier.I.Limit.Amount", 4);

        config.addDefault(diamondGen + ".Tier.II.Message", "&bDiamond generators &ehave been upgraded to Tier &cII");
        config.addDefault(diamondGen + ".Tier.II.SpawnTime", 23);
        config.addDefault(diamondGen + ".Tier.II.SpawnAmount", 1);
        config.addDefault(diamondGen + ".Tier.II.Limit.Enabled", true);
        config.addDefault(diamondGen + ".Tier.II.Limit.Amount", 4);

        config.addDefault(diamondGen + ".Tier.III.Message", "&bDiamond generators &ehave been upgraded to Tier &cIII");
        config.addDefault(diamondGen + ".Tier.III.SpawnTime", 13);
        config.addDefault(diamondGen + ".Tier.III.SpawnAmount", 1);
        config.addDefault(diamondGen + ".Tier.III.Limit.Enabled", true);
        config.addDefault(diamondGen + ".Tier.III.Limit.Amount", 4);

        config.addDefault(emeraldGen + ".TextLines",
                Arrays.asList("&eTier &c<tier>", "&2&lEmerald", "&eSpawns in &c<spawnsIn> &eseconds"));

        config.addDefault(emeraldGen + ".Tier.I.SpawnTime", 50);
        config.addDefault(emeraldGen + ".Tier.I.SpawnAmount", 1);
        config.addDefault(emeraldGen + ".Tier.I.Limit.Enabled", true);
        config.addDefault(emeraldGen + ".Tier.I.Limit.Amount", 2);

        config.addDefault(emeraldGen + ".Tier.II.Message", "&2Emerald generators &ehave been upgraded to Tier &cII");
        config.addDefault(emeraldGen + ".Tier.II.SpawnTime", 25);
        config.addDefault(emeraldGen + ".Tier.II.SpawnAmount", 1);
        config.addDefault(emeraldGen + ".Tier.II.Limit.Enabled", true);
        config.addDefault(emeraldGen + ".Tier.II.Limit.Amount", 2);

        config.addDefault(emeraldGen + ".Tier.III.Message", "&2Emerald generators &ehave been upgraded to Tier &cIII");
        config.addDefault(emeraldGen + ".Tier.III.SpawnTime", 15);
        config.addDefault(emeraldGen + ".Tier.III.SpawnAmount", 1);
        config.addDefault(emeraldGen + ".Tier.III.Limit.Enabled", true);
        config.addDefault(emeraldGen + ".Tier.III.Limit.Amount", 2);

        config.addDefault(path + ".IceFishLimit", 3);
        config.addDefault(path + ".IceFishName", "<teamColor><teamName>'s Silverfish");
        config.addDefault(path + ".IronGolemLimit", 3);
        config.addDefault(path + ".IronGolemName", "<teamColor><teamName>'s Iron Golem");

        config.addDefault(path + ".HealthBar.Enabled", Boolean.TRUE);
        config.addDefault(path + ".HealthBar.Display", "&4�?�");

        config.addDefault(path + ".SeparateChatArena", false);
        config.addDefault(path + ".ModifiedChat.InWaiting.Enabled", Boolean.TRUE);
        config.addDefault(path + ".ModifiedChat.InWaiting.Format", "&7<player>: <message>");
        config.addDefault(path + ".ModifiedChat.Spectator.Enabled", Boolean.TRUE);
        config.addDefault(path + ".ModifiedChat.Spectator.Format", "&7[SPECTATOR] <player>: <message>");
        config.addDefault(path + ".ModifiedChat.Match.Enabled", Boolean.TRUE);
        config.addDefault(path + ".ModifiedChat.Match.Format", "<playerTeamColor>[<playerTeam>] &7<player>: <message>");
        config.addDefault(path + ".ModifiedChat.Shout.Format",
                "&6[SHOUT] <playerTeamColor> [<playerTeamNameUpperCase>] &7<player>: <message>");

        config.addDefault(path + ".BedBreakCommands.Enabled", Boolean.FALSE);
        config.addDefault(path + ".BedBreakCommands.Commands", Collections.singletonList("say <player> break a bed."));
        config.addDefault(path + ".FinalKillCommands.Enabled", Boolean.FALSE);
        config.addDefault(path + ".FinalKillCommands.Commands", Collections.singletonList("say <player> have made a final kill."));
        config.addDefault(path + ".LoserCommands.Enabled", Boolean.FALSE);
        config.addDefault(path + ".LoserCommands.Commands", Collections.singletonList("say <player> lost the match."));
        config.addDefault(path + ".WinnerCommands.Enabled", Boolean.FALSE);
        config.addDefault(path + ".WinnerCommands.Commands", Collections.singletonList("say <player> won the match."));

        config.addDefault(path + ".ArenaSign.FirstLine", "&5&lBedwars");
        config.addDefault(path + ".ArenaSign.SecondLine", "<mode>");
        config.addDefault(path + ".ArenaSign.ThirdLine", "<arenaName>");
        config.addDefault(path + ".ArenaSign.FourthLine", "&7<inGame>/<maxPlayers>");
        config.addDefault(path + ".ArenaSign.InGameDisplay", "&cIn Game");
        config.addDefault(path + ".ArenaSign.InWaitingDisplay", "&aIn waiting");

        config.addDefault(path + ".BedDestroyedTitle.Enabled", Boolean.TRUE);
        config.addDefault(path + ".BedDestroyedTitle.Title", "&cBED DESTROYED!");
        config.addDefault(path + ".BedDestroyedTitle.SubTitle", "&fYou will no longer respawn!");
        config.addDefault(path + ".BedDestroyedTitle.Stay", 20);
        config.addDefault(path + ".BedDestroyedTitle.FadeIn", 20);
        config.addDefault(path + ".BedDestroyedTitle.FadeOut", 20);

        config.addDefault(path + ".CountdownTitle.Enabled", Boolean.TRUE);
        config.addDefault(path + ".CountdownTitle.Title", "&a<seconds>");
        config.addDefault(path + ".CountdownTitle.SubTitle", " ");
        config.addDefault(path + ".CountdownTitle.Stay", 20);
        config.addDefault(path + ".CountdownTitle.FadeIn", 20);
        config.addDefault(path + ".CountdownTitle.FadeOut", 20);

        config.addDefault(path + ".StartTitle.Title", "&a&lGO!");
        config.addDefault(path + ".StartTitle.SubTitle", " ");
        config.addDefault(path + ".StartTitle.Stay", 20);
        config.addDefault(path + ".StartTitle.FadeIn", 20);
        config.addDefault(path + ".StartTitle.FadeOut", 20);

        config.addDefault(path + ".WinTitle.Enabled", Boolean.TRUE);
        config.addDefault(path + ".WinTitle.Title", "&6&lWINNER");
        config.addDefault(path + ".WinTitle.SubTitle", "");
        config.addDefault(path + ".WinTitle.Stay", 20);
        config.addDefault(path + ".WinTitle.FadeIn", 20);
        config.addDefault(path + ".WinTitle.FadeOut", 20);

        config.addDefault(path + ".RespawnTitle.Enabled", Boolean.TRUE);
        config.addDefault(path + ".RespawnTitle.Title", "&cYOU DIED!");
        config.addDefault(path + ".RespawnTitle.SubTitle", "&eYou will be respawned in <seconds> seconds!");
        config.addDefault(path + ".RespawnTitle.FadeIn", 20);
        config.addDefault(path + ".RespawnTitle.Stay", 20);
        config.addDefault(path + ".RespawnTitle.FadeOut", 20);

        config.addDefault(path + ".RespawnedTitle.FadeIn", 20);
        config.addDefault(path + ".RespawnedTitle.Stay", 20);
        config.addDefault(path + ".RespawnedTitle.FadeOut", 20);

        config.addDefault(path + ".RespawnedTitle.Enabled", Boolean.TRUE);
        config.addDefault(path + ".RespawnedTitle.Title", "&aYou have been respawned.");
        config.addDefault(path + ".RespawnedTitle.SubTitle", "");
        config.addDefault(path + ".RespawnedTitle.FadeIn", 20);
        config.addDefault(path + ".RespawnedTitle.Stay", 40);
        config.addDefault(path + ".RespawnedTitle.FadeOut", 20);

        config.addDefault(path + ".AllBedsDestroyed.Enabled", Boolean.TRUE);
        config.addDefault(path + ".AllBedsDestroyed.Title", "&cAll beds have been destroyed!");
        config.addDefault(path + ".AllBedsDestroyed.SubTitle", "");
        config.addDefault(path + ".AllBedsDestroyed.FadeIn", 20);
        config.addDefault(path + ".AllBedsDestroyed.Stay", 40);
        config.addDefault(path + ".AllBedsDestroyed.FadeOut", 20);

        config.options().copyDefaults(true);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadDefaultConfiguration() {
        this.loadConfiguration(this.defaultFile, this.defaultConfig, "DEFAULT");
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public Map<Integer, GeneratorTier> getTier(String generatorType, String arenaType) {

        Map<Integer, GeneratorTier> map = new HashMap<>();
        String path = "ArenaType." + arenaType + "." + generatorType + ".Tier";
        int tier = 0;

        for (String s : this.config.getConfigurationSection(path).getKeys(false)) {

            tier++;

            int time = this.config.getInt(path + "." + s + ".SpawnTime");
            int amount = this.config.getInt(path + "." + s + ".SpawnAmount");
            boolean spawnLimit = this.config.getBoolean(path + "." + s + ".Limit.Enabled");
            int limitAmount = this.config.getInt(path + "." + s + ".Limit.Amount");
            String message = this.config.getString(path + "." + s + ".Message");

            if (message == null) {
                message = "";
            }

            GeneratorTier genTier = new GeneratorTier(time, amount, Utils.translate(message));
            genTier.setSpawnLimit(spawnLimit);
            genTier.setLimitAmount(limitAmount);

            map.put(tier, genTier);

        }

        return map;
    }

    public String getArenaType() {
        return this.arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getDefaultFile() {
        return defaultFile;
    }

    public FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }
}
