package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
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
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.menu.GUIItem;
import ro.marius.bedwars.utils.StringUtils;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ArenaInventory extends ExtraInventory {

    private static final String SEARCHING_DISPLAY = Utils
            .translate(GUIStructure.getConfig().getString("Menu.ArenaInventory.SearchingDisplay"));
    private static final String STARTING_DISPLAY = Utils
            .translate(GUIStructure.getConfig().getString("Menu.ArenaInventory.StartingDisplay"));
    private static final int NEXT_PAGE_SLOT = GUIStructure.getConfig().getInt("Menu.ArenaInventory.NextPage.Slot");
    private static final int PREV_PAGE_SLOT = GUIStructure.getConfig().getInt("Menu.ArenaInventory.PreviousPage.Slot");
    private static final int SIZE = GUIStructure.getInventorySize("Menu.ArenaInventory");
    private static final String MENU_NAME = GUIStructure.getConfig().getString("Menu.ArenaInventory.InventoryName");
    private static final List<Integer> SLOTS = GUIStructure.getSlot("Menu.ArenaInventory.ArenaSlot");
    private static final ItemBuilder IN_WAITING = GUIStructure.readBuilder("Menu.ArenaInventory.WaitingArena");
    private static final ItemBuilder STARTING = GUIStructure.readBuilder("Menu.ArenaInventory.StartingArena");
    private static final ItemBuilder NEXT_PAGE = GUIStructure.readBuilder("Menu.ArenaInventory.NextPage");
    private static final ItemBuilder PREVIOUS_PAGE = GUIStructure.readBuilder("Menu.ArenaInventory.PreviousPage");
    private static final Map<Integer, GUIItem> ITEMS = GUIStructure.readInventory("Menu.ArenaInventory");

//    private Map<String, ConfiguredGUIItem> items = new TeamSelectorConfiguration()

    private final String arenaType;
    private final String arenaTypeFirstLetter;
    private int page;
    private BukkitTask bukkitTask;

    public ArenaInventory() {
        this.page = 0;
        this.arenaType = "";
        this.arenaTypeFirstLetter = "";
    }

    public ArenaInventory(String arenaType) {
        this.page = 0;
        this.arenaType = arenaType;
        this.arenaTypeFirstLetter = StringUtils.getFirstLetterUpperCase(arenaType);
    }

    public ArenaInventory(String arenaType, int page) {
        this.page = page;
        this.arenaType = arenaType;
        this.arenaTypeFirstLetter = StringUtils.getFirstLetterUpperCase(arenaType);
    }

    @Override
    public @NotNull Inventory getInventory() {

        Inventory inventory = Bukkit.createInventory(this, SIZE, Utils.translate(MENU_NAME));
        VersionWrapper versionWrapper = ManagerHandler.getVersionManager().getVersionWrapper();

        List<Game> games = ManagerHandler.getGameManager().getGames().stream()
                .filter(g -> (g.getMatch().getMatchState() == MatchState.IN_WAITING) && !g.getMatch().isFull()
                        && (this.arenaType.isEmpty() || g.getArenaType().equals(this.arenaType)))
                .collect(Collectors.toList());

        Map<Integer, List<Game>> pages = new LinkedHashMap<>();
        int size = games.size();
        int totalItems = SLOTS.size();
        int pageSize = ((size % totalItems) == 0) ? (size / totalItems) : ((size / totalItems) + 1);

        for (int i = 0; i < pageSize; i++) {
            pages.put(i, Utils.getSubList(games, i * totalItems, (i * totalItems) + totalItems));
        }

        List<Game> pageGames = pages.get(this.page);

        if (pageGames == null) {
            pageGames = new ArrayList<>();
        }

        int pageGamesSize = pageGames.size();

        for (int i = 0; i < totalItems; i++) {

            int slot = SLOTS.get(i);

            if (i >= pageGamesSize) {
                break;
            }

            AMatch match = pageGames.get(i).getMatch();
            Game game = match.getGame();
            String arenaName = game.getName();
            String arenaType = game.getArenaType();

            ItemStack waiting = IN_WAITING.clone()
                    .setDisplayName(IN_WAITING.getDisplayName()
                            .replace("<arenaName>", arenaName)
                            .replace("<arenaTypeFirstLetterUppercase>", this.arenaTypeFirstLetter)
                            .replace("<arenaType>", arenaType).replace("<inGame>", match.getPlayers().size() + "")
                            .replace("<maxPlayers>", game.getMaxPlayers() + ""))
                    .replaceInLore("<arenaName>", arenaName).replaceInLore("<arenaType>", arenaType)
                    .replaceInLore("<inGame>", match.getPlayers().size() + "")
                    .replaceInLore("<maxPlayers>", game.getMaxPlayers() + "").setNBTTag(versionWrapper,"BWArena", arenaName)
                    .replaceInLore("<status>", match.isStarting() ? STARTING_DISPLAY : SEARCHING_DISPLAY)
                    .replaceInLore("<arenaTypeFirstLetterUppercase>", this.arenaTypeFirstLetter).build();
//			ItemStack game = IN_GAME.clone().setNBTTag("BWArena", arenaName).build();

            inventory.setItem(slot, waiting);
        }

        for (Entry<Integer, GUIItem> entry : ITEMS.entrySet()) {

            int key = entry.getKey();

            if (key >= SIZE) {
                Bukkit.getLogger().info(Utils.translate("&e[BedWars] Couldn't set the item on slot " + key
                        + " because it's out of inventory size &d" + SIZE));
                continue;
            }

            inventory.setItem(key, entry.getValue().getBuilder().build());

        }

        if ((pages.get(this.page + 1) != null) && (NEXT_PAGE_SLOT < SIZE)) {
            inventory.setItem(NEXT_PAGE_SLOT, NEXT_PAGE.setNBTTag(versionWrapper,"BWNextPage", String.valueOf(this.page + 1)).build());
        }

        if ((pages.get(this.page - 1) != null) && (PREV_PAGE_SLOT < SIZE)) {
            inventory.setItem(PREV_PAGE_SLOT,
                    PREVIOUS_PAGE.setNBTTag(versionWrapper,"BWPreviousPage", String.valueOf(this.page - 1)).build());
        }

        this.onUpdate(inventory);

        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);

        ItemStack item = e.getCurrentItem();

        if (ManagerHandler.getVersionManager().getVersionWrapper().containsNBTTag(item, "BWArena")) {
            String arenaName = ManagerHandler.getVersionManager().getVersionWrapper().getNBTTag(item, "BWArena");
            ManagerHandler.getGameManager().getGame(arenaName).getMatch().addPlayer(p);
            return;
        }

        if (ManagerHandler.getVersionManager().getVersionWrapper().containsNBTTag(item, "BWNextPage")) {
            ArenaInventory arenaInventory = new ArenaInventory(this.arenaType, ++this.page);
            p.openInventory(arenaInventory.getInventory());
            return;
        }

        if (ManagerHandler.getVersionManager().getVersionWrapper().containsNBTTag(item, "BWPreviousPage")) {
            ArenaInventory arenaInventory = new ArenaInventory(this.arenaType, --this.page);
            p.openInventory(arenaInventory.getInventory());
            return;
        }

        super.onClick(e);
    }

    @Override
    public void onClose(InventoryCloseEvent e) {

        if (this.bukkitTask == null) {
            return;
        }

        this.bukkitTask.cancel();
    }

    public void onUpdate(Inventory inventory) {

        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {

                VersionWrapper versionWrapper = ManagerHandler.getVersionManager().getVersionWrapper();

                for (int slot : SLOTS) {

                    ItemStack item = inventory.getItem(slot);

                    if (item == null) {
                        return;
                    }

                    String arenaName = ManagerHandler.getVersionManager().getVersionWrapper().getNBTTag(item, "BWArena");

                    if (arenaName == null) {
                        continue;
                    }

                    Game game = ManagerHandler.getGameManager().getGame(arenaName);

                    if (game == null) {
                        continue;
                    }

                    AMatch match = game.getMatch();

                    if (match.getMatchState() != MatchState.IN_WAITING) {
                        List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());
                        viewers.forEach(v -> v.openInventory(ArenaInventory.this.getInventory()));
                        this.cancel();
                        return;
                    }

                    ItemBuilder builder = match.isStarting() ? STARTING.clone() : IN_WAITING.clone();

                    inventory.setItem(slot,
                            builder.setDisplayName(builder.getDisplayName().replace("<arenaName>", arenaName)
                                    .replace("<arenaTypeFirstLetterUppercase>", ArenaInventory.this.arenaTypeFirstLetter))
                                    .replaceInLore("<arenaTypeFirstLetterUppercase>", ArenaInventory.this.arenaTypeFirstLetter)
                                    .replaceInLore("<arenaName>", arenaName)
                                    .replaceInLore("<arenaType>", game.getArenaType())
                                    .replaceInLore(
                                            "<status>",
                                            match.isStarting()
                                                    ? STARTING_DISPLAY.replace("<seconds>",
                                                    match.getStartingTime() + "")
                                                    : SEARCHING_DISPLAY)
                                    .replaceInLore("<inGame>", match.getPlayers().size() + "")
                                    .replaceInLore("<maxPlayers>", game.getMaxPlayers() + "")
                                    .replaceInLore("<seconds>", match.getStartingTime() + "")
                                    .setNBTTag(versionWrapper,"BWArena", arenaName).build());

                }

            }
        };

        this.bukkitTask = runnable.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);

    }

}
