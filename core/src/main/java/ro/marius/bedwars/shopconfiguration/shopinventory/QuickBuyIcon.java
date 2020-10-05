package ro.marius.bedwars.shopconfiguration.shopinventory;

import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.playerdata.ArenaData;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.List;

public class QuickBuyIcon extends InventoryIcon {

    private final int slot;

    public QuickBuyIcon(String path, ItemBuilder itemBuilder, int slot) {
        super(path, itemBuilder);
        this.slot = slot;
    }

    @Override
    public ItemBuilder getItemBuilder() {

        APlayerData pData = ManagerHandler.getGameManager().getData(this.getPlayer());
        String arenaType = this.getGame().getArenaType();
        ArenaData arenaData = pData.getArenaData(arenaType);
        String itemName = arenaData.getQuickBuy().get(this.slot);

        if (itemName == null) {
            return super.getItemBuilder();
        }

//        WOOD:
//        Slot: 24
//        Material: WOOD
//        Amount: 16
//        Name: '&cWOOD'
//        Lore:
//            - '&7Cost: &64 Gold'
//            - ''
//            - '&7Good block to defend your bed'
//            - '&7Strong against pickaxes.'
//            - ''
//            - <sneakClickQuickBuy>
//            - <hasEnoughResources>
//        Action: BUY_ITEM
//        Price: 4
//        Price-Material: GOLD_INGOT
//        Receive:
//        ITEM:
//          WOOD:
//              Material: WOOD
//              Amount: 16

        InventoryIcon icon = this.getGame().getShopPath().getItem(itemName).clone();

        if (icon == null) {
            return super.getItemBuilder();
        }

        icon.setPlayer(this.getPlayer());
        icon.setGame(this.getGame());
        icon.setTeam(this.getTeam());

        this.setClickAction(icon.getClickAction());
        this.setRequirement(icon.getRequirement());

        return (icon.getItemBuilder() != null) ? icon.getItemBuilder().removeFromLore("<sneakClickQuickBuy>")
                : super.getItemBuilder();

    }

    public IconResult getResult() {

        APlayerData pData = ManagerHandler.getGameManager().getData(this.getPlayer());
        String arenaType = this.getGame().getArenaType();
        ArenaData arenaData = pData.getArenaData(arenaType);
        String itemName = arenaData.getQuickBuy().get(this.slot);

        if (itemName == null) {
            return new IconResult(false, super.getObject());
        }

        InventoryIcon icon = this.getGame().getShopPath().getItem(itemName).clone();

        if (icon == null) {
            return new IconResult(false, super.getObject());
        }

        icon.setPlayer(this.getPlayer());
        icon.setGame(this.getGame());
        icon.setTeam(this.getTeam());

        this.setClickAction(icon.getClickAction());
        this.setRequirement(icon.getRequirement());

        return (icon.getItemBuilder() != null) ? new IconResult(true, this) : new IconResult(false, super.getObject());

    }

    @Override
    public List<IconAction> getClickAction() {

        if ((this.getPlayer() == null) || (this.getTeam() == null) || (this.getGame() == null)) {
            return super.getClickAction();
        }

        APlayerData pData = ManagerHandler.getGameManager().getData(this.getPlayer());
        String arenaType = this.getGame().getArenaType();
        ArenaData arenaData = pData.getArenaData(arenaType);
        String itemName = arenaData.getQuickBuy().get(this.slot);

        if (itemName == null) {
            return super.getClickAction();
        }

        InventoryIcon icon = this.getGame().getShopPath().getItem(itemName).clone();

        if (icon == null) {
            return super.getClickAction();
        }

        return icon.getClickAction();
    }

    @Override
    public InventoryIcon clone() {
        QuickBuyIcon ic = new QuickBuyIcon(this.getPath(), new ItemBuilder(super.getItemBuilder()), this.slot);
        ic.setRequirement(this.getRequirement());
        ic.setClickAction(this.getClickAction());

        return ic;
    }

    public int getSlot() {
        return this.slot;
    }

    public static class IconResult {
        private final boolean result;
        private final InventoryIcon icon;

        public IconResult(boolean result, InventoryIcon icon) {
            this.result = result;
            this.icon = icon;
        }

        public boolean isResult() {
            return this.result;
        }

        public InventoryIcon getIcon() {
            return this.icon;
        }
    }

}
