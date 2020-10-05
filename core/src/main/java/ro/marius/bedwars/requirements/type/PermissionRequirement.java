package ro.marius.bedwars.requirements.type;

import org.bukkit.entity.Player;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.requirements.RequirementBuilder;
import ro.marius.bedwars.team.Team;

public class PermissionRequirement extends Requirement {

    //	String permission
    private String permission;
    private RequirementBuilder reqBuilder;

    public PermissionRequirement(String permission, RequirementBuilder reqBuilder) {

        this.permission = permission;
        this.reqBuilder = reqBuilder;

    }

    @Override
    public void readRequirement(Team team, Player p) {

        this.setActivated(p.hasPermission(this.permission));

    }

    @Override
    public RequirementBuilder getRequirementBuilder() {

        return this.reqBuilder;
    }

    @Override
    public Requirement clone() {

        return new PermissionRequirement(this.permission, this.reqBuilder.clone());
    }

}
