package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.menu.GUIItem;
import ro.marius.bedwars.utils.Utils;

import java.util.Map;

public class PaginatedInventory extends ExtraInventory {

    private String inventoryName;
    private final int inventorySize;
    private final int nextPageSlot, previousPageSlot;
    private final ItemStack nextPageItem, previousPageItem;

    private Pagination<InventoryItem> paginatedInventoryItems;
    private final Map<Integer, GUIItem> extraInventoryItems;
    private Inventory inventory;
    protected int currentPage;

    public PaginatedInventory(String inventoryName,
                              int inventorySize,
                              int nextPageSlot,
                              int previousPageSlot,
                              ItemStack nextPageItem,
                              ItemStack previousPageItem,
                              Pagination<InventoryItem> paginatedInventoryItems,
                              Map<Integer, GUIItem> extraInventoryItems) {
        this.inventoryName = Utils.translate(inventoryName);
        this.inventorySize = inventorySize;
        this.nextPageSlot = nextPageSlot;
        this.previousPageSlot = previousPageSlot;
        this.nextPageItem = nextPageItem;
        this.previousPageItem = previousPageItem;
        this.paginatedInventoryItems = paginatedInventoryItems;
        this.inventory = Bukkit.createInventory(this, inventorySize, Utils.translate(inventoryName));
        this.extraInventoryItems = extraInventoryItems;

    }

    public PaginatedInventory(PaginatedInventory paginatedInventory, int currentPage) {
        this.inventoryName = paginatedInventory.inventoryName;
        this.inventorySize = paginatedInventory.inventorySize;
        this.nextPageSlot = paginatedInventory.nextPageSlot;
        this.previousPageSlot = paginatedInventory.previousPageSlot;
        this.nextPageItem = paginatedInventory.nextPageItem;
        this.previousPageItem = paginatedInventory.previousPageItem;
        this.paginatedInventoryItems = paginatedInventory.paginatedInventoryItems;
        this.extraInventoryItems = paginatedInventory.extraInventoryItems;
        this.currentPage = currentPage;

    }

    @Override
    public @NotNull Inventory getInventory() {
        inventory = Bukkit.createInventory(this, inventorySize, Utils.translate(inventoryName));
        setInventoryItems();

        return inventory;
    }

    private void setInventoryItems() {
        setPaginatedItems();
        setExtraInventoryItems();
        createNextPage();
        createPreviousPage();
    }

    public void setPaginatedItems() {

        for (InventoryItem paginatedInventoryItem : paginatedInventoryItems.getPage(currentPage)) {

            if (isOutOfInventorySize(paginatedInventoryItem.getSlot())) {
                Bukkit.getLogger().info(Utils.translate("&4&l[BedWars] Couldn't set the item on slot " + paginatedInventoryItem.getSlot()
                        + " because it's out of inventory size &d" + inventorySize));
                continue;
            }

            inventory.setItem(paginatedInventoryItem.getSlot(), paginatedInventoryItem.getItemStack());
        }

    }

    private void setExtraInventoryItems() {

        for (Map.Entry<Integer, GUIItem> entry : extraInventoryItems.entrySet()) {

            if (isOutOfInventorySize(entry.getKey())) {
                Bukkit.getLogger().info(Utils.translate("&4&l[BedWars] Couldn't set the item on slot " + entry.getKey()
                        + " because it's out of inventory size &d" + inventorySize));
                continue;
            }

            inventory.setItem(entry.getKey(), entry.getValue().getBuilder().build());
        }

    }

    public boolean isOutOfInventorySize(int slot) {
        return slot >= inventorySize;
    }

    private void createNextPage() {

        if (!paginatedInventoryItems.existsPage(currentPage + 1))
            return;

        inventory.setItem(nextPageSlot, nextPageItem);
    }

    private void createPreviousPage() {

        if (!paginatedInventoryItems.existsPage(currentPage - 1))
            return;

        inventory.setItem(previousPageSlot, previousPageItem);
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null)
            return;

        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);
        handlePageClick(e.getCurrentItem(), player);
    }

    public void handlePageClick(ItemStack itemStack, Player player){

        if (itemStack.isSimilar(nextPageItem)) {
            handleNextPageClick(player);
            return;
        }

        if (itemStack.isSimilar(previousPageItem)) {
            handlePreviousPageClick(player);
        }
    }

    public void handleNextPageClick(Player player) {
        PaginatedInventory paginatedInventory = new PaginatedInventory(this, ++currentPage);
        player.openInventory(paginatedInventory.getInventory());
    }

    public void handlePreviousPageClick(Player player) {
        PaginatedInventory paginatedInventory = new PaginatedInventory(this, --currentPage);
        player.openInventory(paginatedInventory.getInventory());
    }

    public void setPaginatedInventoryItems(Pagination<InventoryItem> paginatedInventoryItems) {
        this.paginatedInventoryItems = paginatedInventoryItems;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }
}
