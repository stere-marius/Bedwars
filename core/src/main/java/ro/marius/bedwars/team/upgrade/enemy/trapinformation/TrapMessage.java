package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.entity.Player;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class TrapMessage extends TrapInformation {

    private final Map<String, List<String>> messageMap;

    public TrapMessage(Map<String, List<String>> messageMap) {
        this.messageMap = messageMap;
    }

    @Override
    public void onTriggered(Team team, Player p, AMatch match) {

        if (this.messageMap.isEmpty()) {
            return;
        }

        List<String> randomTeamPlayer = this.messageMap.get("RANDOM_TEAM_PLAYER");

        if (randomTeamPlayer != null) {

            Random random = new Random();
            int randomIndex = random.nextInt(team.getPlayers().size());
            Player randomPlayer = (Player) team.getPlayers().toArray()[randomIndex];

            randomTeamPlayer.forEach(randomPlayer::sendMessage);

        }

        List<String> allTeam = this.messageMap.get("ALL_TEAM");

        if (allTeam != null) {
            allTeam.forEach(team::sendMessage);
        }

        List<String> nearEnemy = this.messageMap.get("NEAR_ENEMY");

        if (nearEnemy != null) {
            nearEnemy.forEach(p::sendMessage);
        }

    }

}
