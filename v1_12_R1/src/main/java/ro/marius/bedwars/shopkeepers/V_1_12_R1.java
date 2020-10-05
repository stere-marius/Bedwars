package ro.marius.bedwars.shopkeepers;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import ro.marius.bedwars.NMSUtils;
import ro.marius.bedwars.NMSUtils.Type;
import ro.marius.bedwars.IShopKeepers;
import ro.marius.bedwars.utils.ReflectionUtils;

import java.util.LinkedHashSet;

public class V_1_12_R1 extends EntityVillager implements IShopKeepers {

    static {
        NMSUtils.registerEntity("CustomVillager", Type.VILLAGER, V_1_12_R1.class, false);
        // addToMaps(V_1_12_R1.class, "CustomVillager", 120);
    }

    public V_1_12_R1(World world) {
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
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));

    }

    @Override
    public void spawn(Location location) {
        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.getBukkitEntity().teleport(location);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    protected SoundEffect F() {
        return null;

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


    // @SuppressWarnings("unchecked")
    // private static void addToMaps(Class<?> clazz, String name, int id) {
    // ((Map<String, Class<?>>) ReflectionUtils.getPrivateField("c",
    // net.minecraft.server.v1_12_R1.EntityTypes.class, null)).put(name,
    // clazz);
    // ((Map<Class<?>, String>) ReflectionUtils.getPrivateField("d",
    // net.minecraft.server.v1_12_R1.EntityTypes.class, null)).put(clazz,
    // name);
    // ((Map<Class<?>, Integer>) ReflectionUtils.getPrivateField("f",
    // net.minecraft.server.v1_12_R1.EntityTypes.class, null))
    // .put(clazz, Integer.valueOf(id));
    // }


}
