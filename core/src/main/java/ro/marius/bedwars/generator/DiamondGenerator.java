package ro.marius.bedwars.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public class DiamondGenerator implements IGenerator {

    private static final FixedMetadataValue DIAMOND_GENERATOR_METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(),
            "DiamondMatch");
    private static final ItemStack DIAMOND_BLOCK = new ItemStack(Material.DIAMOND_BLOCK);
    private ArmorStand supportArmorStand;
    private AMatch match;
    private Location location;
    private Map<Integer, GeneratorTier> generatorTier;
    private List<String> textLines;
    private List<BukkitTask> tasks = new ArrayList<>();
    private List<Item> droppedItems = new ArrayList<>();
    private List<ArmorStand> textStand = new ArrayList<>();
    private int tier = 1;
    private int time;
    private String uuid;

    public DiamondGenerator(Location location, AMatch match) {
        this.location = location;
        this.uuid = UUID.randomUUID().toString();
        this.match = match;
        this.textLines = match.getGame().getArenaOptions().getStringList("DiamondGenerator.TextLines");
        this.generatorTier = match.getGame().getArenaOptions().getTier("DiamondGenerator",
                match.getGame().getArenaOptionName());
        this.time = this.generatorTier.get(1).getSpawnAmount() + 1;
    }

    @Override
    public void spawn() {

        if (this.textLines == null) {
            this.textLines = Collections.emptyList();
            return;
        }

        Location clonedLocation = this.location.clone().add(0, (this.textLines.size() * 0.35) + 3.50, 0);

        for (String textLine : this.textLines) {
            String s = Utils.translate(textLine).replace("<tier>", this.getStringFromTier() + "").replace("<spawnsIn>",
                    this.time + "");
            ArmorStand stand = clonedLocation.getWorld().spawn(clonedLocation, ArmorStand.class);
            stand.setMarker(true);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCustomName(s);
            stand.setCustomNameVisible(true);
            this.textStand.add(stand);
            clonedLocation.subtract(0, 0.35, 0);
        }

        this.supportArmorStand = this.location.getWorld().spawn(clonedLocation.subtract(0, 1.70, 0), ArmorStand.class);
        this.supportArmorStand.setMarker(true);
        this.supportArmorStand.setVisible(false);
        this.supportArmorStand.setGravity(false);
        this.supportArmorStand.setCustomNameVisible(false);
        this.supportArmorStand.setHelmet(DIAMOND_BLOCK);
    }

    @Override
    public void start() {

        World world = this.location.getWorld();

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {

                DiamondGenerator.this.setTime(DiamondGenerator.this.getTime() - 1);

                String tier = DiamondGenerator.this.getStringFromTier();

                for (int i = 0; i < DiamondGenerator.this.textStand.size(); i++) {
                    ArmorStand stand = DiamondGenerator.this.textStand.get(i);
                    String text = Utils.translate(DiamondGenerator.this.textLines.get(i));
                    stand.setCustomName(text.replace("<tier>", tier).replace("<spawnsIn>", DiamondGenerator.this.time + ""));
                }


                if (DiamondGenerator.this.getTime() > 0) {
                    return;
                }
                if (DiamondGenerator.this.getTime() <= 0) {
                    DiamondGenerator.this.setTime(DiamondGenerator.this.getTotalTime() + 1);
                }

                int droppedAmount = 0;

                DiamondGenerator.this.droppedItems.removeIf(i -> !i.isValid());

                for (Item i : DiamondGenerator.this.droppedItems) {
                    droppedAmount += i.getItemStack().getAmount();
                }

                int dropLimit = DiamondGenerator.this.getLimitAmount();

                if (DiamondGenerator.this.isLimit() && (droppedAmount >= dropLimit)) {
                    return;
                }

                int dropAmount = DiamondGenerator.this.getDropAmount();
                int calculatedDroppedAmount = !DiamondGenerator.this.isLimit() ? dropAmount
                        : (((droppedAmount + dropAmount) > dropLimit) ? (dropLimit - droppedAmount) : dropAmount);

                Item item = world.dropItem(DiamondGenerator.this.location, new ItemStack(Material.DIAMOND, calculatedDroppedAmount));
                item.setMetadata("DiamondMatch", DIAMOND_GENERATOR_METADATA);
                item.setVelocity(Utils.EMPTY_VECTOR);
                DiamondGenerator.this.droppedItems.add(item);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);
        this.tasks.add(task);
    }

    @Override
    public void removeGenerator() {
        this.cancelTasks();
        this.textStand.forEach(Entity::remove);
        this.droppedItems.forEach(Entity::remove);
        this.droppedItems.clear();
        this.textStand.clear();
        this.removeSupportStand();
        this.tier = 1;

    }

    public int getDropAmount() {

        return this.generatorTier.get(this.tier).getSpawnAmount();
    }

    public boolean isLimit() {

        return this.generatorTier.get(this.tier).isSpawnLimit();
    }

    public int getLimitAmount() {

        return this.generatorTier.get(this.tier).getLimitAmount();
    }

    public int getTotalTime() {

        return this.generatorTier.get(this.tier).getTime();
    }

    @Override
    public void cancelTasks() {
        this.tasks.forEach(BukkitTask::cancel);
    }

    public void upgradeTier() {
        this.tier += 1;
    }

    public String getStringFromTier() {
        return (this.tier <= 1) ? "I" : ((this.tier == 2) ? "II" : "III");
    }

    @Override
    public List<ArmorStand> getTextStand() {

        return this.textStand;
    }

    public void removeSupportStand() {

        if (this.supportArmorStand == null) {
            return;
        }

        this.supportArmorStand.remove();
    }

    @Override
    public ArmorStand getSupportStand() {

        return this.supportArmorStand;
    }

    public ArmorStand getSupportArmorStand() {
        return this.supportArmorStand;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public List<BukkitTask> getTasks() {
        return this.tasks;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public void setTime(int time) {
        this.time = time;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
