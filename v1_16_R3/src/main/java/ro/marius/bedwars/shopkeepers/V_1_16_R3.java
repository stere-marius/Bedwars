package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class V_1_16_R3 extends EntityVillager implements IShopKeepers {

    public V_1_16_R3(EntityTypes<? extends EntityVillager> entityTypes, World world) {
        super(EntityTypes.VILLAGER, world);

        Set<?> goalB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("d", PathfinderGoalSelector.class,
                this.goalSelector);
        goalB.clear();
        Map<?, ?> goalC = (Map<?, ?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
        goalC.clear();
        Set<?> targetB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("d", PathfinderGoalSelector.class,
                this.targetSelector);
        targetB.clear();
        Map<?, ?> targetC = (Map<?, ?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class,
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
    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {

    }

    @Override
    public void setOnFire(int i, boolean callEvent) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {

        if (damagesource == DamageSource.DROWN) {
            return false;
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return null;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource var0) {
        return null;
    }

    @Override
    protected void dropDeathLoot(DamageSource var0, int var1, boolean var2) {

    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {

    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

}
