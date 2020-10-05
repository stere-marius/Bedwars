package ro.marius.bedwars.shopconfiguration.shopinventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.CustomInventory;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.shopconfiguration.shopinventory.QuickBuyIcon.IconResult;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.Map;
import java.util.Map.Entry;

public class QuickBuyInventory extends CustomInventory {

    private final InventoryIcon itemToAdd;
    private static final String CLICK_TO_REPLACE = Lang.CLICK_TO_REPLACE.getString();
    private static final String CLICK_TO_SET = Lang.CLICK_TO_SET.getString();
    private final Game game;
    private final Player player;
    private final Team team;

    public QuickBuyInventory(String inventoryName, int size, Map<Integer, InventoryIcon> items, Player p, Game game,
                             Team team, InventoryIcon itemToAdd) {
        super(inventoryName, size, items);
        this.game = game;
        this.player = p;
        this.team = team;
        this.itemToAdd = itemToAdd;
    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, this.getSize(), this.getInventoryName());

        if (this.itemToAdd != null) {
            inv.setItem(4, this.itemToAdd.getItemBuilder().build());
        }

        for (Entry<Integer, InventoryIcon> entry : this.getItems().entrySet()) {

            InventoryIcon icon = entry.getValue();

            if (icon == null) {
                continue;
            }

            QuickBuyIcon quickIcon = (QuickBuyIcon) icon.clone();

            quickIcon.setPlayer(this.player);
            quickIcon.setGame(this.game);
            quickIcon.setTeam(this.team);

            IconResult result = quickIcon.getResult();
            ItemBuilder builder = result.getIcon().getItemBuilder();

            if (result.isResult()) {
                builder.setLore(CLICK_TO_REPLACE);
            } else {
                builder.setLore(CLICK_TO_SET);
            }

            inv.setItem(entry.getKey(), builder.build());

        }

        return inv;
    }

    public void onClick(Player p, Team team, Game game, int slot) {

        APlayerData pData = ManagerHandler.getGameManager().getData(p);

        int s = slot;

        if ((s >= 19) && (s <= 25)) {
            s = s - 19;
        } else if ((s >= 28) && (s <= 34)) {
            s = (s - 28) + 7;
        } else if ((s >= 37) && (s <= 43)) {
            s = (s - 37) + 14;
        }

        Map<Integer, String> slots = pData.getArenaData(game.getArenaType()).getQuickBuy();

        slots.put(s, this.itemToAdd.getPath());

        p.closeInventory();

        ShopInventory inv = game.getShopPath().getInventory().get("MAIN_INVENTORY");
        inv.setPlayer(p);
        inv.setTeam(team);
        inv.setGame(game);

        p.openInventory(inv.getInventory());

    }

}
