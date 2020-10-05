package ro.marius.bedwars;

import net.minecraft.server.v1_11_R1.*;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.plugin.java.JavaPlugin;
import ro.marius.bedwars.irongolem.I_V_1_11_R1;
import ro.marius.bedwars.shopkeepers.*;
import ro.marius.bedwars.utils.ReflectionUtils;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class v1_11_R1 implements VersionWrapper {

    @Override
    public void hidePlayer(Player p, Player playerToBeHidden, JavaPlugin javaPlugin) {
        p.hidePlayer(playerToBeHidden);
    }

    @Override
    public void showPlayer(Player p, Player toShow, JavaPlugin javaPlugin) {
        p.showPlayer(toShow);
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
    public void sendPacketEquipment(Player p, Player sendTo, ItemStack modified, int slotIndex) {

        EnumItemSlot enumItemSlot;

        switch (slotIndex) {
            case 1:
                enumItemSlot = EnumItemSlot.FEET;
                break;
            case 2:
                enumItemSlot = EnumItemSlot.LEGS;
                break;
            case 3:
                enumItemSlot = EnumItemSlot.CHEST;
                break;
            case 4:
                enumItemSlot = EnumItemSlot.HEAD;
                break;
            default:
                throw new NullPointerException("Couldn't find the EnumItemSlot with index " + slotIndex + " on v1_11_R1 ");
        }

        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        ReflectionUtils.setFieldValue("a", packet.getClass(), packet, ((CraftPlayer) p).getHandle().getBukkitEntity().getEntityId());
        ReflectionUtils.setFieldValue("b", packet.getClass(), packet, enumItemSlot);
        ReflectionUtils.setFieldValue("c", packet.getClass(), packet, CraftItemStack.asNMSCopy(modified));
        ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(packet);

    }

    @Override
    public Location getBedHead(Location location) {

        BlockState state = location.getBlock().getState();

        if (state.getData() instanceof Bed) {

            Bed bed = (Bed) state.getData();

            if (!bed.isHeadOfBed()) {
                location = location.getBlock().getRelative(bed.getFacing()).getLocation();
            }

        }

        return location;
    }

    @Override
    public ItemStack addGlow(ItemStack item) {
        net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) {
            tag = nmsStack.getTag();
        }
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public ItemStack setNBTTag(ItemStack item, String tag, String value) {
        net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        compound.setString(tag, value);
        nmsItem.setTag(compound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public boolean containsNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() && nmsItem.getTag().hasKey(tag);
    }

    @Override
    public String getNBTTag(ItemStack item, String tag) {

        net.minecraft.server.v1_11_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        return nmsItem.hasTag() ? nmsItem.getTag().getString(tag) : "";
    }

    @Override
    public void freezeEntity(Entity en) {

    }

    @Override
    public int getPing(Player p) {
        return ((CraftPlayer) p).getHandle().ping;

    }

    @Override
    public Set<Block> getPlacedBedBlocks(BlockFace bedFace, Location loc, org.bukkit.Material bedMaterial) {

        Set<Block> placedBlocks = new HashSet<>();

        Block bedHeadBlock = loc.getBlock();
        Block bedFootBlock = bedHeadBlock.getRelative(bedFace.getOppositeFace());

        BlockState bedFootState = bedFootBlock.getState();
        bedFootState.setType(bedMaterial);
        Bed bedFootData = new Bed(bedMaterial);
        bedFootData.setHeadOfBed(false);
        bedFootData.setFacingDirection(bedFace);
        bedFootState.setData(bedFootData);
        bedFootState.update(true);

        BlockState bedHeadState = bedHeadBlock.getState();
        bedHeadState.setType(bedMaterial);
        Bed bedHeadData = new Bed(bedMaterial);
        bedHeadData.setHeadOfBed(true);
        bedHeadData.setFacingDirection(bedFace);
        bedHeadState.setData(bedHeadData);
        bedHeadState.update(true);

        placedBlocks.add(bedHeadBlock);
        placedBlocks.add(bedFootBlock);

        return placedBlocks;
    }

    @Override
    public BlockFace getBedFace(Location location) {

        BlockFace face = null;
        BlockState state = location.getBlock().getState();

        if (state.getData() instanceof Bed) {

            Bed bed = (Bed) state.getData();

            face = bed.getFacing();

        }

        return face;
    }

    @Override
    public Villager spawnVillager(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        V_1_11_R1 villager = new V_1_11_R1(world);
        villager.spawn(location);

        return (Villager) villager.getCustomEntity();
    }

    @Override
    public Blaze spawnBlaze(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        B_1_11_R1 blaze = new B_1_11_R1(world);
        blaze.spawn(location);

        return (Blaze) blaze.getCustomEntity();
    }

    @Override
    public Creeper spawnCreeper(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        C_1_11_R1 creeper = new C_1_11_R1(world);
        creeper.spawn(location);

        return (Creeper) creeper.getCustomEntity();
    }

    @Override
    public Skeleton spawnSkeleton(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        S_1_11_R1 skeleton = new S_1_11_R1(world);
        skeleton.spawn(location);

        return (Skeleton) skeleton.getCustomEntity();
    }

    @Override
    public IronGolem spawnGolem(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        I_V_1_11_R1 golem = new I_V_1_11_R1(world);
        golem.spawn(location);

        return (IronGolem) golem.getCustomEntity();
    }

    @Override
    public Zombie spawnZombie(Location location) {

        World world = ((CraftWorld) location.getWorld()).getHandle();
        Z_1_11_R1 zombie = new Z_1_11_R1(world);
        zombie.spawn(location);

        return (Zombie) zombie.getBukkitEntity();
    }

    @Override
    public PigZombie spawnPigZombie(Location location) {

        World world = ((CraftWorld) location.getWorld()).getHandle();
        ZP_1_11_R1 zombie = new ZP_1_11_R1(world);
        zombie.spawn(location);

        return (PigZombie) zombie.getBukkitEntity();
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

        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_GAME_MODE, entityPlayer);

        entityPlayer.playerConnection.sendPacket(info);

    }

}
