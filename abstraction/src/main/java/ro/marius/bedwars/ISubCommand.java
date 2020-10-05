package ro.marius.bedwars;

import org.bukkit.command.CommandSender;

public interface ISubCommand {

    void onCommand(CommandSender sender, String[] args);

}
