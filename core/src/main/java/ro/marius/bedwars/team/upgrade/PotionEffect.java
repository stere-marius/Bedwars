package ro.marius.bedwars.team.upgrade;

import org.bukkit.potion.PotionEffectType;

public class PotionEffect implements Comparable<PotionEffect> {

    private String potionEffectType;
    private int amplifier;
    private int time;
    //	private double activationRange = 5.5;
    private org.bukkit.potion.PotionEffect potionEffect;

    public PotionEffect(String potionEffectType, int amplifier, int time) {
        this.potionEffectType = potionEffectType;
        this.amplifier = amplifier;
        this.time = time;
//		this.activationRange = (double) Math.round(activationRange * 100) / 100;
    }

    public org.bukkit.potion.PotionEffect getPotionEffect() {

        if (this.potionEffect != null) {
            return this.potionEffect;
        }

        PotionEffectType type = PotionEffectType.getByName(this.potionEffectType);
        org.bukkit.potion.PotionEffect p = new org.bukkit.potion.PotionEffect(type, this.time, this.amplifier, false, false);

        return this.potionEffect = p;
    }

    public void setPotionEffect(org.bukkit.potion.PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
    }

    @Override
    public int compareTo(PotionEffect o) {

        if (this.amplifier > o.getAmplifier()) {
            return 1;
        }

        if ((this.amplifier == o.getAmplifier()) && (this.time > o.getTime())) {
            return 1;
        }

        return 0;
    }

    public String getPotionEffectType() {
        return this.potionEffectType;
    }

    public void setPotionEffectType(String potionEffectType) {
        this.potionEffectType = potionEffectType;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    // RANGE_ENEMY,ALL_ENEMY,ALL_TEAM,RANDOM_TEAM_PLAYER,RANDOM_ENEMY

}
