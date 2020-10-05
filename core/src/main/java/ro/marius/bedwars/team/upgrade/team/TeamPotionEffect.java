package ro.marius.bedwars.team.upgrade.team;

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

public class TeamPotionEffect implements IUpgrade {

    private PotionEffectType potionEffectType;
    private int amplifier;
    private int time;
    private double activationRange;
    private boolean permanent;
    private BukkitTask task;

    public TeamPotionEffect(PotionEffectType potionEffectType, int time, int amplifier, double activationRange,
                            boolean permanent) {
        this.potionEffectType = potionEffectType;
        this.time = time;
        this.amplifier = amplifier;
        this.activationRange = (double) Math.round(activationRange * 100) / 100;
        this.permanent = permanent;
    }

    @Override
    public void onActivation(AMatch match, Player pl) {

        Team team = match.getPlayerTeam().get(pl.getUniqueId());

        if (team == null) {
            return;
        }

        PotionEffect eff = new PotionEffect(this.potionEffectType, this.time, this.amplifier, false, false);

        if ((this.activationRange <= 0) && !this.permanent) {
            team.getPlayers().forEach(p -> p.addPotionEffect(eff, true));
            return;
        }

        if ((this.activationRange <= 0) && this.permanent) {
            team.getPlayers().forEach(p -> p.addPotionEffect(eff, true));
            team.getPermanentEffects().add(this.potionEffectType, this.amplifier, this.time);
            return;
        }

        Location spawnLocation = team.getSpawnLocation().getLocation();
        String worldName = spawnLocation.getWorld().getName();

        this.task = new BukkitRunnable() {

            @Override
            public void run() {

                for (Player p : team.getPlayers()) {

                    if (p == null) {
                        continue;
                    }
                    if (!p.getWorld().getName().equals(worldName)) {
                        continue;
                    }
                    if (p.getLocation().distance(spawnLocation) > TeamPotionEffect.this.activationRange) {
                        continue;
                    }

                    p.addPotionEffect(eff, true);

                }

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 30);
    }

    public void onActivation(Player p) {

        p.addPotionEffect(new PotionEffect(this.potionEffectType, this.time, this.amplifier), true);
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

        return new TeamPotionEffect(this.potionEffectType, this.time, this.amplifier, this.activationRange, this.permanent);
    }

}
