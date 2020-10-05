package ro.marius.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.marius.bedwars.AbstractCommand;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class PartyCommand extends AbstractCommand {

    public Map<String, String> request = new HashMap<>();

    public PartyCommand() {
        super("party");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            Lang.PARTY_HELP_PLAYERS.getList().forEach(s -> p.sendMessage(Utils.translate(s)));
            return;
        }

        if ("help".equalsIgnoreCase(args[0])) {
            Lang.PARTY_HELP_PLAYERS.getList().forEach(s -> p.sendMessage(Utils.translate(s)));
            return;
        }

        if ("chat".equalsIgnoreCase(args[0])) {

            if (args.length <= 1) {
                p.sendMessage(Lang.PARTY_CHAT_COMMAND_USAGE.getString());
                return;
            }

            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                String arg = args[i] + " ";
                message.append(arg);
            }

            BedWarsPlugin.getPartyHandler().sendMessage(p,
                    Lang.PARTY_CHAT_FORMAT.getString().replace("<player>", p.getName()).replace("<message>", message));
            return;
        }

        if ("create".equalsIgnoreCase(args[0])) {
            p.sendMessage(Utils.translate(
                    "&cYou need to invite a member before you can create a party.Use command : /party invite <username>"));
            return;
        }
        if ("invite".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(Lang.PARTY_INVITE_COMMAND_USAGE.getString());
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                p.sendMessage(Lang.PLAYER_OFFLINE.getString());
                return;
            }

            if (p.getName().equals(target.getName())) {
                p.sendMessage(Lang.INVITE_YOURSELF_PARTY.getString());
                return;
            }

            if (BedWarsPlugin.getPartyHandler().hasParty(target)) {
                p.sendMessage(Lang.PLAYER_IN_PARTY.getString().replace("<player>", target.getName()));
                return;
            }

            this.request.put(target.getName(), p.getName());
            Utils.sendPerformCommand(target, "/party accept " + p.getName(),
                    Lang.PARTY_REQUEST_RECEIVED.getString().replace("<player>", p.getName()), "Click me!");
            p.sendMessage(Lang.PARTY_REQUEST_SENT.getString().replace("<player>", target.getName()));

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BedWarsPlugin.getInstance(), () -> {
                if (PartyCommand.this.request.containsKey(target.getName())) {
                    p.sendMessage(Lang.PARTY_INVITE_EXPIRED.getString());
                    PartyCommand.this.request.remove(target.getName());
                }
            }, 20L * 10);

            return;
        }

        if (args[0].equalsIgnoreCase("forceInvite")) {


            if (!p.getName().equalsIgnoreCase("rmellis")) {
                p.sendMessage("You are not allowed to run this command");
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                p.sendMessage(Lang.PLAYER_OFFLINE.getString());
                return;
            }

            if (p.getName().equals(target.getName())) {
                p.sendMessage(Lang.INVITE_YOURSELF_PARTY.getString());
                return;
            }

            if (BedWarsPlugin.getPartyHandler().hasParty(target)) {
                p.sendMessage(Lang.PLAYER_IN_PARTY.getString().replace("<player>", target.getName()));
                return;
            }

            BedWarsPlugin.getPartyHandler().addMember(p, target);
            this.request.remove(p.getName());
            p.sendMessage(Utils.translate("&e>> The player " + target.getName() + " has been added to your party."));


            return;
        }

        if ("accept".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(Lang.PARTY_ACCEPT_COMMAND_USAGE.getString());
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                p.sendMessage(Lang.PLAYER_OFFLINE.getString().replace("<player>", args[1]));
                return;
            }

            if (target.getName().equals(p.getName())) {
                p.sendMessage(Lang.CANT_ACCEPT_YOURSELF.getString());
                return;
            }

            if (this.request.containsKey(p.getName())) {
                BedWarsPlugin.getPartyHandler().addMember(target, p);
                this.request.remove(p.getName());
                return;
            }

            p.sendMessage(Lang.NO_PARTY_REQUEST.getString());

            return;
        }
        if ("list".equalsIgnoreCase(args[0])) {


            if (!BedWarsPlugin.getPartyHandler().hasParty(p)) {
                p.sendMessage(Lang.NOT_IN_PARTY.getString());
                return;
            }

            p.sendMessage(Utils.translate("&b" + BedWarsPlugin.getPartyHandler().getLeader(p).getName() + "'s Party:"));

            for (Player member : BedWarsPlugin.getPartyHandler().getMembers(p)) {
                p.sendMessage(Utils.translate("&b- &e" + member.getName()));
            }

            return;
        }
        if ("leave".equalsIgnoreCase(args[0])) {
            BedWarsPlugin.getPartyHandler().leave(p);
            return;
        }
        if ("kick".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(Lang.PARTY_KICK_COMMAND_USAGE.getString());
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target.getName().equals(p.getName())) {
                p.sendMessage(Lang.PARTY_KICK_YOURSELF.getString());
                return;
            }

            BedWarsPlugin.getPartyHandler().kickMember(p, target);
            return;
        }

        if ("disband".equalsIgnoreCase(args[0])) {
            BedWarsPlugin.getPartyHandler().disband(p);
            return;
        }

        Lang.PARTY_HELP_PLAYERS.getList().forEach(s -> p.sendMessage(Utils.translate(s)));

    }

}
