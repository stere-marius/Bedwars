package ro.marius.bedwars.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.NMSHologramWrapper;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.playerdata.APlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHologram {

    private final Map<Integer, List<NMSHologramWrapper>> hm = new HashMap<>();
    private Player player;
    private boolean hasChangedWorld;
    private List<Location> locationHolograms;
    private List<String> statsHologramText;

    public PlayerHologram(Player player, List<Location> locationHolograms, List<String> statsHologramText) {
        this.player = player;
        this.locationHolograms = locationHolograms;
        this.statsHologramText = statsHologramText;
    }

    public void spawnOneStatsHologram() {
        APlayerData data = ManagerHandler.getGameManager().getData(this.player);
        int totalKills = data.getTotalKills();
        int totalBedsBroken = data.getTotalBedsBroken();
        int totalFinalKills = data.getTotalFinalKills();
        int totalDeaths = data.getTotalDeaths();
        int totalBedsLost = data.getTotalBedsLost();
        int totalGamesPlayed = data.getTotalGamesPlayed();
        int wins = data.getTotalWins();
        int losses = data.getTotalDefeats();

        List<NMSHologramWrapper> holograms = new ArrayList<NMSHologramWrapper>();

        for (int j = 0; j < this.statsHologramText.size(); j++) {
            double distance = (double) j / 4;
            Location loc = this.player.getLocation().clone().subtract(0, distance, 0);
            String text = this.statsHologramText.get(j).replace("<totalKills>", totalKills + "")
                    .replace("<totalBedsBroken>", totalBedsBroken + "")
                    .replace("<totalFinalKills>", totalFinalKills + "").replace("<totalDeaths>", totalDeaths + "")
                    .replace("<totalBedsLost>", totalBedsLost + "").replace("<totalGamesPlayed>", totalGamesPlayed + "")
                    .replace("<totalWins>", wins + "").replace("<totalLosses>", losses + "");


            NMSHologramWrapper hologram = ManagerHandler.getVersionManager().getIHologram();
            hologram.spawn(loc, text);
            hologram.sendTo(this.player);
            holograms.add(hologram);

            this.hm.put(this.hm.size(), holograms);
        }
    }

    public void spawnAllStatsHologram() {

        if (this.locationHolograms.isEmpty()) {
            return;
        }

        APlayerData data = ManagerHandler.getGameManager().getData(this.player);
        int totalKills = data.getTotalKills();
        int totalBedsBroken = data.getTotalBedsBroken();
        int totalFinalKills = data.getTotalFinalKills();
        int totalDeaths = data.getTotalDeaths();
        int totalBedsLost = data.getTotalBedsLost();
        int totalGamesPlayed = data.getTotalGamesPlayed();
        int wins = data.getTotalWins();
        int losses = data.getTotalDefeats();

        for (int i = 0; i < this.locationHolograms.size(); i++) {
            List<NMSHologramWrapper> lines = new ArrayList<NMSHologramWrapper>();
            for (int j = 0; j < this.statsHologramText.size(); j++) {
                double distance = (double) j / 4;
                Location loc = this.locationHolograms.get(i).clone().subtract(0, distance, 0);
                String text = this.statsHologramText.get(j).replace("<totalKills>", totalKills + "")
                        .replace("<totalBedsBroken>", totalBedsBroken + "")
                        .replace("<totalFinalKills>", totalFinalKills + "").replace("<totalDeaths>", totalDeaths + "")
                        .replace("<totalBedsLost>", totalBedsLost + "")
                        .replace("<totalGamesPlayed>", totalGamesPlayed + "").replace("<totalWins>", wins + "")
                        .replace("<totalLosses>", losses + "");

                NMSHologramWrapper hologram = ManagerHandler.getVersionManager().getIHologram();
                hologram.spawn(loc, text);
                hologram.sendTo(this.player);
                lines.add(hologram);

            }
            this.hm.put(i, lines);
        }

    }

    public void spawnWorldHolograms(World world) {

        if (this.locationHolograms.isEmpty()) {
            return;
        }

        boolean isWorld = false;

        for (Location loc : this.locationHolograms) {

            if (!loc.getWorld().getName().equals(world.getName())) {
                continue;
            }

            isWorld = true;

        }

        if (!isWorld) {
            return;
        }

        List<Location> locations = new ArrayList<Location>();
        APlayerData data = ManagerHandler.getGameManager().getData(this.player);
        int totalKills = data.getTotalKills();
        int totalBedsBroken = data.getTotalBedsBroken();
        int totalFinalKills = data.getTotalFinalKills();
        int totalDeaths = data.getTotalDeaths();
        int totalBedsLost = data.getTotalBedsLost();
        int totalGamesPlayed = data.getTotalGamesPlayed();
        int wins = data.getTotalWins();
        int losses = data.getTotalDefeats();

        for (int i = 0; i < this.locationHolograms.size(); i++) {

            Location loc = this.locationHolograms.get(i);

            if (!loc.getWorld().getName().equals(world.getName())) {
                continue;
            }

            locations.add(loc);

        }

        if (locations.isEmpty()) {
            return;
        }

        for (int i = 0; i < locations.size(); i++) {
            List<NMSHologramWrapper> lines = new ArrayList<NMSHologramWrapper>();
            for (int j = 0; j < this.statsHologramText.size(); j++) {
                double distance = (double) j / 4;
                Location loc = this.locationHolograms.get(i).clone().subtract(0, distance, 0);
                String text = this.statsHologramText.get(j).replace("<totalKills>", totalKills + "")
                        .replace("<totalBedsBroken>", totalBedsBroken + "")
                        .replace("<totalFinalKills>", totalFinalKills + "").replace("<totalDeaths>", totalDeaths + "")
                        .replace("<totalBedsLost>", totalBedsLost + "")
                        .replace("<totalGamesPlayed>", totalGamesPlayed + "").replace("<totalWins>", wins + "")
                        .replace("<totalLosses>", losses + "");

                NMSHologramWrapper hologram = ManagerHandler.getVersionManager().getIHologram();
                hologram.spawn(loc, text);
                hologram.sendTo(this.player);
                lines.add(hologram);

            }
            this.hm.put(i, lines);
        }

    }

    public void updateHologram() {

        new BukkitRunnable() {

            @Override
            public void run() {
                APlayerData data = ManagerHandler.getGameManager().getData(PlayerHologram.this.player);
                int totalKills = data.getTotalKills();
                int totalBedsBroken = data.getTotalBedsBroken();
                int totalFinalKills = data.getTotalFinalKills();
                int totalDeaths = data.getTotalDeaths();
                int totalBedsLost = data.getTotalBedsLost();
                int totalGamesPlayed = data.getTotalGamesPlayed();
                int wins = data.getTotalWins();
                int losses = data.getTotalDefeats();

                for (int i = 0; i < PlayerHologram.this.hm.size(); i++) {
                    List<NMSHologramWrapper> holograms = PlayerHologram.this.hm.get(i);
                    for (int j = 0; j < PlayerHologram.this.statsHologramText.size(); j++) {
                        NMSHologramWrapper hologram = holograms.get(j);
                        String text = PlayerHologram.this.statsHologramText.get(j).replace("<totalKills>", totalKills + "")
                                .replace("<totalBedsBroken>", totalBedsBroken + "")
                                .replace("<totalFinalKills>", totalFinalKills + "")
                                .replace("<totalDeaths>", totalDeaths + "")
                                .replace("<totalBedsLost>", totalBedsLost + "")
                                .replace("<totalGamesPlayed>", totalGamesPlayed + "").replace("<totalWins>", wins + "")
                                .replace("<totalLosses>", losses + "");
                        hologram.setArmorStandText(text);
                        hologram.sendTo(PlayerHologram.this.player);
                    }

                }

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20 * 1);

    }

    public void removeHologram() {

        for (int i = 0; i < this.hm.size(); i++) {
            List<NMSHologramWrapper> holograms = this.hm.get(i);
            for (NMSHologramWrapper hologram : holograms) {
                hologram.remove(this.player);
            }
        }

    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Location> getLocationHolograms() {
        return this.locationHolograms;
    }

    public List<String> getStatsHologramText() {
        return this.statsHologramText;
    }

    public boolean hasChangedWorld() {
        return this.hasChangedWorld;
    }

    public void setHasChangedWorld(boolean hasChangedWorld) {
        this.hasChangedWorld = hasChangedWorld;
    }


}
