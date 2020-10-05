package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.configuration.ConfiguredGUIItem;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.configuration.TeamSelectorConfiguration;
import ro.marius.bedwars.game.gameobserver.GameObserver;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.UUID;

public class TeamSelectorInventory extends ExtraInventory implements GameObserver {

    private final AMatch match;


    private final TeamSelectorConfiguration teamSelectorConfiguration = new TeamSelectorConfiguration();
    private final ConfiguredGUIItem teamPicker = ConfiguredGUIItem.readFromConfig("Menu.TeamSelector.Contents.TEAM_PICKER_ITEM", teamSelectorConfiguration.getConfig());
    private final Inventory inventory;

    public TeamSelectorInventory(AMatch match) {
        this.match = match;
        this.inventory = createInventory();
        this.match.getGame().registerObserver(this);
    }

    @Override
    public @NotNull Inventory getInventory() {

        setInventoryItems();

        return inventory;
    }

    public Inventory createInventory() {
        String string = teamSelectorConfiguration.getConfig().getString("Menu.TeamSelector.InventoryName");
        int size = teamSelectorConfiguration.getConfig().getInt("Menu.TeamSelector.InventorySize");
        return Bukkit.createInventory(this, size, Utils.translate(string));
    }

    public void setInventoryItems() {

        int currentTeamPickerSlot = 0;
        boolean updateBasedOnTeamPlayers = (boolean) teamPicker.getAdditionalProperties().get("StackBasedOnTeamPlayers");

        for (Team team : match.getTeams()) {

            if (currentTeamPickerSlot >= teamPicker.getSlotList().size())
                continue;

            ItemBuilder itemBuilder = teamPicker.getItemBuilder().clone();
            itemBuilder.replaceItemStack(team.getTeamColor().getBuildMaterial());
            itemBuilder.setNBTTag(ManagerHandler.getVersionManager().getVersionWrapper(),"TeamName", team.getName());

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

            inventory.setItem(teamPicker.getSlotList().get(currentTeamPickerSlot), itemBuilder.build());
            currentTeamPickerSlot++;
        }

    }

    @Override
    public void onClick(InventoryClickEvent e) {

        e.setCancelled(true);

        if (e.getCurrentItem() == null)
            return;

        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        String teamName = ManagerHandler.getVersionManager().getVersionWrapper().getNBTTag(itemStack, "TeamName");
        Team team = match.getTeam(teamName);

        if (team == null)
            return;

        if (team.getPlayers().size() >= match.getGame().getPlayersPerTeam()) {
            player.sendMessage(Lang.TEAM_FULL.getString().replace("<team>", team.getName()));
            return;
        }

        moveToTeam(team, player);
        setInventoryItems();
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
        setInventoryItems();
    }


    @Override
    public void onClose(InventoryCloseEvent e) {
        this.match.getGame().removeObserver(this);
    }
}
