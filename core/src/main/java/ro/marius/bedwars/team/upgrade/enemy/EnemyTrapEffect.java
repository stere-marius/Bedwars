package ro.marius.bedwars.team.upgrade.enemy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.upgrade.IUpgrade;
import ro.marius.bedwars.team.upgrade.enemy.trapinformation.TrapInformation;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnemyTrapEffect implements IUpgrade {

    public BukkitTask task;
    private List<String> decreaseTier;
    private String name;
    private double activationRange;
    private Map<String, List<ro.marius.bedwars.team.upgrade.PotionEffect>> effect;
    private List<TrapInformation> information = new ArrayList<>();

    public EnemyTrapEffect(String name, List<String> decreaseTier, double activationRange,
                           Map<String, List<ro.marius.bedwars.team.upgrade.PotionEffect>> effect) {
        this.name = name;
        this.decreaseTier = decreaseTier;
        this.activationRange = activationRange;
        this.effect = effect;
    }

    // TODO: getFirstTrap().onActivation(match,p)
    @Override
    public void onActivation(AMatch match, Player optionalPlayer) {

        Team team = match.getPlayerTeam().get(optionalPlayer.getUniqueId());

        if (team == null) {
            return;
        }

        Location bedLocation = team.getBedLocation().getLocation();
        String worldName = bedLocation.getWorld().getName();

        boolean isEnemy = this.effect.get("RADIUS_ENEMY").isEmpty();
        boolean isTeam = this.effect.get("ALL_TEAM").isEmpty();

        // TODO: Verific daca e custom effect pentru a scoate invizibilitatea de la
        // inamici
        // TODO: Ii dau mesaje si title ca un inamic a intrat in baza

        // TODO: Cand se dezactiveaza primul sa dea drumul la al doilea

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
                    if (match.getSpectators().contains(p)) {
                        continue;
                    }
                    if (p.getLocation().distance(bedLocation) > EnemyTrapEffect.this.activationRange) {
                        continue;
                    }
                    if (team.getTrappedPlayers().containsKey(p)) {
                        continue;
                    }
                    if (match.getPreventTrap().containsKey(p.getUniqueId())) {
                        continue;
                    }

//					Bukkit.broadcastMessage(currentTrap + ". FOUND " + p.getName());
                    // TODO: Verific daca este doar un sigur effect pentru inamic si acelasi fiind
                    // REVEAL_INVISIBILITY si mai verific daca este invizibil
//					if(effect.get("ENEMY").get(0)).equalsIgnoreCase("REVEAL_INVISIBILITY") && hasInvisibility(p))
                    if (!isEnemy) {

                        for (ro.marius.bedwars.team.upgrade.PotionEffect eff : EnemyTrapEffect.this.effect.get("RADIUS_ENEMY")) {

//							Bukkit.broadcastMessage("Added effect: " + eff.getPotionEffectType() + " : " + eff.getTime() + " : " + eff.getAmplifier());

                            // TODO: Verific REVEAL_ENEMY
//							if(eff.getName().equalsIgnoreCase("REVEAL_INVISIBILITY")
//							#removeFromInvisibilityTeam
//							TODO: Verific sa nu fie acelasi inamic ca primul

                            p.addPotionEffect(eff.getPotionEffect());
                        }
                    }

                    if (!isTeam) {

                        for (ro.marius.bedwars.team.upgrade.PotionEffect eff : EnemyTrapEffect.this.effect.get("ALL_TEAM")) {

//							Bukkit.broadcastMessage("Added effect: " + eff.getPotionEffectType());

                            // TODO: Verific REVEAL_ENEMY
//							if(eff.getName().equalsIgnoreCase("REVEAL_INVISIBILITY")
//							#removeFromInvisibilityTeam

                            team.getPlayers().forEach(a -> a.addPotionEffect(eff.getPotionEffect()));
                        }

                    }


//					Bukkit.broadcastMessage("Trap informations " + informations.size());

                    for (TrapInformation trapInformation : EnemyTrapEffect.this.information) {
                        trapInformation.onTriggered(team, p, match);
                    }

                    team.removeFirstTrap();
                    team.addTrappedPlayer(p);
                    this.cancel();

                    for (String s : EnemyTrapEffect.this.decreaseTier) {

                        TeamUpgrade upg = team.getGameUpgrades().get(s);

                        if (upg == null) {
                            continue;
                        }

//						Bukkit.broadcastMessage("Decreased tier for " + s);

                        upg.decreaseTier();

                    }

                    EnemyTrapEffect trapEffect = team.getFirstTrap();

                    if (trapEffect != null) {
                        trapEffect.onActivation(match, optionalPlayer);
//						Bukkit.broadcastMessage(currentTrap + ". Dezactivare trap, pornire trap " + trapEffect);
//						currentTrap++;
                    }
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

        EnemyTrapEffect enemyTrapEffect = new EnemyTrapEffect(this.name, this.decreaseTier, this.activationRange, this.effect);
        enemyTrapEffect.getInformation().addAll(this.information);

        return enemyTrapEffect;
    }

    public List<String> getDecreaseTier() {
        return this.decreaseTier;
    }

    public String getName() {
        return this.name;
    }

    public double getActivationRange() {
        return this.activationRange;
    }

    public List<TrapInformation> getInformation() {
        return this.information;
    }

    public BukkitTask getTask() {
        return this.task;
    }
}
