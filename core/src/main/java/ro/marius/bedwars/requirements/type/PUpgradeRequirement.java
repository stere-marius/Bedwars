package ro.marius.bedwars.requirements.type;

import org.bukkit.entity.Player;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.requirements.RequirementBuilder;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;

public class PUpgradeRequirement extends Requirement {

    private String upgradeName;
    private RequirementBuilder requirementBuilder;

    public PUpgradeRequirement(String upgradeName, RequirementBuilder requirementBuilder) {
        this.upgradeName = upgradeName;
        this.requirementBuilder = requirementBuilder;
    }

    @Override
    public void readRequirement(Team team, Player p) {

        this.setActivated(false);

        TeamUpgrade teamUpgrade = team.getPlayerShopUpgrades().get(p.getUniqueId()).get(this.upgradeName);

        if (teamUpgrade == null) {
            p.sendMessage("Couldn't find the upgrade with name " + this.upgradeName);
            return;
        }

        this.setActivated(teamUpgrade.getTierIndex() > 0);
    }

    @Override
    public RequirementBuilder getRequirementBuilder() {

        return this.requirementBuilder;
    }

    @Override
    public Requirement clone() {

        return new PUpgradeRequirement(this.upgradeName, this.requirementBuilder.clone());
    }

    public String getUpgradeName() {
        return this.upgradeName;
    }
}
