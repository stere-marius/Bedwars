package ro.marius.bedwars.match;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.ActionBarUtils;
import ro.marius.bedwars.utils.Utils;

public class MatchSpectator {

    private AMatch match;
    private Player spectator;

    private boolean isAutoTeleport;
    private boolean hasFly;
    private boolean hasNightVision;
    private BukkitTask task;

    public MatchSpectator(AMatch match, Player spectator) {
        this.match = match;
        this.spectator = spectator;
    }

    public void startTeleport(Player target) {

        if (!this.match.getPlayers().contains(target)) {
            return;
        }

        if (this.task != null) {
            this.task.cancel();
        }

        this.task = new BukkitRunnable() {

            @Override
            public void run() {

                if (MatchSpectator.this.match.getMatchState() != MatchState.IN_GAME) {
                    this.cancel();
                    return;
                }

                if (!MatchSpectator.this.match.getSpectators().contains(MatchSpectator.this.spectator)) {
                    this.cancel();
                    return;
                }

                if (MatchSpectator.this.match.getSpectators().contains(target)) {
                    ActionBarUtils.sendActionbar(MatchSpectator.this.spectator, Utils.translate("&cTarget lost!"));
                    this.cancel();
                    return;
                }

                if (!target.isOnline()) {
                    ActionBarUtils.sendActionbar(MatchSpectator.this.spectator, Utils.translate("&cTarget lost!"));
                    this.cancel();
                    return;
                }

                double distance = MatchSpectator.this.spectator.getLocation().distance(target.getLocation());

                if ((distance >= 10) && MatchSpectator.this.isAutoTeleport) {
                    MatchSpectator.this.spectator.teleport(target);
                }

                String msg = Utils
                        .translate("&fTarget: &a&l" + target.getName() + "   &fDistance: &a&l" + Math.floor(distance * 100) / 100 + "m");

                ActionBarUtils.sendActionbar(MatchSpectator.this.spectator, msg);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);

    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    public Player getSpectator() {
        return this.spectator;
    }

    public void setSpectator(Player spectator) {
        this.spectator = spectator;
    }

    public boolean isAutoTeleport() {
        return this.isAutoTeleport;
    }

    public void setAutoTeleport(boolean autoTeleport) {
        this.isAutoTeleport = autoTeleport;
    }

    public boolean hasFly() {
        return this.hasFly;
    }

    public void setHasFly(boolean hasFly) {
        this.hasFly = hasFly;
    }

    public boolean hasNightVision() {
        return this.hasNightVision;
    }

    public void setHasNightVision(boolean hasNightVision) {
        this.hasNightVision = hasNightVision;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

}
