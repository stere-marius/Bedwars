package ro.marius.bedwars.shopconfiguration.shopinventory;

import org.bukkit.entity.Player;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.team.Team;

class OpenGUIAction implements IconAction {

    private final String inventoryName;

    public OpenGUIAction(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    @Override
    public void onClick(Player p, Team team, Game game) {
        ShopInventory inv = game.getShopPath().getInventory().get(this.inventoryName);

        if (inv == null) {
            return;
        }

        inv.setPlayer(p);
        inv.setTeam(team);
        inv.setGame(game);

        p.openInventory(inv.getInventory());
    }

}
