package ro.marius.bedwars.match;

public class MatchData {

    private int deaths;
    private int kills;
    private int bedBroken;
    private int bedLost;
    private int finalDeaths;
    private int finalKills;

    public void addDeath() {
        this.deaths++;
    }

    public void addKill() {
        this.kills++;
    }

    public void addBedBroken() {
        this.bedBroken++;
    }

    public void addBedLost() {
        this.bedLost++;
    }

    public void addFinalDeath() {
        this.finalDeaths++;
    }

    public void addFinalKill(boolean b) {

        if (!b) {
            return;
        }

        this.finalKills++;

    }

    public int getDeaths() {
        return this.deaths;
    }

    public int getKills() {
        return this.kills;
    }

    public int getBedBroken() {
        return this.bedBroken;
    }

    public int getFinalKills() {
        return this.finalKills;
    }
}
