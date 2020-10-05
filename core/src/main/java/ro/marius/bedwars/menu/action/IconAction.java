package ro.marius.bedwars.menu.action;

import org.bukkit.entity.Player;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.team.Team;

public interface IconAction {

    void onClick(Player p, Team team, Game game);

}
