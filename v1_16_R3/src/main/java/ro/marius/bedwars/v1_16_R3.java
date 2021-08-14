package ro.marius.bedwars;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.irongolem.I_V_1_16_R3;
import ro.marius.bedwars.shopkeepers.*;
import ro.marius.bedwars.utils.ReflectionUtils;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class v1_16_R3 implements VersionWrapper {

    private CustomEntityType_1_16_R3<B_1_16_R3> blaze;
    private CustomEntityType_1_16_R3<C_1_16_R3> creeper;
    private CustomEntityType_1_16_R3<S_1_16_R3> skeleton;
    private CustomEntityType_1_16_R3<V_1_16_R3> villager;
    private CustomEntityType_1_16_R3<Z_1_16_R3> zombie;
    private CustomEntityType_1_16_R3<ZP_1_16_R3> pigman;
    private CustomEntityType_1_16_R3<I_V_1_16_R3> golem;

    public v1_16_R3() {
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

        if (!(blockData instanceof Bed))
            return null;

        Bed bed = (Bed) blockData;

        if (bed.getPart() == Part.FOOT) {
            return block.getRelative(bed.getFacing()).getLocation();
        }

        if (bed.getPart() == Part.HEAD) {
            return location;
        }

        return null;
    }

    @Override
    public Set<Block> getPlacedBedBlocks(BlockFace bedFace, Location loc, Material bedMaterial) {

        Block b = loc.getBlock();

        if (b.getType().name().contains("BED")) {
            b.setType(Material.AIR);
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
            o.setType(Material.AIR);
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

        this.villager = new CustomEntityType_1_16_R3<>("bedwars_villager", V_1_16_R3.class, EntityTypes.VILLAGER, V_1_16_R3::new);
        this.villager.register(EnumCreatureType.CREATURE);
        this.golem = new CustomEntityType_1_16_R3<>("bedwars_golem", I_V_1_16_R3.class, EntityTypes.IRON_GOLEM, I_V_1_16_R3::new);
        this.golem.register(EnumCreatureType.MISC);
        this.skeleton = new CustomEntityType_1_16_R3<>("bedwars_skeleton", S_1_16_R3.class, EntityTypes.SKELETON, S_1_16_R3::new);
        this.skeleton.register(EnumCreatureType.MONSTER);
        this.blaze = new CustomEntityType_1_16_R3<>("bedwars_blaze", B_1_16_R3.class, EntityTypes.BLAZE, B_1_16_R3::new);
        this.blaze.register(EnumCreatureType.MONSTER);
        this.creeper = new CustomEntityType_1_16_R3<>("bedwars_creeper", C_1_16_R3.class, EntityTypes.CREEPER, C_1_16_R3::new);
        this.creeper.register(EnumCreatureType.MONSTER);
        this.zombie = new CustomEntityType_1_16_R3<>("bedwars_zombie", Z_1_16_R3.class, EntityTypes.ZOMBIE, Z_1_16_R3::new);
        this.zombie.register(EnumCreatureType.MONSTER);
        this.pigman = new CustomEntityType_1_16_R3<>("bedwars_creeper", ZP_1_16_R3.class, EntityTypes.ZOMBIFIED_PIGLIN, ZP_1_16_R3::new);
        this.pigman.register(EnumCreatureType.MONSTER);
    }

    public void unregisterEntities() {
        this.blaze.unregister();
    }

    @Override
    public int getPing(Player p) {

        return ((CraftPlayer) p).getHandle().ping;
    }

    @Override
    public void freezeEntity(Entity en) {

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
        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        compound.setString(tag, value);
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public boolean containsNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() && nmsItem.getTag().hasKey(tag);
    }

    @Override
    public String getNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() ? nmsItem.getTag().getString(tag) : "";
    }

    @Override
    public void sendHideEquipmentPacket(Player player, List<Player> playersToSendPacket) {
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> items = new ArrayList<>();
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairHead = new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(null));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairChest = new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(null));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairLegs = new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(null));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairFeet = new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(null));
        items.add(pairHead);
        items.add(pairChest);
        items.add(pairLegs);
        items.add(pairFeet);

        int entityID = ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId();
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, items);
        playersToSendPacket.forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    @Override
    public void sendShowEquipmentPacket(Player player, List<Player> playersToSendPacket) {
        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack>> items = new ArrayList<>();
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairHead =
                new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(player.getInventory().getHelmet()));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairChest =
                new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(player.getInventory().getChestplate()));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairLegs =
                new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(player.getInventory().getLeggings()));
        Pair<EnumItemSlot, net.minecraft.server.v1_16_R3.ItemStack> pairFeet =
                new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(player.getInventory().getBoots()));
        items.add(pairHead);
        items.add(pairChest);
        items.add(pairLegs);
        items.add(pairFeet);

        int entityID = ((CraftPlayer) player).getHandle().getBukkitEntity().getEntityId();
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, items);
        playersToSendPacket.forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    @Override
    public Villager spawnVillager(Location loc) {

        return (Villager) this.villager.spawn(loc);
    }

    @Override
    public Blaze spawnBlaze(Location loc) {

        return (Blaze) this.blaze.spawn(loc);
    }

    @Override
    public Creeper spawnCreeper(Location loc) {

        return (Creeper) this.creeper.spawn(loc);
    }

    @Override
    public Skeleton spawnSkeleton(Location loc) {

        return (Skeleton) this.skeleton.spawn(loc);
    }

    @Override
    public IronGolem spawnGolem(Location loc) {

        return (IronGolem) this.golem.spawn(loc);
    }

    @Override
    public Zombie spawnZombie(Location loc) {

        return (Zombie) this.zombie.spawn(loc);
    }

    @Override
    public PigZombie spawnPigZombie(Location loc) {

        return (PigZombie) this.pigman.spawn(loc);
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
        PacketPlayOutGameStateChange pack = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 2f);
        entityPlayer.playerConnection.sendPacket(pack);
    }

    @Override
    public void sendPlayerInfoPackets(Player p, JavaPlugin javaPlugin) {
        p.setGameMode(GameMode.SURVIVAL);
        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        entityPlayer.playerInteractManager.setGameMode(EnumGamemode.SURVIVAL);
        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_GAME_MODE, entityPlayer);
        List<PacketPlayOutPlayerInfo.PlayerInfoData> list = new ArrayList<>();
        list.add(info.new PlayerInfoData(entityPlayer.getProfile(), entityPlayer.ping, EnumGamemode.SURVIVAL,
                entityPlayer.listName));
        ReflectionUtils.setFieldValue("b", info.getClass(), info, list);
        entityPlayer.playerConnection.sendPacket(info);
        PacketPlayOutGameStateChange pack = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 0f);
        entityPlayer.playerConnection.sendPacket(pack);
        entityPlayer.setInvisible(true);
        new BukkitRunnable() {

            @Override
            public void run() {
                entityPlayer.setInvisible(false);

            }
        }.runTask(javaPlugin);
    }

}
