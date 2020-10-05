package ro.marius.bedwars.upgradeconfiguration;

import ro.marius.bedwars.upgradeconfiguration.upgradeinventory.UpgradeInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UpgradePath {

    private Map<String, TeamUpgrade> upgrades;
    private int ironTime, goldTime;
    private int ironAmount, goldAmount;
    private Map<String, UpgradeInventory> upgradeInventoryMap;

    public UpgradePath(Map<String, TeamUpgrade> upgrades, Map<String, UpgradeInventory> upgradeInventoryMap) {
        this.upgrades = upgrades;
        this.upgradeInventoryMap = upgradeInventoryMap;
    }

    public Map<String, TeamUpgrade> getUpgrades() {

        Map<String, TeamUpgrade> upgrades = new HashMap<>();

        for (Entry<String, TeamUpgrade> entry : this.upgrades.entrySet()) {
            upgrades.put(entry.getKey(), entry.getValue().clone());
        }

        return upgrades;
    }

    public void setUpgrades(Map<String, TeamUpgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public int getIronTime() {
        return this.ironTime;
    }

    public void setIronTime(int ironTime) {
        this.ironTime = ironTime;
    }

    public int getGoldTime() {
        return this.goldTime;
    }

    public void setGoldTime(int goldTime) {
        this.goldTime = goldTime;
    }

    public int getIronAmount() {
        return this.ironAmount;
    }

    public void setIronAmount(int ironAmount) {
        this.ironAmount = ironAmount;
    }

    public int getGoldAmount() {
        return this.goldAmount;
    }

    public void setGoldAmount(int goldAmount) {
        this.goldAmount = goldAmount;
    }

    public Map<String, UpgradeInventory> getUpgradeInventoryMap() {
        return this.upgradeInventoryMap;
    }

    public void setUpgradeInventoryMap(Map<String, UpgradeInventory> upgradeInventoryMap) {
        this.upgradeInventoryMap = upgradeInventoryMap;
    }
}
