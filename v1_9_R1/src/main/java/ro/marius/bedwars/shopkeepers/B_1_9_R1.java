package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.LinkedHashSet;
import java.util.Map;

public class B_1_9_R1 extends EntityBlaze implements IShopKeepers {

    static {
        addToMaps();
    }

    public B_1_9_R1(World world) {
        super(world);
        LinkedHashSet<?> goalB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class, this.goalSelector);
        goalB.clear();
        LinkedHashSet<?> goalC = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
        goalC.clear();
        LinkedHashSet<?> targetB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class,
                this.targetSelector);
        targetB.clear();
        LinkedHashSet<?> targetC = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class,
                this.targetSelector);
        targetC.clear();
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 20.0F));

    }

    @SuppressWarnings("unchecked")
    private static void addToMaps() {
        ((Map<String, Class<?>>) ReflectionUtils.getPrivateField("c", EntityTypes.class, null)).put("CustomBlaze",
                B_1_9_R1.class);
        ((Map<Class<?>, String>) ReflectionUtils.getPrivateField("d", EntityTypes.class, null)).put(B_1_9_R1.class,
                "CustomBlaze");
        ((Map<Class<?>, Integer>) ReflectionUtils.getPrivateField("f", EntityTypes.class, null)).put(B_1_9_R1.class,
                61);
    }

    @Override
    public void spawn(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {

        if (damagesource == DamageSource.DROWN) {
            return false;
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        return;
    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    public void move(double d0, double d1, double d2) {
        return;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

}
