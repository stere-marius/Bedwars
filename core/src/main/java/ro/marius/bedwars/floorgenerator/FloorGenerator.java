package ro.marius.bedwars.floorgenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class FloorGenerator {

    private final int limit;
    private FloorGeneratorType type;
    private AMatch match;
    //	private Team team;
    private int amount;
    private GameLocation location;
    private BukkitTask task;
    private boolean startedGenerator;
    private int timeTask;

    // FloorGenerator informations
    private List<Item> droppedItems = new ArrayList<>();
    private Material drop;
    private boolean isLimit;

    public FloorGenerator(AMatch match, FloorGeneratorType type, int amount, int time, GameLocation loc) {
        this.match = match;
        this.type = type;
        this.amount = (amount == 0) ? 1 : amount;
        this.timeTask = time;
        this.location = loc;
//		this.team = team;
        this.limit = match.getGame().getArenaOptions().getInt(type.getLimitAmountPath());
        this.isLimit = match.getGame().getArenaOptions().getBoolean(type.getIsLimitPath());
        this.drop = type.getDrop();

    }

    public void start() {

        Location dropLocation = this.location.getLocation();
        World world = dropLocation.getWorld();

        this.task = new BukkitRunnable() {

            @Override
            public void run() {

                int droppedAmount = 0;

                FloorGenerator.this.droppedItems.removeIf(i -> !i.isValid());

                for (Item i : FloorGenerator.this.droppedItems) {
                    droppedAmount += i.getItemStack().getAmount();
                }

                if (FloorGenerator.this.isLimit && (droppedAmount >= FloorGenerator.this.limit)) {
                    return;
                }

                int limitedAmount = !FloorGenerator.this.isLimit ? FloorGenerator.this.amount
                        : (((droppedAmount + FloorGenerator.this.amount) > FloorGenerator.this.limit) ? (FloorGenerator.this.limit - droppedAmount) : FloorGenerator.this.amount);
                ItemStack itemStack = new ItemStack(FloorGenerator.this.drop, limitedAmount);
                Item item = world.dropItem(dropLocation, itemStack);
                item.setVelocity(Utils.EMPTY_VECTOR);
                FloorGenerator.this.match.getMatchEntity().add(item);
                FloorGenerator.this.droppedItems.add(item);

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, this.timeTask);

        this.startedGenerator = true;
    }

    public void clearDrops() {

        this.droppedItems.forEach(Entity::remove);
        this.droppedItems.clear();

    }

    public void cancelTask() {
        if (this.task == null) {
            return;
        }
        this.task.cancel();
    }

    public List<Item> getValidDroppedItems() {

        List<Item> list = new ArrayList<>();

        for (Item i : this.droppedItems) {

            if (!i.isValid()) {
                continue;
            }

            list.add(i);
        }

        return list;
    }

    public int getDroppedItemsSize() {

        List<Item> list = this.getValidDroppedItems();
        int size = 0;

        for (Item i : list) {
            size += i.getItemStack().getAmount();
        }

        return size;
    }


    @Override
    public String toString() {
        return "FloorGenerator [type=" + this.type.name() + ", match=" + this.match.getGame().getName() + ", amount=" + this.amount
                + ", location=" + this.location + ", taskIsNull="
                + (this.task == null ? "true" : "false, taskIsRunning=" + this.task.isCancelled()) + ", startedGenerator="
                + this.startedGenerator + ", timeTask=" + this.timeTask + ", droppedItems=" + this.droppedItems.size()
                + ", droppedValidItemsSize=" + this.getDroppedItemsSize() + ", drop=" + this.drop.name() + ", isLimit=" + this.isLimit
                + ", isLimitDropping=" + (this.getDroppedItemsSize() >= this.limit) + ", limit=" + this.limit + "]";
    }

    public FloorGeneratorType getType() {
        return this.type;
    }

    public void setType(FloorGeneratorType type) {
        this.type = type;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public GameLocation getLocation() {
        return this.location;
    }

    public void setLocation(GameLocation location) {
        this.location = location;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void setTimeTask(int timeTask) {
        this.timeTask = timeTask;
    }

}
