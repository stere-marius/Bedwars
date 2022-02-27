package ro.marius.bedwars.listeners.game.players;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

import java.util.Iterator;

public class PlayerChatGame implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {

        Iterator<Player> it = e.getRecipients().iterator();
        while (it.hasNext()) {
            Player p = it.next();
            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
            if (match == null) {
                continue;
            }
            if (!match.getGame().getArenaOptions().getBoolean("SeparateChatArena")) {
                continue;
            }

            it.remove();

        }
    }

    @EventHandler
    public void onChatMatch(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Game game = match.getGame();
        ArenaOptions arenaOptions = game.getArenaOptions();

        if (!arenaOptions.getBoolean("ModifiedChat.Match.Enabled")) {
            return;
        }
        if (match.getMatchState() == MatchState.WAITING) {
            return;
        }
        if (match.getSpectators().contains(p)) {
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        Team team = match.getPlayerTeam().get(p.getUniqueId());
        e.setCancelled(true);

        if (game.getPlayersPerTeam() == 1) {

            String chatMessage = Utils.translate(arenaOptions.getString("ModifiedChat.Match.Format"));

            if ((plugin != null) && plugin.isEnabled()) {
                chatMessage = PlaceholderAPI.setPlaceholders(p, chatMessage);
            }

            String finalMessage = chatMessage.replace("<message>", e.getMessage()).replace("<player>", p.getName())
                    .replace("<playerTeamColor>", team.getTeamColor().getChatColor())
                    .replace("<playerTeam>", team.getName());
            match.sendMessage(finalMessage);
            Bukkit.getConsoleSender().sendMessage(Lang.BEDWARS_CONSOLE_CHAT.getString()
                    .replace("<message>", finalMessage).replace("<arenaName>", match.getGame().getName()));
            return;
        }

        String chatMessage = Utils.translate(arenaOptions.getString("ModifiedChat.Match.Format"));

        if ((plugin != null) && plugin.isEnabled()) {
            chatMessage = PlaceholderAPI.setPlaceholders(p, chatMessage);
        }

        String finalMessage = chatMessage.replace("<message>", e.getMessage()).replace("<player>", p.getName())
                .replace("<playerTeamColor>", team.getTeamColor().getChatColor())
                .replace("<playerTeam>", team.getName());
        team.sendMessage(finalMessage);
        Bukkit.getConsoleSender().sendMessage(Lang.BEDWARS_CONSOLE_CHAT.getString().replace("<message>", finalMessage)
                .replace("<arenaName>", match.getGame().getName()));
    }

}
