package ro.marius.bedwars.upgradeconfiguration.upgradeinventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.icon.InventoryIcon;
import ro.marius.bedwars.team.upgrade.IUpgrade;
import ro.marius.bedwars.team.upgrade.PotionEffect;
import ro.marius.bedwars.team.upgrade.enemy.EnemyTrapEffect;
import ro.marius.bedwars.team.upgrade.enemy.trapinformation.*;
import ro.marius.bedwars.team.upgrade.player.PlayerEnchantment;
import ro.marius.bedwars.team.upgrade.player.PlayerPotionEffect;
import ro.marius.bedwars.team.upgrade.team.GeneratorUpgrade;
import ro.marius.bedwars.team.upgrade.team.TeamEnchantment;
import ro.marius.bedwars.team.upgrade.team.TeamPotionEffect;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.Sounds;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class UpgradeConfiguration {

    private final File file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "upgrade", "upgrades.yml");
//	private YamlConfiguration upgradeConfig = YamlConfiguration.loadConfiguration(file);

    public void loadDefaultConfiguration(Game game) {

        InputStream stream = BedWarsPlugin.getInstance().getResource("upgrades.yml");

        YamlConfiguration upgradeConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));

        String arenaType = "DEFAULT";

        if (!upgradeConfig.contains("ArenaType")) {
            return;
        }
        if (upgradeConfig.get("ArenaType." + arenaType) == null) {
            return;
        }

        int timeIron, timeGold;
        int dropAmountIron, dropAmountGold;

        timeIron = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.IRON.SpawnTime");
        dropAmountIron = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.IRON.Amount");
        timeGold = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.GOLD.SpawnTime");
        dropAmountGold = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.GOLD.Amount");

        if (upgradeConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu") == null) {
            return;
        }

        String path = "ArenaType." + arenaType + ".Menu";
        String mainMenu = upgradeConfig.getString("ArenaType." + arenaType + ".MainMenu");

        Map<String, TeamUpgrade> upgrades = new HashMap<>();
        Map<String, UpgradeInventory> upgradeInventoryMap = new HashMap<>();

        for (String menu : upgradeConfig.getConfigurationSection(path).getKeys(false)) {

            String pathMenu = "ArenaType." + arenaType + ".Menu." + menu;
            int size = upgradeConfig.getInt(pathMenu + ".InventorySize");
            size = (size <= 0) ? 54 : (((size % 9) != 0) ? 54 : size);
            String inventoryName = upgradeConfig.getString(pathMenu + ".InventoryName");
            UpgradeInventory upgradeInventory = new UpgradeInventory(game, inventoryName, size);

            if (upgradeConfig.getConfigurationSection(pathMenu + ".Contents") != null) {

                for (String item : upgradeConfig.getConfigurationSection(pathMenu + ".Contents").getKeys(false)) {

                    String contentsPath = pathMenu + ".Contents." + item;
                    Object slotPath = upgradeConfig.get(contentsPath + ".Slot");

                    if (slotPath == null) {
                        this.sendUpgradeError("&cPath: " + contentsPath, "&cMissing slot path [.Slot]");
                        continue;
                    }

                    List<Integer> slotList = Utils.getListOfIntegerFromObject(slotPath);

                    String action = upgradeConfig.getString(contentsPath + ".Action");
                    String replaceWith = upgradeConfig.getString(contentsPath + ".ReplaceIfActive");

                    if ((action == null) && (upgradeConfig.get(contentsPath + ".Tier") != null)) {

                        TeamUpgrade teamUpgrade = new TeamUpgrade(
                                this.readTier(upgradeConfig, game, item, menu, item, contentsPath));
                        InventoryIcon inventoryIcon = new UpgradableIcon(item, item);
                        upgrades.put(item, teamUpgrade);
                        slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                        continue;
                    }
//						

                    if ((action == null) && (replaceWith != null) && !replaceWith.isEmpty()) {
                        ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                        InventoryIcon inventoryIcon = new ReplaceableIcon(item, replaceWith, builder);
                        slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));
                        continue;
                    }

                    if (action == null) {
                        ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                        InventoryIcon inventoryIcon = new InventoryIcon(item, builder);
                        slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));
                        continue;
                    }

                    if ("OPEN_MENU".equalsIgnoreCase(action)) {

                        String menuName = upgradeConfig.getString(contentsPath + ".MenuName");
                        boolean use = true;

                        if ((menuName == null) || menuName.isEmpty()) {
                            this.sendUpgradeError("&cPath: " + contentsPath + ".MenuName",
                                    "&cCouldn't find main menu name for action " + action.toUpperCase(),
                                    "&cIt's not defined .");
                            use = false;
                        }

                        ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                        InventoryIcon inventoryIcon = new InventoryIcon(item, builder);

                        if (use) {
                            inventoryIcon.addAction(new OpenGUIAction(menuName));
                        }

                        slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                    }

                    if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                        TeamUpgrade teamUpgrade = new TeamUpgrade(
                                this.readTier(upgradeConfig, game, item, menu, item, contentsPath));
                        InventoryIcon inventoryIcon = new UpgradableIcon(item, item);
                        inventoryIcon.addAction(new BuyUpgradeAction(item));
                        upgrades.put(item, teamUpgrade);
                        slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                    }

                }

            }
            upgradeInventoryMap.put(menu, upgradeInventory);
        }

        UpgradeInventory mainInventory = upgradeInventoryMap.get(mainMenu);

        if (mainInventory == null) {

            Entry<String, UpgradeInventory> entry = upgradeInventoryMap.entrySet().iterator().next();
            String key = entry.getKey();
            mainInventory = entry.getValue();

            this.sendUpgradeError("&cPath: ArenaType." + arenaType + ".MainMenu",
                    "&cCouldn't find main menu name [ " + mainMenu + " ]", "&cIt's not defined in Menu path.",
                    "&cReplaced it with menu " + key);

        }

        upgradeInventoryMap.put("MAIN_INVENTORY", mainInventory);

        // TODO Sa nu mai adaug MAIN_INVENTORY si sa-l adaug pe cel din map

        // TODO: Scanez fiecare UpgradePath si verific daca exista numele la
        // upgrade-uri,upgrade-urile definite, inventare definite

        UpgradePath upg = new UpgradePath(upgrades, upgradeInventoryMap);
        upg.setIronTime((timeIron <= 0) ? 30 : timeIron);
        upg.setIronAmount((dropAmountIron <= 0) ? 1 : dropAmountIron);
        upg.setGoldTime((timeGold <= 0) ? 70 : timeGold);
        upg.setGoldAmount((dropAmountGold <= 0) ? 1 : dropAmountGold);

        game.setUpgradePath(upg);

    }

    public void loadUpgrades(Game game) {

        String arenaType = game.getUpgradePathName();

        File baseFile = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "upgrade");

        if (!baseFile.isDirectory()) {
            return;
        }

        for (File file : baseFile.listFiles()) {

            YamlConfiguration upgradeConfig = YamlConfiguration.loadConfiguration(file);

            if (!upgradeConfig.contains("ArenaType")) {
                continue;
            }
            if (upgradeConfig.get("ArenaType." + arenaType) == null) {
                continue;
            }

//			Map<String, UpgradePath> upgradePath = new HashMap<>();

            int timeIron, timeGold;
            int dropAmountIron, dropAmountGold;

            timeIron = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.IRON.SpawnTime");
            dropAmountIron = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.IRON.Amount");
            timeGold = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.GOLD.SpawnTime");
            dropAmountGold = upgradeConfig.getInt("ArenaType." + arenaType + ".DefaultGenerator.GOLD.Amount");

            if (upgradeConfig.getConfigurationSection("ArenaType." + arenaType + ".Menu") == null) {
                continue;
            }

            String path = "ArenaType." + arenaType + ".Menu";
            String mainMenu = upgradeConfig.getString("ArenaType." + arenaType + ".MainMenu");

            Map<String, TeamUpgrade> upgrades = new HashMap<>();
            Map<String, UpgradeInventory> upgradeInventoryMap = new HashMap<>();

            for (String menu : upgradeConfig.getConfigurationSection(path).getKeys(false)) {

                String pathMenu = "ArenaType." + arenaType + ".Menu." + menu;
                int size = upgradeConfig.getInt(pathMenu + ".InventorySize");
                size = (size <= 0) ? 54 : (((size % 9) != 0) ? 54 : size);
                String inventoryName = upgradeConfig.getString(pathMenu + ".InventoryName");
                UpgradeInventory upgradeInventory = new UpgradeInventory(game, inventoryName, size);

                if (upgradeConfig.getConfigurationSection(pathMenu + ".Contents") != null) {

                    for (String item : upgradeConfig.getConfigurationSection(pathMenu + ".Contents").getKeys(false)) {

                        String contentsPath = pathMenu + ".Contents." + item;
                        Object slotPath = upgradeConfig.get(contentsPath + ".Slot");

                        if (slotPath == null) {
                            this.sendUpgradeError("&cPath: " + contentsPath, "&cMissing slot path [.Slot]");
                            continue;
                        }

                        List<Integer> slotList = Utils.getListOfIntegerFromObject(slotPath);

                        String action = upgradeConfig.getString(contentsPath + ".Action");
                        String replaceWith = upgradeConfig.getString(contentsPath + ".ReplaceIfActive");

                        if ((action == null) && (upgradeConfig.get(contentsPath + ".Tier") != null)) {

                            TeamUpgrade teamUpgrade = new TeamUpgrade(
                                    this.readTier(upgradeConfig, game, item, menu, item, contentsPath));
                            InventoryIcon inventoryIcon = new UpgradableIcon(item, item);
                            upgrades.put(item, teamUpgrade);
                            slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                            continue;
                        }
//						

                        if ((action == null) && (replaceWith != null) && !replaceWith.isEmpty()) {
                            ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                            InventoryIcon inventoryIcon = new ReplaceableIcon(item, replaceWith, builder);
                            slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));
                            continue;
                        }

                        if (action == null) {
                            ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                            InventoryIcon inventoryIcon = new InventoryIcon(item, builder);
                            slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));
                            continue;
                        }

                        if ("OPEN_MENU".equalsIgnoreCase(action)) {

                            String menuName = upgradeConfig.getString(contentsPath + ".MenuName");
                            boolean use = true;

                            if ((menuName == null) || menuName.isEmpty()) {
                                this.sendUpgradeError("&cPath: " + contentsPath + ".MenuName",
                                        "&cCouldn't find main menu name for action " + action.toUpperCase(),
                                        "&cIt's not defined .");
                                use = false;
                            }

                            ItemBuilder builder = this.readItemBuilder(upgradeConfig, contentsPath);
                            InventoryIcon inventoryIcon = new InventoryIcon(item, builder);

                            if (use) {
                                inventoryIcon.addAction(new OpenGUIAction(menuName));
                            }

                            slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                        }

                        if ("BUY_UPGRADE".equalsIgnoreCase(action)) {

                            TeamUpgrade teamUpgrade = new TeamUpgrade(
                                    this.readTier(upgradeConfig, game, item, menu, item, contentsPath));
                            InventoryIcon inventoryIcon = new UpgradableIcon(item, item);
                            inventoryIcon.addAction(new BuyUpgradeAction(item));
                            upgrades.put(item, teamUpgrade);
                            slotList.forEach(slot -> upgradeInventory.getItems().put(slot, inventoryIcon));

                        }

                    }

                }
                upgradeInventoryMap.put(menu, upgradeInventory);
            }

            UpgradeInventory mainInventory = upgradeInventoryMap.get(mainMenu);

            if (mainInventory == null) {

                Entry<String, UpgradeInventory> entry = upgradeInventoryMap.entrySet().iterator().next();
                String key = entry.getKey();
                mainInventory = entry.getValue();

                this.sendUpgradeError("&cPath: ArenaType." + arenaType + ".MainMenu",
                        "&cCouldn't find main menu name [ " + mainMenu + " ]", "&cIt's not defined in Menu path.",
                        "&cReplaced it with menu " + key);

            }

            upgradeInventoryMap.put("MAIN_INVENTORY", mainInventory);

            // TODO Sa nu mai adaug MAIN_INVENTORY si sa-l adaug pe cel din map

            // TODO: Scanez fiecare UpgradePath si verific daca exista numele la
            // upgrade-uri,upgrade-urile definite, inventare definite

            UpgradePath upg = new UpgradePath(upgrades, upgradeInventoryMap);
            upg.setIronTime((timeIron <= 0) ? 30 : timeIron);
            upg.setIronAmount((dropAmountIron <= 0) ? 1 : dropAmountIron);
            upg.setGoldTime((timeGold <= 0) ? 70 : timeGold);
            upg.setGoldAmount((dropAmountGold <= 0) ? 1 : dropAmountGold);

            game.setUpgradePath(upg);

            break;
        }

    }

    public ItemBuilder readItemBuilder(YamlConfiguration upgradeConfig, String path) {

        String material = upgradeConfig.getString(path + ".Material").toUpperCase();
        String name = upgradeConfig.getString(path + ".Name");
        int data = upgradeConfig.getInt(path + ".Data");
        int amount = upgradeConfig.getInt(path + ".Amount");
        boolean isGlowing = upgradeConfig.getBoolean(path + ".Glowing");
        List<String> lore = upgradeConfig.getStringList(path + ".Lore");

        XMaterial m = XMaterial.matchXMaterial(material, (byte) data);

        if (m == null) {
            this.sendUpgradeError("&cPath: " + path + ".Material",
                    "&cMaterial " + material + (data != 0 ? " with data " + data : "") + " does not exist.",
                    "&cReplaced it with STONE");
            m = XMaterial.STONE;
        }

        ItemBuilder builder = new ItemBuilder(m, amount);

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

//	public 

    public List<UpgradeTier> readTier(YamlConfiguration upgradeConfig, Game game, String upgradeName, String inventory,
                                      String item, String path) {

        if (upgradeConfig.get(path + ".Tier") == null) {
            this.sendUpgradeError("&cPath: " + path, "&cMissing tier path");
            return Collections.emptyList();
        }

        Map<String, UpgradeTier> upgradeTierList = new HashMap<>();

        boolean isFirstTier = true;

        for (String tier : upgradeConfig.getConfigurationSection(path + ".Tier").getKeys(false)) {

            Material displayMaterial = XMaterial.STONE.parseMaterial();
            Material priceMaterial = XMaterial.DIAMOND.parseMaterial();
            List<IUpgrade> upgrades = new ArrayList<>();
            List<String> increaseTier = new ArrayList<>();
            List<String> decreaseTier = new ArrayList<>();
            boolean glowing = false;
            boolean decreaseTierDeath = false;
            boolean respawnItems = false;
            int data = 0;
            int price = 99;
            int amount = 1;
            String name = "MISSING NAME";
            List<String> lore = new ArrayList<String>();

            String tierPath = path + ".Tier." + tier;

            if (upgradeConfig.getString(tierPath + ".InheritsTier") != null) {

                boolean use = true;
                String inhTier = upgradeConfig.getString(tierPath + ".InheritsTier");

                if (inhTier.isEmpty()) {
                    use = false;
                }

                if (isFirstTier && use) {
                    this.sendUpgradeError("&cPath: " + tierPath + ".InheritsTier", "&cFirst tier can't inherits other tier");
                    use = false;
                }

                if (inhTier.equals(tier) && use) {
                    this.sendUpgradeError("&cPath: " + tierPath + ".InheritsTier", "&cFirst tier can't inherits itself");
                    use = false;
                }

                if (!upgradeTierList.containsKey(inhTier) && use) {
                    this.sendUpgradeError("&cPath: " + tierPath + ".InheritsTier",
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
                    decreaseTierDeath = upgTier.isDecreaseTierOnDeath();
                    respawnItems = upgTier.isRespawnItems();

                }

            }

            if (upgradeConfig.getString(tierPath + ".Name") != null) {
                name = upgradeConfig.getString(tierPath + ".Name");
            }

            if (upgradeConfig.getStringList(tierPath + ".Lore") != null) {
                lore = upgradeConfig.getStringList(tierPath + ".Lore");
            }

            if (upgradeConfig.get(tierPath + ".Data") != null) {
                data = upgradeConfig.getInt(tierPath + ".Data");
            }

            if (upgradeConfig.getString(tierPath + ".Material") != null) {

                String m = upgradeConfig.getString(tierPath + ".Material").toUpperCase();

                XMaterial x = XMaterial.matchXMaterial(m, (byte) data);

                if (m == null) {
                    this.sendUpgradeError("&cPath: " + tierPath + ".Material",
                            "&cMaterial " + m + (data != 0 ? " with data " + data : "") + " does not exist.",
                            "&cReplaced it with STONE");
                    x = XMaterial.STONE;
                }

                displayMaterial = x.parseMaterial();
            }

            if (upgradeConfig.get(tierPath + ".Price") != null) {
                price = upgradeConfig.getInt(tierPath + ".Price");
            }

            if (upgradeConfig.get(tierPath + ".Amount") != null) {
                amount = upgradeConfig.getInt(tierPath + ".Amount");
            }

            if (upgradeConfig.getString(tierPath + ".Material-Price") != null) {

                String m = upgradeConfig.getString(tierPath + ".Material-Price").toUpperCase();

                XMaterial x = XMaterial.matchXMaterial(m, (byte) 0);

                if (m == null) {
                    this.sendUpgradeError("&cPath: " + tierPath + ".Material-Price", "&cMaterial " + m + " does not exist.",
                            "&cReplaced it with STONE");
                    x = XMaterial.STONE;
                }

                priceMaterial = x.parseMaterial();
            }

            List<IUpgrade> upgr = this.readUpgrade(upgradeConfig, path + ".Tier." + tier, upgradeName);

            if (!upgr.isEmpty()) {
                upgrades = upgr;
            }

            if (upgradeConfig.get(path + ".Tier." + tier + ".IncreaseTier") != null) {
                increaseTier = upgradeConfig.getStringList(path + ".Tier." + tier + ".IncreaseTier");
            }

            if (upgradeConfig.get(path + ".Tier." + tier + ".DecreaseTier") != null) {
                decreaseTier = upgradeConfig.getStringList(path + ".Tier." + tier + ".DecreaseTier");
            }

            if (upgradeConfig.getBoolean(path + ".Tier." + tier + ".Glowing")) {
                glowing = true;
            }

            UpgradeTier upgradeTier = new UpgradeTier(game, upgradeName, increaseTier, decreaseTier, upgrades,
                    Collections.emptyList(), displayMaterial, data, amount, priceMaterial, price, name, lore, glowing);
            upgradeTier.setDecreaseTierOnDeath(decreaseTierDeath);
            upgradeTier.setRespawnItems(respawnItems);
            upgradeTierList.put(tier, upgradeTier);
            isFirstTier = false;

        }

        return new ArrayList<>(upgradeTierList.values());

    }

    @SuppressWarnings("deprecation")
    public List<IUpgrade> readUpgrade(YamlConfiguration upgradeConfig, String path, String upgradeName) {

        List<IUpgrade> upgrades = new ArrayList<IUpgrade>();

        if (upgradeConfig.get(path + ".GeneratorUpgrade") != null) {

            for (String genType : upgradeConfig.getConfigurationSection(path + ".GeneratorUpgrade").getKeys(false)) {

                FloorGeneratorType type = FloorGeneratorType.IRON;
                String generatorTypePath = null;
                int time = 999;
                int amount = 1;

                if ("IRON".equalsIgnoreCase(genType)) {
                    generatorTypePath = path + ".GeneratorUpgrade.IRON";
                    type = FloorGeneratorType.IRON;
                } else if ("GOLD".equalsIgnoreCase(genType)) {
                    generatorTypePath = path + ".GeneratorUpgrade.GOLD";
                    type = FloorGeneratorType.GOLD;
                } else if ("EMERALD".equalsIgnoreCase(genType)) {
                    generatorTypePath = path + ".GeneratorUpgrade.EMERALD";
                    type = FloorGeneratorType.EMERALD;
                }

                if (generatorTypePath == null) {
                    this.sendUpgradeError("&cPath: " + path + ".GeneratorUpgrade",
                            "&cCannot find the generator type " + genType,
                            "&cIt must be one of type : IRON , GOLD , EMERALD .");
                    continue;
                }

                if (upgradeConfig.get(generatorTypePath + ".SpawnTime") != null) {
                    time = upgradeConfig.getInt(generatorTypePath + ".SpawnTime");
                }

                if (upgradeConfig.get(generatorTypePath + ".Amount") != null) {
                    amount = upgradeConfig.getInt(generatorTypePath + ".Amount");
                }

                if (amount <= 0) {
                    this.sendUpgradeError("&cPath: " + generatorTypePath + ".Amount",
                            "&cAmount cannot be less or equal to 0 (zero) ", "&cReplaced it with 1");
                }

                if (time <= 0) {
                    this.sendUpgradeError("&cPath: " + generatorTypePath + ".SpawnTime",
                            "&cSpawnTime cannot be less or equal to 0 (zero) ",
                            "&cReplaced it with 40 ticks (2 seconds) .");
                }

                GeneratorUpgrade generatorUpgrade = new GeneratorUpgrade(type, time, amount);

                upgrades.add(generatorUpgrade);
            }

        }

        if (upgradeConfig.get(path + ".EnemyTrap") != null) {

            Map<String, List<PotionEffect>> effect = new HashMap<>();
            effect.put("ALL_TEAM", new ArrayList<>());
            effect.put("RADIUS_ENEMY", new ArrayList<>());
            double activationRange = upgradeConfig.getDouble(path + ".EnemyTrap.ActivationRadius");
            List<String> decreaseTier = new ArrayList<>();

            List<String> dt = upgradeConfig.getStringList(path + ".EnemyTrap.onTriggered.DecreaseTier");

            if ((dt != null) && !dt.isEmpty()) {
                decreaseTier.addAll(dt);
            }

            for (String potion : upgradeConfig.getConfigurationSection(path + ".EnemyTrap").getKeys(false)) {

                if ("ALL_TEAM".equals(potion)) {

                    String teamPath = path + ".EnemyTrap.ALL_TEAM";

                    Set<String> teamEff = upgradeConfig.getConfigurationSection(teamPath).getKeys(false);

                    for (String eff : teamEff) {

                        PotionEffect potionEff = this.readEffect(upgradeConfig, eff.toUpperCase(), teamPath + "." + eff);
                        effect.get("ALL_TEAM").add(potionEff);

                    }

                }

                if ("RADIUS_ENEMY".equals(potion)) {

                    String enemyPath = path + ".EnemyTrap.RADIUS_ENEMY";

                    Set<String> enemyEff = upgradeConfig.getConfigurationSection(enemyPath).getKeys(false);

                    for (String eff : enemyEff) {

                        PotionEffect potionEff = this.readEffect(upgradeConfig, eff.toUpperCase(), enemyPath + "." + eff);
                        effect.get("RADIUS_ENEMY").add(potionEff);

                    }

                }

            }

            EnemyTrapEffect eff = new EnemyTrapEffect(upgradeName, decreaseTier, activationRange, effect);
            eff.getInformation().addAll(this.readTrapInformation(upgradeConfig, path + ".EnemyTrap"));
            upgrades.add(eff);
        }

        if (upgradeConfig.get(path + ".PlayerPotionEffect") != null) {

            for (String potion : upgradeConfig.getConfigurationSection(path + ".PlayerPotionEffect").getKeys(false)) {

                PotionEffectType type = PotionEffectType.getByName(potion);

                if (type == null) {
                    this.sendUpgradeError("&cPath: " + path + ".PlayerPotionEffect." + potion,
                            "&cPotion effect type " + potion + " does not exist.", "&cReplaced it with ABSORPTION");
                    type = PotionEffectType.ABSORPTION;
                }

                int time = 1;
                int amplifier = 0;
                double activationRange = 0;
                String potionPath = path + ".PlayerPotionEffect." + potion;

                if (upgradeConfig.get(potionPath + ".Time") != null) {
                    time = upgradeConfig.getInt(potionPath + ".Time");
                }

                if (upgradeConfig.get(potionPath + ".Amplifier") != null) {
                    amplifier = upgradeConfig.getInt(potionPath + ".Amplifier");
                }

                if (upgradeConfig.get(potionPath + ".ActivationRadius") != null) {
                    activationRange = upgradeConfig.getDouble(potionPath + ".ActivationRadius");
                }

                PlayerPotionEffect playerPotionEffect = new PlayerPotionEffect(type, time, amplifier, activationRange,
                        false);

                upgrades.add(playerPotionEffect);
            }
        }

        if (upgradeConfig.get(path + ".PlayerEnchantment") != null) {

            Map<String, Map<Enchantment, Integer>> enchantsPath = new HashMap<>();

            for (String armorPath : upgradeConfig.getConfigurationSection(path + ".PlayerEnchantment").getKeys(false)) {

                Map<Enchantment, Integer> enchants = new HashMap<>();
                String enchantPath = path + ".PlayerEnchantment." + armorPath;

                for (String enchantName : upgradeConfig.getConfigurationSection(enchantPath).getKeys(false)) {

                    Enchantment enchant = (Enchantment.getByName(enchantName.toUpperCase()) != null)
                            ? Enchantment.getByName(enchantName.toUpperCase())
                            : Enchantment.PROTECTION_ENVIRONMENTAL;
                    int level = 1;
                    level = upgradeConfig.getInt(enchantPath + "." + enchantName + ".Level");
                    enchants.put(enchant, level);
                }

                if ("ARMOR".equalsIgnoreCase(armorPath)) {
                    enchantsPath.put("HELMET", enchants);
                    enchantsPath.put("CHESTPLATE", enchants);
                    enchantsPath.put("LEGGINGS", enchants);
                    enchantsPath.put("BOOTS", enchants);
                    continue;
                }

                enchantsPath.put(armorPath.toUpperCase(), enchants);

            }

            PlayerEnchantment playerEnchantment = new PlayerEnchantment(enchantsPath);

            upgrades.add(playerEnchantment);

        }

        if (upgradeConfig.get(path + ".TeamEnchantment") != null) {

            Map<String, Map<Enchantment, Integer>> enchantsPath = new HashMap<>();

            for (String armorPath : upgradeConfig.getConfigurationSection(path + ".TeamEnchantment").getKeys(false)) {

                Map<Enchantment, Integer> enchants = new HashMap<>();
                String enchantPath = path + ".TeamEnchantment." + armorPath;

                for (String enchantName : upgradeConfig.getConfigurationSection(enchantPath).getKeys(false)) {

                    Enchantment enchant = (Enchantment.getByName(enchantName.toUpperCase()) != null)
                            ? Enchantment.getByName(enchantName.toUpperCase())
                            : Enchantment.PROTECTION_ENVIRONMENTAL;
                    int level = 1;
                    level = upgradeConfig.getInt(enchantPath + "." + enchantName + ".Level");
                    enchants.put(enchant, level);
                }

                if ("ARMOR".equalsIgnoreCase(armorPath)) {
                    enchantsPath.put("HELMET", enchants);
                    enchantsPath.put("CHESTPLATE", enchants);
                    enchantsPath.put("LEGGINGS", enchants);
                    enchantsPath.put("BOOTS", enchants);
                    continue;
                }

                enchantsPath.put(armorPath.toUpperCase(), enchants);

            }

            TeamEnchantment teamEnchantment = new TeamEnchantment(enchantsPath);

            upgrades.add(teamEnchantment);

        }

        // TODO: Verificare enchant-uri ca la celelalte

        if (upgradeConfig.get(path + ".TeamPotionEffect") != null) {

            for (String potion : upgradeConfig.getConfigurationSection(path + ".TeamPotionEffect").getKeys(false)) {

                PotionEffectType type = PotionEffectType.getByName(potion.toUpperCase());

                if (type == null) {
                    this.sendUpgradeError("&cPath: " + path + ".TeamPotionEffect." + potion,
                            "&cPotion effect type " + potion + " does not exist.", "&cReplaced it with ABSORPTION");
                    type = PotionEffectType.ABSORPTION;
                }

                int time = 1;
                int amplifier = 0;
                double activationRange = 0;
                String potionPath = path + ".TeamPotionEffect." + potion;
                Object timeObj = upgradeConfig.get(potionPath + ".Time");

                if (timeObj != null) {
                    time = (timeObj instanceof String) ? (timeObj.equals("INFINITE") ? Integer.MAX_VALUE
                            : Utils.getInteger((String) timeObj)) : upgradeConfig.getInt(potionPath + ".Time");
                }

                if (upgradeConfig.get(potionPath + ".Amplifier") != null) {
                    amplifier = upgradeConfig.getInt(potionPath + ".Amplifier");
                }

                if (upgradeConfig.get(potionPath + ".ActivationRadius") != null) {
                    activationRange = upgradeConfig.getDouble(potionPath + ".ActivationRadius");
                }

                time = (time <= 0) ? 1 : time;
                boolean p = upgradeConfig.getBoolean(potionPath + ".Permanent");

                TeamPotionEffect playerPotionEffect = new TeamPotionEffect(type, time, amplifier, activationRange, p);

                upgrades.add(playerPotionEffect);
            }
        }

        return upgrades;

    }

    public List<TrapInformation> readTrapInformation(FileConfiguration config, String trapPath) {

        Object t1 = config.get(trapPath + ".onTriggered");
        Object t2 = config.get(trapPath + ".OnTriggered");

        if ((t1 == null) && (t2 == null)) {
            return Collections.emptyList();
        }

        String path = (t1 != null) ? (trapPath + ".onTriggered") : (trapPath + ".OnTriggered");
        ConfigurationSection section = config.getConfigurationSection(path);

        if (section == null) {
            return Collections.emptyList();
        }

        Set<String> subPath = section.getKeys(false);

        if (subPath.isEmpty()) {
            return Collections.emptyList();
        }

        List<TrapInformation> list = new ArrayList<>();

        for (String action : subPath) {

            if ("SendMessage".equals(action)) {

                ConfigurationSection applyFor = config.getConfigurationSection(path + ".SendMessage");

                if (applyFor == null) {
                    continue;
                }

                Set<String> applyForSet = applyFor.getKeys(false);

                if (applyForSet.isEmpty()) {
                    continue;
                }

                Map<String, List<String>> messageMap = new HashMap<>();

                for (String applyPath : applyForSet) {

                    List<String> messages = new ArrayList<>();
                    Object message = config.getString(path + ".SendMessage." + applyPath + ".Message");

                    if (message == null) {
                        continue;
                    }

                    if (message instanceof String) {
                        String string = (String) message;
                        messages.add(Utils.translate(string));
                    }

                    if (message instanceof List) {
                        List<?> messagesList = (ArrayList<?>) message;

                        for (Object obj : messagesList) {

                            if (obj instanceof String) {
                                String msg = (String) obj;
                                messages.add(Utils.translate(msg));
                            }

                        }

                    }

                    StringBuilder builder = new StringBuilder();
                    messages.forEach(m -> builder.append(m).append(" , "));

//					Bukkit.broadcastMessage("Reading trap message " + builder.toString());

                    messageMap.put(applyPath, messages);

                }

                list.add(new TrapMessage(messageMap));
                continue;
            }

            if ("SendTitle".equals(action)) {

                ConfigurationSection applyFor = config.getConfigurationSection(path + ".SendTitle");

                if (applyFor == null) {
                    continue;
                }

                Set<String> applyForSet = applyFor.getKeys(false);

                if (applyForSet.isEmpty()) {
                    continue;
                }

                Map<String, Title> titleMap = new HashMap<>();

                for (String applyMember : applyForSet) {

                    String applyPath = path + ".SendTitle." + applyMember;

                    String title = config.getString(applyPath + ".Title");

                    if (title == null) {
                        title = "";
                    }

                    String subTitle = config.getString(applyPath + ".SubTitle");

                    if (subTitle == null) {
                        subTitle = "";
                    }

                    int fadein = config.getInt(applyPath + ".FadeIn");
                    int fadeout = config.getInt(applyPath + ".FadeOut");
                    int stay = config.getInt(applyPath + ".Stay");

//					Bukkit.broadcastMessage("Reading trap title " + title + " , subTitle " +  " , " + fadein + " , " + stay + " , " + fadeout);

                    titleMap.put(applyMember, new Title(title, subTitle, fadein, stay, fadeout));

                }

                list.add(new TrapTitle(titleMap));

                continue;
            }

            if ("PlaySound".equals(action)) {

                ConfigurationSection applyFor = config.getConfigurationSection(path + ".PlaySound");

                if (applyFor == null) {
                    continue;
                }

                Set<String> applyForSet = applyFor.getKeys(false);

                if (applyForSet.isEmpty()) {
                    continue;
                }

                Map<String, SoundInformation> soundMap = new HashMap<>();

                for (String applyMember : applyForSet) {

                    String applyPath = path + ".PlaySound." + applyMember;

                    String soundName = config.getString(applyPath + ".Sound");

                    if (soundName == null) {
                        continue;
                    }

                    Sound sound = Sounds.getSound(soundName);

                    if (sound == null) {
                        this.sendUpgradeError(applyPath + ".Sound", "&cCouldn't find the sound " + sound);
                        continue;
                    }

                    float pitch = (float) config.getDouble(applyPath + ".Pitch");

                    if (pitch == 0) {
                        pitch = 2.0f;
                    }

                    float volume = (float) config.getDouble(applyPath + ".Volume");

                    if (volume == 0) {
                        volume = 2.0f;
                    }

//					Bukkit.broadcastMessage("Reading trap sound " + sound + " , " + pitch + " , " + volume);

                    soundMap.put(applyMember, new SoundInformation(sound, pitch, volume));

                }

                list.add(new TrapSound(soundMap));

                continue;
            }

        }

        return list;
    }

    public PotionEffect readEffect(YamlConfiguration upgradeConfig, String effect, String path) {

        if (!"REVEAL_INVISIBILITY".equalsIgnoreCase(effect)) {
            PotionEffectType type = PotionEffectType.getByName(effect.toUpperCase());

            if (type == null) {
                this.sendUpgradeError("&cPath: " + path, "&cPotion effect type " + effect + " does not exist.",
                        "&cReplaced it with ABSORPTION");
                type = PotionEffectType.ABSORPTION;
            }
        }

        int time;
        int amplifier;

        time = upgradeConfig.getInt(path + ".Time");
        amplifier = upgradeConfig.getInt(path + ".Amplifier");

        return new PotionEffect(effect, ((amplifier - 1) < 0) ? 0 : (amplifier - 1), (time + 1) * 20);
    }

    public void sendUpgradeError(String... message) {

        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Upgrade Configuration Warning --------------"));
        Bukkit.getConsoleSender().sendMessage("");

        for (String s : message) {
            Bukkit.getConsoleSender().sendMessage(Utils.translate(s));
        }

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender()
                .sendMessage(Utils.translate("&4------------- Upgrade Configuration Warning --------------"));

    }

    public File getFile() {
        return this.file;
    }
}
