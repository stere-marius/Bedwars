package ro.marius.bedwars.playerdata;

public class BedwarsLevel {

    private int level, exp;

    public BedwarsLevel(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }


    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
