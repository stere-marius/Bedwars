package ro.marius.bedwars.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final Vector EMPTY_VECTOR = new Vector(0, 0, 0);
    final static Material WOODEN_SWORD = XMaterial.WOODEN_SWORD.parseMaterial();
    private final static int CENTER_PX = 154;

    public static final ServerVersion SERVER_VERSION;

    static {
        SERVER_VERSION = ServerVersion.valueOf("v" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1));
    }

    public static <T> List<T> getSubList(List<T> list, int startIndex, int endIndex) {

        int size = list.size();

        return list.subList(Math.min(startIndex, size), Math.min(endIndex, size));
    }

    public static <E extends Enum<E>> E valueOf(E defaultValue, String s) {
        try {
            return Enum.valueOf(defaultValue.getDeclaringClass(), s);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static BlockFace getBlockFace(String s) {

        BlockFace blockFace = null;

        for (BlockFace face : BlockFace.values()) {

            if (face.name().equalsIgnoreCase(s)) {
                blockFace = face;
            }

        }

        return blockFace;
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    public static String translate(String message) {

        if (SERVER_VERSION.isGreaterOrEqualThan(ServerVersion.v1_16_R2)) {
            Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());
            }

            return matcher.appendTail(buffer).toString();
        }


        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void addAvailableItems(Player sourcePlayer, Player recipientPlayer) {

        // if (GameManager.getManager().getSpectators().containsKey(killer))
        // return;

        int emerald = 0;
        int gold = 0;
        int iron = 0;
        int diamond = 0;
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < sourcePlayer.getInventory().getSize(); i++) {
            ItemStack item = sourcePlayer.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            if (item.getType() == Material.DIAMOND) {
                diamond += item.getAmount();
                items.add(item);
            }
            if (item.getType() == Material.EMERALD) {
                emerald += item.getAmount();
                items.add(item);
            }
            if (item.getType() == Material.IRON_INGOT) {
                iron += item.getAmount();
                items.add(item);
            }
            if (item.getType() == Material.GOLD_INGOT) {
                gold += item.getAmount();
                items.add(item);
            }
        }
        items.forEach(item -> recipientPlayer.getInventory().addItem(item));
        if (emerald > 0) {
            recipientPlayer.sendMessage(Utils.translate("&2" + emerald + " emeralds"));
        }
        if (gold > 0) {
            recipientPlayer.sendMessage(Utils.translate("&6" + gold + " gold"));
        }
        if (iron > 0) {
            recipientPlayer.sendMessage(Utils.translate("&f" + iron + " iron"));
        }
        if (diamond > 0) {
            recipientPlayer.sendMessage(Utils.translate("&b" + diamond + " diamond"));
        }

    }

    public static void playEndermanSound(Player p) {

        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7")) {
            p.playSound(p.getEyeLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
            return;
        }

        if (XMaterial.isNewVersion()) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            return;
        }

        p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 1, 1);

    }

    public static void playSoundBuy(Player p) {
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7")) {
            p.playSound(p.getEyeLocation(), Sound.valueOf("NOTE_PLING"), 2, 2);
            return;
        }

        if (XMaterial.isNewVersion()) {
            p.playSound(p.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
            return;
        }

        p.playSound(p.getEyeLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 2, 2);
    }

    public static <T> T defaultNullValue(T value, T defaultValue) {

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    public static void hideWoodenSword(Player p) {
        for (int i = 0; i < p.getInventory().getContents().length; i++) {
            ItemStack item = p.getInventory().getContents()[i];
            if (item == null) {
                continue;
            }
            if (WOODEN_SWORD != item.getType()) {
                continue;
            }
            p.getInventory().clear(i);
        }
    }

    public static void removeItem(Player p, Material material, int amount) {
        int size = p.getInventory().getSize();
        for (int i = 0; i < size; i++) {
            ItemStack is = p.getInventory().getItem(i);
            if (is == null) {
                continue;
            }
            if (material == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    p.getInventory().clear(i);
                    amount = -newAmount;
                    if (amount == 0) {
                        break;
                    }
                }
            }
        }
    }

    public static void removeItem(Player p, String endsWith) {
        int size = p.getInventory().getSize();
        PlayerInventory pi = p.getInventory();
        for (int i = 0; i < size; i++) {
            ItemStack is = pi.getItem(i);
            if (is == null) {
                continue;
            }
            if (is.getType().name().endsWith(endsWith)) {
                p.getInventory().clear(i);
            }
        }
    }

    public static List<Integer> getListOfIntegerFromObject(Object slot) {

        if (slot == null) {
            return new ArrayList<>();
        }

        List<Integer> list = new ArrayList<>();

        if (slot instanceof Integer) {
            list.add((Integer) slot);
            return list;
        }

        if (slot instanceof String) {

            String array = (String) slot;

            for (String obj : array.split(",")) {

                if (!Utils.isInteger(obj)) {
                    continue;
                }

                list.add(Integer.parseInt(obj));

            }
        }

        return list;

    }

    @SuppressWarnings("deprecation")
    public static void decreaseItemAmountFromHand(Player p) {

        if (p.getItemInHand().getAmount() > 1) {
            p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            return;
        }

        p.setItemInHand(null);
    }

    public static void decreaseItemAmountFromHand(Player p, ItemStack itemToRemove) {
        boolean canUseBothHands = SERVER_VERSION.getID() >= ServerVersion.v1_9_R1
                .getID();

        if (itemToRemove == null) {

            if (canUseBothHands) {
                p.getInventory().setItemInMainHand(null);
                return;
            }

            p.getInventory().setItemInHand(null);
            return;
        }

        if (!canUseBothHands) {

            ItemStack itemFromHand = p.getInventory().getItemInHand();

            if (itemFromHand.getAmount() > 1) {
                itemFromHand.setAmount(itemFromHand.getAmount() - 1);
                return;
            }

            p.getInventory().setItemInHand(null);
            return;
        }

        ItemStack itemFromMainHand = p.getInventory().getItemInMainHand();
        boolean isItemInMainHand = itemFromMainHand.isSimilar(itemToRemove);

        if (isItemInMainHand) {

            if (itemFromMainHand.getAmount() > 1) {
                itemFromMainHand.setAmount(itemFromMainHand.getAmount() - 1);
                return;
            }

            p.getInventory().setItemInMainHand(null);

            return;
        }

        ItemStack itemFromOffHand = p.getInventory().getItemInOffHand();
        boolean isItemInOffHand = itemFromOffHand.isSimilar(itemToRemove);


        if (isItemInOffHand) {

            if (itemFromOffHand.getAmount() > 1) {
                itemFromOffHand.setAmount(itemFromOffHand.getAmount() - 1);
                return;
            }

            p.getInventory().setItemInOffHand(null);

        }


    }

    public static int getArmorID(String armorType) {

        return armorType.startsWith("LEATHER") ? 0
                : (armorType.contains("GOLD") ? 1
                : (armorType.startsWith("CHAINMAIL") ? 2 : (armorType.startsWith("IRON") ? 3 : 4)));
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void sendPerformCommand(Player p, String comanda, String mesaj, String hoverDisplay) {
        TextComponent message = new TextComponent(Utils.translate(mesaj));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, comanda));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(Utils.translate(hoverDisplay)).create()));
        p.spigot().sendMessage(message);

    }

    public static void sendSuggestCommand(Player p, String comanda, String mesaj, String display) {
        TextComponent message = new TextComponent(Utils.translate(mesaj));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, comanda));
        message.setHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.translate(display)).create()));
        p.spigot().sendMessage(message);

    }

    public static Location convertingLocation(String string) {
        String[] sp = string.split(",");

        if (sp.length < 5) {
            return null;
        }

        World world = Bukkit.getWorld(sp[0]);
        double x = Double.parseDouble(sp[1]);
        double y = Double.parseDouble(sp[2]);
        double z = Double.parseDouble(sp[3]);
        float yaw = Float.parseFloat(sp[4]);
        float pitch = Float.parseFloat(sp[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String getStringIntCoordinates(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static String convertingString(Location location) {
        return Objects.requireNonNull(location.getWorld()).getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ()
                + "," + location.getYaw() + "," + location.getPitch();
    }

    public static List<String> setConvertingLocations(List<Location> blocks) {
        List<String> a = new ArrayList<>();
        blocks.forEach(locations -> a.add(convertingString(locations)));
        return a;
    }

    public static List<Location> getConvertingStrings(List<String> stringList) {
        List<Location> list = new ArrayList<>();
        stringList.forEach(string -> list.add(convertingLocation(string)));
        return list;
    }

    public static void doCommandBungeeCord(JavaPlugin javaPlugin) {


        FileConfiguration config = javaPlugin.getConfig();

        if (config.getBoolean("BungeeCord.Enabled")) {
            if (!"".equals(config.getString("BungeeCord.RestartMessage"))) {
                System.out.println(
                        Utils.translate(config.getString("BungeeCord.RestartMessage")));
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                            config.getString("BungeeCord.RestartCommand"));

                }
            }.runTaskLater(javaPlugin, 20 * 10);
        }

    }

    public static ArmorStand getSpawnedArmorStand(Location location, String customName) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(customName);
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        return stand;
    }

    public static void teleportPlayerToServer(Player player, String server, JavaPlugin javaPlugin) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(outputStream);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        player.sendPluginMessage(javaPlugin, "BungeeCord", outputStream.toByteArray());
    }

    public static void teleportToLobby(Player p, JavaPlugin javaPlugin) {
        FileConfiguration config = javaPlugin.getConfig();
        if (config.getBoolean("BungeeCord.Enabled")) {
            List<String> list = config.getStringList("BungeeCord.RandomLobbyServer.Servers");
            String server = config.getBoolean("BungeeCord.RandomLobbyServer.Enabled")
                    ? list.get(new Random().nextInt(list.size()))
                    : config.getString("BungeeCord.LobbyServer");
            teleportPlayerToServer(p, server, javaPlugin);
            return;
        }
        try {
            p.teleport(convertingLocation(config.getString("LobbyLocation")));
            p.setFallDistance(0.0F);
        } catch (NullPointerException e) {
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            p.setFallDistance(0.0F);
            p.sendMessage(Utils.translate("&cLobby location not found. You have been teleported to main world spawn."));
        }
    }

    public static void resetPlayer(Player p, boolean clearPotions, boolean clearInventory) {
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setFireTicks(0);
        p.setWalkSpeed(0.2F);
        p.setFlySpeed(0.1F);
        p.closeInventory();
        p.updateInventory();
        p.setGameMode(GameMode.SURVIVAL);

        if (clearInventory) {
            p.getInventory().setArmorContents(null);
            p.getInventory().clear();
        }

        if (clearPotions) {
            p.getActivePotionEffects().forEach(effects -> p.removePotionEffect(effects.getType()));
        }

    }

    public static void clearPotionEffects(Player p) {
        p.getActivePotionEffects().forEach(effects -> p.removePotionEffect(effects.getType()));
    }

    public static void sendSoundBedBroken(Player p) {
        if (Bukkit.getServer().getVersion().contains("1.8")) {
            p.playSound(p.getLocation(), Sound.valueOf("ENDERDRAGON_GROWL"), 1.0f, 1.0f);
            return;
        }

        if (XMaterial.isNewVersion()) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            return;
        }

        p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ENDERDRAGON_GROWL"), 1.0f, 1.0f);

    }

    public static void sendCenteredMessage(Player player, String message) {
        if (!"".equals(message)) {

            message = ChatColor.translateAlternateColorCodes('&', message);

            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : message.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                    continue;
                } else if (previousCode == true) {
                    previousCode = false;
                    if ((c == 'l') || (c == 'L')) {
                        isBold = true;
                        continue;
                    } else {
                        isBold = false;
                    }
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                    messagePxSize++;
                }
            }

            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }

            player.sendMessage(sb + message);
        }
    }

    public static Integer getInteger(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static Integer getInteger(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
