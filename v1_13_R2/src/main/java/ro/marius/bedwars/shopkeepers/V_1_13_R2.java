package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.LinkedHashSet;

public class V_1_13_R2 extends EntityVillager implements IShopKeepers {

    public V_1_13_R2(World world) {
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
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 20.0F));

    }

    @Override
    public void spawn(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    protected void dropDeathLoot(boolean flag, int i) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {

        if (damagesource == DamageSource.DROWN) {
            return false;
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    protected void dropEquipment(boolean flag, int i) {

    }

    @Override
    public void setOnFire(int i) {

    }

    @Override
    protected SoundEffect D() {

        return null;
    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }


}
