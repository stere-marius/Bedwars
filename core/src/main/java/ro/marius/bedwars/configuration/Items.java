package ro.marius.bedwars.configuration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Items {

    GAME_LEAVE(true, XMaterial.RED_BED.parseMaterial().name(), 0, 8, "&aLeave from arena", ""),
    SPECTATOR_LEAVE(true, XMaterial.RED_BED.parseMaterial().name(), 0, 8, "&aLeave from arena", ""),
    TEAM_SELECTOR(false, "COMPASS", 0, 0, "&aSelect a team", ""),
    //	KIT_SELECTOR(true, "REDSTONE_COMPARATOR", 1, 0, 0, "&eSelect a kit", ""),
    TELEPORTER(true, "COMPASS", 0, 0, "&aTeleport to a player", ""),
    SPECTATOR_SETTINGS(true, "REDSTONE_COMPARATOR", 0, 4, "&bSpectator settings&7(Right click)", ""),
    ;

    private static YamlConfiguration CONFIG;
    private final boolean enabled;
    private final String material;
    private final int data;
    private final int slot;
    private final String displayName;
    private final List<String> lore;
    private ItemBuilder itemBuilder;

    Items(boolean enabled, String material, int data, int slot, String displayName, String... lore) {
        this.enabled = enabled;
        this.material = material;
        this.data = data;
        this.slot = slot;
        this.displayName = displayName;
        this.lore = Arrays.asList(lore);
    }

    public static void setConfig(YamlConfiguration config) {
        Items.CONFIG = config;
    }

    public static void loadItems(BedWarsPlugin plugin) {
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        Logger log = Bukkit.getLogger();
        if (!itemsFile.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                itemsFile.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
                log.severe("[Bedwars] Couldn't create language file.");

            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(itemsFile);
        for (Items item : Items.values()) {
            if (conf.getString(item.getKey()) == null) {
                conf.set(item.getKey() + ".Enabled", item.isENABLED());
                conf.set(item.getKey() + ".Material", item.getMATERIAL());
                conf.set(item.getKey() + ".Data", item.getDATA());
                conf.set(item.getKey() + ".Slot", item.getSLOT());
                conf.set(item.getKey() + ".Amount", 1);
                conf.set(item.getKey() + ".DisplayName", item.getNAME());
                conf.set(item.getKey() + ".Lore", item.getLORE());
            }
        }
        setConfig(conf);

        try {
            conf.save(itemsFile);
        } catch (IOException e) {
            log.log(Level.WARNING, "Bedwars: Failed to save items.yml.");
            e.printStackTrace();
        }

    }

    public String getKey() {
        return this.name().toLowerCase().replace("_", "-");
    }

    public ItemStack toItemStack() {

        if (this.itemBuilder != null) {
            return this.itemBuilder.build();
        }

        Material material = XMaterial.matchXMaterial(this.getMaterial(), (byte) this.getData()).parseMaterial();
        ItemStack itemStack = XMaterial.parseItemStack(material, this.getAmount(), this.getData());
        ItemBuilder builder = this.itemBuilder = new ItemBuilder(itemStack, 1).setDisplayName(this.getName()).setLore(this.getLore());

        return builder.build();
    }

    public Material getType() {

        return XMaterial.matchXMaterial(this.getMaterial(), (byte) this.getData()).parseMaterial();
    }

    public boolean isEnabled() {
        return CONFIG.getBoolean(this.getKey() + ".Enabled");
    }

    public String getMaterial() {
        return CONFIG.getString(this.getKey() + ".Material");
    }

    public int getData() {
        return CONFIG.getInt(this.getKey() + ".Data");
    }

    public int getAmount() {
        return CONFIG.getInt(this.getKey() + ".Amount", 1);
    }

    public String getName() {
        String value = CONFIG.getString(this.getKey() + ".DisplayName");


        if (value == null) {
            Bukkit.getLogger().info("[Bedwars-items.yml] Missing CONFIG data: " + this.getKey());
            value = "";
        }

        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public int getSlot() {
        return CONFIG.getInt(this.getKey() + ".Slot");
    }

    public List<String> getLore() {
        List<String> value = CONFIG.getStringList(this.getKey() + ".Lore");
        return this.translateLore(value);
    }

    public List<String> translateLore(List<String> lore) {
        List<String> list = new ArrayList<>();
        lore.forEach(l -> list.add(Utils.translate(l)));
        return list;
    }

    protected boolean isENABLED() {
        return this.enabled;
    }

    protected String getMATERIAL() {
        return this.material;
    }

    protected int getDATA() {
        return this.data;
    }

    protected String getNAME() {
        return this.displayName;
    }

    protected int getSLOT() {
        return this.slot;
    }

    protected List<String> getLORE() {
        return this.lore;
    }

    @Override
    public String toString() {

        return "[Material=" + this.getMaterial() + " , Data=" + this.getData() + "  ]";
    }

}
