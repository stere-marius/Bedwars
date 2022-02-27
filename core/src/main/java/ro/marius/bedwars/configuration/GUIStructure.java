package ro.marius.bedwars.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.GUIItem;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GUIStructure {

    private final static File FILE = new File(BedWarsPlugin.getInstance().getDataFolder(), "menus.yml");
    private static YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);

    // o clasa care are ConfiguredGuiDAO

    public static void loadConfiguration() {

        String arenaMenuPath = "Menu.ArenaInventory";
        config.addDefault(arenaMenuPath + ".SearchingDisplay", "&aSearching players");
        config.addDefault(arenaMenuPath + ".StartingDisplay", "&aStarting in <seconds> s");
        String firstItemPath = arenaMenuPath + ".Contents.FIRST_ITEM";

        config.addDefault(arenaMenuPath + ".Size", 27);
        config.addDefault(arenaMenuPath + ".InventoryName", "&eJoin in arena");
        config.addDefault(arenaMenuPath + ".ArenaSlot", "9,10,11,12,13,14,15,16,17");

        if (!FILE.exists()) {
            config.addDefault(firstItemPath + ".Slot", "0,1,2,3,4,5,6,7,8,18,19,20,21,22,23,24,25,26");
            config.addDefault(firstItemPath + ".Material", "STAINED_GLASS_PANE");
            config.addDefault(firstItemPath + ".Data", 15);
            config.addDefault(firstItemPath + ".Amount", 1);
            config.addDefault(firstItemPath + ".DisplayName", " ");
            config.addDefault(firstItemPath + ".Glowing", false);
            config.addDefault(firstItemPath + ".Lore", Collections.singletonList(""));
        }

        config.addDefault(arenaMenuPath + ".WaitingArena.Material", "WOOL");
        config.addDefault(arenaMenuPath + ".WaitingArena.Data", (int) XMaterial.LIME_WOOL.getData());
        config.addDefault(arenaMenuPath + ".WaitingArena.Amount", 1);
        config.addDefault(arenaMenuPath + ".WaitingArena.DisplayName", "&e⇨<arenaName>");
        config.addDefault(arenaMenuPath + ".WaitingArena.Glowing", false);
        config.addDefault(arenaMenuPath + ".WaitingArena.Lore",
                Arrays.asList("&e⇨Arena <arenaName>", "&e⇨Mode <arenaType>", " "));

        config.addDefault(arenaMenuPath + ".StartingArena.Material", "WOOL");
        config.addDefault(arenaMenuPath + ".StartingArena.Data", (int) XMaterial.RED_WOOL.getData());
        config.addDefault(arenaMenuPath + ".StartingArena.Amount", 1);
        config.addDefault(arenaMenuPath + ".StartingArena.DisplayName", "&e⇨<arenaName>");
        config.addDefault(arenaMenuPath + ".StartingArena.Glowing", false);
        config.addDefault(arenaMenuPath + ".StartingArena.Lore",
                Arrays.asList("&e⇨Arena <arenaName>", "&e⇨Mode <arenaType>", "&aStarting in <seconds>"));

        config.addDefault(arenaMenuPath + ".NextPage.Slot", 26);
        config.addDefault(arenaMenuPath + ".NextPage.Material", "PAPER");
        config.addDefault(arenaMenuPath + ".NextPage.Data", 0);
        config.addDefault(arenaMenuPath + ".NextPage.Amount", 1);
        config.addDefault(arenaMenuPath + ".NextPage.DisplayName", "&e⇨Next page");
        config.addDefault(arenaMenuPath + ".NextPage.Glowing", false);
        config.addDefault(arenaMenuPath + ".NextPage.Lore", Collections.singletonList(""));

        config.addDefault(arenaMenuPath + ".PreviousPage.Slot", 25);
        config.addDefault(arenaMenuPath + ".PreviousPage.Material", "PAPER");
        config.addDefault(arenaMenuPath + ".PreviousPage.Data", 0);
        config.addDefault(arenaMenuPath + ".PreviousPage.Amount", 1);
        config.addDefault(arenaMenuPath + ".PreviousPage.DisplayName", "&e⇨Previous page");
        config.addDefault(arenaMenuPath + ".PreviousPage.Glowing", false);
        config.addDefault(arenaMenuPath + ".PreviousPage.Lore", Collections.singletonList(""));

        config.addDefault("Menu.BedwarsJoinNPC.Size", 36);
        config.addDefault("Menu.BedwarsJoinNPC.InventoryName", "&8Play Bed Wars");

        if (!FILE.exists()) {
            config.addDefault("Menu.BedwarsJoinNPC.Contents.BED.Slot", 12);
            config.addDefault("Menu.BedwarsJoinNPC.Contents.BED.Material", "BED");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.BED.DisplayName", "&aBed Wars <arenaTypeFirstLetterUppercase>");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.BED.Lore",
                    Arrays.asList("&7Play Bed Wars <arenaTypeFirstLetterUppercase>", "", "&eClick to play!"));
            config.addDefault("Menu.BedwarsJoinNPC.Contents.BED.PlayerCommands",
                    Collections.singletonList("bedwars randomJoin <arenaType>"));

            config.addDefault("Menu.BedwarsJoinNPC.Contents.MAP_SELECTOR.Slot", 14);
            config.addDefault("Menu.BedwarsJoinNPC.Contents.MAP_SELECTOR.Material", "SIGN");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.MAP_SELECTOR.DisplayName",
                    "&aMap Selector (<arenaTypeFirstLetterUppercase>)");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.MAP_SELECTOR.Lore",
                    Arrays.asList("&7Pick which map you want to play", "&7from a list of available servers.", "",
                            "&eClick to browse"));
            config.addDefault("Menu.BedwarsJoinNPC.Contents.MAP_SELECTOR.PlayerCommands",
                    Collections.singletonList("bedwars arenasGUI <arenaType>"));

            config.addDefault("Menu.BedwarsJoinNPC.Contents.CLOSE.Slot", 31);
            config.addDefault("Menu.BedwarsJoinNPC.Contents.CLOSE.Material", "ENDER_PEARL");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.CLOSE.DisplayName", "&cClose");
            config.addDefault("Menu.BedwarsJoinNPC.Contents.CLOSE.PlayerCommands", Collections.singletonList("bedwars closeInventory"));
        }

        config.options().copyDefaults(true);
        saveConfig();

    }

    public static Map<Integer, GUIItem> readInventory(String path) {

        Map<Integer, GUIItem> items = new HashMap<>();

        for (String name : config.getConfigurationSection(path + ".Contents").getKeys(false)) {
            List<Integer> slots = Utils.getListOfIntegerFromObject(config.get(path + ".Contents." + name + ".Slot"));
            ItemBuilder builder = readBuilder(path + ".Contents." + name);
            List<String> playerCommands = config.getStringList(path + ".Contents." + name + ".PlayerCommands");
            slots.forEach(s -> items.put(s, new GUIItem(builder, playerCommands)));
        }

        return items;
    }

    public static ItemBuilder readBuilder(String path) {

        String material = config.getString(path + ".Material");

        if (material == null) {
            sendError(Arrays.asList("&ePath " + path + ".Material", "&eCouldn't find the material",
                    "&eReplaced it with STONE ."));
            material = "STONE";
        }

        int data = config.getInt(path + ".Data");
        XMaterial mat = XMaterial.matchXMaterial(material, (byte) data);

        if (mat == null) {
            sendError(Arrays.asList("&ePath " + path + ".Material",
                    "&eCouldn't find the material " + material + (data != 0 ? " with data " + data : ""),
                    "&eReplaced it with STONE ."));
            mat = XMaterial.STONE;
        }

        int amount = config.getInt(path + ".Amount", 1);
        String displayName = config.getString(path + ".DisplayName");
        boolean glowing = config.getBoolean(path + ".Glowing");
        List<String> lore = config.getStringList(path + ".Lore");

        return new ItemBuilder(mat.parseItem(amount)).setDisplayName(displayName).setLore(lore).glowingItem(ManagerHandler.getVersionManager().getVersionWrapper(), glowing);
    }

    public static List<Integer> getSlot(String path) {

        return Utils.getListOfIntegerFromObject(config.get(path));
    }

    public static int getInventorySize(String path) {

        int size = config.getInt(path + ".Size");

        if ((size % 9) != 0) {
            sendError(Arrays.asList("&4[Bedwars][Path=" + path + "] &cThe inventory size must be multiply of 9 ", "&cAs example: 9, 18, 27"));
            return 54;
        }

        return size;
    }

    public static void sendError(List<String> message) {
        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Bedwars Menus Configuration Warning --------------"));
        Bukkit.getConsoleSender().sendMessage("");

        for (String s : message) {
            Bukkit.getConsoleSender().sendMessage(Utils.translate(s));
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Bedwars Menus Configuration Warning --------------"));
    }

    public static void saveConfig() {
        try {
            config.save(FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(FILE);
    }

    public static YamlConfiguration getConfig() {
        return GUIStructure.config;
    }
}
