package ro.marius.bedwars.listeners.game.players;

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

        if (!e.getItem().getItemStack().getType().name().endsWith("_SWORD"))
            return;

        Utils.hideWoodenSword(p);
        match.getPlayerTeam().get(p.getUniqueId()).applyEnchant("SWORD", p);
    }
}
