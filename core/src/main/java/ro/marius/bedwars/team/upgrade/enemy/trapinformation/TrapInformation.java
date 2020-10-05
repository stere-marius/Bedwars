package ro.marius.bedwars.team.upgrade.enemy.trapinformation;

import org.bukkit.entity.Player;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

public abstract class TrapInformation {

    public abstract void onTriggered(Team team, Player p, AMatch match);

}
