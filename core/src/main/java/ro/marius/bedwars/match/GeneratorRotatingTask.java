package ro.marius.bedwars.match;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;

public class GeneratorRotatingTask extends BukkitRunnable {

    private double y = 0;
    private boolean up = true;
    private int secTeleport = 0;
    private final AMatch match;

    public GeneratorRotatingTask(AMatch match){
        this.match = match;
    }

    @Override
    public void run() {
        this.secTeleport += 1;
        this.y += 0.03;

        if (this.secTeleport == 7) {
            this.up = false;
            this.y = 0;
            return;
        }

        if (this.secTeleport == 14) {
            this.up = true;
            this.y = 0;
            this.secTeleport = 0;
            return;
        }

        int addedYaw = ((this.up ? (40 + this.secTeleport) : (-40 - this.secTeleport)) + 5);

        for (DiamondGenerator gen : match.getDiamondGenerators()) {
            Location location = this.up ? gen.getSupportArmorStand().getLocation().subtract(0, this.y, 0)
                    : gen.getSupportArmorStand().getLocation().add(0, this.y, 0);
            location.setYaw(location.getYaw() + addedYaw);
            gen.getSupportArmorStand().teleport(location);
        }

        for (EmeraldGenerator gen : match.getEmeraldGenerators()) {
            Location location = this.up ? gen.getSupportArmorStand().getLocation().subtract(0, this.y, 0)
                    : gen.getSupportArmorStand().getLocation().add(0, this.y, 0);
            location.setYaw(location.getYaw() + addedYaw);
            gen.getSupportArmorStand().teleport(location);
        }
    }
}
