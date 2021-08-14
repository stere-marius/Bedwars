package ro.marius.bedwars;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.*;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.irongolem.I_V_1_14_R1;
import ro.marius.bedwars.shopkeepers.*;
import ro.marius.bedwars.utils.ReflectionUtils;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public class v1_14_R1 implements VersionWrapper {

    private EntityTypes<EntityBlaze> blaze;
    private EntityTypes<EntityCreeper> creeper;
    private EntityTypes<EntitySkeleton> skeleton;
    private EntityTypes<EntityVillager> villager;
    private EntityTypes<EntityZombie> zombie;
    private EntityTypes<EntityPigZombie> pigman;
    private EntityTypes<EntityGolem> golem;

    public v1_14_R1() {
        this.registerEntities();
    }

    @Override
    public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle,
                          boolean send, boolean sendAgainTitle) {

        if (!send) {
            return;
        }

        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

        if (sendAgainTitle) {
            PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES,
                    null, fadeIn, stay, fadeOut);

            connection.sendPacket(packetPlayOutTimes);
        }

        if (subtitle != null) {

            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer
                    .a("{\"text\": \"" + Utils.translate(subtitle) + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(
                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }
        if (title != null) {

            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer
                    .a("{\"text\": \"" + Utils.translate(title) + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE,
                    titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }

    }

    @Override
    public BlockFace getBedFace(Location location) {

        BlockData blockData = location.getBlock().getState().getBlockData();

        if (blockData instanceof Bed) {

            Bed bed = (Bed) blockData;

            return bed.getFacing();

        }

        return null;
    }

    @Override
    public Location getBedHead(Location location) {

        Block block = location.getBlock();
        BlockData blockData = block.getState().getBlockData();

        if (blockData instanceof Bed) {

            Bed bed = (Bed) blockData;

            if (bed.getPart() == Part.FOOT) {
                return block.getRelative(bed.getFacing()).getLocation();
            }

            if (bed.getPart() == Part.HEAD) {
                return location;
            }

        }

        return null;
    }


    @Override
    public Set<Block> getPlacedBedBlocks(BlockFace bedFace, Location loc, Material bedMaterial) {

        Block b = loc.getBlock();

        if (b.getType().name().contains("BED")) {
            b.setType(Material.AIR, false);
        }

        b.setType(bedMaterial, false);
        BlockState bs = b.getState();

        if (bs.getBlockData() instanceof Bed) {
            BlockData data = bs.getBlockData();
            Bed bed = (Bed) data;
            bed.setFacing(bedFace);
            bed.setPart(Part.HEAD);
            b.setBlockData(bed, true);
        }

        Block o = b.getRelative(bedFace.getOppositeFace());

        if (o.getType().name().contains("BED")) {
            o.setType(Material.AIR, false);
        }

        o.setType(bedMaterial, false);
        BlockState os = o.getState();

        if (os.getBlockData() instanceof Bed) {
            BlockData data = os.getBlockData();
            Bed bed = (Bed) data;
            bed.setFacing(bedFace);
            bed.setPart(Part.FOOT);
            o.setBlockData(bed, true);
        }

        Set<Block> bedBlocks = new HashSet<>();
        bedBlocks.add(o);
        bedBlocks.add(b);

        return bedBlocks;
    }

    public void registerEntities() {
        this.blaze = this.register(0, "bedwars_blaze", "blaze", B_1_14_R1::new, EnumCreatureType.MONSTER);
        this.creeper = this.register(1, "bedwars_creeper", "creeper", C_1_14_R1::new, EnumCreatureType.MONSTER);
        this.skeleton = this.register(2, "bedwars_skeleton", "skeleton", S_1_14_R1::new, EnumCreatureType.MONSTER);
        this.villager = this.register(3, "bedwars_villager", "villager", V_1_14_R1::new, EnumCreatureType.MISC);
        this.zombie = this.register(4, "bedwars_zombie", "zombie", Z_1_14_R1::new, EnumCreatureType.MONSTER);
        this.pigman = this.register(5, "bedwars_zombie_pigman", "pigman", ZP_1_14_R1::new, EnumCreatureType.MONSTER);
        this.golem = this.register(6, "bedwars_iron_golem", "iron_golem", I_V_1_14_R1::new, EnumCreatureType.MISC);
    }

    @SuppressWarnings("rawtypes")
    public <T extends Entity> EntityTypes<T> register(int id, String name, String superTypeName, EntityTypes.b producer,
                                                      EnumCreatureType type) {
        @SuppressWarnings("unchecked")
        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY).types();
        String keyName = "minecraft:" + name;
        dataTypes.put(keyName, dataTypes.get("minecraft:" + superTypeName));

        EntityTypes.a<T> a = EntityTypes.a.a(producer, type);
        return IRegistry.a(IRegistry.ENTITY_TYPE, id, keyName, a.a(name));
    }

    @Override
    public int getPing(Player p) {

        return ((CraftPlayer) p).getHandle().ping;
    }

    @Override
    public void freezeEntity(org.bukkit.entity.Entity en) {

    }

    @Override
    public ItemStack addGlow(ItemStack item) {

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.LUCK, 1);

        return item;
    }

    @Override
    public ItemStack setNBTTag(ItemStack item, String tag, String value) {
        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        compound.setString(tag, value);
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public boolean containsNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() && nmsItem.getTag().hasKey(tag);
    }

    @Override
    public String getNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_14_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() ? nmsItem.getTag().getString(tag) : "";
    }

    @Override
    public void sendHideEquipmentPacket(Player player, List<Player> playersToSendPacket) {

        PacketPlayOutEntityEquipment packetHelmet = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.HEAD,
                net.minecraft.server.v1_14_R1.ItemStack.a
        );
        PacketPlayOutEntityEquipment packetChestplate = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.CHEST,
                net.minecraft.server.v1_14_R1.ItemStack.a
        );
        PacketPlayOutEntityEquipment packetLeggings = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.LEGS,
                net.minecraft.server.v1_14_R1.ItemStack.a
        );
        PacketPlayOutEntityEquipment packetBoots = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.FEET,
                net.minecraft.server.v1_14_R1.ItemStack.a
        );

        for (Player playerToSend : playersToSendPacket) {
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetHelmet);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetChestplate);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetLeggings);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetBoots);
        }

    }

    @Override
    public void sendShowEquipmentPacket(Player player, List<Player> playersToSendPacket) {
        PacketPlayOutEntityEquipment packetHelmet = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.HEAD,
                CraftItemStack.asNMSCopy(player.getInventory().getHelmet())
        );
        PacketPlayOutEntityEquipment packetChestplate = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.CHEST,
                CraftItemStack.asNMSCopy(player.getInventory().getChestplate())
        );
        PacketPlayOutEntityEquipment packetLeggings = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.LEGS,
                CraftItemStack.asNMSCopy(player.getInventory().getLeggings())
        );
        PacketPlayOutEntityEquipment packetBoots = new PacketPlayOutEntityEquipment(
                ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId(),
                EnumItemSlot.FEET,
                CraftItemStack.asNMSCopy(player.getInventory().getBoots())
        );

        for (Player playerToSend : playersToSendPacket) {
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetHelmet);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetChestplate);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetLeggings);
            ((CraftPlayer) playerToSend).getHandle().playerConnection.sendPacket(packetBoots);
        }
    }

    @Override
    public Villager spawnVillager(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.villager.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (Villager) e.getBukkitEntity();
    }

    @Override
    public Blaze spawnBlaze(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.blaze.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (Blaze) e.getBukkitEntity();
    }

    @Override
    public Creeper spawnCreeper(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.creeper.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (Creeper) e.getBukkitEntity();
    }

    @Override
    public Skeleton spawnSkeleton(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.skeleton.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (Skeleton) e.getBukkitEntity();
    }

    @Override
    public IronGolem spawnGolem(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.golem.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (IronGolem) e.getBukkitEntity();
    }

    @Override
    public Zombie spawnZombie(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.zombie.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (Zombie) e.getBukkitEntity();
    }

    @Override
    public PigZombie spawnPigZombie(Location loc) {

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity e = this.pigman.a(world);
        e.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld) loc.getWorld()).getHandle().addEntity(e, SpawnReason.CUSTOM);

        return (PigZombie) e.getBukkitEntity();
    }

    @Override
    public void hidePlayer(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_GAME_MODE, entityPlayer);
        List<PacketPlayOutPlayerInfo.PlayerInfoData> list = new ArrayList<>();
        list.add(info.new PlayerInfoData(entityPlayer.getProfile(), entityPlayer.ping, EnumGamemode.ADVENTURE,
                entityPlayer.listName));
        ReflectionUtils.setFieldValue("b", info.getClass(), info, list);
        entityPlayer.playerConnection.sendPacket(info);
        PacketPlayOutGameStateChange pack = new PacketPlayOutGameStateChange(3, 2f);
        entityPlayer.playerConnection.sendPacket(pack);

    }

    @Override
    public void sendPlayerInfoPackets(Player p, JavaPlugin javaPlugin) {

        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        entityPlayer.playerInteractManager.setGameMode(EnumGamemode.SURVIVAL);
        entityPlayer.setInvisible(true);
        new BukkitRunnable() {

            @Override
            public void run() {
                entityPlayer.setInvisible(false);

            }
        }.runTask(javaPlugin);

    }

}
