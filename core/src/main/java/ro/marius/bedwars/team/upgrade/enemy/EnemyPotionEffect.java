package ro.marius.bedwars.team.upgrade.enemy;

import org.bukkit.Bukkit;
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

public class EnemyPotionEffect implements IUpgrade {

    public BukkitTask task;
    private PotionEffectType potionEffectType;
    private int amplifier;
    private int time;
    private double activationRange;

    public EnemyPotionEffect(PotionEffectType potionEffectType, int time, int amplifier, double activationRange) {
        this.potionEffectType = potionEffectType;
        this.time = time;
        this.amplifier = amplifier;
        this.activationRange = (double) Math.round(activationRange * 100) / 100;
    }

    @Override
    public void onActivation(AMatch match, Player optionalPlayer) {

        Team team = match.getPlayerTeam().get(optionalPlayer.getUniqueId());

        if (team == null) {
            return;
        }

        PotionEffect eff = new PotionEffect(this.potionEffectType, this.time, this.amplifier);

        if (this.activationRange <= 0) {

            for (UUID uuid : match.getPlayerTeam().keySet()) {

                Player player = Bukkit.getPlayer(uuid);

                if (team.getPlayers().contains(player)) {
                    continue;
                }

                player.addPotionEffect(eff);
            }

            return;
        }

        Location bedLocation = team.getBedLocation().getLocation();
        String worldName = bedLocation.getWorld().getName();

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {

                for (UUID uuid : match.getPlayerTeam().keySet()) {

                    Player p = Bukkit.getPlayer(uuid);

                    if (p == null) {
                        continue;
                    }
                    if (team.getPlayers().contains(p)) {
                        continue;
                    }
                    if (!p.getWorld().getName().equals(worldName)) {
                        continue;
                    }
                    if (p.getLocation().distance(bedLocation) > EnemyPotionEffect.this.activationRange) {
                        continue;
                    }
                    if (match.getSpectators().contains(p)) {
                        continue;
                    }

                    p.addPotionEffect(eff);
//					this.cancel();
                }
            }

        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);

        this.task = task;

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

        return new EnemyPotionEffect(this.potionEffectType, this.time, this.amplifier, this.activationRange);
    }

}
