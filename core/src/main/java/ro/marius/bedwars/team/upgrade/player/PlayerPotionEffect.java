package ro.marius.bedwars.team.upgrade.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.upgrade.IUpgrade;

import java.util.UUID;

public class PlayerPotionEffect implements IUpgrade {

    public BukkitTask task;
    private PotionEffectType potionEffectType;
    private int amplifier;
    private int time;
    private double activationRange;
    private boolean permanent;

    public PlayerPotionEffect(PotionEffectType potionEffectType, int time, int amplifier, double activationRange,
                              boolean permanent) {
        this.potionEffectType = potionEffectType;
        this.time = time;
        this.amplifier = amplifier;
        this.activationRange = (double) Math.round(activationRange * 100) / 100;
        this.permanent = permanent;
    }

    @Override
    public void onActivation(AMatch match, Player p) {

        if (this.activationRange <= 0) {
            p.addPotionEffect(new PotionEffect(this.potionEffectType, this.time, this.amplifier));
            return;
        }

        UUID uuid = p.getUniqueId();
        Team team = match.getPlayerTeam().get(uuid);

        if (team == null) {
            return;
        }

        PotionEffect eff = new PotionEffect(this.potionEffectType, this.time, this.amplifier);

        Location spawnLocation = team.getSpawnLocation().getLocation();
        String worldName = spawnLocation.getWorld().getName();

        this.task = new BukkitRunnable() {

            @Override
            public void run() {

                if (!p.getWorld().getName().equals(worldName)) {
                    return;
                }
                if (p.getLocation().distance(spawnLocation) > PlayerPotionEffect.this.activationRange) {
                    return;
                }

                p.addPotionEffect(eff);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);

    }

    @Override
    public void cancelTask() {

        if (this.task == null) {
            return;
        }

        this.task.cancel();

    }

    @Override
    public IUpgrade clone() {

        return new PlayerPotionEffect(this.potionEffectType, this.time, this.amplifier, this.activationRange, this.permanent);
    }

}
