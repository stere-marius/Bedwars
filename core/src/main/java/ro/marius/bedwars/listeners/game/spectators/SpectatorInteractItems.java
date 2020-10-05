package ro.marius.bedwars.listeners.game.spectators;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.configuration.Items;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchSpectator;
import ro.marius.bedwars.menu.extra.SpectatorSettings;
import ro.marius.bedwars.menu.extra.SpectatorTeleport;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class SpectatorInteractItems implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }
        if (!match.getSpectators().contains(p)) {
            return;
        }

        MatchSpectator spectator = this.getPermanentSpectator(match, p.getName());

        if (spectator == null) {
            return;
        }

        ItemStack handItem = e.getItem();

        if (handItem == null) {
            return;
        }

        if (handItem.isSimilar(Items.SPECTATOR_LEAVE.toItemStack())) {

            this.removeSpectator(p, match, spectator);

            return;
        }

        if (handItem.isSimilar(Items.SPECTATOR_SETTINGS.toItemStack())) {

            p.openInventory(new SpectatorSettings(p, match, spectator).getInventory());

            return;
        }

        if (handItem.isSimilar(Items.TELEPORTER.toItemStack())) {

            Action action = e.getAction();

            if ((action == Action.LEFT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_AIR)) {

                Random random = new Random();
                int index = random.nextInt(match.getPlayers().size());
                Player target = new ArrayList<>(match.getPlayers()).get(index);
                p.teleport(target);
                spectator.startTeleport(target);

                return;
            }

            if ((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.RIGHT_CLICK_AIR)) {

                p.openInventory(new SpectatorTeleport(spectator, match).getInventory());

                return;
            }

        }

    }

    public MatchSpectator getPermanentSpectator(AMatch match, String pName) {

        for (MatchSpectator spectator : match.getPermanentSpectators()) {

            if (!spectator.getSpectator().getName().equals(pName)) {
                continue;
            }

            return spectator;

        }

        return null;

    }

    public void removeSpectator(Player p, AMatch match, MatchSpectator spectator) {

        VersionWrapper versionWrapper = ManagerHandler.getVersionManager().getVersionWrapper();

        p.setAllowFlight(false);
        p.setFlying(false);
        match.getPlayers().forEach(players -> versionWrapper.showPlayer(players, p, BedWarsPlugin.getInstance()));
        match.getSpectators().forEach(sp -> {
            versionWrapper.showPlayer(sp, p, BedWarsPlugin.getInstance());
            versionWrapper.showPlayer(p, sp, BedWarsPlugin.getInstance());
        });
        versionWrapper.setCollidable(p, true);
        ManagerHandler.getScoreboardManager().toggleScoreboard(p);
        match.getSpectators().remove(p);
        match.getPermanentSpectators().remove(spectator);
        ManagerHandler.getGameManager().getPlayerMatch().remove(p.getUniqueId());
        Utils.teleportToLobby(p, BedWarsPlugin.getInstance());
        Utils.resetPlayer(p, true, true);
        ManagerHandler.getGameManager().givePlayerContents(p);
        match.getGame().notifyObservers();

    }

}
