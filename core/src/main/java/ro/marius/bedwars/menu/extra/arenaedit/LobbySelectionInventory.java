package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.CuboidSelection;
import ro.marius.bedwars.utils.InventoryUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

public class LobbySelectionInventory extends ExtraInventory {

    private final GameEdit gameEdit;

    public LobbySelectionInventory(GameEdit gameEdit) {
        this.gameEdit = gameEdit;
    }


    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 36, Utils.translate("&eWaiting lobby selection"));

        inventory.setItem(9, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiYzJiY2ZiMmJkMzc1OWU2YjFlODZmYzdhNzk1ODVlMTEyN2RkMzU3ZmMyMDI4OTNmOWRlMjQxYmM5ZTUzMCJ9fX0=")
                .setDisplayName("&e&lPosition one")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        inventory.setItem(13, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkOWVlZWU4ODM0Njg4ODFkODM4NDhhNDZiZjMwMTI0ODVjMjNmNzU3NTNiOGZiZTg0ODczNDE0MTk4NDcifX19=")
                .setDisplayName("&e&lPosition two")
                .setLore(" ",
                        "&e&lLEFT &7click to set",
                        "&e&lRIGHT &7click to teleport")
                .build());

        inventory.setItem(17, new SkullBuilder()
                .withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==")
                .setDisplayName("&e&lGo back to game edit inventory")
                .build());

        InventoryUtils.fillEmptySlotsWithGlass(inventory);

        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        Game game = gameEdit.getGame();

        if (e.getSlot() == 17) {
            GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
            p.openInventory(gameEditInventory.getInventory());
            return;
        }

        if (e.getClick() == ClickType.RIGHT) {

            if (game.getWaitingLobbySelection() == null) {
                p.sendMessage(Utils.translate("&e>> The waiting lobby has not been initialized. Set the fist location to initialize it."));
                return;
            }

            if (e.getSlot() == 9) {
                p.teleport(game.getWaitingLobbySelection().getPositionOne());
                p.sendMessage(Utils.translate("&e>> You have been teleported."));
                return;
            }

            if (e.getSlot() == 13) {
                p.teleport(game.getWaitingLobbySelection().getPositionTwo());
                p.sendMessage(Utils.translate("&e>> You have been teleported."));
                return;
            }

            return;
        }

        if (e.getClick() == ClickType.LEFT) {

            if (game.getWaitingLobbySelection() == null) {
                game.setWaitingLobbySelection(new CuboidSelection(p.getLocation(), p.getLocation().add(1, 3, 1)));
            }

            if (e.getSlot() == 9) {
                game.getWaitingLobbySelection().setPositionOne(p.getLocation());
                p.sendMessage(Utils.translate("&e>> The position one has been set to " + Utils.getStringIntCoordinates(p.getLocation())));
                return;
            }

            if (e.getSlot() == 13) {
                game.getWaitingLobbySelection().setPositionTwo(p.getLocation());
                p.sendMessage(Utils.translate("&e>> The position two has been set to " + Utils.getStringIntCoordinates(p.getLocation())));
                return;
            }

            return;
        }

        if (e.getSlot() == 17) {
            GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
            p.openInventory(gameEditInventory.getInventory());
        }

    }

}
