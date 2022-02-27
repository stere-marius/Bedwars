package ro.marius.bedwars.game.arenareset;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.type.FAWEManager;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;

public class FAWEReset implements ArenaReset {

    // TODO

    @Override
    public void resetArena(Game game) {
        AMatch match = game.getMatch();
        World world = Bukkit.getServer().getWorld(game.getName());
        FAWEManager.pasteSchematic(world, game);

        match.getMatchEntity().forEach(Entity::remove);

        new BukkitRunnable() {

            @Override
            public void run() {
                match.setMatchState(MatchState.WAITING);
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 30);
    }
}
