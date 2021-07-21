package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.utils.Utils;

public class NewPickupEvent implements Listener {

    @EventHandler
    public void onPickSword(EntityPickupItemEvent e) {

        if (!(e.getEntity() instanceof Player))
            return;


        Player p = (Player) e.getEntity();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null)
            return;

        if (match.getMatchState() != MatchState.IN_GAME)
            return;

        if (match.getSpectators().contains(p)) {
            e.setCancelled(true);
            return;
        }

        match.getMatchEntity().remove(e.getItem());

        if (e.getItem().getItemStack().getType().name().endsWith("_SWORD")) {
            Utils.hideWoodenSword(p);
            match.getPlayerTeam().get(p.getUniqueId()).applyEnchant("SWORD", p);
            return;
        }

        // Teams Generator Split Logic
        if (match.getGame().getPlayersPerTeam() < 2)
            return;

        if (e.getItem().getMetadata("FloorGeneratorItem").isEmpty())
            return;

        for (Player player : match.getPlayers()) {

            if (player.getLocation().distance(p.getLocation()) > 1.75)
                continue;

            if (player.getUniqueId().equals(p.getUniqueId()))
                continue;

            if (match.getSpectators().contains(p))
                continue;

            player.getInventory().addItem(e.getItem().getItemStack());
        }
    }
}
