package ro.marius.bedwars.listeners.game.spectators;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class SpectatorChat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        ArenaOptions arenaOptions = match.getGame().getArenaOptions();

        if (!arenaOptions.getBoolean("ModifiedChat.Spectator.Enabled")) {
            return;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if ((plugin != null) && plugin.isEnabled()) {
            String chatMessage = PlaceholderAPI.setPlaceholders(p,
                    Utils.translate(arenaOptions.getString("ModifiedChat.Spectator.Format")));
            match.getSpectators().forEach(player -> player.sendMessage(
                    chatMessage.replace("<message>", e.getMessage()).replace("<player>", e.getPlayer().getName())));
            e.setCancelled(true);
            return;
        }

        match.getSpectators().forEach(
                player -> player.sendMessage(Utils.translate(arenaOptions.getString("ModifiedChat.Spectator.Format"))
                        .replace("<message>", e.getMessage()).replace("<player>", e.getPlayer().getName())));
        e.setCancelled(true);
    }

}
