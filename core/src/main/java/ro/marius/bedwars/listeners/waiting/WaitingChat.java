package ro.marius.bedwars.listeners.waiting;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.utils.Utils;

public class WaitingChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (match.getMatchState() != MatchState.IN_WAITING) {
            return;
        }

        Game game = match.getGame();

        if (!game.getArenaOptions().getBoolean("ModifiedChat.InWaiting.Enabled")) {
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if ((plugin != null) && plugin.isEnabled()) {
            String chatMessage = PlaceholderAPI.setPlaceholders(p,
                    Utils.translate(game.getArenaOptions().getString("ModifiedChat.InWaiting.Format")));
            match.getPlayers().forEach(player -> player.sendMessage(Utils.translate(chatMessage)
                    .replace("<message>", e.getMessage()).replace("<player>", e.getPlayer().getName())));
            e.setCancelled(true);
            return;
        }

        match.getPlayers()
                .forEach(player -> player
                        .sendMessage(Utils.translate(game.getArenaOptions().getString("ModifiedChat.InWaiting.Format"))
                                .replace("<message>", e.getMessage()).replace("<player>", e.getPlayer().getName())));
        e.setCancelled(true);
    }

}
