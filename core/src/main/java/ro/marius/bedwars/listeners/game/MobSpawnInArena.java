package ro.marius.bedwars.listeners.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;

import java.util.List;

public class MobSpawnInArena implements Listener {


    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.isCancelled()) {
            return;
        }

        SpawnReason reason = e.getSpawnReason();

        if ((reason == SpawnReason.CUSTOM) || (reason == SpawnReason.SPAWNER_EGG) || (reason == SpawnReason.EGG)) {
            return;
        }
        if (ManagerHandler.getGameManager() == null) {
            return;
        }

        List<Game> games = ManagerHandler.getGameManager().getGames();

        if (games.isEmpty()) {
            return;
        }

        Entity entity = e.getEntity();

        if (entity == null) {
            return;
        }

        Location loc = entity.getLocation();
        String wName = loc.getWorld().getName();

        for (Game game : games) {

            World world = Bukkit.getWorld(game.getName());

            if (world == null) {
                continue;
            }
            if (!world.getName().equals(wName)) {
                continue;
            }

            e.setCancelled(true);
            break;
        }

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {

        Entity entity = e.getEntity();
        World world = entity.getWorld();
        Game game = ManagerHandler.getGameManager().getGame(world.getName());

        if (game == null)
            return;
        if (entity.getType() == EntityType.FIREBALL || entity.getType() == EntityType.PRIMED_TNT)
            return;
        if (!game.getGameCuboid().isInsideCuboidSelection(entity.getLocation()))
            return;

        e.setCancelled(true);
    }

}
