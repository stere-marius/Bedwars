package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.Utils;

public class PaginatedInventory extends ExtraInventory {

    private final String inventoryName;
    private final int inventorySize;
    private final int nextPageSlot, previousPageSlot;
    private final ItemStack nextPageItem, previousPageItem;
    private final Pagination<InventoryItem> inventoryItems;

    private int currentPage;

    public PaginatedInventory(String inventoryName,
                              int inventorySize,
                              int nextPageSlot,
                              int previousPageSlot,
                              ItemStack nextPageItem,
                              ItemStack previousPageItem,
                              Pagination<InventoryItem> paginatedInventoryItems) {
        this.inventoryName = Utils.translate(inventoryName);
        this.inventorySize = inventorySize;
        this.nextPageSlot = nextPageSlot;
        this.previousPageSlot = previousPageSlot;
        this.nextPageItem = nextPageItem;
        this.previousPageItem = previousPageItem;
        this.inventoryItems = paginatedInventoryItems;
    }

    public PaginatedInventory(PaginatedInventory paginatedInventory, int currentPage) {
        this(paginatedInventory.inventoryName,
                paginatedInventory.inventorySize,
                paginatedInventory.nextPageSlot,
                paginatedInventory.previousPageSlot,
                paginatedInventory.nextPageItem,
                paginatedInventory.previousPageItem,
                paginatedInventory.inventoryItems);
        this.currentPage = currentPage;
    }

    @Override
    public @NotNull Inventory getInventory() {

        Inventory inventory = Bukkit.createInventory(this, inventorySize, inventoryName);
        createNextPage(inventory);
        createPreviousPage(inventory);
        setInventoryItems(inventory);

        return inventory;
    }

    private void setInventoryItems(Inventory inventory) {

        for (InventoryItem inventoryItem : inventoryItems.getPage(currentPage)) {

            if (inventoryItem.getSlot() >= inventorySize) {
                Bukkit.getLogger().info(Utils.translate("&4&l[BedWars] Couldn't set the item on slot " + inventoryItem.getSlot()
                        + " because it's out of inventory size &d" + inventoryItem));
                continue;
            }

            inventory.setItem(inventoryItem.getSlot(), inventoryItem.getItemStack());
        }

    }

    private void createNextPage(Inventory inventory) {

        if (!inventoryItems.existsPage(currentPage + 1))
            return;

        inventory.setItem(nextPageSlot, nextPageItem);
    }

    private void createPreviousPage(Inventory inventory) {

        if (!inventoryItems.existsPage(currentPage - 1))
            return;

        inventory.setItem(previousPageSlot, previousPageItem);
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        if (e.getCurrentItem() == null)
            return;

        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem().isSimilar(nextPageItem)) {
            handleNextPageClick(player);
            return;
        }

        if (e.getCurrentItem().isSimilar(previousPageItem)) {
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

}
