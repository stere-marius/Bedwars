package ro.marius.bedwars.team.upgrade;

import java.util.List;

public class PermanentUpgrade {

    private String upgradeClassName;
    private List<IUpgrade> upgrades;

    public PermanentUpgrade(String upgradeClassName, List<IUpgrade> upgrades) {
        this.upgradeClassName = upgradeClassName;
        this.upgrades = upgrades;
    }


    public String getUpgradeClassName() {
        return this.upgradeClassName;
    }

    public void setUpgradeClassName(String upgradeClassName) {
        this.upgradeClassName = upgradeClassName;
    }

    public List<IUpgrade> getUpgrades() {
        return this.upgrades;
    }

    public void setUpgrades(List<IUpgrade> upgrades) {
        this.upgrades = upgrades;
    }
}
