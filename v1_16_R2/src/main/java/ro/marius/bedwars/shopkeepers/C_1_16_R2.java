package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class C_1_16_R2 extends EntityCreeper implements IShopKeepers {

    public C_1_16_R2(EntityTypes<? extends EntityCreeper> type, World world) {
        super(EntityTypes.CREEPER, world);
        Set<?> goalB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("d", PathfinderGoalSelector.class, this.goalSelector);
        goalB.clear();
        Map<?, ?> goalC = (Map<?, ?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
        goalC.clear();
        Set<?> targetB = (LinkedHashSet<?>) ReflectionUtils.getPrivateField("d", PathfinderGoalSelector.class,
                this.targetSelector);
        targetB.clear();
        Map<?, ?> targetC = (Map<?, ?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class,
                this.targetSelector);
        targetC.clear();
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));

    }

    @Override
    public void spawn(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {

        if (damagesource == DamageSource.DROWN) {
            return false;
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public void setPowered(boolean powered) {

    }


    @Override
    public boolean isIgnited() {

        return false;
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {

    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {

    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    public void setOnFire(int i, boolean callEvent) {


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
    protected boolean playStepSound() {

        return false;
    }
}
