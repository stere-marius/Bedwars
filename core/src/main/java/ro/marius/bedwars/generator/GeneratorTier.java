package ro.marius.bedwars.generator;

public class GeneratorTier {

    private int time;
    private int spawnAmount;
    private boolean spawnLimit;
    private int limitAmount;
    private String message;

    public GeneratorTier(int time, int spawnAmount, String message) {
        this.time = time;
        this.spawnAmount = spawnAmount;
        this.message = message;
    }


    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSpawnAmount() {
        return this.spawnAmount;
    }

    public void setSpawnAmount(int spawnAmount) {
        this.spawnAmount = spawnAmount;
    }

    public boolean isSpawnLimit() {
        return this.spawnLimit;
    }

    public void setSpawnLimit(boolean spawnLimit) {
        this.spawnLimit = spawnLimit;
    }

    public int getLimitAmount() {
        return this.limitAmount;
    }

    public void setLimitAmount(int limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
