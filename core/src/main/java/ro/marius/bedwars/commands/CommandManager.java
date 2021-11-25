package ro.marius.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import ro.marius.bedwars.AbstractCommand;
import ro.marius.bedwars.BedWarsPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class CommandManager {

    private final BedWarsPlugin bedWarsPlugin;

    public CommandManager(BedWarsPlugin bedWarsPlugin) {
        this.bedWarsPlugin = bedWarsPlugin;
    }

    public void registerCommands() {

        Set<AbstractCommand> abstractCommand = new HashSet<>();
        abstractCommand.add(new BedwarsCommand());
        abstractCommand.add(new ShoutCommand(BedWarsPlugin.getInstance().getConfig().getString("ShoutCommand.Name")));

        if (bedWarsPlugin.getConfig().getString("PartyAdapter", "BEDWARS_ADAPTER").equalsIgnoreCase("BEDWARS_ADAPTER")) {
            abstractCommand.add(new PartyCommand());
        }

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            for (BukkitCommand command : abstractCommand) {
                commandMap.register(command.getName(), command);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
