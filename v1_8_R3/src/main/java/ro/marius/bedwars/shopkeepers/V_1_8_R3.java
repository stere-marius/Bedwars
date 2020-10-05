package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.List;
import java.util.Map;

public class V_1_8_R3 extends EntityVillager implements IShopKeepers {

    static {
        addToMaps();
    }

    public V_1_8_R3(World world) {
        super(world);
        List<?> goalB = (List<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class, this.goalSelector);
        goalB.clear();
        List<?> goalC = (List<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.goalSelector);
        goalC.clear();
        List<?> targetB = (List<?>) ReflectionUtils.getPrivateField("b", PathfinderGoalSelector.class, this.targetSelector);
        targetB.clear();
        List<?> targetC = (List<?>) ReflectionUtils.getPrivateField("c", PathfinderGoalSelector.class, this.targetSelector);
        targetC.clear();
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 20.0F));

    }

    @SuppressWarnings("unchecked")
    private static void addToMaps() {
        ((Map<String, Class<?>>) ReflectionUtils.getPrivateField("c", EntityTypes.class, null)).put("CustomVillager",
                V_1_8_R3.class);
        ((Map<Class<?>, String>) ReflectionUtils.getPrivateField("d", EntityTypes.class, null)).put(V_1_8_R3.class,
                "CustomVillager");
        ((Map<Class<?>, Integer>) ReflectionUtils.getPrivateField("f", EntityTypes.class, null)).put(V_1_8_R3.class,
                120);
    }

    @Override
    public void spawn(Location location) {
        this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftWorld) location.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
    }

    @Override
    public Entity getCustomEntity() {
        return this.getBukkitEntity();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {

        if (damagesource == DamageSource.DROWN) {
            return false;
        }

        return super.damageEntity(damagesource, f);
    }

    @Override
    public void move(double d0, double d1, double d2) {
    }

    @Override
    public void makeSound(String s, float f, float f1) {
    }


}
