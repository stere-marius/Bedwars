package ro.marius.bedwars.commands.subcommand;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.ISubCommand;
import ro.marius.bedwars.NPCPlayer;
import ro.marius.bedwars.NPCSkin;
import ro.marius.bedwars.game.mechanics.NPCArena;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.factory.BedwarsNPCFactory;
import ro.marius.bedwars.npc.BedwarsJoinNPC;
import ro.marius.bedwars.npc.SkinFetcher;
import ro.marius.bedwars.npc.SkinFetcherCallback;
import ro.marius.bedwars.npc.bedwars.BedwarsNPC;
import ro.marius.bedwars.utils.Utils;

import java.util.*;
import java.util.Map.Entry;

public class NPCommand implements ISubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player p = (Player) sender;

        if (args.length == 1) {
            p.sendMessage("");
            p.sendMessage(Utils.translate("&e-----------------------------------------------"));
            p.sendMessage(Utils.translate(
                    "&e⇨ /bedwars joinNPC spawn <arenaType> <skinName> <firstLine>;<secondLine>;<thirdLine>..."));
            p.sendMessage(Utils.translate("&e⇨ /bedwars joinNPC setSkin <UUID> <playerName>"));
            p.sendMessage(Utils.translate("&e⇨ /bedwars joinNPC list"));
            p.sendMessage("");
            p.sendMessage(Utils.translate("&e-----------------------------------------------"));

            return;
        }

        if ("spawn".equalsIgnoreCase(args[1])) {

            if (args.length < 5) {
                p.sendMessage(Utils.translate(
                        "&cInsufficient arguments: /bedwars joinNPC spawn <arenaType> <skinName> <firstLine>;<secondLine>;<thirdLine>..."));
                return;
            }

            String arenaType = args[2];
            String skinName = args[3];
            List<String> lines = new ArrayList<>();

            StringBuilder builder = new StringBuilder();

            for (int i = 4; i < args.length; i++) {
                builder.append(args[i]);
                builder.append(" ");
            }

            String[] split = builder.toString().split(";");

            Collections.addAll(lines, split);

            int npcIndex = ManagerHandler.getNPCManager().getNewNpcID();
            ManagerHandler.getNPCManager().spawnNPC(npcIndex, p.getLocation(), skinName, arenaType, lines);
            ManagerHandler.getNPCManager().saveNPC(p.getLocation(), npcIndex, skinName, arenaType, lines);

            return;
        }

        if ("list".equalsIgnoreCase(args[1])) {

            for (Entry<String, List<NPCArena>> entry : ManagerHandler.getNPCManager().getArenaTypeNpc().entrySet()) {

                String arenaType = entry.getKey();
                List<NPCArena> list = entry.getValue();

                p.sendMessage("");
                p.sendMessage(Utils.translate("&e-----------------------------------------------"));
                p.sendMessage(Utils.translate("&a" + arenaType + "'s Join NPC List &d[CLICKABLE MESSAGES]"));
                int index = 0;

                for (NPCArena npcArena : list) {

                    index++;

                    Location location = npcArena.getNpcHologram().getLocation();

                    TextComponent message = new TextComponent(Utils.translate("&e⇨ " + index + "."));

                    TextComponent teleport = new TextComponent(Utils.translate("   &a&lTELEPORT&f   "));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bedwars joinNPC teleport " + Utils.convertingString(location)));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));

                    TextComponent remove = new TextComponent(Utils.translate("&c&lREMOVE"));
                    remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/bedwars joinNPC remove " + npcArena.getUuid()));
                    remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to remove the NPC")).create()));

                    TextComponent npcSkin = new TextComponent(Utils.translate("   &c&lCHANGE SKIN   "));
                    npcSkin.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/bedwars joinNPC setSkin " + npcArena.getUuid() + " playerName"));
                    npcSkin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to write the command for changing the skin")).create()));

                    p.spigot().sendMessage(message, teleport, remove, npcSkin);
                }

            }

            return;
        }

        if ("setSkin".equalsIgnoreCase(args[1])) {

            if (args.length < 4) {
                p.sendMessage(Utils.translate("&e⇨ Usage: /bedwars joinNPC setSkin <UUID> <playerName>"));
                return;
            }

            UUID uuid = UUID.fromString(args[2]);
            NPCArena npcArena = ManagerHandler.getNPCManager().getNPCByUUID(uuid);

            p.sendMessage(Utils.translate("&aFetching the skin..."));

            SkinFetcher.getSkinFromName(args[3], npcSkin -> {
                ManagerHandler.getNPCManager().setSkin(npcArena.getIndex(), npcSkin);
                p.sendMessage(Utils.translate("&aThe skin has been updated."));
            });

            return;
        }

        if ("remove".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(Utils.translate("&e⇨ Usage: /bedwars joinNPC remove <UUID>"));
                return;
            }

            UUID uuid = UUID.fromString(args[2]);
            NPCArena npcArena = ManagerHandler.getNPCManager().getNPCByUUID(uuid);

            if (npcArena == null) {
                p.sendMessage(Utils.translate("&cCouldn't find the NPC with uuid " + uuid
                        + " . Use /bedwars joinNPC list for a better view of NPC."));
                return;
            }

            npcArena.remove();
            ManagerHandler.getNPCManager().removeNPC(npcArena.getIndex());
            ManagerHandler.getNPCManager().despawnNPC(npcArena.getIndex());
            p.sendMessage(Utils.translate("&aThe NPC has been removed successfully."));
            return;
        }

        if ("teleport".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(Utils.translate("&e⇨ Usage: /bedwars joinNPC teleport <location>"));
                return;
            }

            Location location = Utils.convertingLocation(args[2]);

            if (location == null) {
                p.sendMessage(Utils.translate("&cCould not find the location . The world might be null."));
                return;
            }

            p.teleport(location);

        }

    }

}
