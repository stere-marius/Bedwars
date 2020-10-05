package ro.marius.bedwars.upgradeconfiguration.upgradeinventory;

import org.bukkit.entity.Player;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.menu.action.IconAction;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;

class BuyUpgradeAction implements IconAction {

    private final String upgradeName;

    public BuyUpgradeAction(String upgradeName) {
        this.upgradeName = upgradeName;
    }

    @Override
    public void onClick(Player p, Team team, Game game) {

        TeamUpgrade teamUpgrade = team.getGameUpgrades().get(this.upgradeName);

        if (teamUpgrade == null) {
            return;
        }

        teamUpgrade.onPurchase(p, false, false);

    }

    public String getUpgradeName() {
        return this.upgradeName;
    }
}
