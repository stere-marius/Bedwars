package ro.marius.bedwars.game.mechanics;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import ro.marius.bedwars.team.Team;

public class TeamSetup {

    private String teamName;
    private String teamColor;
    private BlockFace bedFace = BlockFace.EAST;
    private Location ironGenerator;
    private Location goldGenerator;
    private Location emeraldGenerator;
    private Location spawnLocation;
    private Location bedLocation;
    private Location shop;
    private Location upgrade;

    public TeamSetup(String teamName, String teamColor) {
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    public TeamSetup(Team team) {
        this.teamName = team.getName();
        this.teamColor = team.getColorName();
        this.bedFace = team.getBedFace();
        this.ironGenerator = team.getIronGenerator().getLocation();
        this.goldGenerator = team.getGoldGenerator().getLocation();
        this.emeraldGenerator = team.getEmeraldGenerator().getLocation();
        this.spawnLocation = team.getSpawnLocation().getLocation();
        this.bedLocation = team.getBedLocation().getLocation();
        this.shop = team.getShopLocation().getLocation();
        this.upgrade = team.getUpgradeLocation().getLocation();
    }

    public boolean isReady() {

        return (this.ironGenerator != null) && (this.goldGenerator != null) && (this.emeraldGenerator != null) && (this.spawnLocation != null)
                && (this.bedLocation != null) && (this.shop != null) && (this.upgrade != null) && (this.bedFace != null);
    }

    public String getTeamName() {
        return this.teamName;
    }

    public String getTeamColor() {
        return this.teamColor;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    public BlockFace getBedFace() {
        return this.bedFace;
    }

    public void setBedFace(BlockFace bedFace) {
        this.bedFace = bedFace;
    }

    public Location getIronGenerator() {
        return this.ironGenerator;
    }

    public void setIronGenerator(Location ironGenerator) {
        this.ironGenerator = ironGenerator;
    }

    public Location getGoldGenerator() {
        return this.goldGenerator;
    }

    public void setGoldGenerator(Location goldGenerator) {
        this.goldGenerator = goldGenerator;
    }

    public Location getEmeraldGenerator() {
        return this.emeraldGenerator;
    }

    public void setEmeraldGenerator(Location emeraldGenerator) {
        this.emeraldGenerator = emeraldGenerator;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getBedLocation() {
        return this.bedLocation;
    }

    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    public Location getShop() {
        return this.shop;
    }

    public void setShop(Location shop) {
        this.shop = shop;
    }

    public Location getUpgrade() {
        return this.upgrade;
    }

    public void setUpgrade(Location upgrade) {
        this.upgrade = upgrade;
    }
}
