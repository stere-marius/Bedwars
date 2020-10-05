package ro.marius.bedwars.playerdata;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class APlayerData {

    private Player player;
    private String skin;
    private BedwarsLevel level;
    private Map<String, ArenaData> arenaData = new HashMap<>();

    public APlayerData(Player player) {
        this.player = player;
    }

    public abstract void loadData();

    public abstract void saveData();

    public abstract void loadData(String arenaType);

    protected Map<String, ArenaData> getArenaData() {

        return this.arenaData;
    }

    public void setArenaData(Map<String, ArenaData> arenaData) {
        this.arenaData = arenaData;
    }

    public String getSkin() {

        return (this.skin == null) ? "DEFAULT" : this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public ArenaData getArenaData(String arenaType) {

        ArenaData arenaData = this.arenaData.get(arenaType);

        if (arenaData == null) {
            ArenaData data = new ArenaData();
            this.arenaData.put(arenaType, data);
            return data;
        }

        return arenaData;

    }

    public int getTotalGamesPlayed() {
        int gamesPlayed = 0;

        for (ArenaData data : this.arenaData.values()) {
            gamesPlayed += data.getGamesPlayed();
        }

        return gamesPlayed;
    }

    public int getTotalWins() {

        int wins = 0;

        for (ArenaData data : this.arenaData.values()) {
            wins += data.getWins();
        }

        return wins;
    }

    public int getTotalDefeats() {

        int defeats = 0;

        for (ArenaData data : this.arenaData.values()) {
            defeats += data.getLosses();
        }

        return defeats;
    }

    public int getTotalKills() {

        int kills = 0;

        for (ArenaData data : this.arenaData.values()) {
            kills += data.getKills();
        }

        return kills;
    }

    public int getTotalDeaths() {

        int deaths = 0;

        for (ArenaData data : this.arenaData.values()) {
            deaths += data.getDeaths();
        }

        return deaths;
    }

    public int getTotalBedsBroken() {

        int i = 0;

        for (ArenaData data : this.arenaData.values()) {
            i += data.getBedsBroken();
        }

        return i;
    }

    public int getTotalBedsLost() {
        int i = 0;

        for (ArenaData data : this.arenaData.values()) {
            i += data.getBedsLost();
        }

        return i;
    }

    public int getTotalFinalKills() {
        int i = 0;

        for (ArenaData data : this.arenaData.values()) {
            i += data.getFinalKills();
        }

        return i;
    }

    public int getGamesPlayed(String arenaType) {

        return this.getArenaData(arenaType).getGamesPlayed();
    }

    public int getWins(String arenaType) {

        return this.getArenaData(arenaType).getWins();
    }

    public int getDefeats(String arenaType) {

        return this.getArenaData(arenaType).getLosses();
    }

    public int getKills(String arenaType) {

        return this.getArenaData(arenaType).getKills();
    }

    public int getDeaths(String arenaType) {

        return this.getArenaData(arenaType).getDeaths();
    }

    public int getBedsBroken(String arenaType) {

        return this.getArenaData(arenaType).getBedsBroken();
    }

    public int getBedsLost(String arenaType) {

        return this.getArenaData(arenaType).getBedsLost();
    }

    public int getFinalKills(String arenaType) {

        return this.getArenaData(arenaType).getFinalKills();
    }

    public int getFinalDeaths(String arenaType) {

        return this.getArenaData(arenaType).getFinalDeaths();
    }

    public int getLosses(String arenaType) {

        return this.getArenaData(arenaType).getLosses();
    }

    public void addGamePlayed(String arenaType) {

        ArenaData data = this.getArenaData(arenaType);
        data.addGamePlayed();

    }

    public void addWin(String arenaType) {

        ArenaData data = this.getArenaData(arenaType);
        data.addWin();


    }

    public void addDefeat(String arenaType) {

        ArenaData data = this.getArenaData(arenaType);
        data.addLoss();

    }

    public void addBedLost(String arenaType) {

        ArenaData data = this.getArenaData(arenaType);
        data.addBedLost();

    }

    public void addKills(String arenaType, int amount) {

        ArenaData data = this.getArenaData(arenaType);
        data.addKills(amount);

    }

    public void addFinalKills(String arenaType, int amount) {

        ArenaData data = this.getArenaData(arenaType);
        data.addFinalKill(amount);

    }

    public void addBedsBroken(String arenaType, int amount) {

        ArenaData data = this.getArenaData(arenaType);
        data.addBedBroken(amount);

    }

    public void addDeaths(String arenaType, int amount) {

        ArenaData data = this.getArenaData(arenaType);
        data.addDeaths(amount);

    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BedwarsLevel getLevel() {
        return this.level;
    }

    public void setLevel(BedwarsLevel level) {
        this.level = level;
    }
}
