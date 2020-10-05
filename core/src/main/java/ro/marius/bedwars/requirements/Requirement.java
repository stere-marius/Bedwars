package ro.marius.bedwars.requirements;

import org.bukkit.entity.Player;
import ro.marius.bedwars.team.Team;

public abstract class Requirement {

    private String description;
    private boolean activated;

//	public abstract void readRequirement(YamlConfiguration config)

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public abstract Requirement clone();

    public abstract void readRequirement(Team team, Player p);

    public abstract RequirementBuilder getRequirementBuilder();

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
