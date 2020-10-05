package ro.marius.bedwars.upgradeconfiguration;

import org.bukkit.entity.Player;
import ro.marius.bedwars.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamUpgrade {

    private List<UpgradeTier> upgradeTier;
    private boolean loseTier;
    private int currentTier = 0;
    private String romanTier = "0";

    public TeamUpgrade(List<UpgradeTier> upgradeTier) {
        this.upgradeTier = upgradeTier;
        this.upgradeTier.get(upgradeTier.size() - 1).setUnlocked(true);
    }

    @Override
    public TeamUpgrade clone() {

        List<UpgradeTier> upgradeTier = new ArrayList<>();

        for (UpgradeTier tier : this.upgradeTier) {
            upgradeTier.add(tier.clone());
        }

        TeamUpgrade teamUpgrade = new TeamUpgrade(upgradeTier);
        teamUpgrade.setCurrentTier(0);
        teamUpgrade.setRomanTier("I");
        teamUpgrade.setLoseTier(this.loseTier);

        return teamUpgrade;
    }

    public UpgradeTier getLastTier() {

        int currentTier = Math.max(this.currentTier - 1, 0);

        return this.upgradeTier.get(currentTier);
    }

    public UpgradeTier getCurrentTier() {

        return this.upgradeTier.get(this.currentTier /* >= upgradeTier.size() ? currentTier - 1 : currentTier */);
    }

    public void setCurrentTier(int currentTier) {
        this.currentTier = currentTier;
    }

    public UpgradeTier getPreviousTier() {
        return this.upgradeTier.get(Math.max(this.currentTier - 1, 0));
    }

    public int getTierIndex() {

        return this.currentTier;
    }

    public UpgradeTier getFirstTier() {

        return this.upgradeTier.get(0);
    }

    public void onPurchase(Player p, boolean isShop, boolean isPlayerUpgrade) {

        UpgradeTier upgradeTier = this.upgradeTier.get(this.currentTier);

        if (upgradeTier.onPurchase(p, isShop, isPlayerUpgrade)) {
            this.currentTier = ((this.currentTier + 1) >= this.upgradeTier.size()) ? (this.upgradeTier.size() - 1)
                    : (this.currentTier + 1);
            this.romanTier = StringUtils.toRoman(this.currentTier);
        }

    }

    public void onReset() {
        this.upgradeTier.forEach(UpgradeTier::reset);
        this.currentTier = 0;
        this.romanTier = "I";
    }

    public String getRomanTier() {

        return this.romanTier;
    }

    public void setRomanTier(String romanTier) {
        this.romanTier = romanTier;
    }

    public void decreaseTier() {

        this.getCurrentTier().setUnlocked(false);

        this.currentTier = Math.max(this.currentTier - 1, 0);

        this.upgradeTier.get(this.currentTier).setUnlocked(false);

        this.romanTier = StringUtils.toRoman(this.currentTier);
    }

    public void increaseTier() {
        this.getCurrentTier().setUnlocked(true);

        this.currentTier = ((this.currentTier + 1) >= this.upgradeTier.size()) ? (this.upgradeTier.size() - 1) : (this.currentTier + 1);

        this.romanTier = StringUtils.toRoman(this.currentTier);

    }

    public void setLoseTier(boolean loseTier) {
        this.loseTier = loseTier;
    }
}
