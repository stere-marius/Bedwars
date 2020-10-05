package ro.marius.bedwars.team.upgrade;

import org.bukkit.entity.Player;
import ro.marius.bedwars.match.AMatch;

public interface IUpgrade {

    void onActivation(AMatch match, Player p);

    void cancelTask();

    IUpgrade clone();

}
