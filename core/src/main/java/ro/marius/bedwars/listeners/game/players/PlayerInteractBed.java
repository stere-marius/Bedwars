package ro.marius.bedwars.listeners.game.players;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;

public class PlayerInteractBed implements Listener {

    @EventHandler
    public void onPlayerBed(PlayerBedEnterEvent e) {
        if (!ManagerHandler.getGameManager().getPlayerMatch().containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        if (!e.getBed().hasMetadata("Bed")) {
            return;
        }

        e.setCancelled(true);

    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());
        Block block = e.getClickedBlock();
        Action action = e.getAction();

        if (match == null) {
            return;
        }
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!block.getType().name().contains("BED")) {
            return;
        }
        if (player.isSneaking() && (e.getItem() != null)) {
            return;
        }

        e.setCancelled(true);

    }

}
