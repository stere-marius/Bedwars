package ro.marius.bedwars.team;

import org.bukkit.Material;
import ro.marius.bedwars.menu.action.RewardItem;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;

public class PermanentItemList extends ArrayList<RewardItem> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(RewardItem e) {

        Material material = e.getReward().getItemStack().getType();
        String itemName = material.name();

        if (itemName.endsWith("_HELMET")) {

            int currentHelmetIndex = this.getCurrentHelmet();

            if (currentHelmetIndex == -1) {
                return super.add(e);
            }

            RewardItem currentHelmet = this.get(currentHelmetIndex);
            Material currentMaterial = currentHelmet.getReward().getItemStack().getType();

            if (currentMaterial == material) {
                return false;
            }

            int currentID = Utils.getArmorID(currentMaterial.name());
            int wantedID = Utils.getArmorID(itemName);

            if (currentID >= wantedID) {
                return false;
            }

            this.set(currentHelmetIndex, e);

            return true;
        }

        if (itemName.endsWith("_CHESTPLATE")) {

            int currentChestplateIndex = this.getCurrentChestplate();

            if (currentChestplateIndex == -1) {
                return super.add(e);
            }

            RewardItem currentChestplate = this.get(currentChestplateIndex);
            Material currentMaterial = currentChestplate.getReward().getItemStack().getType();

            if (currentMaterial == material) {
                return false;
            }

            int currentID = Utils.getArmorID(currentMaterial.name());
            int wantedID = Utils.getArmorID(itemName);

            if (currentID >= wantedID) {
                return false;
            }

            this.set(currentChestplateIndex, e);

            return true;
        }

        if (itemName.endsWith("_LEGGINGS")) {

            int currentLeggingsIndex = this.getCurrentLeggings();

            if (currentLeggingsIndex == -1) {
                return super.add(e);
            }

            RewardItem currentLeggings = this.get(currentLeggingsIndex);
            Material currentMaterial = currentLeggings.getReward().getItemStack().getType();

            if (currentMaterial == material) {
                return false;
            }

            int currentID = Utils.getArmorID(currentMaterial.name());
            int wantedID = Utils.getArmorID(itemName);

            if (currentID >= wantedID) {
                return false;
            }

            this.set(currentLeggingsIndex, e);

            return true;
        }

        if (itemName.endsWith("_BOOTS")) {

            int currentBootsIndex = this.getCurrentBoots();

            if (currentBootsIndex == -1) {
                return super.add(e);
            }

            RewardItem currentBoots = this.get(currentBootsIndex);
            Material currentMaterial = currentBoots.getReward().getItemStack().getType();

            if (currentMaterial == material) {
                return false;
            }

            int currentID = Utils.getArmorID(currentMaterial.name());
            int wantedID = Utils.getArmorID(itemName);

            if (currentID >= wantedID) {
                return false;
            }

            this.set(currentBootsIndex, e);

            return true;
        }

        return super.add(e);
    }

    private int getCurrentHelmet() {

        for (int i = 0; i < this.size(); i++) {

            RewardItem rewardItem = this.get(i);
            String itemName = rewardItem.getReward().getItemStack().getType().name();

            if (!itemName.endsWith("_HELMET")) {
                continue;
            }

            return i;
        }

        return -1;

    }

    private int getCurrentChestplate() {

        for (int i = 0; i < this.size(); i++) {

            RewardItem rewardItem = this.get(i);
            String itemName = rewardItem.getReward().getItemStack().getType().name();

            if (!itemName.endsWith("_CHESTPLATE")) {
                continue;
            }

            return i;
        }

        return -1;

    }

    private int getCurrentLeggings() {

        for (int i = 0; i < this.size(); i++) {

            RewardItem rewardItem = this.get(i);
            String itemName = rewardItem.getReward().getItemStack().getType().name();

            if (!itemName.endsWith("_LEGGINGS")) {
                continue;
            }

            return i;
        }

        return -1;

    }

    private int getCurrentBoots() {

        for (int i = 0; i < this.size(); i++) {

            RewardItem rewardItem = this.get(i);
            String itemName = rewardItem.getReward().getItemStack().getType().name();

            if (!itemName.endsWith("_BOOTS")) {
                continue;
            }

            return i;
        }

        return -1;

    }

}
