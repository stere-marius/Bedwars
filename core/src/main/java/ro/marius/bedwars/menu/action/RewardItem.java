package ro.marius.bedwars.menu.action;

import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class RewardItem {

    private final ItemBuilder reward;
    private boolean permanent = false;
    private List<Requirement> requirement = new ArrayList<>();

    public RewardItem(ItemBuilder builder) {
        this.reward = builder;
    }

    public RewardItem(ItemBuilder builder, List<Requirement> requirement) {

        this.reward = builder;
        this.requirement = requirement;

    }

    public RewardItem(ItemBuilder builder, List<Requirement> requirement, boolean permanent) {

        this.reward = builder;
        this.requirement = requirement;
        this.permanent = permanent;

    }

    public RewardItem(ItemBuilder builder, boolean permanent) {

        this.reward = builder;
        this.permanent = permanent;

    }

    public ItemBuilder getReward() {
        return this.reward;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public List<Requirement> getRequirement() {
        return this.requirement;
    }
}
