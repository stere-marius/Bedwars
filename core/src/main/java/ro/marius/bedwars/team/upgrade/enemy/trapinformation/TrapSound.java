package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.entity.Player;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

import java.util.Map;

public class TrapSound extends TrapInformation {

    private Map<String, SoundInformation> soundMap;

    public TrapSound(Map<String, SoundInformation> soundMap) {
        this.soundMap = soundMap;
    }

    @Override
    public void onTriggered(Team team, Player p, AMatch match) {

        SoundInformation allTeam = this.soundMap.get("ALL_TEAM");

        if (allTeam != null) {
            team.getPlayers().forEach(
                    t -> t.playSound(t.getLocation(), allTeam.getSound(), allTeam.getVolume(), allTeam.getPitch()));
        }

        //TODO: Adaugare NEAR_ENEMY, RANDOM_TEAM_PLAYER

    }
}
