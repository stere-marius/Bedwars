package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.PlayerHologram;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HologramManager {

    private final Map<Player, PlayerHologram> playerHologram = new HashMap<>();
    private final List<Location> locationHolograms = new ArrayList<>();
    private final List<String> statsHologramText = new ArrayList<>();
    private final File file = new File(BedWarsPlugin.getInstance().getDataFolder(), "hologram.yml");
    private final YamlConfiguration config = YamlConfiguration.loadConfiguration(this.file);

    public HologramManager() {
        this.generateConfig();
        this.loadStatisticsHologramText();
        this.loadStatisticsHologramLocation();
    }

    public PlayerHologram getPlayerHologram(Player p) {
        PlayerHologram playerHologram = this.getPlayerHologram().get(p);

        if (playerHologram == null) {
            playerHologram = new PlayerHologram(p, this.getLocationHolograms(), this.getStatsHologramText());
            this.getPlayerHologram().put(p, playerHologram);
            return playerHologram;
        }

        return playerHologram;
    }

    public void spawnPlayersHologram() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerHologram hologram = new PlayerHologram(player, this.locationHolograms, this.statsHologramText);
            hologram.spawnAllStatsHologram();
            this.playerHologram.put(player, hologram);
        }

    }

    public void removePlayersHologram() {

        for (Player player : this.playerHologram.keySet()) {
            PlayerHologram hologram = this.playerHologram.get(player);
            hologram.removeHologram();
        }

    }

    public void generateConfig() {
        if (this.config.get("StatisticsHologram.Text") == null) {
            this.config.set("StatisticsHologram.Text",
                    Arrays.asList("&6&lYour Bed Wars Profile", "&fTotal Wins: &a<totalWins>",
                            "&fTotal Kills: &a<totalKills>", "&fTotal Games Played: &a<totalGamesPlayed>",
                            "&fTotal Losses: &a<totalLosses>", "&fTotal Beds Broken: &a<totalBedsBroken>",
                            "&fTotal Beds Lost: &a<totalBedsLost>"));
        }
        this.saveConfig();
    }

    public void loadStatisticsHologramText() {
        if (!this.file.exists()) {
            return;
        }
        if (this.config.getStringList("StatisticsHologram.Text") == null) {
            return;
        }
        if (this.config.getStringList("StatisticsHologram.Text").isEmpty()) {
            return;
        }
        this.config.getStringList("StatisticsHologram.Text").forEach(s -> this.statsHologramText.add(Utils.translate(s)));
    }

    public void loadStatisticsHologramLocation() {
        if (!this.file.exists()) {
            return;
        }
        if (this.config.getStringList("StatisticsHologram.Location").isEmpty()) {
            return;
        }

        for (String str : this.config.getStringList("StatisticsHologram.Location")) {

            Location location = Utils.convertingLocation(str);

            if (!((location != null) && (location.getWorld() != null))) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED
                        + "[Bedwars] Couldn't load hologram at the location " + str + " . The world might not exist.");
                continue;
            }

            this.locationHolograms.add(location);
        }

    }

    public void addLocation(Location loc) {
        this.locationHolograms.add(loc);
        this.config.set("StatisticsHologram.Location", Utils.setConvertingLocations(this.locationHolograms));
        this.saveConfig();
    }

    public void removeLocation(Location loc) {
        this.locationHolograms.remove(loc);
        this.config.set("StatisticsHologram.Location", Utils.setConvertingLocations(this.locationHolograms));
        this.saveConfig();
    }

    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Player, PlayerHologram> getPlayerHologram() {
        return this.playerHologram;
    }

    public List<Location> getLocationHolograms() {
        return this.locationHolograms;
    }

    public List<String> getStatsHologramText() {
        return this.statsHologramText;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }
}
