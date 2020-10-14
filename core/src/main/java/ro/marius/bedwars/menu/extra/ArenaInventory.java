package ro.marius.bedwars.menu.extra;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.configuration.GUIStructure;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.menu.GUIItem;
import ro.marius.bedwars.utils.StringUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArenaInventory extends PaginatedInventory {

    private static final String SEARCHING_DISPLAY = Utils
            .translate(GUIStructure.getConfig().getString("Menu.ArenaInventory.SearchingDisplay"));
    private static final String STARTING_DISPLAY = Utils
            .translate(GUIStructure.getConfig().getString("Menu.ArenaInventory.StartingDisplay"));
    private static final int NEXT_PAGE_SLOT = GUIStructure.getConfig().getInt("Menu.ArenaInventory.NextPage.Slot");
    private static final int PREVIOUS_PAGE_SLOT = GUIStructure.getConfig().getInt("Menu.ArenaInventory.PreviousPage.Slot");
    private static final int INVENTORY_SIZE = GUIStructure.getInventorySize("Menu.ArenaInventory");
    private static final String MENU_NAME = GUIStructure.getConfig().getString("Menu.ArenaInventory.InventoryName");
    private static final List<Integer> ARENA_ITEM_SLOTS = GUIStructure.getSlot("Menu.ArenaInventory.ArenaSlot");
    private static final ItemBuilder IN_WAITING = GUIStructure.readBuilder("Menu.ArenaInventory.WaitingArena");
    private static final ItemBuilder STARTING = GUIStructure.readBuilder("Menu.ArenaInventory.StartingArena");
    private static final ItemBuilder NEXT_PAGE_ITEM = GUIStructure.readBuilder("Menu.ArenaInventory.NextPage");
    private static final ItemBuilder PREVIOUS_PAGE_ITEM = GUIStructure.readBuilder("Menu.ArenaInventory.PreviousPage");
    private static final Map<Integer, GUIItem> ITEMS = GUIStructure.readInventory("Menu.ArenaInventory");


    private final String arenaType;
    private final String arenaTypeFirstLetter;
    private BukkitTask taskUpdateInventory;

    public ArenaInventory() {
        super(MENU_NAME,
                INVENTORY_SIZE,
                NEXT_PAGE_SLOT,
                PREVIOUS_PAGE_SLOT,
                NEXT_PAGE_ITEM.build(),
                PREVIOUS_PAGE_ITEM.build(),
                new Pagination<>(ARENA_ITEM_SLOTS.size()),
                ITEMS);
        this.arenaType = "";
        this.arenaTypeFirstLetter = "";
        this.setPaginatedInventoryItems(getPaginationGameMappedItem());
    }


    public ArenaInventory(String arenaType) {
        super(MENU_NAME,
                INVENTORY_SIZE,
                NEXT_PAGE_SLOT,
                PREVIOUS_PAGE_SLOT,
                NEXT_PAGE_ITEM.build(),
                PREVIOUS_PAGE_ITEM.build(),
                new Pagination<>(ARENA_ITEM_SLOTS.size()),
                ITEMS);
        this.arenaType = arenaType;
        this.arenaTypeFirstLetter = StringUtils.getFirstLetterUpperCase(arenaType);
        this.setPaginatedInventoryItems(getPaginationGameMappedItem());
    }

    @Override
    public @NotNull Inventory getInventory() {

        Inventory builtPaginatedInventory = super.getInventory();
        updateInventoryBasedOnGameActivity();

        return builtPaginatedInventory;
    }

    private Pagination<InventoryItem> getPaginationGameMappedItem() {

        Pagination<InventoryItem> itemPagination = new Pagination<>(ARENA_ITEM_SLOTS.size());
        VersionWrapper versionWrapper = ManagerHandler.getVersionManager().getVersionWrapper();

        List<Game> games = ManagerHandler.getGameManager().getGames().stream()
                .filter(g -> (g.getMatch().getMatchState() == MatchState.IN_WAITING) && !g.getMatch().isFull()
                        && (this.arenaType.isEmpty() || g.getArenaType().equals(this.arenaType)))
                .collect(Collectors.toList());

        int currentSlotIndex = 0;

        for (Game game : games) {
            AMatch match = game.getMatch();
            String arenaName = game.getName();
            String arenaType = game.getArenaType();
            ItemBuilder stateItemBuilder = match.isStarting() ? STARTING : IN_WAITING;
            ItemStack waiting = stateItemBuilder.clone()
                    .setDisplayName(stateItemBuilder.getDisplayName()
                            .replace("<arenaName>", arenaName)
                            .replace("<arenaTypeFirstLetterUppercase>", this.arenaTypeFirstLetter)
                            .replace("<arenaType>", arenaType).replace("<inGame>", match.getPlayers().size() + "")
                            .replace("<maxPlayers>", game.getMaxPlayers() + ""))
                    .replaceInLore("<arenaName>", arenaName).replaceInLore("<arenaType>", arenaType)
                    .replaceInLore("<inGame>", match.getPlayers().size() + "")
                    .replaceInLore("<maxPlayers>", game.getMaxPlayers() + "").setNBTTag(versionWrapper, "BWArena", arenaName)
                    .replaceInLore(
                            "<status>",
                            match.isStarting() ? STARTING_DISPLAY.replace("<seconds>", match.getStartingTime() + "") : SEARCHING_DISPLAY)
                    .replaceInLore("<seconds>", match.getStartingTime() + "")
                    .replaceInLore("<arenaTypeFirstLetterUppercase>", this.arenaTypeFirstLetter).build();
            itemPagination.add(new InventoryItem(ARENA_ITEM_SLOTS.get(currentSlotIndex), waiting));
            currentSlotIndex = currentSlotIndex + 1 >= ARENA_ITEM_SLOTS.size() ? 0 : ++currentSlotIndex;
        }

        return itemPagination;
    }


    public void updateInventoryBasedOnGameActivity() {

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                ArenaInventory.this.setPaginatedInventoryItems(getPaginationGameMappedItem());
                ArenaInventory.super.setPaginatedItems();
            }
        };

        this.taskUpdateInventory = bukkitRunnable.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        e.setCancelled(true);

        if (ManagerHandler.getVersionManager().getVersionWrapper().containsNBTTag(item, "BWArena")) {
            String arenaName = ManagerHandler.getVersionManager().getVersionWrapper().getNBTTag(item, "BWArena");
            ManagerHandler.getGameManager().getGame(arenaName).getMatch().addPlayer(p);
            return;
        }

        super.onClick(e);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        taskUpdateInventory.cancel();
    }


}
