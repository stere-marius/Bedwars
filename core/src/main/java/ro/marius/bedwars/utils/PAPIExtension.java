package ro.marius.bedwars.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.playerdata.APlayerData;

import java.util.Set;

public class PAPIExtension extends PlaceholderExpansion {

    private final Set<String> arenaType;

    public PAPIExtension() {
        this.arenaType = ManagerHandler.getGameManager().getArenaType();
        this.sendInfoMessage();
    }

    public void sendInfoMessage() {

        StringBuilder arena = new StringBuilder();

        for (String type : this.arenaType) {

            arena.append("%bedwars_").append(type).append("_gamesPlayed% ,");
            arena.append("%bedwars_").append(type).append("_bedsBroken% ,");
            arena.append("%bedwars_").append(type).append("_bedsLost% ,");
            arena.append("%bedwars_").append(type).append("_kills% ,");
            arena.append("%bedwars_").append(type).append("_deaths% ,");
            arena.append("%bedwars_").append(type).append("_finalKills% ,");
            arena.append("%bedwars_").append(type).append("_finalDeaths% ,");
            arena.append("%bedwars_").append(type).append("_wins% ,");

        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "[Bedwars] &aRegistered PlaceholderAPI placeholders: %bedwars_totalKills% , %bedwars_totalFinalKills% , %bedwars_totalBedsBroken% , %bedwars_totalBedsLost% , %bedwars_totalWins% , %bedwars_totalLosses% , %bedwars_totalGamesPlayed% , %bedwars_totalDeaths% , "
                        + arena));

    }

    @Override
    public boolean persist() {

        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String identifier) {

        if (p == null) {
            return "";
        }

        if ("totalKills".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalKills() + "";
        }

        if ("totalFinalKills".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalFinalKills() + "";
        }

        if ("totalBedsBroken".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalBedsBroken() + "";
        }

        if ("totalBedsLost".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalBedsLost() + "";
        }

        if ("totalWins".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalWins() + "";
        }

        if ("totalLosses".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalDefeats() + "";
        }

        if ("totalGamesPlayed".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalGamesPlayed() + "";
        }

        if ("totalDeaths".equalsIgnoreCase(identifier)) {

            APlayerData playerData = ManagerHandler.getGameManager().getData(p);

            return playerData.getTotalDeaths() + "";
        }

        APlayerData playerData = ManagerHandler.getGameManager().getData(p);

        for (String arenaType : this.arenaType) {

            if (identifier.equals(arenaType + "_gamesPlayed")) {
                return playerData.getGamesPlayed(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_bedsBroken")) {
                return playerData.getBedsBroken(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_bedsLost")) {
                return playerData.getBedsLost(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_kills")) {
                return playerData.getKills(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_deaths")) {
                return playerData.getDeaths(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_finalKills")) {
                return playerData.getFinalKills(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_finalDeaths")) {
                return playerData.getFinalDeaths(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_wins")) {
                return playerData.getWins(arenaType) + "";
            }

            if (identifier.equals(arenaType + "_losses")) {
                return playerData.getWins(arenaType) + "";
            }

        }

        return "NULL-PLACEHOLDER";
    }

    @Override
    public @NotNull String getAuthor() {

        return "Marius";
    }

    @Override
    public @NotNull String getIdentifier() {

        return "bedwars";
    }

    @Override
    public @NotNull String getVersion() {

        return BedWarsPlugin.getInstance().getDescription().getVersion();
    }

}
