package ro.marius.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.marius.bedwars.AbstractCommand;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;

public class ShoutCommand extends AbstractCommand {

    public ShoutCommand(String name) {
        super(name);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player p = (Player) sender;
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            p.sendMessage(Lang.NOT_IN_GAME.getString());
            return;
        }

        if (args.length == 0) {
            p.sendMessage(Lang.SHOUT_MISSING_ARGUMENTS.getString());
            return;
        }

        if (match.getMatchState() != MatchState.IN_GAME) {
            return;
        }

        if (match.getSpectators().contains(p)) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        ArenaOptions arenaOptions = match.getGame().getArenaOptions();
        StringBuilder message = new StringBuilder();

        for (String arg : args) {
            message.append(arg).append(" ");
        }

        match.sendMessage(arenaOptions.getString("ModifiedChat.Shout.Format")
                .replace("<playerTeamColor>", team.getTeamColor().getChatColor())
                .replace("<playerTeamNameUpperCase>", team.getName().toUpperCase())
                .replace("<playerTeam>", team.getName()).replace("<player>", p.getName())
                .replace("<message>", message.toString()));

    }

}
