package ro.marius.bedwars;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public abstract class AbstractCommand extends BukkitCommand {

    public AbstractCommand(String name) {
        super(name);
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        this.onCommand(sender, args);
        return false;
    }

}
