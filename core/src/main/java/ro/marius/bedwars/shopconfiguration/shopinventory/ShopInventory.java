package ro.marius.bedwars.shopconfiguration.shopinventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.CustomInventory;
import ro.marius.bedwars.menu.action.BuyItemAction;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.ArenaData;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ShopInventory extends CustomInventory {

    private static final String CLICK_TO_PURCHASE = Lang.CLICK_TO_PURCHASE.getString();
    private static final String NOT_ENOUGH_RESOURCES = Lang.NOT_ENOUGH_RESOURCES.getString();
    private static final String CLICK_ADD = Lang.SNEAK_CLICK_QUICK_BUY_ADD.getString();
    private String arenaType;
    private Player player;
    private Team team;
    private Game game;
//	private final String CLICK_REMOVE = Lang.SNEAK_CLICK_QUICK_BUY_REMOVE.getString();

    public ShopInventory(String arenaType, String inventoryName, int size, Map<Integer, InventoryIcon> items) {
        super(inventoryName, size, items);
        this.arenaType = arenaType;
    }

// 	definesc not enough resources si enough resources	
//

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.getSize(), this.getInventoryName());

        Map<String, TeamUpgrade> upgradeMap = this.team.getGameUpgrades();
        APlayerData data = ManagerHandler.getGameManager().getData(this.player);
        ArenaData arenaData = data.getArenaData(this.game.getArenaType());
        Map<Integer, String> quickBuy = arenaData.getQuickBuy();
        Collection<String> values = quickBuy.values();

        for (Map.Entry<Integer, InventoryIcon> entry : this.getItems().entrySet()) {

            if (entry.getKey() >= this.getSize()) {
                continue;
            }

            InventoryIcon ic = entry.getValue();

            if (ic == null) {
                continue;
            }

            InventoryIcon icon = ic.clone();
            icon.setPlayer(this.player);
            icon.setGame(this.game);
            icon.setTeam(this.team);

            ItemBuilder itemBuilder = new ItemBuilder(icon.getItemBuilder())/* .clone() */;

            for (Requirement r : icon.getRequirement()) {

                r.readRequirement(this.team, this.player);

                if (!r.isActivated()) {
                    continue;
                }

//					TODO: Verific cand dau load ca requirement builder sa nu fie null
//					if(r.getRequirementBuilder() == null)
//						continue;

                r.getRequirementBuilder().apply(icon);
            }


            if (!icon.getClickAction().isEmpty()) {
                IconAction clickAction = icon.getClickAction().get(0);

                if (clickAction instanceof BuyItemAction) {

                    BuyItemAction action = (BuyItemAction) clickAction;
                    int price = action.getPrice();
                    boolean contains = this.player.getInventory().containsAtLeast(action.getPriceItemStack(), price);

                    String s = contains ? CLICK_TO_PURCHASE : NOT_ENOUGH_RESOURCES;
                    itemBuilder.replaceInLore("<hasEnoughResources>", s);
                    itemBuilder.replaceInLore("<cost>", price + "");

                    for (String u : upgradeMap.keySet()) {
                        itemBuilder.replaceInLore("<TIER_TEAM_UPGRADE:" + u + ">", upgradeMap.get(u).getRomanTier());
                    }
                }
            }

            if (values.contains(icon.getPath())) {
                itemBuilder.removeFromLore("<sneakClickQuickBuy>");
            } else {
                itemBuilder.replaceInLore("<sneakClickQuickBuy>", CLICK_ADD);
            }

            inventory.setItem(entry.getKey(), itemBuilder.build());
        }

        return inventory;
    }

    public void updateInventory(Player player, Game game, Team team, Inventory inventory) {

        Map<String, TeamUpgrade> upgradeMap = team.getGameUpgrades();
        APlayerData data = ManagerHandler.getGameManager().getData(player);
        ArenaData arenaData = data.getArenaData(game.getArenaType());
        Map<Integer, String> quickBuy = arenaData.getQuickBuy();
        Collection<String> values = quickBuy.values();

        for (Map.Entry<Integer, InventoryIcon> entry : this.getItems().entrySet()) {

            if (entry.getKey() >= this.getSize()) {
                continue;
            }

            InventoryIcon ic = entry.getValue();

            if (ic == null) {
                continue;
            }

            InventoryIcon icon = ic.clone();
            icon.setPlayer(player);
            icon.setGame(game);
            icon.setTeam(team);

            ItemBuilder itemBuilder = icon.getItemBuilder();


            for (Requirement r : icon.getRequirement()) {

                r.readRequirement(team, player);

                //					TODO: Verific cand dau load ca requirement builder sa nu fie null
//					if(r.getRequirementBuilder() == null)
//						continue;

//					r.getRequirementBuilder().apply(icon);
            }


            if (!icon.getClickAction().isEmpty()) {
                IconAction clickAction = icon.getClickAction().get(0);

                if (clickAction instanceof BuyItemAction) {

                    BuyItemAction action = (BuyItemAction) clickAction;
                    int price = action.getPrice();
                    boolean contains = player.getInventory().containsAtLeast(action.getPriceItemStack(), price);
                    String s = contains ? CLICK_TO_PURCHASE : NOT_ENOUGH_RESOURCES;
                    itemBuilder.replaceInLore("<hasEnoughResources>", s);
                    itemBuilder.replaceInLore("<cost>", price + "");

                    for (String u : upgradeMap.keySet()) {
                        itemBuilder.replaceInLore("<TIER_UPGRADE:" + u + ">", upgradeMap.get(u).getRomanTier());
                    }

                }

            }

            if (values.contains(icon.getPath())) {
                itemBuilder.removeFromLore("<sneakClickQuickBuy>");
            } else {
                itemBuilder.replaceInLore("<sneakClickQuickBuy>", CLICK_ADD);
            }

            inventory.setItem(entry.getKey(), itemBuilder.build());

        }

    }

    public boolean hasBuyAction(int slot) {

        InventoryIcon icon = super.getItems().get(slot);

        return (icon != null) && !icon.getClickAction().isEmpty()
                && !(icon.getClickAction().get(0) instanceof OpenGUIAction);

    }

    public boolean isQuickBuyIcon(int slot) {

        InventoryIcon icon = super.getItems().get(slot);

        return icon instanceof QuickBuyIcon;

    }

    public void onClick(Player p, Team team, Game game, int slot) {

        InventoryIcon icon = this.getItems().get(slot);

        if (icon == null) {
            return;
        }

        icon.setPlayer(p);
        icon.setTeam(team);
        icon.setGame(game);

        List<IconAction> clickActionList = icon.getClickAction();

        if (clickActionList.isEmpty()) {
            return;
        }

        IconAction clickAction = clickActionList.get(0);

        clickAction.onClick(p, team, game);

    }

    public String getArenaType() {
        return this.arenaType;
    }

    public void setArenaType(String arenaType) {
        this.arenaType = arenaType;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
