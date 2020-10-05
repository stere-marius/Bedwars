package ro.marius.bedwars.game.mechanics;

import org.bukkit.entity.Player;
import ro.marius.bedwars.team.Team;

import java.util.concurrent.TimeUnit;

public class PlayerDamageCause {

    private long start;
    private final Player damager;
    private final Cause damageCause;
    private Team team;

    public PlayerDamageCause(long start, Player damager, Cause damageCause) {

        this.start = start;
        this.damager = damager;
        this.damageCause = damageCause;
    }

    public int getSeconds() {
        return (int) TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - this.start);
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public Player getDamager() {
        return this.damager;
    }

    public Cause getDamageCause() {
        return this.damageCause;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
