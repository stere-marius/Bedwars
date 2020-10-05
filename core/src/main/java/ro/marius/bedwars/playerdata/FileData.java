package ro.marius.bedwars.playerdata;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

public class FileData extends APlayerData {

    public File file;
    public YamlConfiguration config;

    public FileData(Player player) {
        super(player);

        this.file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "data",
                super.getPlayer().getUniqueId() + ".yml");
        if (!(this.file.exists())) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public void loadData() {

        String skin = this.config.getString("SkinName");
        this.setSkin((skin == null) ? "VILLAGER" : skin);

        Set<String> arenaType = ManagerHandler.getGameManager().getArenaType();

        for (String type : arenaType) {

            ArenaData data = new ArenaData();
            data.setBedsBroken(this.config.getInt("ArenaType." + type + ".BedsBroken"));
            data.setBedsLost(this.config.getInt("ArenaType." + type + ".BedsLost"));
            data.setDeaths(this.config.getInt("ArenaType." + type + ".Deaths"));
            data.setFinalDeaths(this.config.getInt("ArenaType." + type + ".FinalDeaths"));
            data.setGamesPlayed(this.config.getInt("ArenaType." + type + ".GamesPlayed"));
            data.setKills(this.config.getInt("ArenaType." + type + ".Kills"));
            data.setLosses(this.config.getInt("ArenaType." + type + ".Losses"));
            data.setWins(this.config.getInt("ArenaType." + type + ".Wins"));

            ConfigurationSection section = this.config.getConfigurationSection("ArenaType." + type + ".QuickBuy");

            if (section != null) {
                for (String s : section.getKeys(false)) {
                    data.getQuickBuy().put(Integer.parseInt(s),
                            this.config.getString("ArenaType." + type + ".QuickBuy." + s));
                }
            }

            this.getArenaData().put(type, data);

        }

    }

    @Override
    public void saveData() {

        for (Entry<String, ArenaData> entry : this.getArenaData().entrySet()) {

            String type = entry.getKey();
            ArenaData data = entry.getValue();

            this.config.set("ArenaType." + type + ".BedsBroken", data.getBedsBroken());
            this.config.set("ArenaType." + type + ".BedsLost", data.getBedsLost());
            this.config.set("ArenaType." + type + ".Deaths", data.getDeaths());
            this.config.set("ArenaType." + type + ".FinalDeaths", data.getFinalDeaths());
            this.config.set("ArenaType." + type + ".GamesPlayed", data.getGamesPlayed());
            this.config.set("ArenaType." + type + ".Kills", data.getKills());
            this.config.set("ArenaType." + type + ".Losses", data.getLosses());
            this.config.set("ArenaType." + type + ".Wins", data.getWins());

            if (!data.getQuickBuy().isEmpty()) {
                data.getQuickBuy().forEach((i, s) -> this.config.set("ArenaType." + type + ".QuickBuy." + i, s));
            }

        }

        this.config.set("SkinName", this.getSkin());

        this.saveConfig();

    }

    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void loadData(String arenaType) {

        ArenaData arenaData = this.getArenaData().get(arenaType);

        if (arenaData == null) {
            this.getArenaData().put(arenaType, new ArenaData());
        }

    }

}
