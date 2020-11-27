package ro.marius.bedwars.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.shopconfiguration.shopinventory.QuickBuyIcon;
import ro.marius.bedwars.shopconfiguration.shopinventory.QuickBuyInventory;
import ro.marius.bedwars.shopconfiguration.shopinventory.ShopInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.upgradeinventory.UpgradeInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClickUpgrade(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getHolder() == null) {
            return;
        }
        if (!(e.getView().getTopInventory().getHolder() instanceof UpgradeInventory)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());

        if (match == null) {
            return;
        }

        Game game = match.getGame();
        Team team = match.getPlayerTeam().get(player.getUniqueId());

        if (team == null) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        UpgradeInventory upgradeInventory = (UpgradeInventory) e.getView().getTopInventory().getHolder();
        upgradeInventory.onClick(player, team, game, e.getSlot());
        upgradeInventory.updateInventory(player, team, game, e.getView().getTopInventory());
    }

    @EventHandler
    public void onClickShop(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getHolder() == null) {
            return;
        }
        if (!(e.getView().getTopInventory().getHolder() instanceof ShopInventory)) {
            return;
        }

        ShopInventory shopInventory = (ShopInventory) e.getView().getTopInventory().getHolder();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());

        if (match == null) {
            e.setCancelled(true);
            return;
        }

        Game game = match.getGame();
        Team team = match.getPlayerTeam().get(player.getUniqueId());

        if (team == null) {
            e.setCancelled(true);
            return;
        }

        int clickedSlot = e.getSlot();
        e.setCancelled(true);

        boolean isShiftClick = e.getClick() == ClickType.SHIFT_RIGHT;

        if (isShiftClick && shopInventory.hasBuyAction(clickedSlot) && !shopInventory.isQuickBuyIcon(clickedSlot)) {

            e.setCancelled(true);
            List<QuickBuyIcon> icons = game.getShopPath().getQuickBuySlots();

            if (!icons.isEmpty()) {

                InventoryIcon icon = shopInventory.getItems().get(e.getSlot()).clone();

                icon.setTeam(team);
                icon.setGame(game);
                icon.setPlayer(player);
                int slot = 19;

                Map<Integer, InventoryIcon> items = new HashMap<>();

                for (QuickBuyIcon ic : icons) {

                    ic.setPlayer(player);
                    ic.setGame(game);
                    ic.setTeam(team);

                    if ((slot == 26) || (slot == 27)) {
                        slot = 28;
                    }

                    if ((slot == 35) || (slot == 36)) {
                        slot = 37;
                    }

                    items.put(slot, ic);

                    if (slot == 43) {
                        break;
                    }

                    slot++;

                }

                QuickBuyInventory inv = new QuickBuyInventory("Adding to quick buy", 54, items, player, game, team,
                        icon);
                player.openInventory(inv.getInventory());
                e.setCancelled(true);
                return;
            }
        }

        e.setCancelled(true);
        shopInventory.onClick(player, team, game, e.getSlot());
        shopInventory.updateInventory(player, game, team, e.getView().getTopInventory());
    }

    @EventHandler
    public void onClickShop2(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getHolder() == null) {
            return;
        }
        if (!(e.getView().getTopInventory().getHolder() instanceof QuickBuyInventory)) {
            return;
        }

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());

        if (match == null) {
            return;
        }

        Game game = match.getGame();
        Team team = match.getPlayerTeam().get(player.getUniqueId());

        if (team == null) {
            return;
        }

        QuickBuyInventory inventory = (QuickBuyInventory) e.getView().getTopInventory().getHolder();

        e.setCancelled(true);
        inventory.onClick(player, team, game, e.getSlot());

    }

    @EventHandler
    public void onClickExtra(InventoryClickEvent e) {

//		Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            return;
        }

        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        if (e.getInventory().getHolder() == null) {
            return;
        }

        if (!(e.getView().getTopInventory().getHolder() instanceof ExtraInventory)) {
            return;
        }

//		AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());
//
//		if (match == null)
//			return;

        ExtraInventory extraInventory = (ExtraInventory) e.getView().getTopInventory().getHolder();
        extraInventory.onClick(e);

    }


    @EventHandler
    public void onCloseExtraInventory(InventoryCloseEvent e) {


        if (e.getInventory().getHolder() == null) {
            return;
        }

        if (!(e.getView().getTopInventory().getHolder() instanceof ExtraInventory)) {
            return;
        }


        ExtraInventory extraInventory = (ExtraInventory) e.getView().getTopInventory().getHolder();
        extraInventory.onClose(e);

    }

}
