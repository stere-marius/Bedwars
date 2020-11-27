package ro.marius.bedwars.menu.extra;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.configuration.ConfiguredGUIItem;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.configuration.TeamSelectorConfiguration;
import ro.marius.bedwars.game.gameobserver.GameObserver;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.HashMap;
import java.util.UUID;

public class TeamSelectorInventory extends PaginatedInventory implements GameObserver {

    private final AMatch match;
    public static final TeamSelectorConfiguration TEAM_SELECTOR_CONFIGURATION = new TeamSelectorConfiguration();

    public TeamSelectorInventory(AMatch match) {
        super(TEAM_SELECTOR_CONFIGURATION.getConfig().getString("Menu.TeamSelector.InventoryName"),
                TEAM_SELECTOR_CONFIGURATION.getConfig().getInt("Menu.TeamSelector.InventorySize"),
                ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.NEXT_PAGE_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig()).getSlotList().get(0),
                ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.PREVIOUS_PAGE_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig()).getSlotList().get(0),
                ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.NEXT_PAGE_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig()).getItemBuilder().build(),
                ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.PREVIOUS_PAGE_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig()).getItemBuilder().build(),
                new Pagination<>(
                        ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.Contents.TEAM_PICKER_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig()).getSlotList().size()
                ),
                new HashMap<>());
        this.match = match;
        this.setPaginatedInventoryItems(getPaginationGameMappedItem());
    }

    public Pagination<InventoryItem> getPaginationGameMappedItem() {

        ConfiguredGUIItem teamPickerItem = ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.Contents.TEAM_PICKER_ITEM", TEAM_SELECTOR_CONFIGURATION.getConfig());
        Pagination<InventoryItem> pagination = new Pagination<>(teamPickerItem.getSlotList().size());

        int currentTeamPickerSlot = 0;
        boolean updateBasedOnTeamPlayers = (boolean) teamPickerItem.getAdditionalProperties().get("StackBasedOnTeamPlayers");

        for (Team team : match.getTeams()) {

            if (currentTeamPickerSlot >= teamPickerItem.getSlotList().size())
                continue;

            ItemBuilder itemBuilder = teamPickerItem.getItemBuilder().clone();
            itemBuilder.replaceItemStack(team.getTeamColor().getBuildMaterial());
            itemBuilder.setNBTTag(ManagerHandler.getVersionManager().getVersionWrapper(), "TeamName", team.getName());

            if (updateBasedOnTeamPlayers) {
                itemBuilder.withAmount(team.getPlayers().isEmpty() ? 1 : team.getPlayers().size());
            }

            itemBuilder.setDisplayName(itemBuilder.getDisplayName()
                    .replace("<teamColor>", team.getTeamColor().getChatColor())
                    .replace("<teamColorName>", team.getColorName())
                    .replace("<teamName>", team.getName()));
            itemBuilder
                    .replaceInLore("<teamColor>", team.getTeamColor().getChatColor())
                    .replaceInLore("<teamColorName>", team.getColorName())
                    .replaceInLore("<teamName>", team.getName())
                    .replaceInLore("<teamPlayersInLine>", team.getPlayersName());

            pagination.add(new InventoryItem(teamPickerItem.getSlotList().get(currentTeamPickerSlot), itemBuilder.build()));
            currentTeamPickerSlot = currentTeamPickerSlot + 1 >= teamPickerItem.getSlotList().size() ? 0 : ++currentTeamPickerSlot;
        }

        return pagination;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);

        if (e.getCurrentItem() == null)
            return;

        ItemStack itemStack = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        super.onClick(e);

        if (!ManagerHandler.getVersionManager().getVersionWrapper().containsNBTTag(itemStack, "TeamName"))
            return;

        String teamName = ManagerHandler.getVersionManager().getVersionWrapper().getNBTTag(itemStack, "TeamName");
        Team team = match.getTeam(teamName);

        if (team == null)
            return;

        if (team.getPlayers().size() >= match.getGame().getPlayersPerTeam()) {
            player.sendMessage(Lang.TEAM_FULL.getString().replace("<team>", team.getName()));
            return;
        }

        moveToTeam(team, player);
        e.setCancelled(true);
    }

    public void moveToTeam(Team chosenTeam, Player player) {

        UUID playerUUID = player.getUniqueId();
        Team currentPlayerTeam = match.getPlayerTeam().get(playerUUID);
        currentPlayerTeam.getPlayers().remove(player);
        match.getPlayerTeam().remove(playerUUID);
        match.getPlayerTeam().put(playerUUID, chosenTeam);
        chosenTeam.getPlayers().add(player);

    }

    @Override
    public void update() {
        TeamSelectorInventory.this.setPaginatedInventoryItems(getPaginationGameMappedItem());
        TeamSelectorInventory.super.setPaginatedItems();
    }

    @Override
    public void onClose(InventoryCloseEvent e) {
        this.match.getGame().removeObserver(this);
    }
}
