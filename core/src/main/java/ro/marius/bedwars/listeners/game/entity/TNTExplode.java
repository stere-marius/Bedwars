package ro.marius.bedwars.listeners.game.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.match.AMatch;

import java.util.*;
import java.util.function.Predicate;

public class TNTExplode implements Listener {


    @EventHandler
    public void onFireballExplode(EntityExplodeEvent e) {

        Entity entity = e.getEntity();

        if (entity.getType() != EntityType.PRIMED_TNT) {
            return;
        }
        if (!entity.hasMetadata("Match")) {
            return;
        }

        AMatch match = (AMatch) e.getEntity().getMetadata("Match").get(0).value();
        List<Block> explodedBlocks = e.blockList();

        if (explodedBlocks.isEmpty()) {
            return;
        }

        Set<Block> placedTeamBeds = new HashSet<>();
        match.getPlacedBeds().values().forEach(teamBed -> placedTeamBeds.addAll(teamBed.getBedBlocks()));

        explodedBlocks.removeIf(b ->
                (!match.getPlacedBlocks().contains(b))
                        || placedTeamBeds.contains(b)
                        || b.getType().name().endsWith("GLASS"));
//        explodedBlocks.removeIf(b -> b.getType().name().endsWith("GLASS"));

//        Iterator<Block> it = explodedBlocks.iterator();

        Location location = e.getLocation().clone();
        location.setY(location.getBlockY() + 0.5);
        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);


//        while (it.hasNext()) {
//            Block block = it.next();
//            Location blockLocation = block.getLocation().clone().add(0.5, 0.5, 0.5);
//            RayTraceResult rayTraceResult = rayTraceResult(location, calculate(location, blockLocation),
//                    (int) (location.distance(blockLocation) + 1));
//
//            if (rayTraceResult != null && rayTraceResult.getHitBlock() != null && rayTraceResult.getHitBlock().getType().name().endsWith("GLASS")) {
//                it.remove();
////                continue;
//            }
//
////            startRunnable(location, blockLocation);
//        }


    }

//    private void startRunnable(Location start, Location end) {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                spawnParticleAlongLine(start, end, Particle.FLAME, 10, 2, 0.1, 0.1, 0.1, 0.0, null, true, null);
//            }
//        }.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);
//    }
//
//    private void spawnParticleAlongLine(final Location start, final Location end, final Particle particle, final int pointsPerLine, final int particleCount, final double offsetX, final double offsetY, final double offsetZ, final double extra, final Double data, final boolean forceDisplay, final Predicate<Location> operationPerPoint) {
//        final double d = start.distance(end) / pointsPerLine;
//        for (int i = 0; i < pointsPerLine; ++i) {
//            final Location l = start.clone();
//            final Vector direction = end.toVector().subtract(start.toVector()).normalize();
//            final Vector v = direction.multiply(i * d);
//            l.add(v.getX(), v.getY(), v.getZ());
//            if (operationPerPoint == null) {
//                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, (Object) data, forceDisplay);
//            } else if (operationPerPoint.test(l)) {
//                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, (Object) data, forceDisplay);
//            }
//        }
//    }
//
//    private Vector calculate(Location loc1, Location loc2) {
//        double vecX = (loc1.getX() - loc2.getX()) * -1.0D;
//        double vecY = (loc1.getY() - loc2.getY()) * -1.0D;
//        double vecZ = (loc1.getZ() - loc2.getZ()) * -1.0D;
//        return new Vector(vecX, vecY, vecZ).normalize();
//    }

//    private RayTraceResult rayTraceResult(Location location, Vector direction, int range) {
//        if (range < 0)
//            return null;
//        RayTraceResult result = Objects.requireNonNull(location.getWorld()).rayTraceBlocks(location, direction, range);
//        if ((result != null && result.getHitBlock() != null && !result.getHitBlock().getType().name().endsWith("GLASS")))
//            return rayTraceResult(location.clone().add(direction), direction, range - 1);
//        return result;
//    }


}
