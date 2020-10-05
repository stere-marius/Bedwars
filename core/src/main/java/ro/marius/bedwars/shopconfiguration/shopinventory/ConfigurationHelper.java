package ro.marius.bedwars.shopconfiguration.shopinventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.action.BuyItemAction;
import ro.marius.bedwars.menu.action.RewardItem;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.requirements.Requirement;
import ro.marius.bedwars.requirements.RequirementBuilder;
import ro.marius.bedwars.requirements.type.PUpgradeRequirement;
import ro.marius.bedwars.requirements.type.PermissionRequirement;
import ro.marius.bedwars.requirements.type.TUpgradeRequirement;
import ro.marius.bedwars.shopconfiguration.ShopPath;
import ro.marius.bedwars.team.upgrade.IUpgrade;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.PotionBuilder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class ConfigurationHelper {

    private final XMaterial STONE = XMaterial.STONE;
    private final Map<String, TeamUpgrade> playerUpgrade = new HashMap<>();

    public ConfigurationHelper() {

    }

    public void loadDefaultConfiguration(Game game) {

        InputStream inputStream = BedWarsPlugin.getInstance().getResource("default.yml");
        YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));

        String arenaType = "DEFAULT";

        Map<String, TeamUpgrade> playerUpgrade = new HashMap<>();
        String mainMenu = shopConfig.getString("ArenaType." + arenaType + ".MainMenu");
        Map<String, InventoryIcon> definedItem = new HashMap<>();

        String definedMaterialPath = "ArenaType." + arenaType + ".DefinedMaterial";

        if (shopConfig.getConfigurationSection(definedMaterialPath) != null) {

            for (String definedMaterial : shopConfig.getConfigurationSection(definedMaterialPath).getKeys(false)) {

                String definedPath = definedMaterialPath + "." + definedMaterial;

                String action = shopConfig.getString(definedPath + ".Action");

                if ("BUY_UPGRADE".equalsIgnoreCase(action)) {
                    TeamUpgrade teamUpgrade = new TeamUpgrade(this.readTier(shopConfig, game, definedPath, definedPath));
                    InventoryIcon item = new UpgradableIcon(definedMaterial, definedMaterial);
                    item.addAction(new BuyUpgradeAction(definedMaterial));
                    playerUpgrade.put(definedMaterial, teamUpgrade);
                    definedItem.put(definedMaterial, item);
                    continue;
                }

                ItemBuilder builder = this.readItemBuilder(shopConfig, definedPath);
                InventoryIcon item = new InventoryIcon(definedMaterial, builder);

                this.readAction(shopConfig, game, definedMaterial, item, shopConfig.getString(definedPath + ".Action"),
                        definedPath);

                definedItem.put(definedMaterial, item);

            }

        }

        Map<String, ShopInventory> inv = new HashMap<>();

        if (shopConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu") != null) {

            for (String menu : shopConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu").getKeys(false)) {

                String pathMenu = "ArenaType." + arenaType + ".Menu." + menu;
                int size = shopConfig.getInt(pathMenu + ".InventorySize");
                size = (size <= 0) ? 54 : (((size % 9) != 0) ? 54 : size);
                String inventoryName = shopConfig.getString(pathMenu + ".InventoryName");
                Map<Integer, InventoryIcon> items = new HashMap<>();

                if (shopConfig.getConfigurationSection(pathMenu + ".Contents") != null) {

                    for (String item : shopConfig.getConfigurationSection(pathMenu + ".Contents").getKeys(false)) {

                        String contentsPath = pathMenu + ".Contents." + item;

                        Object slotPath = shopConfig.get(contentsPath + ".Slot");
                        List<Integer> slot = Utils.getListOfIntegerFromObject(slotPath);
                        boolean isDefined = (shopConfig.getString(contentsPath + ".DefinedMaterial") != null)
                                && definedItem.containsKey(shopConfig.getString(contentsPath + ".DefinedMaterial"));

                        if (isDefined) {

                            String action = shopConfig.getString(contentsPath + ".Action");

                            if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                                TeamUpgrade teamUpgrade = new TeamUpgrade(
                                        this.readTier(shopConfig, game, item, contentsPath));
                                InventoryIcon icon = new UpgradableIcon(menu + "_" + item, item);
                                icon.addAction(new BuyUpgradeAction(item));
                                playerUpgrade.put(item, teamUpgrade);
                                icon.getRequirement().addAll(
                                        this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                                slot.forEach(s -> items.put(s, icon));
                                continue;
                            }

                            InventoryIcon icon = definedItem
                                    .get(shopConfig.getString(contentsPath + ".DefinedMaterial"));

                            if (shopConfig.getBoolean(contentsPath + ".EmptyQuickBuySlot")) {

                                int quickBuySlot = 0;

                                for (Integer s : slot) {
                                    QuickBuyIcon qIcon = new QuickBuyIcon(icon.getPath(), icon.getItemBuilder(),
                                            quickBuySlot);

                                    this.readAction(shopConfig, game, item, qIcon,
                                            shopConfig.getString(contentsPath + ".Action"), contentsPath);
                                    qIcon.getRequirement().addAll(this.readRequirement(shopConfig, game, arenaType,
                                            contentsPath + ".Requirement"));
                                    items.put(s, qIcon);
                                    quickBuySlot++;
                                }

                                continue;

                            }

                            this.readAction(shopConfig, game, item, icon, shopConfig.getString(contentsPath + ".Action"),
                                    contentsPath);
                            icon.getRequirement().addAll(
                                    this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                            slot.forEach(s -> items.put(s, icon));
                            continue;
                        }

                        String action = shopConfig.getString(contentsPath + ".Action");
                        // String replaceWith = shopConfig.getString(contentsPath + ".ReplaceIfActive");

                        if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                            TeamUpgrade teamUpgrade = new TeamUpgrade(this.readTier(shopConfig, game, item, contentsPath));
                            InventoryIcon icon = new UpgradableIcon(item, item);
                            icon.addAction(new BuyUpgradeAction(item));
                            playerUpgrade.put(item, teamUpgrade);
                            icon.getRequirement().addAll(
                                    this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                            slot.forEach(s -> items.put(s, icon));
                            continue;
                        }

                        ItemBuilder builder = this.readItemBuilder(shopConfig, contentsPath);
                        InventoryIcon icon = new InventoryIcon(menu + "_" + item, builder);

                        if (shopConfig.getBoolean(contentsPath + ".EmptyQuickBuySlot")) {

                            int quickBuySlot = 0;

                            for (Integer s : slot) {
                                QuickBuyIcon qIcon = new QuickBuyIcon(icon.getPath(), icon.getItemBuilder(),
                                        quickBuySlot);

                                this.readAction(shopConfig, game, item, qIcon,
                                        shopConfig.getString(contentsPath + ".Action"), contentsPath);
                                qIcon.getRequirement().addAll(
                                        this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                                items.put(s, qIcon);
                                quickBuySlot++;
                            }

                            continue;

                        }

                        this.readAction(shopConfig, game, item, icon, shopConfig.getString(contentsPath + ".Action"),
                                contentsPath);
                        icon.getRequirement()
                                .addAll(this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                        slot.forEach(s -> items.put(s, icon));

                    }

                }

                ShopInventory shopInventory = new ShopInventory(arenaType, inventoryName, size, items);

                inv.put(menu, shopInventory);

            }

            ShopInventory mainInventory = inv.get(mainMenu);

            if (mainInventory == null) {

                Entry<String, ShopInventory> entry = inv.entrySet().iterator().next();
                String key = entry.getKey();
                mainInventory = entry.getValue();

                this.sendShopError("&cPath: ArenaType." + arenaType + ".MainMenu",
                        "&cCouldn't find main menu name [ " + mainMenu + " ]", "&cIt's not defined in Menu path.",
                        "&cReplaced it with " + key);

            }

            inv.put("MAIN_INVENTORY", mainInventory);

        }

        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&aLoading shop path " + arenaType + " for game " + game.getName()));

        ShopPath shopPath = new ShopPath(inv, playerUpgrade);
        game.setShopPath(shopPath);

    }

    public void loadShopConfiguration(Game game) {

        File baseFile = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "shop");

        if (!baseFile.exists()) {
            return;
        }

        if (!baseFile.isDirectory()) {
            return;
        }

        for (File file : Objects.requireNonNull(baseFile.listFiles())) {

            YamlConfiguration shopConfig = YamlConfiguration.loadConfiguration(file);

            if (!shopConfig.contains("ArenaType")) {
                continue;
            }
            if (shopConfig.get("ArenaType." + game.getShopPathName()) == null) {
                continue;
            }

            String arenaType = game.getShopPathName();

            Map<String, TeamUpgrade> playerUpgrade = new HashMap<>();
            String mainMenu = shopConfig.getString("ArenaType." + arenaType + ".MainMenu");
            Map<String, InventoryIcon> definedItem = new HashMap<>();

            String definedMaterialPath = "ArenaType." + arenaType + ".DefinedMaterial";

            if (shopConfig.getConfigurationSection(definedMaterialPath) != null) {

                for (String definedMaterial : shopConfig.getConfigurationSection(definedMaterialPath).getKeys(false)) {

                    String definedPath = definedMaterialPath + "." + definedMaterial;

                    String action = shopConfig.getString(definedPath + ".Action");

                    if ("BUY_UPGRADE".equalsIgnoreCase(action)) {
                        TeamUpgrade teamUpgrade = new TeamUpgrade(this.readTier(shopConfig, game, definedPath, definedPath));
                        InventoryIcon item = new UpgradableIcon(definedMaterial, definedMaterial);
                        item.addAction(new BuyUpgradeAction(definedMaterial));
                        playerUpgrade.put(definedMaterial, teamUpgrade);
                        definedItem.put(definedMaterial, item);
                        continue;
                    }

                    ItemBuilder builder = this.readItemBuilder(shopConfig, definedPath);
                    InventoryIcon item = new InventoryIcon(definedMaterial, builder);

                    this.readAction(shopConfig, game, definedMaterial, item, shopConfig.getString(definedPath + ".Action"),
                            definedPath);

                    definedItem.put(definedMaterial, item);

                }

            }

            Map<String, ShopInventory> inv = new HashMap<>();

            if (shopConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu") != null) {

                for (String menu : shopConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu")
                        .getKeys(false)) {

                    String pathMenu = "ArenaType." + arenaType + ".Menu." + menu;
                    int size = shopConfig.getInt(pathMenu + ".InventorySize");
                    size = (size <= 0) ? 54 : (((size % 9) != 0) ? 54 : size);
                    String inventoryName = shopConfig.getString(pathMenu + ".InventoryName");
                    Map<Integer, InventoryIcon> items = new HashMap<>();

                    if (shopConfig.getConfigurationSection(pathMenu + ".Contents") != null) {

                        for (String item : shopConfig.getConfigurationSection(pathMenu + ".Contents").getKeys(false)) {

                            String contentsPath = pathMenu + ".Contents." + item;

                            Object slotPath = shopConfig.get(contentsPath + ".Slot");
                            List<Integer> slot = Utils.getListOfIntegerFromObject(slotPath);
                            boolean isDefined = (shopConfig.getString(contentsPath + ".DefinedMaterial") != null)
                                    && definedItem.containsKey(shopConfig.getString(contentsPath + ".DefinedMaterial"));

                            if (isDefined) {

                                String action = shopConfig.getString(contentsPath + ".Action");

                                if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                                    TeamUpgrade teamUpgrade = new TeamUpgrade(
                                            this.readTier(shopConfig, game, item, contentsPath));
                                    InventoryIcon icon = new UpgradableIcon(menu + "_" + item, item);
                                    icon.addAction(new BuyUpgradeAction(item));
                                    playerUpgrade.put(item, teamUpgrade);
                                    icon.getRequirement().addAll(this.readRequirement(shopConfig, game, arenaType,
                                            contentsPath + ".Requirement"));
                                    slot.forEach(s -> items.put(s, icon));
                                    continue;
                                }

                                InventoryIcon icon = definedItem
                                        .get(shopConfig.getString(contentsPath + ".DefinedMaterial"));

                                if (shopConfig.getBoolean(contentsPath + ".EmptyQuickBuySlot")) {

                                    int quickBuySlot = 0;

                                    for (Integer s : slot) {
                                        QuickBuyIcon qIcon = new QuickBuyIcon(icon.getPath(), icon.getItemBuilder(),
                                                quickBuySlot);

                                        this.readAction(shopConfig, game, item, qIcon,
                                                shopConfig.getString(contentsPath + ".Action"), contentsPath);
                                        qIcon.getRequirement().addAll(this.readRequirement(shopConfig, game, arenaType,
                                                contentsPath + ".Requirement"));
                                        items.put(s, qIcon);
                                        quickBuySlot++;
                                    }

                                    continue;

                                }

                                this.readAction(shopConfig, game, item, icon, shopConfig.getString(contentsPath + ".Action"),
                                        contentsPath);
                                icon.getRequirement().addAll(
                                        this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                                slot.forEach(s -> items.put(s, icon));
                                continue;
                            }

                            String action = shopConfig.getString(contentsPath + ".Action");
                            // String replaceWith = shopConfig.getString(contentsPath + ".ReplaceIfActive");

                            if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                                TeamUpgrade teamUpgrade = new TeamUpgrade(
                                        this.readTier(shopConfig, game, item, contentsPath));
                                InventoryIcon icon = new UpgradableIcon(item, item);
                                icon.addAction(new BuyUpgradeAction(item));
                                playerUpgrade.put(item, teamUpgrade);
                                icon.getRequirement().addAll(
                                        this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                                slot.forEach(s -> items.put(s, icon));
                                continue;
                            }

                            ItemBuilder builder = this.readItemBuilder(shopConfig, contentsPath);
                            InventoryIcon icon = new InventoryIcon(menu + "_" + item, builder);

                            if (shopConfig.getBoolean(contentsPath + ".EmptyQuickBuySlot")) {

                                int quickBuySlot = 0;

                                for (Integer s : slot) {
                                    QuickBuyIcon qIcon = new QuickBuyIcon(icon.getPath(), icon.getItemBuilder(),
                                            quickBuySlot);

                                    this.readAction(shopConfig, game, item, qIcon,
                                            shopConfig.getString(contentsPath + ".Action"), contentsPath);
                                    qIcon.getRequirement().addAll(this.readRequirement(shopConfig, game, arenaType,
                                            contentsPath + ".Requirement"));
                                    items.put(s, qIcon);
                                    quickBuySlot++;
                                }

                                continue;

                            }

                            this.readAction(shopConfig, game, item, icon, shopConfig.getString(contentsPath + ".Action"),
                                    contentsPath);
                            icon.getRequirement().addAll(
                                    this.readRequirement(shopConfig, game, arenaType, contentsPath + ".Requirement"));
                            slot.forEach(s -> items.put(s, icon));

                        }

                    }

                    ShopInventory shopInventory = new ShopInventory(arenaType, inventoryName, size, items);

                    inv.put(menu, shopInventory);

                }

                ShopInventory mainInventory = inv.get(mainMenu);

                if (mainInventory == null) {

                    Entry<String, ShopInventory> entry = inv.entrySet().iterator().next();
                    String key = entry.getKey();
                    mainInventory = entry.getValue();

                    this.sendShopError("&cPath: ArenaType." + arenaType + ".MainMenu",
                            "&cCouldn't find main menu name [ " + mainMenu + " ]", "&cIt's not defined in Menu path.",
                            "&cReplaced it with inventory " + key);

                }

                inv.put("MAIN_INVENTORY", mainInventory);

            }

            Bukkit.getConsoleSender()
                    .sendMessage(Utils.translate("&aLoading shop path " + arenaType + " for game " + game.getName()));

            ShopPath shopPath = new ShopPath(inv, playerUpgrade);

            game.setShopPath(shopPath);
            break;

        }

    }

    @SuppressWarnings("deprecation")
    public Map<Enchantment, Integer> getEnchants(String string) {

        HashMap<Enchantment, Integer> enchants = new HashMap<>();

        if (string.contains(";")) {

            String[] splitted = string.split(";");

            for (String s : splitted) {

                String[] split = s.split(":");

                Enchantment enchant = Enchantment.getByName(split[0]);
                int value = Integer.parseInt(split[1]);

                enchants.put(enchant, value);

            }

            return enchants;
        }

        String[] splitted = string.split(":");
        Enchantment enchant = Enchantment.getByName(splitted[0]);
        int value = Integer.parseInt(splitted[1]);
        enchants.put(enchant, value);

        return enchants;

    }

    public ItemBuilder readItemBuilder(FileConfiguration shopConfig, String path) {

        String material = shopConfig.getString(path + ".Material");
        String name = shopConfig.getString(path + ".Name");
        int data = shopConfig.getInt(path + ".Data");
        int amount = shopConfig.getInt(path + ".Amount", 1);
        boolean isGlowing = shopConfig.getBoolean(path + ".Glowing");
        List<String> lore = shopConfig.getStringList(path + ".Lore");

        if (material == null) {
            this.sendShopError("&cPath: " + path + ".Material",
                    "&cMaterial " + material + (data != 0 ? " with data " + data : "") + " does not exist.",
                    "&cReplaced it with STONE");
            material = "STONE";
        }

        XMaterial m = XMaterial.matchXMaterial(material.toUpperCase(), (byte) data);

        if (m == null) {
            this.sendShopError("&cPath: " + path + ".Material",
                    "&cMaterial " + material + (data != 0 ? " with data " + data : "") + " does not exist.",
                    "&cReplaced it with STONE");
            m = this.STONE;
        }

        Material mat = m.parseMaterial();

        if (mat == null) {
            this.sendShopError("&cPath: " + path + ".Material",
                    "&cMaterial " + material + (data != 0 ? " with data " + data : "") + " does not exist.",
                    "&cReplaced it with STONE");
            mat = Material.STONE;
        }

        ItemBuilder builder = new ItemBuilder(m, amount);

        if (m.parseMaterial() == Material.POTION) {
            builder = new PotionBuilder(amount).addEffectType(PotionEffectType.ABSORPTION, 20 * 10, 0);
        }

        if (name != null) {
            builder.setDisplayName(name);
        }

        if (lore != null) {
            builder.setLore(lore);
        }

        if (isGlowing) {
            builder.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
        }

        return builder;

    }

    public List<String> readUpgrades(FileConfiguration shopConfig, String upgradesPath) {

        if (!shopConfig.contains(upgradesPath + ".Receive.UPGRADE")) {
            return Collections.emptyList();
        }

        return new ArrayList<>(shopConfig.getConfigurationSection(upgradesPath + ".Receive.UPGRADE").getKeys(false));
    }

    public List<String> readUpgrades(FileConfiguration shopConfig, String arenaType, String path) {

        if (shopConfig.getStringList(path + ".Receive.UPGRADE") == null) {
            return Collections.emptyList();
        }
        if (shopConfig.getStringList(path + ".Receive.UPGRADE").isEmpty()) {
            return Collections.emptyList();
        }

        List<String> upgrades = new ArrayList<>();
        UpgradePath upgradePath = ManagerHandler.getGameManager().getUpgradePath().get(arenaType);
        Map<String, TeamUpgrade> map = upgradePath.getUpgrades();
        StringBuilder builder = new StringBuilder();
        map.keySet().forEach(s -> builder.append(s).append(" "));

        for (String upgradeName : shopConfig.getStringList(path + ".Receive.UPGRADE")) {

            if (!map.containsKey(upgradeName)) {
                String isEmpty = builder.toString().isEmpty() ? (" Available upgrades " + builder) : "";
                throw new NullPointerException("[ " + path + "] Null upgrade name " + upgradeName
                        + ". You didn't defined an upgrade with name " + upgradeName + isEmpty);
            }

            upgrades.add(upgradeName);

        }

        return upgrades;
    }

    public List<RewardItem> readRewards(FileConfiguration shopConfig, String contentsPath) {

        if (!shopConfig.contains(contentsPath + ".Receive.ITEM")) {
            return Collections.emptyList();
        }

        List<RewardItem> itemsList = new ArrayList<>();

        for (String reward : shopConfig.getConfigurationSection(contentsPath + ".Receive.ITEM").getKeys(false)) {

            String rewardPath = contentsPath + ".Receive.ITEM." + reward;
            String rMaterial = shopConfig.getString(rewardPath + ".Material");
            String rName = shopConfig.getString(rewardPath + ".Name");
            int rData = shopConfig.getInt(rewardPath + ".Data");
            int rAmount = shopConfig.getInt(rewardPath + ".Amount");
            List<String> rLore = shopConfig.getStringList(rewardPath + ".Lore");
            boolean rEnchanted = shopConfig.getBoolean(rewardPath + ".Glowing");
            boolean isPermanent = shopConfig.getBoolean(rewardPath + ".Permanent");
            boolean isPermanentItem = shopConfig.getBoolean(rewardPath + ".PermanentItem");

            if (rMaterial == null) {
                this.sendShopError("&cPath: " + contentsPath + ".Receive.ITEM." + reward + ".Material",
                        "&cMaterial name doesn't exist ");
                rMaterial = "STONE";
                continue;
            }

            String enchant = shopConfig.getString(contentsPath + ".Receive.ITEM." + reward + ".Enchantment");

            if (rMaterial.endsWith("_ARMOR")) {

                String armorType = rMaterial.split("_")[0].toUpperCase();

                if (!("CHAINMAIL".equals(armorType) || "IRON".equals(armorType) || "GOLDEN".equals(armorType)
                        || "LEATHER".equals(armorType) || "DIAMOND".equals(armorType))) {
                    continue;
                }

                Material helmetType = XMaterial.valueOf(armorType + "_HELMET").parseMaterial();
                Material chestplateType = XMaterial.valueOf(armorType + "_CHESTPLATE").parseMaterial();
                Material leggingsType = XMaterial.valueOf(armorType + "_LEGGINGS").parseMaterial();
                Material bootsType = XMaterial.valueOf(armorType + "_BOOTS").parseMaterial();

                ItemBuilder helmet = new ItemBuilder(helmetType);
                ItemBuilder chestplate = new ItemBuilder(chestplateType);
                ItemBuilder leggings = new ItemBuilder(leggingsType);
                ItemBuilder boots = new ItemBuilder(bootsType);

                if (rName != null) {
                    helmet.setDisplayName(rName);
                    chestplate.setDisplayName(rName);
                    leggings.setDisplayName(rName);
                    boots.setDisplayName(rName);
                }

                if (rLore != null) {
                    helmet.setLore(rLore);
                    chestplate.setLore(rLore);
                    leggings.setLore(rLore);
                    boots.setLore(rLore);
                }

                if (rEnchanted) {
                    helmet.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
                    chestplate.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
                    leggings.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
                    boots.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
                }

                if (enchant != null) {

                    helmet.addEnchants(this.getEnchants(enchant));
                    chestplate.addEnchants(this.getEnchants(enchant));
                    leggings.addEnchants(this.getEnchants(enchant));
                    boots.addEnchants(this.getEnchants(enchant));

                }

                itemsList.add(new RewardItem(helmet, isPermanent || isPermanentItem));
                itemsList.add(new RewardItem(chestplate, isPermanent || isPermanentItem));
                itemsList.add(new RewardItem(leggings, isPermanent || isPermanentItem));
                itemsList.add(new RewardItem(boots, isPermanent || isPermanentItem));

                return itemsList;
            }

            XMaterial m = XMaterial.matchXMaterial(rMaterial, (byte) rData);

            if (m == null) {
                this.sendShopError("&cPath: " + contentsPath + ".Receive.ITEM." + reward + ".Material",
                        "&cMaterial " + rMaterial + (rData != 0 ? " with data " + rData : "") + " does not exist.",
                        "&cReplaced it with ");
                m = this.STONE;
            }

            ItemBuilder rBuilder = new ItemBuilder(m, rAmount);

            if (m == XMaterial.POTION) {
                rBuilder = this.readPotionEffect(shopConfig, contentsPath + ".Receive.ITEM." + reward);
            }

            if (rName != null) {
                rBuilder.setDisplayName(rName);
            }

            if (rLore != null) {
                rBuilder.setLore(rLore);
            }

            if (rEnchanted) {
                rBuilder.glowingItem(ManagerHandler.getVersionManager().getVersionWrapper());
            }

            if (enchant != null) {

                rBuilder.addEnchants(this.getEnchants(enchant));

            }

            itemsList.add(new RewardItem(rBuilder, isPermanent || isPermanentItem));
        }

        return itemsList;
    }

    public PotionBuilder readPotionEffect(FileConfiguration shopConfig, String path) {

        PotionBuilder potionBuilder = new PotionBuilder(1);
        String potionEffect = shopConfig.getString(path + ".PotionEffect");

        if (potionEffect == null) {
            return potionBuilder;
        }

        String[] v = potionEffect.split(";");

        if (v.length == 0) {
            return potionBuilder;
        }

        for (String s : v) {

            if (s.isEmpty()) {
                continue;
            }

            String[] split = s.split("#");

            if (split.length == 0) {
//			sendShopError("&cPath: " + path + ".PotionEffect", "&cReplaced it with STONE");
                continue;
            }

            if (!Utils.isInteger(split[1])) {
                this.sendShopError("&cPath: " + path + ".PotionEffect", "&c" + split[1]
                        + " must be a number . For example INVISIBILITY:0#45 , where INVISIBILITY is the potion name, 0 is the amplifier, and the number 45 represents the seconds");
                continue;
            }

            String[] comma = split[0].split(":");

            if (comma.length == 0) {
                this.sendShopError("&cPath: " + path + ".PotionEffect",
                        "&cMissing the potion type and amplifier . For example INVISIBILITY:0#45 , where INVISIBILITY is the potion name, 0 is the amplifier, and the number 45 represents the seconds");
                continue;
            }

            if (!Utils.isInteger(comma[1])) {
                this.sendShopError("&cPath: " + path + ".PotionEffect", "&c" + comma[1]
                        + " must be a number . For example INVISIBILITY:0#45 , where INVISIBILITY is the potion name, 0 is the amplifier, and the number 45 represents the seconds");
                continue;
            }

            int seconds = Integer.parseInt(split[1]);
            PotionEffectType type = PotionEffectType.getByName(comma[0]);
            int amplifier = Integer.parseInt(comma[1]);

            potionBuilder.addEffectType(type, 20 * seconds, amplifier);
        }

        return potionBuilder;
    }

    public RequirementBuilder readRequirement(FileConfiguration shopConfig, String path) {

        RequirementBuilder builder = new RequirementBuilder();

        if (shopConfig.getString(path + ".Material") != null) {
            String s = shopConfig.getString(path + ".Material");
            int data = 0;

            if (shopConfig.get(path + ".Data") != null) {
                data = shopConfig.getInt(path + ".Data");
                builder.withDisplayData(data);
            }

            XMaterial m = XMaterial.matchXMaterial(s, (byte) data);

            if (m == null) {
                this.sendShopError("&cPath: " + path + ".Material",
                        "&cMaterial " + s + (data != 0 ? " with data " + data : "") + " does not exist.",
                        "&cReplaced it with STONE");
                m = this.STONE;
            }

            builder.withDisplayMaterial(m.parseMaterial());
        }

        if (shopConfig.getStringList(path + ".Lore") != null) {
            builder.withDisplayLore(shopConfig.getStringList(path + ".Lore"));
        }

        if (shopConfig.getInt(path + ".Amount") > 0) {
            builder.withDisplayAmount(shopConfig.getInt(path + ".Amount"));
        }

        if (shopConfig.getBoolean(path + ".Glowing")) {
            builder.glowing();
        }

        if (shopConfig.get(path + ".Price-Material") != null) {
            String s = shopConfig.getString(path + ".Price-Material");
            XMaterial m = XMaterial.matchXMaterial(s, (byte) 0);

            if (m == null) {
                this.sendShopError("&cPath: " + path + ".Price-Material", "&cMaterial " + s + " does not exist.",
                        "&cReplaced it with STONE");
                m = this.STONE;
            }

            // TODO: adaug referinta in mesaje la materialele mai jos de 1.13 si pt 1.13
            // (link-uri)
            builder.withPriceMaterial(m.parseMaterial());
        }

        if (shopConfig.getInt(path + ".Price") > 0) {
            builder.withPrice(shopConfig.getInt(path + ".Price"));
        }

        return builder;
    }

    public List<Requirement> readRequirement(FileConfiguration shopConfig, Game game, String arenaType,
                                             String reqPath) {

        if (shopConfig.get(reqPath) == null) {
            return Collections.emptyList();
        }

        List<Requirement> requirementList = new ArrayList<>();

        for (String reqName : shopConfig.getConfigurationSection(reqPath).getKeys(false)) {
            String path = reqPath + "." + reqName;
            String input = shopConfig.getString(path + ".Type");

            if (input == null) {
                continue;
            }

            if ("is activated team upgrade".equalsIgnoreCase(input)) {

                String upgradeName = shopConfig.getString(path + ".UpgradeName");

                if (upgradeName == null) {
                    this.sendShopError("&cPath: " + path + ".UpgradeName",
                            "&cMissing upgrade name for requirement is activated team upgrade");
                    continue;
                }

                requirementList.add(new TUpgradeRequirement(upgradeName, this.readRequirement(shopConfig, path)));
                continue;
            }

            if ("is activated player upgrade".equalsIgnoreCase(input)) {

                String upgradeName = shopConfig.getString(path + ".UpgradeName");

                if (upgradeName == null) {
                    this.sendShopError("&cPath: " + path + ".UpgradeName",
                            "&cMissing upgrade name for requirement is activated player upgrade");
                    continue;
                }

                requirementList.add(new PUpgradeRequirement(upgradeName, this.readRequirement(shopConfig, path)));
                continue;
            }

            if ("has permission".equalsIgnoreCase(input)) {
                String permission = shopConfig.getString(path + ".Permission");

                if (permission == null) {
                    this.sendShopError("&cPath: " + path + ".Permission",
                            "&cMissing permission name for requirement has permission");
                    continue;
                }

                requirementList.add(new PermissionRequirement(permission, this.readRequirement(shopConfig, path)));

//                continue;
            }

//            if ("is tier upgrade".equalsIgnoreCase(input)) {
//
//                // TODO: Read tier and upgrade name
//
//            }
//
//            if ("is bed broken".equalsIgnoreCase(input)) {
//
//                // TODO: Instance of class
//
//            }
//
//            if ("has item".equalsIgnoreCase(input)) {
//
//                // TODO: Read item name , data, amount
//
//            }

            // TODO: Add view requirement
        }

        return requirementList;
    }

    public void sendShopError(String... message) {

        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Shop Configuration Warning --------------"));
        Bukkit.getConsoleSender().sendMessage("");

        for (String s : message) {
            Bukkit.getConsoleSender().sendMessage(Utils.translate(s));
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Shop Configuration Warning --------------"));

    }

    public List<UpgradeTier> readTier(FileConfiguration shopConfig, Game game, String upgradeName, String path) {

        Map<String, UpgradeTier> upgradeTierList = new HashMap<>();

        boolean isFirstTier = true;

        for (String tier : shopConfig.getConfigurationSection(path + ".Tier").getKeys(false)) {

            Material displayMaterial = XMaterial.STONE.parseMaterial();
            Material priceMaterial = XMaterial.DIAMOND.parseMaterial();
            List<IUpgrade> upgrades = new ArrayList<>();
            List<String> increaseTier = new ArrayList<>();
            List<String> decreaseTier = new ArrayList<>();
            List<Requirement> requirements = new ArrayList<>();
            boolean glowing = false;
            boolean decreaseTierDeath;
            boolean respawnItems;
            int data = 0;
            int price = 99;
            int amount = 1;
            String name = "MISSING NAME";
            List<String> lore = new ArrayList<>();

            String tierPath = path + ".Tier." + tier;

            if (shopConfig.getString(tierPath + ".InheritsTier") != null) {

                boolean use = true;
                String inhTier = shopConfig.getString(tierPath + ".InheritsTier");

                if (inhTier.isEmpty()) {
                    use = false;
                }

                if (isFirstTier && use) {
                    this.sendShopError("&cPath: " + tierPath + ".InheritsTier", "&cFirst tier can't inherits other tier");
                    use = false;
                }

                if (inhTier.equals(tier) && use) {
                    this.sendShopError("&cPath: " + tierPath + ".InheritsTier", "&cFirst tier can't inherits itself");
                    use = false;
                }

                if (!upgradeTierList.containsKey(inhTier) && use) {
                    this.sendShopError("&cPath: " + tierPath + ".InheritsTier",
                            "&cFirst tier can't inherits lower tier or non existing tier");
                    use = false;
                }

                if (use) {
                    UpgradeTier upgTier = upgradeTierList.get(inhTier);

                    priceMaterial = upgTier.getPriceMaterial();
                    displayMaterial = upgTier.getDisplayMaterial();
                    data = upgTier.getMaterialData();
                    price = upgTier.getPrice();
                    amount = upgTier.getAmount();
                    name = upgTier.getName();
                    lore = upgTier.getLore();
                    increaseTier = upgTier.getIncreaseTier();
                    decreaseTier = upgTier.getDecreaseTier();
                    glowing = upgTier.isGlowing();
                    upgrades = upgTier.getUpgrades();
                    requirements = upgTier.getRequirements();
                    decreaseTierDeath = upgTier.isDecreaseTierOnDeath();
                    respawnItems = upgTier.isRespawnItems();

                }

            }

            if (shopConfig.getString(tierPath + ".Name") != null) {
                name = shopConfig.getString(tierPath + ".Name");
            }

            if (shopConfig.getStringList(tierPath + ".Lore") != null) {
                lore = shopConfig.getStringList(tierPath + ".Lore");
            }

            if (shopConfig.get(tierPath + ".Data") != null) {
                data = shopConfig.getInt(tierPath + ".Data");
            }

            if (shopConfig.getString(tierPath + ".Material") != null) {

                String m = shopConfig.getString(tierPath + ".Material");

                if (m == null) {
                    this.sendShopError("&cPath: " + tierPath + ".Material",
                            "&cMaterial " + m + (data != 0 ? " with data " + data : "") + " does not exist.",
                            "&cReplaced it with STONE");
                    m = XMaterial.STONE.name();
                }

                XMaterial x = XMaterial.matchXMaterial(m, (byte) data);

                displayMaterial = x.parseMaterial();
            }

            if (shopConfig.get(tierPath + ".Price") != null) {
                price = shopConfig.getInt(tierPath + ".Price");
            }

            if (shopConfig.get(tierPath + ".Amount") != null) {
                amount = shopConfig.getInt(tierPath + ".Amount");
            }

            if (shopConfig.getString(tierPath + ".Price-Material") != null) {

                String m = shopConfig.getString(tierPath + ".Price-Material");

                if (m == null) {
                    this.sendShopError("&cPath: " + tierPath + ".Price-Material", "&cMaterial " + m + " does not exist.",
                            "&cReplaced it with STONE");
                    m = XMaterial.STONE.name();
                }

                XMaterial x = XMaterial.matchXMaterial(m, (byte) 0);

                priceMaterial = x.parseMaterial();
            }

            if (shopConfig.getBoolean(tierPath + ".Glowing")) {
                glowing = true;
            }

            if (shopConfig.get(path + ".Tier." + tier + ".IncreaseTier") != null) {
                increaseTier = shopConfig.getStringList(path + ".Tier." + tier + ".IncreaseTier");
            }

            if (shopConfig.get(path + ".Tier." + tier + ".DecreaseTier") != null) {
                decreaseTier = shopConfig.getStringList(path + ".Tier." + tier + ".DecreaseTier");
            }

            if (shopConfig.get(path + ".Tier." + tier + ".Requirement") != null) {
                requirements = this.readRequirement(shopConfig, game, "", tierPath + ".Requirement");
            }

            decreaseTierDeath = shopConfig.getBoolean(path + ".Tier." + tier + ".DecreaseTierOnDeath");
            respawnItems = shopConfig.getBoolean(path + ".Tier." + tier + ".ReceiveItemsOnRespawn");

            List<RewardItem> reward = this.readRewards(shopConfig, tierPath);

            UpgradeTier upgradeTier = new UpgradeTier(game, upgradeName, increaseTier, decreaseTier, upgrades, reward,
                    displayMaterial, data, amount, priceMaterial, price, name, lore, glowing);
            upgradeTier.setDecreaseTierOnDeath(decreaseTierDeath);
            upgradeTier.setRespawnItems(respawnItems);
            upgradeTier.getRequirements().addAll(requirements);
            upgradeTierList.put(tier, upgradeTier);
            isFirstTier = false;

        }

        return new ArrayList<>(upgradeTierList.values());

    }

    public void readAction(FileConfiguration shopConfig, Game game, String itemName, InventoryIcon item, String action,
                           String path) {

        if (action == null) {
            return;
        }

        if ("OPEN_MENU".equalsIgnoreCase(action)) {

            String menuName = shopConfig.getString(path + ".MenuName");
            boolean use = true;

            if ((menuName == null) || menuName.isEmpty()) {
                this.sendShopError("&cPath: " + path + ".MenuName",
                        "&cCouldn't find main menu name for action " + action.toUpperCase(), "&cIt's not defined .");
                use = false;
            }

            if (use) {
                item.addAction(new OpenGUIAction(menuName));
            }

        }

        if ("BUY_ITEM".equalsIgnoreCase(action)) {

            String materialPrice = shopConfig.getString(path + ".Price-Material");
            int priceAmount = shopConfig.getInt(path + ".Price");

            XMaterial m = XMaterial.matchXMaterial(materialPrice, (byte) 0);

            if (m == null) {
                this.sendShopError("&cPath: " + path + ".Price-Material", "&cMaterial " + materialPrice + " does not exist.",
                        "&cReplaced it with DIAMOND");
                m = XMaterial.DIAMOND;
            }

            BuyItemAction itemAction = new BuyItemAction();
            itemAction.setPriceMaterial(m.parseMaterial());
            itemAction.setPrice(priceAmount);
            itemAction.setReward(this.readRewards(shopConfig, path));
            itemAction.setUpgrades(this.readUpgrades(shopConfig, path));

            item.addAction(itemAction);

        }

    }

    public XMaterial getSTONE() {
        return this.STONE;
    }
}
