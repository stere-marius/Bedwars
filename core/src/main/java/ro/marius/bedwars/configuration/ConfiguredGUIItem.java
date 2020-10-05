package ro.marius.bedwars.configuration;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.*;

public class ConfiguredGUIItem {

    private final List<Integer> slotList;
    private final Map<String, Object> additionalProperties;
    private final ItemBuilder itemBuilder;

    private static final Set<String> IGNORE_PROPERTIES = new HashSet<String>() {{
        add("Slot");
        add("Material");
        add("Amount");
        add("Lore");
        add("Glowing");
        add("Data");
        add("Name");
    }};

    public ConfiguredGUIItem(ItemBuilder itemBuilder, List<Integer> slotList, Map<String, Object> additionalProperties) {
        this.slotList = slotList;
        this.additionalProperties = additionalProperties;
        this.itemBuilder = itemBuilder;
    }


    public static ConfiguredGUIItem readFromConfig(String path, FileConfiguration fileConfiguration) {

        List<Integer> slotList = Utils.getListOfIntegerFromObject(fileConfiguration.getString(path + ".Slot"));
        String materialName = fileConfiguration.getString(path + ".Material", "STONE");
        byte data = (byte) fileConfiguration.getInt(path + ".Data");
        List<String> lore = fileConfiguration.getStringList(path + ".Lore");
        int amount = fileConfiguration.getInt(path + ".Amount");
        boolean glowing = fileConfiguration.getBoolean(path + ".Glowing");
        String displayName = fileConfiguration.getString(path + ".Name", "");
        Material material = XMaterial.parseMaterial(materialName, data);
        ItemStack itemStack = XMaterial.parseItemStack(material, data, amount);
        Map<String, Object> additionalProperties = new HashMap<>();

        for (String property : fileConfiguration.getConfigurationSection(path).getKeys(true)) {

            if (IGNORE_PROPERTIES.contains(property))
                continue;

            additionalProperties.put(property, fileConfiguration.get(path + "." + property));
        }

        ItemBuilder itemBuilder = new ItemBuilder(itemStack)
                .withAmount(amount)
                .setDisplayName(displayName)
                .setLore(lore)
                .glowingItem(ManagerHandler.getVersionManager().getVersionWrapper(), glowing);


        return new ConfiguredGUIItem(itemBuilder, slotList, additionalProperties);
    }

    public List<Integer> getSlotList() {
        return slotList;
    }

    public ItemBuilder getItemBuilder() {
        return itemBuilder;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }
}
