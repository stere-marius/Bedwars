package ro.marius.bedwars.menu.icon;

import org.bukkit.entity.Player;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class InventoryIcon  {

    private final String path;
    private Player player;
    private Team team;
    private Game game;
    private List<IconAction> clickAction = new ArrayList<>();
    private List<Requirement> requirement = new ArrayList<>();
    private ItemBuilder itemBuilder;

    public InventoryIcon(String path, ItemBuilder itemBuilder) {
        this.path = path;
        this.itemBuilder = itemBuilder;
    }

    @Override
    public InventoryIcon clone() {
        InventoryIcon icon = new InventoryIcon(this.getPath(), new ItemBuilder(this.getItemBuilder()));
        icon.setRequirement(this.getRequirement());
        icon.setClickAction(this.getClickAction());

        return icon;
    }

    public InventoryIcon getObject() {

        return this;
    }

    public void addAction(IconAction action) {
        this.clickAction.add(action);
    }

    public String getPath() {
        return this.path;
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

    public List<IconAction> getClickAction() {
        return this.clickAction;
    }

    public void setClickAction(List<IconAction> clickAction) {
        this.clickAction = clickAction;
    }

    public List<Requirement> getRequirement() {
        return this.requirement;
    }

    public void setRequirement(List<Requirement> requirement) {
        this.requirement = requirement;
    }

    public ItemBuilder getItemBuilder() {
        return this.itemBuilder;
    }

    public void setItemBuilder(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }
}
