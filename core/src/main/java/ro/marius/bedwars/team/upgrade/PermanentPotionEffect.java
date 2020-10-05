package ro.marius.bedwars.team.upgrade;

import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class PermanentPotionEffect {

    private final Map<PotionEffectType, PotionEffect> effect = new HashMap<>();

    public void add(PotionEffectType type, int amplifier, int time) {

        PotionEffect eff = this.effect.get(type);
        PotionEffect newEff = new PotionEffect(type.getName(), amplifier, time);

        if (eff == null) {
            this.effect.put(type, newEff);
            return;
        }

        if (newEff.compareTo(eff) == 0) {
            return;
        }

        this.effect.put(type, newEff);

    }

    public Map<PotionEffectType, PotionEffect> getEffect() {
        return this.effect;
    }
}
