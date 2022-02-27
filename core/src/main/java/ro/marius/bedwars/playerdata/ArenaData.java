package ro.marius.bedwars.playerdata;

import java.util.HashMap;
import java.util.Map;

public class ArenaData {

    private int gamesPlayed;
    private int bedsBroken;
    private int bedsLost;
    private int kills;
    private int deaths;
    private int finalKills;
    private int finalDeaths;
    private int wins;
    private int losses;
    private Map<Integer, String> quickBuy = new HashMap<>();

    public ArenaData(int gamesPlayed,
                     int bedsBroken,
                     int bedsLost,
                     int kills,
                     int deaths,
                     int finalKills,
                     int finalDeaths,
                     int wins,
                     int losses,
                     Map<Integer, String> quickBuy) {
        this.gamesPlayed = gamesPlayed;
        this.bedsBroken = bedsBroken;
        this.bedsLost = bedsLost;
        this.kills = kills;
        this.deaths = deaths;
        this.finalKills = finalKills;
        this.finalDeaths = finalDeaths;
        this.wins = wins;
        this.losses = losses;
        this.quickBuy = quickBuy;
    }

    public ArenaData() {

    }

    public void addGamePlayed() {
        this.gamesPlayed++;
    }

    public void addWin() {
        this.wins++;
    }

    public void addFinalDeath() {
        this.finalDeaths++;
    }

    public void addLoss() {
        this.losses++;
    }

    public void addBedLost() {
        this.bedsLost++;
    }


    public void addBedBroken(int amount) {
        this.bedsBroken += amount;
    }

    public void addKills(int amount) {
        this.kills += amount;
    }

    public void addDeaths(int amount) {
        this.deaths += amount;
    }

    public void addFinalKill(int amount) {
        this.finalKills += amount;
    }


    public int getGamesPlayed() {
        return this.gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getBedsBroken() {
        return this.bedsBroken;
    }

    public void setBedsBroken(int bedsBroken) {
        this.bedsBroken = bedsBroken;
    }

    public int getBedsLost() {
        return this.bedsLost;
    }

    public void setBedsLost(int bedsLost) {
        this.bedsLost = bedsLost;
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getFinalKills() {
        return this.finalKills;
    }

    public void setFinalKills(int finalKills) {
        this.finalKills = finalKills;
    }

    public int getFinalDeaths() {
        return this.finalDeaths;
    }

    public void setFinalDeaths(int finalDeaths) {
        this.finalDeaths = finalDeaths;
    }

    public int getWins() {
        return this.wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return this.losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public Map<Integer, String> getQuickBuy() {
        return this.quickBuy;
    }

    public void setQuickBuy(Map<Integer, String> quickBuy) {
        this.quickBuy = quickBuy;
    }
}
