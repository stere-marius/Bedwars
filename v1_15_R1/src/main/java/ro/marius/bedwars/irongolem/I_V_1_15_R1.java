package ro.marius.bedwars.irongolem;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.ICustomGolem;
import ro.marius.bedwars.utils.ReflectionUtils;
import ro.marius.bedwars.utils.Utils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class I_V_1_15_R1 extends EntityIronGolem implements ICustomGolem {

    public I_V_1_15_R1(EntityTypes<? extends EntityIronGolem> entityTypes, World world) {
        super(EntityTypes.IRON_GOLEM, world);
    }

    @Override
    protected void initPathfinder() {
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
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, false));
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(20.0D);
    }

    @Override
    public void spawn(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public void setOnFire(int i, boolean callEvent) {

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {

        return null;
    }

    @Override
    protected SoundEffect getSoundDeath() {

        return null;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {

    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        super.setCustomNameVisible(flag);
    }

    @Override
    public void setCustomName(String s) {
        super.setCustomName(new ChatComponentText(Utils.translate(s)));
    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {

    }

}
