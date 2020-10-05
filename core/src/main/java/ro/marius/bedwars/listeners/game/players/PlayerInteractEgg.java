package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.CuboidSelection;

public class PlayerInteractEgg implements Listener {

    private static final MetadataValue EGG_METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(), "MatchEgg");

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent e) {

        if (e.getEgg().hasMetadata("MatchEgg")) {
            e.setHatching(false);
        }

    }

    @EventHandler
    public void onEggLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Egg)) {
            return;
        }
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity().getShooter();

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        if (match.getMatchState() != MatchState.IN_GAME) {
            return;
        }

        if (match.getSpectators().contains(p)) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        Egg egg = (Egg) e.getEntity();
        egg.setMetadata("MatchEgg", EGG_METADATA);
        egg.teleport(p.getTargetBlock(null, 2).getLocation());
        egg.setBounce(false);
        egg.setTicksLived(20);

        Material material = team.getTeamColor().getBuildMaterial().getType();
        World world = egg.getWorld();
        Game game = match.getGame();
        CuboidSelection gameCuboid = game.getGameCuboid();

        new BukkitRunnable() {

            @Override
            public void run() {

                if (egg.isDead()) {
                    this.cancel();
                    return;
                }

                Location location = egg.getLocation();

                for (int x = location.getBlockX() - 1; x < (location.getBlockX() + 1); x++) {
                    for (int y = location.getBlockY(); y < (location.getBlockY() + 1); y++) {
                        for (int z = location.getBlockZ() - 1; z < (location.getBlockZ() + 1); z++) {

                            Block b = world.getBlockAt(x, y - 2, z);

                            if (b.getType() != Material.AIR) {
                                continue;
                            }

                            Location loc = b.getLocation();

                            if (!gameCuboid.isInsideCuboidSelection(loc)) {
                                continue;
                            }
                            if(game.isNearAirGenerators(loc)){
                                continue;
                            }
                            if(match.isDenyPlacingBlock(loc)){
                                continue;
                            }

                            b.setType(material);
                            match.getPlacedBlocks().add(b);
                        }
                    }
                }

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 1);

    }

}
