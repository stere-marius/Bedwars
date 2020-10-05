package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

import java.util.List;

public class PlayerProcessCommand implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        ArenaOptions arenaOptions = match.getGame().getArenaOptions();
        String command = e.getMessage();
        List<String> blockedCommands = arenaOptions.getStringList("BlockedCommands.List");
        boolean blockForOp = arenaOptions.getBoolean("BlockedCommands.EnableForOp");

        if (blockedCommands.isEmpty()) {
            return;
        }
        if (!blockedCommands.contains(command.split(" ")[0].replace("/", ""))) {
            return;
        }
        if (p.isOp() && blockForOp) {
            return;
        }

        p.sendMessage(Utils.translate("&cRestricted command."));
        e.setCancelled(true);

    }

}
