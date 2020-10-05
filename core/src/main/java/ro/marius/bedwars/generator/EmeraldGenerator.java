package ro.marius.bedwars.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public class EmeraldGenerator implements IGenerator {

    private static final MetadataValue EMERALD_GENERATOR_METADATA = new FixedMetadataValue(BedWarsPlugin.getInstance(),
            "EmeraldMatch");
    private static final ItemStack EMERALD_BLOCK = new ItemStack(Material.EMERALD_BLOCK);
    private ArmorStand supportArmorStand;
    private AMatch match;
    private Location location;
    private Map<Integer, GeneratorTier> generatorTier = new HashMap<>();
    private List<String> textLines = new ArrayList<>();
    private List<BukkitTask> tasks = new ArrayList<>();
    private List<Item> droppedItems = new ArrayList<>();
    private List<ArmorStand> textStand = new ArrayList<>();
    private int tier = 1;
    private int time;

    public EmeraldGenerator(Location location, AMatch match) {
        this.location = location;
        this.match = match;
        this.textLines = match.getGame().getArenaOptions().getStringList("EmeraldGenerator.TextLines");
        this.generatorTier = match.getGame().getArenaOptions().getTier("EmeraldGenerator",
                match.getGame().getArenaOptionName());
        this.time = this.generatorTier.get(1).getSpawnAmount() + 1;
    }

    @Override
    public void spawn() {

        if (this.textLines == null) {
            this.textLines = Collections.emptyList();
            return;
        }

        Location clonedLocation = this.location.clone().add(0, (this.textLines.size() * 0.35) + 3.5, 0);

        for (String textLine : this.textLines) {
            String s = Utils.translate(textLine).replace("<tier>", this.getStringFromTier() + "").replace("<spawnsIn>",
                    this.time + "");
            ArmorStand stand = clonedLocation.getWorld().spawn(clonedLocation, ArmorStand.class);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setCustomName(s);
            stand.setCustomNameVisible(true);
            stand.setMarker(true);
            this.textStand.add(stand);
            clonedLocation.subtract(0, 0.35, 0);
        }

        this.supportArmorStand = this.location.getWorld().spawn(clonedLocation.subtract(0, 1.70, 0), ArmorStand.class);
        this.supportArmorStand.setMarker(true);
        this.supportArmorStand.setVisible(false);
        this.supportArmorStand.setGravity(false);
        this.supportArmorStand.setCustomNameVisible(false);
        this.supportArmorStand.setHelmet(EMERALD_BLOCK);
    }

    @Override
    public void start() {

        World world = this.location.getWorld();
        int size = this.textStand.size();

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {

                EmeraldGenerator.this.setTime(EmeraldGenerator.this.getTime() - 1);

                String tier = EmeraldGenerator.this.getStringFromTier();

                for (int i = 0; i < size; i++) {
                    ArmorStand stand = EmeraldGenerator.this.textStand.get(i);
                    String text = Utils.translate(EmeraldGenerator.this.textLines.get(i));
                    stand.setCustomName(text.replace("<tier>", tier).replace("<spawnsIn>", EmeraldGenerator.this.time + ""));
                }

                if (EmeraldGenerator.this.getTime() > 0) {
                    return;
                }

                if (EmeraldGenerator.this.getTime() <= 0) {
                    EmeraldGenerator.this.setTime(EmeraldGenerator.this.getTotalTime() + 1);
                }

                int droppedAmount = 0;

                EmeraldGenerator.this.droppedItems.removeIf(i -> !i.isValid());

                for (Item i : EmeraldGenerator.this.droppedItems) {
                    droppedAmount += i.getItemStack().getAmount();
                }

                int dropLimit = EmeraldGenerator.this.getLimitAmount();

                if (EmeraldGenerator.this.isLimit() && (droppedAmount >= dropLimit)) {
                    return;
                }

                int dropAmount = EmeraldGenerator.this.getDropAmount();

                int calculatedDroppedAmount = !EmeraldGenerator.this.isLimit() ? dropAmount
                        : (((droppedAmount + dropAmount) > dropLimit) ? (dropLimit - droppedAmount) : dropAmount);

                if (calculatedDroppedAmount <= 0) {
                    return;
                }

                Item item = world.dropItem(EmeraldGenerator.this.location, new ItemStack(Material.EMERALD, calculatedDroppedAmount));
                item.setMetadata("EmeraldMatch", EMERALD_GENERATOR_METADATA);
                item.setVelocity(Utils.EMPTY_VECTOR);
                EmeraldGenerator.this.droppedItems.add(item);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);

        this.tasks.add(task);
    }

    @Override
    public void removeGenerator() {
        this.cancelTasks();
        this.textStand.forEach(Entity::remove);
        this.droppedItems.forEach(Entity::remove);
        this.textStand.clear();
        this.removeSupportStand();
        this.droppedItems.clear();
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

    public void setTextStand(List<ArmorStand> textStand) {
        this.textStand = textStand;
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

    public void setSupportArmorStand(ArmorStand supportArmorStand) {
        this.supportArmorStand = supportArmorStand;
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

    public Map<Integer, GeneratorTier> getGeneratorTier() {
        return this.generatorTier;
    }

    public void setGeneratorTier(Map<Integer, GeneratorTier> generatorTier) {
        this.generatorTier = generatorTier;
    }

    public List<String> getTextLines() {
        return this.textLines;
    }

    public void setTextLines(List<String> textLines) {
        this.textLines = textLines;
    }

    @Override
    public List<BukkitTask> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<BukkitTask> tasks) {
        this.tasks = tasks;
    }

    public List<Item> getDroppedItems() {
        return this.droppedItems;
    }

    public void setDroppedItems(List<Item> droppedItems) {
        this.droppedItems = droppedItems;
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
}
