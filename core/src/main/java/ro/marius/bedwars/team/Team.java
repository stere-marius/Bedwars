package ro.marius.bedwars.team;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.menu.action.RewardItem;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.team.upgrade.*;
import ro.marius.bedwars.team.upgrade.enemy.EnemyTrapEffect;
import ro.marius.bedwars.upgradeconfiguration.TeamUpgrade;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

import java.util.*;
import java.util.Map.Entry;

public class Team {

    private static final MetadataValue SHOP_METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(), "MatchShop");
    private static final MetadataValue UPGRADE_METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(), "MatchUpgrade");
    private final MetadataValue teamMetadata;
    private final List<IronGolem> golems = new ArrayList<>();
    private final List<Silverfish> silverFish = new ArrayList<>();
    private final Set<Player> players = new HashSet<>();
    private final Map<String, List<IUpgrade>> permanentUpgrades = new HashMap<>();
    private final Map<UUID, PermanentItemList> permanentItems = new HashMap<>();
    private final Map<EquipmentSlot, ItemStack> kitItems = new HashMap<>();
    @NotNull
    private String name;
    private String colorName;
    private final String letter;
    private TeamColor teamColor;
    private BlockFace bedFace;
    private GameLocation bedLocation;
    private GameLocation spawnLocation;
    private GameLocation goldGenerator;
    private GameLocation ironGenerator;
    private GameLocation emeraldGenerator;
    private GameLocation shopLocation;
    private GameLocation upgradeLocation;
    private boolean bedBroken;
    private FloorGenerator ironFloorGenerator;
    private FloorGenerator goldFloorGenerator;
    private FloorGenerator emeraldFloorGenerator;
    private Map<String, TeamUpgrade> upgrades = new HashMap<>();
    private final PermanentPotionEffect permanentEffects = new PermanentPotionEffect();
    private final List<QueuedTrap> traps = new ArrayList<>();
    private final Map<Player, BukkitTask> trappedPlayers = new HashMap<>();
    private final Map<String, TeamUpgrade> gameUpgrades = new HashMap<>();
    private final Map<String, TeamUpgrade> shopUpgrades = new HashMap<>();
    private final Map<UUID, Map<String, TeamUpgrade>> playerShopUpgrades = new HashMap<>();

    public Team(@NotNull String name, String letter, String colorName, BlockFace bedFace, GameLocation bedGameLocation,
                GameLocation spawnGameLocation, GameLocation goldGenerator, GameLocation ironGenerator,
                GameLocation emeraldGenerator, GameLocation shopGameLocation, GameLocation upgradeGameLocation) {
        this.name = name;
        this.colorName = colorName;
        this.teamColor = TeamColor.valueOf(colorName.replace("-", "_"));
        this.bedFace = bedFace;
        this.bedLocation = bedGameLocation;
        this.spawnLocation = spawnGameLocation;
        this.goldGenerator = goldGenerator;
        this.ironGenerator = ironGenerator;
        this.emeraldGenerator = emeraldGenerator;
        this.shopLocation = shopGameLocation;
        this.upgradeLocation = upgradeGameLocation;
        this.letter = letter;
        this.teamMetadata = new FixedMetadataValue(BedWarsPlugin.getInstance(), name);
        this.generateKitItems();
    }

    public void setupPlayers() {

        ItemStack helmet = this.kitItems.get(EquipmentSlot.HEAD);
        ItemStack chestplate = this.kitItems.get(EquipmentSlot.CHEST);
        ItemStack leggings = this.kitItems.get(EquipmentSlot.LEGS);
        ItemStack boots = this.kitItems.get(EquipmentSlot.FEET);
        ItemStack sword = this.kitItems.get(EquipmentSlot.HAND);
        Location spawnLocation = this.getSpawnLocation().getLocation();

        for (Player p : this.getPlayers()) {
            p.teleport(spawnLocation);
            p.setFallDistance(0.0f);
            Map<String, TeamUpgrade> shopUpgrades = new HashMap<>();


            for (Entry<String, TeamUpgrade> entry : this.shopUpgrades.entrySet()) {
                shopUpgrades.put(entry.getKey(), entry.getValue().clone());
            }


            // TODO: Catch shopUpgrades

            this.playerShopUpgrades.put(p.getUniqueId(), shopUpgrades);
            PlayerInventory inv = p.getInventory();
            inv.setHelmet(helmet);
            inv.setChestplate(chestplate);
            inv.setLeggings(leggings);
            inv.setBoots(boots);
            inv.setItem(0, sword);
        }

    }

    public void giveRespawnKit(AMatch match, Player p) {

        List<RewardItem> permanentItems = this.getPermanentItems(p);
        PlayerInventory pInventory = p.getInventory();

        boolean hasChestPlate = false;
        boolean hasHelmet = false;
        boolean hasLeggings = false;
        boolean hasBoots = false;
        boolean hasSword = false;

        for (RewardItem rewardItem : permanentItems) {

            ItemStack finalItem = rewardItem.getReward().build();
            Material material = finalItem.getType();
            String mName = material.name();

            if (mName.endsWith("CHESTPLATE")) {
                p.getInventory().setChestplate(finalItem);
                this.applyEnchant("CHESTPLATE", p);
                hasChestPlate = true;
                continue;
            }

            if (mName.endsWith("HELMET")) {
                p.getInventory().setHelmet(finalItem);
                this.applyEnchant("HELMET", p);
                hasHelmet = true;
                continue;
            }

            if (mName.endsWith("LEGGINGS")) {
                p.getInventory().setLeggings(finalItem);
                this.applyEnchant("LEGGINGS", p);
                hasLeggings = true;
                continue;
            }

            if (mName.endsWith("BOOTS")) {
                p.getInventory().setBoots(finalItem);
                this.applyEnchant("BOOTS", p);
                hasBoots = true;
                continue;
            }

            if (mName.endsWith("SWORD")) {

                ItemStack item = p.getInventory().getItem(0);

                p.getInventory().setItem(0, finalItem);

                if (item != null) {
                    p.getInventory().addItem(item);
                }

                this.applyEnchant("SWORD", p);
                hasSword = true;
                continue;
            }

            if (material.name().endsWith("WOOL")) {
                ItemStack wool = this.getTeamColor().getBuildMaterial();
                wool.setItemMeta(finalItem.getItemMeta());
                wool.setAmount(finalItem.getAmount());
                finalItem = wool;
            }

            pInventory.addItem(finalItem);
        }

        if (!hasChestPlate) {
            ItemStack chestplate = this.kitItems.get(EquipmentSlot.CHEST);
            pInventory.setChestplate(chestplate);
            this.applyEnchant("CHESTPLATE", p);
        }

        if (!hasHelmet) {
            ItemStack helmet = this.kitItems.get(EquipmentSlot.HEAD);
            pInventory.setHelmet(helmet);
            this.applyEnchant("HELMET", p);
        }

        if (!hasLeggings) {
            ItemStack leggings = this.kitItems.get(EquipmentSlot.LEGS);
            pInventory.setLeggings(leggings);
            this.applyEnchant("LEGGINGS", p);
        }

        if (!hasBoots) {
            ItemStack boots = this.kitItems.get(EquipmentSlot.FEET);
            pInventory.setBoots(boots);
            this.applyEnchant("BOOTS", p);
        }

        if (!hasSword) {
            ItemStack sword = this.kitItems.get(EquipmentSlot.HAND);
            ItemStack item = p.getInventory().getItem(0);

            if (item != null) {
                p.getInventory().setItem(0, sword);
                p.getInventory().addItem(item);
            } else {
                p.getInventory().setItem(0, sword);
            }

            this.applyEnchant("SWORD", p);
        }

        for (List<IUpgrade> list : this.permanentUpgrades.values()) {

            for (IUpgrade upg : list) {
                upg.onActivation(match, p);
            }

        }

        for (PotionEffect eff : this.permanentEffects.getEffect().values()) {
            p.addPotionEffect(eff.getPotionEffect(), true);
        }

        this.decreaseTierDeath(p);

    }

    public boolean containsSword(PlayerInventory inv) {

        for (int i = 0; i < 36; i++) {

            ItemStack item = inv.getItem(i);

            if (item == null) {
                continue;
            }

            if (item.getType().name().endsWith("_SWORD")) {
                return true;
            }

        }

        return false;

    }

    public void decreaseTierDeath(Player p) {

        Map<String, TeamUpgrade> teamUpgrade = this.playerShopUpgrades.get(p.getUniqueId());

        if (teamUpgrade == null) {
            return;
        }

        for (TeamUpgrade upg : teamUpgrade.values()) {

            UpgradeTier upgTier = upg.getPreviousTier();

            if (!upgTier.isUnlocked()) {
                continue;
            }

            boolean isDecrease = upgTier.isDecreaseTierOnDeath();
            boolean isRespawn = upgTier.isRespawnItems();

            if (isDecrease && isRespawn) {
                upg.decreaseTier();
                UpgradeTier current = upg.getPreviousTier();
                current.giveReward(p, this);
                continue;
            }

            if (isRespawn) {
                UpgradeTier current = upg.getPreviousTier();
                current.giveReward(p, this);
                continue;
            }

            if (isDecrease) {
                upg.decreaseTier();
            }

        }

    }

    public void applyEnchant(String part, Player p) {

//		System.out.println("Permanent Upgrades is not empty " + permanentUpgrades.size());

        Collection<TeamUpgrade> teamUpgrade = this.gameUpgrades.values();

        for (TeamUpgrade upg : teamUpgrade) {

            UpgradeTier upgTier = upg.getLastTier();

            if (!upgTier.isUnlocked()) {
                continue;
            }

            for (IUpgrade upgrade : upgTier.getUpgrades()) {

                if (!(upgrade instanceof EnchantUpgrade)) {
                    continue;
                }

                EnchantUpgrade pEnchant = (EnchantUpgrade) upgrade;

                pEnchant.onActivation(part, p);

            }

        }
    }

    public void sendMessage(String message) {

        String m = Utils.translate(message);

        for (Player p : this.players) {
            p.sendMessage(m);
        }

    }


    public void removeFirstTrap() {

        if (this.traps.isEmpty()) {
            return;
        }

        this.traps.remove(0);

    }

    // primul trap dispare si trap-ul de pe index-ul 2 trece pe index 1 si
    // trap-ul de pe index 3 trece pe index 2

    public EnemyTrapEffect getFirstTrap() {

        if (this.traps.isEmpty()) {
            return null;
        }

        return this.traps.get(0).getEffect();
    }

    public UpgradeTier getTrapTier(int index) {

        if (this.traps.isEmpty()) {
            return null;
        }
        if ((this.traps.size() - 1) < index) {
            return null;
        }

        return this.traps.get(index).getTier();
    }

    public void addTrappedPlayer(Player p) {

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {

                if (!Team.this.trappedPlayers.containsKey(p)) {
                    this.cancel();
                    return;
                }

                Team.this.trappedPlayers.remove(p);

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 20 * 30);

        this.trappedPlayers.put(p, task);

    }

    public void reset() {
        this.bedBroken = false;

        for (QueuedTrap trap : this.traps) {
            trap.getTier().reset();
        }

        for (TeamUpgrade upg : this.gameUpgrades.values()) {
            upg.onReset();
        }

        for (BukkitTask task : this.trappedPlayers.values()) {
            task.cancel();
        }

        for (Map<String, TeamUpgrade> map : this.playerShopUpgrades.values()) {

            for (TeamUpgrade upg : map.values()) {
                upg.onReset();
            }
        }

        this.permanentUpgrades.clear();
        this.playerShopUpgrades.clear();

//		this.destroyBed();
        this.golems.forEach(Entity::remove);
        this.silverFish.forEach(Entity::remove);
        this.trappedPlayers.clear();
        this.permanentItems.clear();
        this.golems.clear();
        this.silverFish.clear();
        this.players.clear();
        this.traps.clear();

        if (this.emeraldFloorGenerator != null) {
            this.emeraldFloorGenerator.cancelTask();
            this.emeraldFloorGenerator.clearDrops();
        }

        if (this.ironFloorGenerator != null) {
            this.ironFloorGenerator.cancelTask();
            this.ironFloorGenerator.clearDrops();
        }

        if (this.goldFloorGenerator != null) {
            this.goldFloorGenerator.cancelTask();
            this.goldFloorGenerator.clearDrops();
        }

    }

    public List<Entity> spawnNPC() {

        List<Entity> entity = new ArrayList<>();
        String skinName = "VILLAGER";

        if (!this.players.isEmpty()) {
            UUID uuid = this.players.iterator().next().getUniqueId();
            APlayerData pData = ManagerHandler.getGameManager().getPlayerData().get(uuid);
            skinName = pData.getSkin();
        }

        Entity eShop = ManagerHandler.getVersionManager().getSpawnedEntity(skinName, this.shopLocation.getLocation());
        Entity eUpgrade = ManagerHandler.getVersionManager().getSpawnedEntity(skinName, this.upgradeLocation.getLocation());

        eShop.setMetadata("MatchShop", SHOP_METADATA);
        eUpgrade.setMetadata("MatchUpgrade", UPGRADE_METADATA);
        entity.add(eShop);
        entity.add(eUpgrade);

        return entity;
    }

    @NotNull
    public PermanentItemList getPermanentItems(Player p) {

        PermanentItemList items = this.permanentItems.get(p.getUniqueId());

        if (items == null) {

            PermanentItemList list = new PermanentItemList();
            this.permanentItems.put(p.getUniqueId(), list);
            return list;
        }

        return items;

    }

    public FloorGenerator getIronFloorGenerator(AMatch match) {

        if (this.ironFloorGenerator == null) {
            this.ironFloorGenerator = new FloorGenerator(match, FloorGeneratorType.IRON, 1, 35, this.getIronGenerator());
            return this.ironFloorGenerator;
        }

        return this.ironFloorGenerator;
    }

    public FloorGenerator getGoldFloorGenerator(AMatch match) {

        if (this.goldFloorGenerator == null) {
            this.goldFloorGenerator = new FloorGenerator(match, FloorGeneratorType.GOLD, 1, 50, this.getGoldGenerator());
            return this.goldFloorGenerator;
        }

        return this.goldFloorGenerator;
    }

    public void generateKitItems() {

        VersionWrapper versionWrapper = ManagerHandler.getVersionManager().getVersionWrapper();
        ItemBuilder helmet = new ItemBuilder(Material.LEATHER_HELMET);
        helmet.setColorLeather(this.teamColor.getArmorColor());
        helmet.setUnbreakable(versionWrapper);

        ItemBuilder chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE);
        chestplate.setColorLeather(this.teamColor.getArmorColor());
        chestplate.setUnbreakable(versionWrapper);

        ItemBuilder leggings = new ItemBuilder(Material.LEATHER_LEGGINGS);
        leggings.setColorLeather(this.teamColor.getArmorColor());
        leggings.setUnbreakable(versionWrapper);

        ItemBuilder boots = new ItemBuilder(Material.LEATHER_BOOTS);
        boots.setColorLeather(this.teamColor.getArmorColor());
        boots.setUnbreakable(versionWrapper);

        ItemBuilder swordBuilder = new ItemBuilder(XMaterial.WOODEN_SWORD.parseItem());
        swordBuilder.setUnbreakable(versionWrapper);

        this.kitItems.put(EquipmentSlot.HEAD, helmet.build());
        this.kitItems.put(EquipmentSlot.CHEST, chestplate.build());
        this.kitItems.put(EquipmentSlot.LEGS, leggings.build());
        this.kitItems.put(EquipmentSlot.FEET, boots.build());
        this.kitItems.put(EquipmentSlot.HAND, swordBuilder.build());
    }

    @Override
    public String toString() {
        return "Team [name=" + this.name + ", colorName=" + this.colorName + ", bedLocation=" + this.bedLocation + ", spawnLocation="
                + this.spawnLocation + ", goldGenerator=" + this.goldGenerator + ", ironGenerator=" + this.ironGenerator
                + ", emeraldGenerator=" + this.emeraldGenerator + ", shopLocation=" + this.shopLocation + ", upgradeLocation="
                + this.upgradeLocation + "]";
    }

    public void setSpawnLocation(GameLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void setIronGenerator(GameLocation ironGenerator) {
        this.ironGenerator = ironGenerator;
    }

    public void setGoldGenerator(GameLocation goldGenerator) {
        this.goldGenerator = goldGenerator;
    }

    public void setEmeraldGenerator(GameLocation emeraldGenerator) {
        this.emeraldGenerator = emeraldGenerator;
    }

    public void setBedLocation(GameLocation bedLocation) {
        this.bedLocation = bedLocation;
    }

    public void setShopLocation(GameLocation shopLocation) {
        this.shopLocation = shopLocation;
    }

    public void setUpgradeLocation(GameLocation upgradeLocation) {
        this.upgradeLocation = upgradeLocation;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
        this.teamColor = TeamColor.valueOf(colorName);
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public String getColorName() {
        return this.colorName;
    }

    public String getLetter() {
        return this.letter;
    }

    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public MetadataValue getTeamMetadata() {
        return teamMetadata;
    }

    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public BlockFace getBedFace() {
        return this.bedFace;
    }

    public void setBedFace(BlockFace bedFace) {
        this.bedFace = bedFace;
    }

    public GameLocation getBedLocation() {
        return this.bedLocation;
    }

    public GameLocation getSpawnLocation() {
        return this.spawnLocation;
    }

    public GameLocation getGoldGenerator() {
        return this.goldGenerator;
    }

    public GameLocation getIronGenerator() {
        return this.ironGenerator;
    }

    public GameLocation getEmeraldGenerator() {
        return this.emeraldGenerator;
    }

    public GameLocation getShopLocation() {
        return this.shopLocation;
    }

    public GameLocation getUpgradeLocation() {
        return this.upgradeLocation;
    }

    public boolean isBedBroken() {
        return this.bedBroken;
    }

    public void setBedBroken(boolean bedBroken) {
        this.bedBroken = bedBroken;
    }

    public List<IronGolem> getGolems() {
        return this.golems;
    }

    public List<Silverfish> getSilverFish() {
        return this.silverFish;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public String getPlayersName() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        getPlayers().forEach(p -> stringJoiner.add(p.getName()));
        return stringJoiner.toString();
    }

    public FloorGenerator getIronFloorGenerator() {
        return this.ironFloorGenerator;
    }

    public void setIronFloorGenerator(FloorGenerator ironFloorGenerator) {
        this.ironFloorGenerator = ironFloorGenerator;
    }

    public FloorGenerator getGoldFloorGenerator() {
        return this.goldFloorGenerator;
    }

    public void setGoldFloorGenerator(FloorGenerator goldFloorGenerator) {
        this.goldFloorGenerator = goldFloorGenerator;
    }

    public FloorGenerator getEmeraldFloorGenerator() {
        return this.emeraldFloorGenerator;
    }

    public void setEmeraldFloorGenerator(FloorGenerator emeraldFloorGenerator) {
        this.emeraldFloorGenerator = emeraldFloorGenerator;
    }

    public Map<String, TeamUpgrade> getUpgrades() {
        return this.upgrades;
    }

    public void setUpgrades(Map<String, TeamUpgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public PermanentPotionEffect getPermanentEffects() {
        return this.permanentEffects;
    }

    public List<QueuedTrap> getTraps() {
        return this.traps;
    }

    public Map<Player, BukkitTask> getTrappedPlayers() {
        return this.trappedPlayers;
    }

    public Map<String, TeamUpgrade> getGameUpgrades() {
        return this.gameUpgrades;
    }

    public Map<String, TeamUpgrade> getShopUpgrades() {
        return this.shopUpgrades;
    }

    public Map<UUID, Map<String, TeamUpgrade>> getPlayerShopUpgrades() {
        return this.playerShopUpgrades;
    }

    public Map<String, List<IUpgrade>> getPermanentUpgrades() {
        return this.permanentUpgrades;
    }
}
