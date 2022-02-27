package ro.marius.bedwars.game.arenareset;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.WorldCallback;

public class WorldReset implements ArenaReset {


    @Override
    public void resetArena(Game game) {
        World world = Bukkit.getServer().getWorld(game.getName());
        AMatch match = game.getMatch();

        ManagerHandler.getWorldManager().regenerateWorld(world, new WorldCallback() {

            @Override
            public void onComplete(World result, String[] message) {

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        match.getGame().getSpectateLocation().reloadLocation();
                        match.getGame().getWaitingLocation().reloadLocation();
                        match.getGame().getDiamondGenerator().forEach(GameLocation::reloadLocation);
                        match.getGame().getEmeraldGenerator().forEach(GameLocation::reloadLocation);

                        for (Team team : match.getTeams()) {
                            team.getSpawnLocation().reloadLocation();
                            team.getIronFloorGenerator().getLocation().reloadLocation();
                            team.getGoldFloorGenerator().getLocation().reloadLocation();
                            team.getGoldGenerator().reloadLocation();
                            team.getIronGenerator().reloadLocation();
                            team.getEmeraldGenerator().reloadLocation();
                            team.getBedLocation().reloadLocation();
                            team.getShopLocation().reloadLocation();
                            team.getUpgradeLocation().reloadLocation();
                        }

                        match.setMatchState(MatchState.WAITING);

                    }
                }.runTaskLater(BedWarsPlugin.getInstance(), 1);

            }

            @Override
            public void onError(String[] message) {
                Bukkit.getConsoleSender().sendMessage(message);

            }
        });

    }
}
