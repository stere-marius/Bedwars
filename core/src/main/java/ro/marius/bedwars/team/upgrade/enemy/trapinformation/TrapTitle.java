package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.entity.Player;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

import java.util.Map;
import java.util.Random;

public class TrapTitle extends TrapInformation {

    private Map<String, Title> titleMap;

    public TrapTitle(Map<String, Title> titleMap) {
        this.titleMap = titleMap;
    }

    @Override
    public void onTriggered(Team team, Player p, AMatch match) {

        Title randomTeamPlayer = this.titleMap.get("RANDOM_TEAM_PLAYER");

        if (randomTeamPlayer != null) {

            Random random = new Random();
            int randomIndex = random.nextInt(team.getPlayers().size());
            Player randomPlayer = (Player) team.getPlayers().toArray()[randomIndex];

            randomTeamPlayer.sendTitle(randomPlayer);

        }

        Title allTeam = this.titleMap.get("ALL_TEAM");

        if (allTeam != null) {

            for (Player t : team.getPlayers()) {
                allTeam.sendTitle(t);
            }

        }

        Title nearEnemy = this.titleMap.get("NEAR_ENEMY");

        if (nearEnemy != null) {

            nearEnemy.sendTitle(p);

        }

    }
}
