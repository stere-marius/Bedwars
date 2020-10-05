package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.shopconfiguration.ShopPath;
import ro.marius.bedwars.shopconfiguration.shopinventory.ShopInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.upgradeconfiguration.upgradeinventory.UpgradeInventory;

public class PlayerInteractNPC implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {

        if (e.getRightClicked() == null) {
            return;
        }

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        if (e.getRightClicked().hasMetadata("MatchShop")) {

            e.setCancelled(true);
            ShopPath shopPath = match.getGame().getShopPath();
            ShopInventory inv = shopPath.getInventory().get("MAIN_INVENTORY");
            inv.setGame(match.getGame());
            inv.setTeam(team);
            inv.setPlayer(p);
            p.openInventory(inv.getInventory());

            return;
        }

        if (e.getRightClicked().hasMetadata("MatchUpgrade")) {

            e.setCancelled(true);
            UpgradePath upgradePath = match.getGame().getUpgradePath();
            UpgradeInventory inv = upgradePath.getUpgradeInventoryMap().get("UpgradeMenu");
            inv.setGame(match.getGame());
            inv.setTeam(team);
            inv.setPlayer(p);
            p.openInventory(inv.getInventory());

        }

    }

}
