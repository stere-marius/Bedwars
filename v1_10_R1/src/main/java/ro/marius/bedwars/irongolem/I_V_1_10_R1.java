package ro.marius.bedwars.irongolem;

import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.ICustomGolem;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.List;
import java.util.Map;

public class I_V_1_10_R1 extends EntityIronGolem implements ICustomGolem {

    static {
        addToMaps();
    }

    public I_V_1_10_R1(World world) {
        super(world);
        List<?> goalB = (List<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class, this.goalSelector);
        goalB.clear();
        List<?> goalC = (List<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
        goalC.clear();
        List<?> targetB = (List<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class, this.targetSelector);
        targetB.clear();
        List<?> targetC = (List<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.targetSelector);
        targetC.clear();
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.goalSelector.a(2,
                new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, 0, true, false, null));
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(10.0D);

    }

    @SuppressWarnings("unchecked")
    private static void addToMaps() {
        ((Map<String, Class<?>>) ReflectionUtils.getPrivateField("c", EntityTypes.class, null)).put("CustomGolem",
                I_V_1_10_R1.class);
        ((Map<Class<?>, String>) ReflectionUtils.getPrivateField("d", EntityTypes.class, null)).put(I_V_1_10_R1.class,
                "CustomGolem");
        ((Map<Class<?>, Integer>) ReflectionUtils.getPrivateField("f", EntityTypes.class, null))
                .put(I_V_1_10_R1.class, 99);
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        super.setCustomNameVisible(true);
    }

    @Override
    public void setCustomName(String s) {
        super.setCustomName(s);
    }

    @Override
    public void spawn(Location loc) {
        this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    protected void dropDeathLoot(boolean flag, int i) {
    }

}
