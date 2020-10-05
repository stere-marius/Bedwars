package ro.marius.bedwars.team;

import org.bukkit.Material;
import org.bukkit.block.Block;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;

import java.util.HashSet;
import java.util.Set;

public class TeamBed {

    private Set<Block> bedBlocks = new HashSet<>();
    private final Team team;

    public TeamBed(Team team) {
        this.team = team;
    }

    public void placeBed() {
//        Bukkit.getLogger().info("Team " + team);
//        Bukkit.getLogger().info("Bed face == null " + (team.getBedFace() == null));
//        Bukkit.getLogger().info("team.getBedLocation().getLocation() == null " + (team.getBedLocation().getLocation() == null));
//        Bukkit.getLogger().info("team.getTeamColor().getBedBlock() == null " + (team.getTeamColor().getBedBlock() == null));
        bedBlocks = ManagerHandler.getVersionManager().getVersionWrapper().
                getPlacedBedBlocks(team.getBedFace(), team.getBedLocation().getLocation(), team.getTeamColor().getBedBlock());
        bedBlocks.forEach(placedBlock -> placedBlock.setMetadata("TeamBed", team.getTeamMetadata()));
    }

    public void destroyBed() {

        for (Block block : bedBlocks) {
            block.removeMetadata("TeamBed", BedWarsPlugin.getInstance());
            block.setType(Material.AIR, false);
        }

        bedBlocks.clear();
    }

    public void removeMetadata() {
        bedBlocks.forEach(block -> block.removeMetadata("TeamBed", BedWarsPlugin.getInstance()));
    }

    public Set<Block> getBedBlocks() {
        return bedBlocks;
    }
}
