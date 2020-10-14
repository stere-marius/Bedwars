package ro.marius.bedwars.manager.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import ro.marius.bedwars.*;
import ro.marius.bedwars.hologram.*;
import ro.marius.bedwars.utils.ServerVersion;

public class VersionManager {

    private final String versionName;
    private NMSHologramWrapper hologram;
    private VersionWrapper versionWrapper;
    private ServerVersion serverVersion;

    public VersionManager() {
        this.versionName = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        this.setupVersionNMS();
        this.setupHologramNMS();
    }

    private void setupHologramNMS() {

        if (this.versionName == null) {
            return;
        }

        this.hologram = this.getNewHologramWrapper();
    }

    public NMSHologramWrapper getNewHologramWrapper() {

        NMSHologramWrapper hologramWrapper = null;

        switch (this.versionName) {
            case "v1_16_R2":
                hologramWrapper = new H_1_16_R2();
                break;
            case "v1_16_R1":
                hologramWrapper = new H_1_16_R1();
                break;
            case "v1_15_R1":
                hologramWrapper = new H_1_15_R1();
                break;
            case "v1_14_R1":
                hologramWrapper = new H_1_14_R1();
                break;
            case "v1_13_R2":
                hologramWrapper = new H_1_13_R2();
                break;
            case "v1_12_R1":
                hologramWrapper = new H_1_12_R1();
                break;
            case "v1_11_R1":
                hologramWrapper = new H_1_11_R1();
                break;
            case "v1_10_R1":
                hologramWrapper = new H_1_10_R1();
                break;
            case "v1_9_R2":
                hologramWrapper = new H_1_9_R2();
                break;
            case "v1_9_R1":
                hologramWrapper = new H_1_9_R1();
                break;
            case "v1_8_R3":
                hologramWrapper = new H_1_8_R3();
                break;
        }

        return hologramWrapper;
    }

    private void setupVersionNMS() {

        if (this.versionName == null) {
            return;
        }

        switch (this.versionName) {

            case "v1_16_R2":
                this.serverVersion = ServerVersion.v1_16_R2;
                this.versionWrapper = new v1_16_R2();
                break;
            case "v1_16_R1":
                this.serverVersion = ServerVersion.v1_16_R1;
                this.versionWrapper = new v1_16_R1();
                break;
            case "v1_15_R1":
                this.serverVersion = ServerVersion.v1_15_R1;
                this.versionWrapper = new v1_15_R1();
                break;
            case "v1_14_R1":
                this.serverVersion = ServerVersion.v1_14_R1;
                this.versionWrapper = new v1_14_R1();
                break;
            case "v1_13_R2":
                this.serverVersion = ServerVersion.v1_13_R2;
                this.versionWrapper = new v1_13_R2();
                break;
            case "v1_12_R1":
                this.serverVersion = ServerVersion.v1_12_R1;
                this.versionWrapper = new v1_12_R1();
                break;
            case "v1_11_R1":
                this.serverVersion = ServerVersion.v1_11_R1;
                this.versionWrapper = new v1_11_R1();
                break;
            case "v1_10_R1":
                this.serverVersion = ServerVersion.v1_10_R1;
                this.versionWrapper = new v1_10_R1();
                break;
            case "v1_9_R1":
                this.serverVersion = ServerVersion.v1_9_R1;
                this.versionWrapper = new v1_9_R1();
                break;
            case "v1_9_R2":
                this.serverVersion = ServerVersion.v1_9_R2;
                this.versionWrapper = new v1_9_R2();
                break;
            case "v1_8_R3":
                this.serverVersion = ServerVersion.v1_8_R3;
                this.versionWrapper = new v1_8_R3();
                break;
        }

    }

    public Entity getSpawnedEntity(String name, Location location) {

        if ("VILLAGER".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnVillager(location);
        }

        if ("BLAZE".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnBlaze(location);
        }

        if ("CREEPER".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnCreeper(location);
        }

        if ("SKELETON".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnSkeleton(location);
        }

        if ("ZOMBIE".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnZombie(location);
        }

        if ("PIGMAN".equalsIgnoreCase(name)) {
            return this.versionWrapper.spawnPigZombie(location);
        }

        return this.versionWrapper.spawnVillager(location);
    }

    @SuppressWarnings("deprecation")
    public Player getOwningPlayer(SkullMeta skullMeta) {

        return Bukkit.getPlayer(skullMeta.getOwner());

    }

    public ServerVersion getServerVersion() {

        return this.serverVersion;
    }

    public NMSHologramWrapper getHologramWrapper() {
        return this.hologram;
    }

    public VersionWrapper getVersionWrapper() {
        return this.versionWrapper;
    }


}
