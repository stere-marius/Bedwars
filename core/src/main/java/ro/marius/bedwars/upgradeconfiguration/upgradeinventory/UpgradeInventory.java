package ro.marius.bedwars.upgradeconfiguration.upgradeinventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.menu.CustomInventory;
import ro.marius.bedwars.menu.action.BuyItemAction;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeInventory extends CustomInventory {

    private final String CLICK_TO_PURCHASE = Lang.CLICK_TO_PURCHASE.getString();
    private final String NOT_ENOUGH_RESOURCES = Lang.NOT_ENOUGH_RESOURCES.getString();
    private Player player;
    private Team team;
    private Game game;

    public UpgradeInventory(Game game, String inventoryName, int size, Map<Integer, InventoryIcon> items) {
        super(inventoryName, size, items);
        this.game = game;
    }

    public UpgradeInventory(Game game, String inventoryName, int size) {
        super(inventoryName, size, new HashMap<>());
        this.game = game;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, this.getSize(), this.getInventoryName());

//		int index = 0;

        Map<String, TeamUpgrade> upgradeMap = this.team.getGameUpgrades();

        for (Map.Entry<Integer, InventoryIcon> entry : this.getItems().entrySet()) {

            if (entry.getKey() >= this.getSize()) {
                continue;
            }

            InventoryIcon ic = entry.getValue();

            if (ic == null) {
                continue;
            }

            ic.setPlayer(this.player);
            ic.setGame(this.game);
            ic.setTeam(this.team);
            InventoryIcon icon = ic.clone();
            icon.setPlayer(this.player);
            icon.setGame(this.game);
            icon.setTeam(this.team);

            ItemBuilder itemBuilder = icon.getItemBuilder();

            for (Requirement r : icon.getRequirement()) {

                r.readRequirement(this.team, this.player);

                if (!r.isActivated()) {
                    continue;
                }

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
                    boolean contains = this.player.getInventory().containsAtLeast(action.getPriceItemStack(), price);
                    String s = contains ? this.CLICK_TO_PURCHASE : this.NOT_ENOUGH_RESOURCES;
                    itemBuilder.replaceInLore("<hasEnoughResources>", s);
                    itemBuilder.replaceInLore("<cost>", price + "");

                    for (String u : upgradeMap.keySet()) {
                        itemBuilder.replaceInLore("<TIER_UPGRADE:" + u + ">", upgradeMap.get(u).getRomanTier());
                    }

                }

            }

            inventory.setItem(entry.getKey(), itemBuilder.build());
//			index++;
        }

        return inventory;
    }

    public void updateInventory(Player player, Team team, Game game, Inventory inventory) {

        Map<String, TeamUpgrade> upgradeMap = team.getGameUpgrades();

        for (Map.Entry<Integer, InventoryIcon> entry : this.getItems().entrySet()) {

            if (entry.getKey() >= this.getSize()) {
                continue;
            }

            InventoryIcon ic = entry.getValue();

            if (ic == null) {
                continue;
            }

            ic.setPlayer(player);
            ic.setGame(game);
            ic.setTeam(team);
            InventoryIcon icon = ic.clone();
            icon.setTeam(team);
            icon.setPlayer(player);
            icon.setGame(game);

            ItemBuilder itemBuilder = icon.getItemBuilder();

            for (Requirement r : icon.getRequirement()) {

                r.readRequirement(team, player);

                if (!r.isActivated()) {
                    continue;
                }

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
                    String s = contains ? this.CLICK_TO_PURCHASE : this.NOT_ENOUGH_RESOURCES;
                    itemBuilder.replaceInLore("<hasEnoughResources>", s);
                    itemBuilder.replaceInLore("<cost>", price + "");

                    for (String u : upgradeMap.keySet()) {
                        itemBuilder.replaceInLore("<TIER_UPGRADE:" + u + ">", upgradeMap.get(u).getRomanTier());
                    }

                }

            }

            inventory.setItem(entry.getKey(), itemBuilder.build());

        }

    }

    public void onClick(Player p, Team team, Game game, int slot) {

        InventoryIcon icon = this.getItems().get(slot);

        if (icon == null) {
            return;
        }

        List<IconAction> clickActionList = icon.getClickAction();

        if (clickActionList.isEmpty()) {
            return;
        }

        IconAction clickAction = clickActionList.get(0);

        clickAction.onClick(p, team, game);

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

    public String getCLICK_TO_PURCHASE() {
        return this.CLICK_TO_PURCHASE;
    }

    public String getNOT_ENOUGH_RESOURCES() {
        return this.NOT_ENOUGH_RESOURCES;
    }
}
