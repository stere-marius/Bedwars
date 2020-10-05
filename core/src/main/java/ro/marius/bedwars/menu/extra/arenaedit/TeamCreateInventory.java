package ro.marius.bedwars.menu.extra.arenaedit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.Utils;

public class TeamCreateInventory extends ExtraInventory {

    private final Game game;
    private final String teamName;
    private final String teamColor;

    private Location spawnLocation;
    private Location bedLocation;
    private Location ironGeneratorLocation;
    private Location goldGeneratorLocation;
    private Location emeraldGeneratorLocation;
    private Location shopLocation;
    private Location upgradeLocation;

    public TeamCreateInventory(Game game, String teamName, String teamColor) {
        this.game = game;
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    @Override
    public Inventory getInventory() {

        Inventory inventory = Bukkit.createInventory(this,
                45,
                Utils.translate("&e&lCreating the team " + teamName));

        return inventory;
    }

}
