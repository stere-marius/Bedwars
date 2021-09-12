package ro.marius.bedwars.commands;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.*;
import ro.marius.bedwars.commands.subcommand.EditCommand;
import ro.marius.bedwars.commands.subcommand.NPCommand;
import ro.marius.bedwars.commands.subcommand.TestCommand;
import ro.marius.bedwars.configuration.*;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.game.GameSetup;
import ro.marius.bedwars.game.mechanics.TeamSetup;
import ro.marius.bedwars.game.mechanics.worldadapter.SimpleWorldAdapter;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.listeners.GameEditListener;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.FAWEManager;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.menu.extra.ArenaInventory;
import ro.marius.bedwars.menu.extra.TeamSelectorInventory;
import ro.marius.bedwars.playerdata.APlayerData;
import ro.marius.bedwars.shopconfiguration.shopinventory.ShopInventory;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.upgradeconfiguration.upgradeinventory.UpgradeInventory;
import ro.marius.bedwars.utils.*;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;
import ro.marius.bedwars.utils.itembuilder.PotionBuilder;

import java.io.File;
import java.util.*;

public class BedwarsCommand extends AbstractCommand {

    private final String prefix = Utils.translate("&e⇨");
    private final String insfArgs = Utils.translate("&e⇨ Insufficent arguments: /bedwars");
    //	public static HashMap<Player, Team> team = new HashMap<>();
    private final Map<String, ISubCommand> subCommand = new HashMap<>();

    public BedwarsCommand() {
        super("bedwars");
        this.subCommand.put("joinNPC", new NPCommand());
        this.subCommand.put("arenaEdit", new EditCommand());
        this.subCommand.put("test", new TestCommand());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {

            if (sender.isOp() || sender.hasPermission("bedwars.admin")) {

                Lang.BEDWARS_HELP_ADMINS.getList().forEach(s -> sender.sendMessage(Utils.translate(s)));

                return;
            }

            Lang.BEDWARS_HELP_PLAYERS.getList().forEach(s -> sender.sendMessage(Utils.translate(s)));

            return;
        }

        if ("getJSON".equalsIgnoreCase(args[0])) {

            Gson gson = new Gson();
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("ServerUUID", "53b2c1eee5c04d08bebde0e1025bc8f7");
            jsonMap.put("ServerIP", Bukkit.getServer().getIp());
            jsonMap.put("ServerPort", Bukkit.getServer().getPort());
            jsonMap.put("GameName", "arena1");
            jsonMap.put("ArenaType", "SOLO");
            jsonMap.put("PlayersPerTeam", "1");
            jsonMap.put("MatchState", "WAITING");
            jsonMap.put("MatchPlayers", "0");
            Set<String> rejoinList = new HashSet<>();
            rejoinList.add("571542b0-8588-49d0-875c-97f99c8a4920");
            rejoinList.add("2be5117e-e200-4d12-896d-d6b1f99a25d9");
            rejoinList.add("53b2c1ee-e5c0-4d08-bebd-e0e1025bc8f7");
//            JSONArray rejoinJsonArray = new JSONArray();
//            rejoinList.forEach(rejoinUUID -> rejoinJsonArray.add(rejoinUUID));
            jsonMap.put("RejoinUUID", rejoinList);
            Set<String> spectatorsList = new HashSet<>();
            spectatorsList.add("571542b0-8588-49d0-875c-97f99c8a4920");
            spectatorsList.add("2be5117e-e200-4d12-896d-d6b1f99a25d9");
            spectatorsList.add("53b2c1ee-e5c0-4d08-bebd-e0e1025bc8f7");
//            JSONArray spectatorsJsonArray = new JSONArray();
//            spectatorsList.forEach(spectatorUUID -> spectatorsJsonArray.add(spectatorUUID));
            jsonMap.put("SpectatorUUID", spectatorsList);

            System.out.println(gson.toJson(jsonMap).toString());

            return;
        }

        if ("arenaPlayerInfo".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                sender.sendMessage(this.insfArgs + " arenaPlayerInfo <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                sender.sendMessage(this.prefix + "Couldn't find the arena " + args[1]);
                return;
            }

            AMatch match = game.getMatch();
            sender.sendMessage("Rejoin map " + DebugUtils.getRejoinMapToString(match));
            sender.sendMessage("Players " + DebugUtils.getPlayersToString(match));
            sender.sendMessage("Players team " + DebugUtils.getTeamsToString(match));

            StringJoiner stringJoiner = new StringJoiner(" , ");
            match.getPlayerTeam().values().forEach(team -> stringJoiner.add(team.getName() + " " + team.getPlayers()));

            sender.sendMessage("Player team map " + stringJoiner.toString());
            return;
        }


        if ("setShopKeeperSkin".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                sender.sendMessage(this.insfArgs + " setShopKeeperSkin <playerName> <skinName>");
                return;
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                sender.sendMessage(Lang.PLAYER_OFFLINE.getString());
                return;
            }

            if ((sender instanceof Player) && (!sender.hasPermission("bedwars.admin"))) {
                sender.sendMessage(Lang.NO_PERMISSION.getString());
                return;
            }

            Player p = Bukkit.getPlayer(args[1]);
            List<String> skins = Arrays.asList("BLAZE", "CREEPER", "SKELETON", "VILLAGER", "ZOMBIE", "PIGMAN");

            if (!skins.contains(args[2].toUpperCase())) {
                p.sendMessage(Lang.SKIN_DOESNT_EXIST.getString().replace("<skinName>", args[2])
                        .replace("<availableSkins>", "BLAZE, CREEPER, SKELETON, VILLAGER, ZOMBIE, PIGMAN"));
                return;
            }

            if (!p.hasPermission("shopkeeperskin." + args[2].toLowerCase())) {
                sender.sendMessage(Lang.NO_PERMISSION.getString());
                p.sendMessage(Lang.NO_PERMISSION_FOR_SKIN.getString().replace("<skinName>", args[1]));
                p.closeInventory();
                return;
            }

            ManagerHandler.getGameManager().getData(p).setSkin(args[2].toUpperCase());

            p.sendMessage(Lang.YOUR_SKIN_SET.getString().replace("<skinName>", args[2]));
            sender.sendMessage(Lang.PLAYER_SKIN_SET.getString().replace("<skinName>", args[2]).replace("<playerName>",
                    p.getName()));
            return;
        }

        if (!(sender instanceof Player)) {

            return;
        }

        Player p = (Player) sender;

        if ("closeInventory".equalsIgnoreCase(args[0])) {
            p.closeInventory();
            return;
        }

        if (args[0].equalsIgnoreCase("spawnNPC")) {

            NPCPlayer npcPlayer = ManagerHandler.getVersionManager().getNewNPC();
            npcPlayer.spawnNPC(p.getLocation(), new NPCSkin("eyJ0aW1lc3RhbXAiOjE1Njg1NjQzMTk3ODgsInByb2ZpbGVJZCI6ImMxYWYxODI5MDYwZTQ0OGRhNjYwOWRmZGM2OGEzOWE4IiwicHJvZmlsZU5hbWUiOiJCQVJLeDQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU1NTlkZmU4ZGJhNWM4ODlkMDE2MTJhNTMxYjhkNDY5YzBmMzE3ZDk3OTcxOTA4ZWZiNjNhYTk0MzE1Yzg3YjkiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==",
                    "mH5lrRm89N5X65cG2bSC6ukfERdmM1Es1Z2a4JXzJD+42nljSys3QVYmgzZeFgq660fRWyn7zPZQBP/iNQSWb3t1Qx4nVwiMvDSGeEjO80LRIvMJRv4zKPJh2HCJ1a8POkDS0kytYIZ2OhcTnYfHrWHBHJlk9e8tjJIgu9EOtCR3FJVyuUVZpcXF7u65mRiSp5xlAGiGLVVC9LPIUe0suvkRDEGDOWiFKjMGH403RDMb4Qn4vvyXpY2K7T8IA9jB9JwahdXk0or3Oz7DGhMPPDBe3gktN1XJFn3UkaBMLjiM4tksHIvCi/AqadFr4bN9PpKdKUde+L4Q/w64l49hkTbfL2DK5XgSBqcCfMRT2gqm4T6xkeANMafF7vIVyOHoP+FARZR9FHv0ER9yhhEVOvihtLpND2+pUau2a+gxbtpPhFDp42rV5mxH5rS2uiNLrfNVqEI4Q8wBXbt03J8aLerVbF8uVzkLrfy7qpgUJk1Lw4luKqELApf/c5nBRpPyu2h2RzKTWbf6wDHWPMhyzohmGWQSsvL3rkJl2QkOQH1+FUSI3rQ4we3mA9RyWHQEB0BwbNValreHxU0n5a4KYTJS5e0y2wt+63xTgMURxbWsNvPezTghvFOWK3zP+is/NKJcdNad046fnHk3DCNpCDO/naYHhmK0ei+icdlTsf8="));
            npcPlayer.getViewers().add(p);
            npcPlayer.hideName();
            npcPlayer.sendSpawnPackets(new HashSet<>(Collections.singletonList(p)));
            Bukkit.getScheduler().scheduleSyncDelayedTask(BedWarsPlugin.getInstance(), npcPlayer::removeFromTablist, 20);
            p.sendMessage("Spawned");

            return;
        }

        if ("getHolograms".equalsIgnoreCase(args[0])) {

            PlayerHologram playerHologram = ManagerHandler.getHologramManager().getPlayerHologram(p);
            int size = playerHologram.getPlayerHolograms().size();
            p.sendMessage("Player hologram size " + size);
            p.sendMessage("Player hologram location list " + playerHologram.getLocationHolograms().size());
            p.sendMessage("Player hologram list " + playerHologram.getPlayerHolograms().values().size());

            return;
        }

        if ("getTeamSelector".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
            p.openInventory(new TeamSelectorInventory(match).getInventory());
            p.sendMessage("OPENED");

            return;
        }

        if ("arenasGUI".equalsIgnoreCase(args[0])) {

            if (args.length >= 2) {
                p.openInventory(new ArenaInventory(args[1]).getInventory());
                return;
            }

            p.openInventory(new ArenaInventory().getInventory());

            return;
        }

        if ("join".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(Lang.JOIN_COMMAND_USAGE.getString());
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Lang.GAME_NULL.getString());
                return;
            }

            game.getMatch().addPlayer(p);
            return;
        }

        if ("leave".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {
                p.sendMessage(Lang.NOT_IN_GAME.getString());
                return;
            }

            match.removePlayer(p);

            return;
        }

        if ("randomJoin".equalsIgnoreCase(args[0])) {

            if (ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId())) {
                p.sendMessage(Lang.ALREADY_IN_GAME.getString());
                return;
            }

            if (args.length >= 2) {

                String arenaType = args[1];
                AMatch match = ManagerHandler.getGameManager().getMatchByArenaType(arenaType);

                if (match == null) {
                    p.sendMessage(Lang.COULD_NOT_FIND_MATCH.getString());
                    return;
                }

                match.addPlayer(p);

                return;
            }

            AMatch match = ManagerHandler.getGameManager().getEmptyMatch();

            if (match == null) {
                p.sendMessage(Lang.COULD_NOT_FIND_MATCH.getString());
                return;
            }

            match.addPlayer(p);

            return;
        }

        if ("test".equalsIgnoreCase(args[0])) {
            this.subCommand.get("test").onCommand(p, args);
            return;
        }

        if (!p.hasPermission("bedwars.admin")) {
            p.sendMessage(Lang.NO_PERMISSION.getString());
            return;
        }

        if ("list".equalsIgnoreCase(args[0])) {

            return;
        }

        if ("closeArena".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Lang.GAME_NULL.getString());
                return;
            }

            game.getMatch().setMatchState(MatchState.CLOSED);
            p.sendMessage(Utils.translate("&a>> The arena " + args[1] + " has been closed"));
            return;
        }

        if ("openArena".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Lang.GAME_NULL.getString());
                return;
            }

            game.getMatch().setMatchState(MatchState.IN_WAITING);
            p.sendMessage(Utils.translate("&a>> The arena " + args[1] + " has been opened"));
            return;
        }

        if ("spawnGenerator".equalsIgnoreCase(args[0])) {

            DiamondGenerator gen = new DiamondGenerator(p.getLocation(),
                    ManagerHandler.getGameManager().getGame("arena1").getMatch());
            gen.spawn();
            gen.getSupportArmorStand().setVisible(true);

            new BukkitRunnable() {

                double y = 0;
                boolean up = true;
                int secTeleport = 0;

                boolean inverse = true;
//				int secInverse = 0;

                @Override
                public void run() {

                    this.secTeleport += 1;
                    this.y += 0.03;

                    if (this.secTeleport == 7) {
                        this.inverse = false;
                        this.up = false;
                        this.y = 0;
                        return;
                    }

                    if (this.secTeleport == 14) {
                        this.inverse = true;
                        this.up = true;
                        this.y = 0;
                        this.secTeleport = 0;
                        return;
                    }

                    Location location = this.up ? gen.getSupportArmorStand().getLocation().subtract(0, this.y, 0)
                            : gen.getSupportArmorStand().getLocation().add(0, this.y, 0);
                    location.setYaw(location.getYaw() + ((this.inverse ? (40 + this.secTeleport) : (-40 - this.secTeleport)) + 5));
                    gen.getSupportArmorStand().teleport(location);

                }
            }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 3);

            p.sendMessage("DEBUG");

            return;
        }

        if ("forceStart".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {

                if (args.length < 2) {
                    p.sendMessage(Lang.FORCE_START_COMMAND_USAGE.getString());
                    return;
                }

                Game game = ManagerHandler.getGameManager().getGame(args[1]);

                if (game == null) {
                    p.sendMessage(Lang.GAME_NULL.getString());
                    return;
                }

                AMatch gameMatch = game.getMatch();

                if (!gameMatch.isForceStart()) {
                    gameMatch.setForceStart(true);
                    gameMatch.startGame();
                    gameMatch.setCancelledTask();
                    p.sendMessage(Lang.GAME_FORCE_START.getString().replace("<arenaName>", game.getName()));
                    return;
                }

                gameMatch.setForceStart(false);
                gameMatch.setCancelledTask();
                p.sendMessage(Lang.STOPPED_FORCE_START.getString().replace("<arenaName>", game.getName()));

                return;
            }

            if (!match.isForceStart()) {
                match.setForceStart(true);
                match.startGame();
                match.setCancelledTask();
                p.sendMessage(Lang.GAME_FORCE_START.getString().replace("<arenaName>", match.getGame().getName()));
                return;
            }

            match.setForceStart(false);
            match.setCancelledTask();
            p.sendMessage(Lang.STOPPED_FORCE_START.getString().replace("<arenaName>", match.getGame().getName()));

            return;
        }

        if ("forceStop".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {

                if (args.length < 2) {
                    p.sendMessage(Lang.FORCE_STOP_COMMAND_USAGE.getString());
                    return;
                }

                Game game = ManagerHandler.getGameManager().getGame(args[1]);

                if (game == null) {
                    p.sendMessage(Lang.GAME_NULL.getString());
                    return;
                }

                AMatch gameMatch = game.getMatch();
                gameMatch.endGame("FORCE-STOP");
                p.sendMessage(Lang.GAME_FORCED_STOPPED.getString().replace("<arenaName>", game.getName()));

                return;
            }

            match.endGame("FORCE-STOP");
            p.sendMessage(Lang.GAME_FORCED_STOPPED.getString().replace("<arenaName>", match.getGame().getName()));

            return;
        }

        if ("setStartingTime".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " setStartingTime <arenaName> <startingTime> [players-size]");
                p.sendMessage(Utils.translate("&e⇨     <argumentName> - mandatory arguments"));
                p.sendMessage(Utils.translate("&e⇨     [argumentName] - optional arguments"));
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&cThe arena " + args[1] + " does not exist."));
                return;
            }

            if (!Utils.isInteger(args[2])) {
                p.sendMessage(Utils.translate("&cThe third argument (startingTime) is not a number"));
                return;
            }

            int time = Utils.getInteger(args[2]);

            if (time <= 0) {
                p.sendMessage(Utils.translate("&c⇨The starting time must be greater than 0 (zero)."));
                return;
            }

            if (args.length == 3) {
                game.getArenaStartingTime().put(0, Utils.getInteger(args[2]));
                p.sendMessage(Utils.translate("&a⇨The starting time for arena has been set to " + time + " ."));
            }

            if (args.length == 4) {

                if (!Utils.isInteger(args[3])) {
                    p.sendMessage(Utils.translate("&cThe fourth argument (players-size) is not a number"));
                    return;
                }

                int playersSize = Utils.getInteger(args[3]);

                if (playersSize <= 0) {
                    p.sendMessage(Utils.translate("&c⇨The players-size argument must be greater than 0 (zero)."));
                    return;
                }

                if (playersSize > game.getMaxPlayers()) {
                    p.sendMessage(
                            Utils.translate("&c⇨The players-size argument can't be greater than arena max-players ("
                                    + playersSize + ">" + game.getMaxPlayers() + ")"));
                    return;
                }

                game.getArenaStartingTime().put(playersSize, time);
                p.sendMessage(Utils
                        .translate("&a⇨The starting time for " + playersSize + " players has been set to " + time));
            }

            ManagerHandler.getGameManager().saveStartingTime(game);
            return;
        }

        if ("showPlayer".equalsIgnoreCase(args[0])) {

            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.showPlayer(p);
                p.showPlayer(pl);
            }

            p.sendMessage("DONE");
            return;
        }

        if ("hidePlayer".equalsIgnoreCase(args[0])) {

            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.showPlayer(p);
                p.showPlayer(pl);
            }

            p.sendMessage("DONE");
            return;
        }

        if ("givePotions".equalsIgnoreCase(args[0])) {

            PotionBuilder potionBuilder = new PotionBuilder(1);

            if (args.length >= 5) {
                potionBuilder.setPotionBaseType(PotionType.valueOf(args[1]));
                potionBuilder.addEffectType(PotionEffectType.getByName(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            }

            if (args.length == 2) {
                potionBuilder.setPotionBaseType(PotionType.valueOf(args[1]));
            }


            ItemBuilder itemBuilder = new ItemBuilder(potionBuilder);

            p.getInventory().addItem(itemBuilder.build());
            p.getInventory().addItem(potionBuilder.build());
            p.sendMessage("DONE");

            return;
        }

        if ("spawnGolem".equalsIgnoreCase(args[0])) {

            Location location = p.getLocation();
            ManagerHandler.getVersionManager().getVersionWrapper().spawnGolem(location).setCustomName("Nume");
            ManagerHandler.getVersionManager().getVersionWrapper().spawnBlaze(location.add(1, 0, 0));
            ManagerHandler.getVersionManager().getVersionWrapper().spawnCreeper(location.add(1, 0, 0));
            ManagerHandler.getVersionManager().getVersionWrapper().spawnSkeleton(location.add(1, 0, 0));
            p.sendMessage("Spawned");
            return;
        }

        if ("repause".equalsIgnoreCase(args[0])) {

            if (args.length <= 1) {
                p.sendMessage(Utils.translate("&cYou must provide the arena name: /bedwars repause <arenaName>"));
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Lang.GAME_NULL.getString());
                return;
            }

            game.getMatch().setMatchState(MatchState.IN_WAITING);
            p.sendMessage("&eThe arena state has been setting to IN_WAITING");

            return;
        }

        if ("myLocationInfo".equalsIgnoreCase(args[0])) {
            Location location = p.getLocation();
            p.sendMessage("World: " + location.getWorld().getName());
            p.sendMessage("X: " + location.getBlockX());
            p.sendMessage("Y: " + location.getBlockY());
            p.sendMessage("Z: " + location.getBlockZ());
            return;
        }

        if ("removeJoinNPCHolograms".equalsIgnoreCase(args[0])) {

            int i = 0;

            for (Entity entity : p.getWorld().getEntities()) {

                if (entity.getType() != EntityType.ARMOR_STAND)
                    continue;
                if (!entity.hasMetadata("BedwarsStand"))
                    continue;

                entity.remove();
                i++;
            }

            p.sendMessage(Utils.translate("&eRemoved " + i + " holograms"));
            return;
        }

        if ("openShopInventory".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame("arena1");
            ShopInventory inv = game.getShopPath().getInventory().get("POTIONS");
            Team team = game.getMatch().findAvailableTeam();
            game.getMatch().getPlayerTeam().put(p.getUniqueId(), team);
            inv.setGame(game);
            inv.setPlayer(p);
            inv.setTeam(team);
            p.openInventory(inv.getInventory());

            return;
        }

        if ("openUpgradeInventory".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame("arena1");
            UpgradeInventory inv = game.getUpgradePath().getUpgradeInventoryMap().get("MAIN_INVENTORY");
            Team team = game.getMatch().findAvailableTeam();
            team.getPlayers().add(p);
            game.getMatch().getPlayerTeam().put(p.getUniqueId(), team);
            inv.setGame(game);
            inv.setPlayer(p);
            inv.setTeam(team);
            p.openInventory(inv.getInventory());
            ManagerHandler.getGameManager().getPlayerMatch().put(p.getUniqueId(), game.getMatch());

            return;
        }

        if ("removeArmorStand".equalsIgnoreCase(args[0])) {

            double distance = Double.parseDouble(args[1]);

            for (Entity e : p.getNearbyEntities(distance, distance, distance)) {

                if (e instanceof ArmorStand) {
                    e.remove();
                }

            }

            p.sendMessage("DONE");

            return;
        }

        if ("canSee".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage("/bedwars canSee <firstPlayer> <secondPlayer>");
                return;
            }

            Player firstPlayer = Bukkit.getPlayer(args[1]);
            Player secondPlayer = Bukkit.getPlayer(args[2]);

            if (firstPlayer == null) {
                p.sendMessage("The player " + args[1] + " is not online.");
                return;
            }

            if (secondPlayer == null) {
                p.sendMessage("The player " + args[2] + " is not online.");
                return;
            }

            String msg = firstPlayer.canSee(secondPlayer)
                    ? ("The player " + firstPlayer.getName() + " can see the player " + secondPlayer.getName())
                    : ("The player " + firstPlayer.getName() + " can't see the player " + secondPlayer.getName());
            p.sendMessage(msg);

            return;
        }

        if ("arenaEdit".equalsIgnoreCase(args[0])) {

            // TODO Check for game stopped

            if (args.length == 1 && ManagerHandler.getGameManager().getGameEdit().containsKey(p.getUniqueId())) {
                this.subCommand.get("arenaEdit").onCommand(p, args);
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " arenaEdit <arenaName>");
                return;
            }

            Game editedGame = ManagerHandler.getGameManager().getGame(args[1]);

            if (editedGame == null) {
                p.sendMessage(Utils.translate("&c>> Could not find the arena " + args[1]));
                return;
            }

            GameEdit gameEdit = new GameEdit(editedGame);

            if (!p.getInventory().contains(GameEditListener.EDIT_GAME_ITEM)) {
                p.getInventory().setItem(0, GameEditListener.EDIT_GAME_ITEM);
            }

            ManagerHandler.getGameManager().getGameEdit().put(p.getUniqueId(), gameEdit);
            this.subCommand.get("arenaEdit").onCommand(p, args);

            return;
        }

        if ("giveNewWool".equalsIgnoreCase(args[0])) {


            return;
        }

        if ("generateArenaOptions".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " generateArenaOptions <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&eThe arena " + args[1] + " does not exist."));
                return;
            }

            if (args.length == 2) {
                TextComponent message = new TextComponentBuilder(
                        "&7>> Do you want to generate the settings in a different file? ").build();
                TextComponent yes = new TextComponentBuilder("&aYES ")
                        .withClickEvent(Action.RUN_COMMAND, "/bedwars generateArenaOptions " + args[1] + " true")
                        .build();
                TextComponent no = new TextComponentBuilder("&cNO")
                        .withClickEvent(Action.RUN_COMMAND, "/bedwars generateArenaOptions " + args[1] + " false")
                        .build();
                p.spigot().sendMessage(message, yes, no);
                return;
            }

            boolean b = Boolean.parseBoolean(args[2]);

            if (!b) {
                p.sendMessage("&aYou've declined the creation of the arena options for arena " + args[1]);
                return;
            }

            ArenaOptions arenaOptions = new ArenaOptions();
            arenaOptions.generateOptions(game.getArenaType(), true);
            game.setArenaOptions(arenaOptions);
            ManagerHandler.getGameManager().getArenaOptions().put(game.getArenaType(), arenaOptions);
            p.sendMessage("&aThe arena settings path has been generated.");
            return;
        }

        if ("getPluginVersion".equalsIgnoreCase(args[0])) {
            p.sendMessage(
                    Utils.translate("&e⇨Plugin version is: " + BedWarsPlugin.getInstance().getDescription().getVersion()));
            return;
        }

        if ("holograms".equalsIgnoreCase(args[0])) {

            List<Location> list = ManagerHandler.getHologramManager().getLocationHolograms();

            if (list.isEmpty()) {
                p.sendMessage(Utils.translate("&cThere is no active hologram."));
                return;
            }

            p.sendMessage("");
            p.sendMessage(Utils.translate("&e-----------------------------------------------"));
            p.sendMessage(Utils.translate("&aStats holograms locations: "));
            for (int i = 0; i < list.size(); i++) {

                Location location = list.get(i);

                TextComponent message = new TextComponent(Utils.translate("&e⇨ " + location.getWorld().getName() + " , "
                        + location.getBlockX() + " , " + location.getBlockY() + " , " + location.getBlockZ()));

                TextComponent teleport = new TextComponent(Utils.translate("   &a&lTELEPORT&f   "));
                teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND,
                        "/bedwars teleportHologram " + Utils.convertingString(location)));
                teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));

                TextComponent remove = new TextComponent(Utils.translate("&c&lREMOVE"));
                remove.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars removeHologram " + i));
                remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(Utils.translate("&aClick me to remove the hologram")).create()));

                p.spigot().sendMessage(message, teleport, remove);

            }

            p.sendMessage(Utils.translate("&e-----------------------------------------------"));
            p.sendMessage("");

            return;
        }

        if ("removeHologram".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " removeHologram <index>");
                return;
            }

            List<Location> list = ManagerHandler.getHologramManager().getLocationHolograms();

            if (list.isEmpty()) {
                p.sendMessage(Utils.translate("&cThere is no active hologram."));
                return;
            }

            if (!Utils.isInteger(args[1])) {
                p.sendMessage(this.prefix + " Argument one (" + args[1] + ") must be a number");
                return;
            }

            int index = Integer.parseInt(args[1]);

            if (index >= list.size()) {
                p.sendMessage(Utils.translate("&cThis hologram doesn't exist anymore."));
                return;
            }

            Location location = list.get(index);
            ManagerHandler.getHologramManager().removeLocation(location);
            ManagerHandler.getHologramManager().removePlayersHologram();
            ManagerHandler.getHologramManager().spawnPlayersHologram();
            p.sendMessage(Utils.translate("&e⇨ &aHologram has been removed."));

            return;
        }

        if ("teleportHologram".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " teleportHologram <stringLocation>");
                return;
            }

//			List<Location> list = ManagerHandler.getHologramManager().getLocationHolograms();

//			if (list.size() == 0) {
//				p.sendMessage(Utils.translate("&cThere is no active hologram."));
//				return;
//			}

            Location location = Utils.convertingLocation(args[1]);

            p.teleport(location);
            p.sendMessage(Utils.translate("&e⇨ You have been teleported."));

            return;
        }

        if ("spawnStatsHologram".equalsIgnoreCase(args[0])) {
            ManagerHandler.getHologramManager().addLocation(p.getLocation());
            ManagerHandler.getHologramManager().getPlayerHologram(p).spawnOneStatsHologram();
            p.sendMessage(this.prefix + " A new stats hologram has been spawned at your current location.");

            return;
        }

        if ("joinNPC".equalsIgnoreCase(args[0])) {

            if (ManagerHandler.getNPCManager() == null) {
                p.sendMessage(Utils.translate(
                        "&cYou have to install Citizens to spawn an npc. Get it for free from here: https://www.spigotmc.org/resources/citizens.13811/"));
                return;
            }

//			/bedwars spawnJoinNPC <arenaType> <skinName> <lines>

            this.subCommand.get("joinNPC").onCommand(sender, args);

            return;
        }

        if ("updateNPC".equalsIgnoreCase(args[0])) {

            for (Game game : ManagerHandler.getGameManager().getGames()) {
                game.notifyObservers();
                p.sendMessage("UPDATING " + game.getName() + " npc");
            }

            return;
        }

        if ("spawnVillager".equalsIgnoreCase(args[0])) {
            Location location = p.getLocation();
            ManagerHandler.getVersionManager().getVersionWrapper().spawnVillager(p.getLocation());
            ManagerHandler.getVersionManager().getVersionWrapper().spawnBlaze(location);
            ManagerHandler.getVersionManager().getVersionWrapper().spawnCreeper(location);
            ManagerHandler.getVersionManager().getSpawnedEntity("VILLAGER", p.getLocation());
            p.sendMessage("SPAWNED");
            return;
        }
        if ("saveSchematic".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " saveSchematic <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&cArena " + args[1] + " does not exist."));
                return;
            }

            if (!BedWarsPlugin.getInstance().isFAWE()) {
                p.sendMessage(Utils.translate("&cYou have not activated FastAsyncWorldEdit or the plugin is missing."));
                return;
            }

            CuboidSelection selection = game.getGameCuboid();

            if (!FAWEManager.saveSchematic(selection.getPositionOne().getWorld(), selection.getPositionOne(),
                    selection.getPositionTwo(), args[1])) {
                p.sendMessage(Utils.translate("&cCouldn't save the schematic for arena " + args[1]));
                return;
            }

            p.sendMessage(Utils.translate("&aThe schematic has been saved for arena " + args[1]));

            return;
        }

        if ("getStats".equalsIgnoreCase(args[0])) {

            APlayerData playerData = ManagerHandler.getGameManager().getPlayerData().get(p.getUniqueId());

            if (args.length >= 2) {

                String arenaType = args[1];

                p.sendMessage("Deaths " + playerData.getDeaths(arenaType));
                p.sendMessage("Kills " + playerData.getKills(arenaType));
                p.sendMessage("GamesPlayed " + playerData.getGamesPlayed(arenaType));
                p.sendMessage("BedsLost " + playerData.getBedsLost(arenaType));
                p.sendMessage("BedsBroken " + playerData.getBedsBroken(arenaType));

                return;
            }

            p.sendMessage("Deaths " + playerData.getTotalDeaths());
            p.sendMessage("Kills " + playerData.getTotalKills());
            p.sendMessage("GamesPlayed " + playerData.getTotalGamesPlayed());
            p.sendMessage("BedsLost " + playerData.getTotalBedsLost());
            p.sendMessage("BedsBroken " + playerData.getTotalBedsBroken());
            p.sendMessage("Final Kills " + playerData.getTotalFinalKills());
            p.sendMessage("Final Deaths " + playerData.getTotalFinalKills());
            p.sendMessage("Total Defeats " + playerData.getTotalDefeats());

            return;
        }

        if ("incrementStats".equalsIgnoreCase(args[0])) {

            APlayerData playerData = ManagerHandler.getGameManager().getPlayerData().get(p.getUniqueId());

            playerData.addDeaths("SOLO", 10);
            playerData.addKills("SOLO", 10);
            playerData.addGamePlayed("SOLO");
            playerData.addBedsBroken("SOLO", 1);
            playerData.addBedLost("SOLO");
            playerData.addFinalKills("SOLO", 1);
            playerData.addWin("SOLO");
            playerData.addDefeat("SOLO");

            p.sendMessage("DONE");

            return;
        }

        if ("saveStats".equalsIgnoreCase(args[0])) {

            APlayerData playerData = ManagerHandler.getGameManager().getPlayerData().get(p.getUniqueId());
            playerData.saveData();

            p.sendMessage("SAVED");

            return;
        }

        if ("arenas".equalsIgnoreCase(args[0])) {
            StringJoiner strJoiner = new StringJoiner(" , ");

            for (Game game : ManagerHandler.getGameManager().getGames()) {
                strJoiner.add(game.getName());
            }

            p.sendMessage(Utils.translate("&e⇨" + strJoiner));

            return;
        }

        if ("clone".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " clone <existingArena> <newArenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&cThe arena " + args[1] + " doesn't exist."));
                return;
            }

            MatchState matchState = game.getMatch().getMatchState();
            boolean canClone = matchState == MatchState.IN_WAITING || matchState == MatchState.CLOSED;

            if (!canClone) {
                p.sendMessage(Utils.translate("&cA match is being played in this arena. You can't clone it for the moment."));
                return;
            }

            String newGame = args[2];

            if (ManagerHandler.getGameManager().getGame(newGame) != null) {
                p.sendMessage(Utils.translate("&cThere is already an arena with name " + newGame));
                return;
            }

            ManagerHandler.getGameManager().cloneGame(game, newGame, sender);

            return;
        }


        if ("getWorlds".equalsIgnoreCase(args[0])) {

            for (World world : Bukkit.getWorlds()) {
                p.sendMessage(world.getName());
            }

            return;
        }

        if ("respawn".equalsIgnoreCase(args[0])) {

            Player target = Bukkit.getPlayer(args[1]);

            p.sendMessage("Target is dead: " + target.isDead());

            target.spigot().respawn();

            return;
        }

        if ("getWorldContainer".equalsIgnoreCase(args[0])) {

            p.sendMessage(Bukkit.getWorldContainer().getAbsolutePath() + " | " + Bukkit.getWorldContainer().getName());

            return;
        }

        if ("regenerateArena".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            ManagerHandler.getWorldManager().regenerateWorld(Bukkit.getWorld(game.getName()), new WorldCallback() {

                @Override
                public void onComplete(World result, String[] message) {
                    p.sendMessage("DONE");

                }

                @Override
                public void onError(String[] message) {
                    // TODO Auto-generated method stub

                }
            });

            return;
        }

        if ("deleteWorldFolder".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " deleteWorldFolder <worldName>");
                return;
            }

            World world = Bukkit.getWorld(args[1]);
            FileUtils.deleteFolder(world.getWorldFolder());
            p.sendMessage("DONE");

            return;
        }

        if ("replaceArenaWorlds".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " replaceArenaWorlds <arenaName>");
                return;
            }

            World world = Bukkit.getWorld(args[1]);
            File worldSaved = new File(
                    BedWarsPlugin.getInstance().getDataFolder().getPath() + "/WorldSaves/" + world.getName());
            SimpleWorldAdapter worldAdapter = (SimpleWorldAdapter) ManagerHandler.getWorldManager().getWorldAdapter();
            worldAdapter.copyWorldFolder(worldSaved, world.getWorldFolder());
            p.sendMessage("DONE");

            return;
        }

        if ("testRegenerateArena".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " testRegenerateArena <arenaName>");
                return;
            }

            World world = Bukkit.getWorld(args[1]);

            for (Chunk chunk : world.getLoadedChunks()) {
                chunk.unload();
            }

            SimpleWorldAdapter worldAdapter = (SimpleWorldAdapter) ManagerHandler.getWorldManager().getWorldAdapter();

            WorldCreator worldCreator = new WorldCreator(args[1]);
            worldCreator.seed(world.getSeed());
            worldCreator.generateStructures(false);
            worldCreator.generator(worldAdapter.getVOID_GENERATOR());
            World createdWorld = worldCreator.createWorld();
            createdWorld.setKeepSpawnInMemory(false);
            createdWorld.setSpawnFlags(true, false);
            createdWorld.setAutoSave(false);
            createdWorld.setTime(6000L);
            createdWorld.setThundering(false);
            createdWorld.setThunderDuration(Integer.MAX_VALUE);
            createdWorld.setStorm(false);
            createdWorld.setTime(0L);

            Bukkit.getWorlds().add(createdWorld);
            p.sendMessage("The world " + args[1] + " has loaded successfully.");

            return;
        }

        if ("setLobby".equalsIgnoreCase(args[0])) {

            if (ManagerHandler.getGameManager().getGame(p.getLocation().getWorld().getName()) != null) {
                p.sendMessage(Utils.translate("&cYou can't have an arena in the same world as lobby "));
                return;
            }

            BedWarsPlugin.getInstance().getConfig().set("LobbyLocation", Utils.convertingString(p.getLocation()));
            BedWarsPlugin.getInstance().saveConfig();
            p.sendMessage(Utils.translate("&eLobby location has been set."));
            return;
        }

        if ("endGame".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            game.getMatch().endGame("NORMAL");

            p.sendMessage("DONE");

            return;
        }

        if ("setPlayerTeam".equalsIgnoreCase(args[0])) {

            ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId()).getPlayerTeam().put(p.getUniqueId(),
                    ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId()).getTeams().get(1));
            p.sendMessage("ADDED");

            return;
        }

        if ("arenaInfo".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            Location waiting = game.getWaitingLocation().getLocation();

            Utils.sendPerformCommand(p, "/bedwars teleportToArena " + Utils.convertingString(waiting),
                    "Teleport to waiting location " + Utils.convertingString(waiting), "");

            for (Team team : game.getTeams()) {

                Location spawn = team.getSpawnLocation().getLocation();

                Utils.sendPerformCommand(p, "/bedwars teleportToArena " + Utils.convertingString(spawn),
                        "Click to teleport to team " + team.getName() + "'s spawn" + Utils.convertingString(spawn), "");

            }

            return;
        }

        if ("teleportToArena".equalsIgnoreCase(args[0])) {

            Location location = Utils.convertingLocation(args[1]);
            p.teleport(location);
            p.sendMessage("TELEPORTED");

            return;
        }

        if ("spawnVillager".equalsIgnoreCase(args[0])) {

            ManagerHandler.getVersionManager().getSpawnedEntity("VILLAGER", p.getLocation());
            ManagerHandler.getVersionManager().getSpawnedEntity("CREEPER", p.getLocation());
            p.sendMessage("Spawned");
            return;
        }

        if ("nextEvent".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {
                p.sendMessage("You're not in a match");
                return;
            }

            match.getEvent().setSeconds(3);
            p.sendMessage("DONE");

            return;
        }

        if ("tpWorld".equalsIgnoreCase(args[0])) {

            World world = Bukkit.getWorld(args[1]);
            p.teleport(world.getSpawnLocation());

            new BukkitRunnable() {

                @Override
                public void run() {
                    p.setGameMode(GameMode.CREATIVE);

                }
            }.runTaskLater(BedWarsPlugin.getInstance(), 30);

            return;
        }

        if ("getItems".equalsIgnoreCase(args[0])) {

            p.getInventory().addItem(Items.GAME_LEAVE.toItemStack());
            p.getInventory().addItem(XMaterial.matchXMaterial("STAINED_GLASS_PANE", (byte) 14).parseItem());

            return;
        }

        if ("putEmptyTeam".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame("arena1");
            AMatch match = game.getMatch();
            Team team = match.getPlayerTeam().containsKey(p.getUniqueId()) ? match.getPlayerTeam().get(p.getUniqueId())
                    : match.findEmptyTeam();
            team.getPlayers().add(p);
            match.getPlayerTeam().put(p.getUniqueId(), team);
            ManagerHandler.getGameManager().getPlayerMatch().put(p.getUniqueId(), match);
            p.sendMessage("Added to team " + team.getColorName());

        }

        if ("saveGameWorld".equalsIgnoreCase(args[0])) {

            ManagerHandler.getWorldManager().saveWorld(args[1]);

            p.sendMessage("Saved. Entities " + Bukkit.getWorld(args[1]).getEntities().size());

            return;
        }


        if ("tpMidOfMap".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(Utils.translate("&c/bedwars tpMidOfMap <arenaName>"));
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);
            CuboidSelection selection = game.getGameCuboid();

            p.teleport(selection.getMid());

            return;
        }

        if ("addAllInArena".equalsIgnoreCase(args[0])) {

            if (args.length == 1) {
                p.sendMessage(Utils.translate("&e&l>> Please provide the arena name: /bedwars addAllInArena <arenaName>"));
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&e&l>> Could not find the arena " + args[1]));
                return;
            }

            AMatch match = ManagerHandler.getGameManager().getGame(args[1]).getMatch();

            for (Player player : Bukkit.getOnlinePlayers()) {

                match.addPlayer(player);

            }

            p.sendMessage("DONE");

            return;
        }


        if ("createWorld".equalsIgnoreCase(args[0])) {

            ManagerHandler.getWorldManager().createWorld(args[1], new WorldCallback() {

                @Override
                public void onError(String[] message) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onComplete(World result, String[] message) {
                    p.teleport(result.getSpawnLocation());

                }
            });

            return;
        }

        if ("gameInfo".equalsIgnoreCase(args[0])) {

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage("Nu a fost gasit nicio arena cu numele " + args[1]);
                return;
            }

            p.sendMessage(game.getShopPathName() + " : " + (game.getShopPath() == null ? "NULL"
                    : game.getShopPath().getPlayerUpgrade().size() + " , " + game.getShopPath().getInventory().size()));

            p.sendMessage(game.getUpgradePathName() + " : "
                    + (game.getUpgradePath() == null ? "NULL "
                    : game.getUpgradePath().getUpgrades().size() + " , "
                    + game.getUpgradePath().getUpgradeInventoryMap().size()));

            return;
        }

        if ("generatorInfo".equalsIgnoreCase(args[0])) {

            AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

            if (match == null) {
                p.sendMessage(ChatColor.RED + "You're not in an arena");
                return;
            }

            if (args.length >= 2) {

                Team team = match.getTeamAlive(args[1]);

                if (team == null) {
                    p.sendMessage(ChatColor.RED + "There is no team with the name " + args[1]);
                    return;
                }

                FloorGenerator ironGenerator = team.getIronFloorGenerator();
                FloorGenerator goldGenerator = team.getGoldFloorGenerator();
                FloorGenerator emeraldGenerator = team.getEmeraldFloorGenerator();

                if (ironGenerator != null) {
                    p.sendMessage(ironGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO IRON GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
                            + Utils.convertingString(ironGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (goldGenerator != null) {
                    p.sendMessage(goldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO GOLD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
                            + Utils.convertingString(goldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (emeraldGenerator != null) {
                    p.sendMessage(emeraldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO EMERALD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
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
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
                            + Utils.convertingString(ironGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (goldGenerator != null) {
                    p.sendMessage(team.getName() + " Gold floor generator");
                    p.sendMessage(goldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO GOLD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
                            + Utils.convertingString(goldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }

                if (emeraldGenerator != null) {
                    p.sendMessage(team.getName() + " Emerald floor generator");
                    p.sendMessage(emeraldGenerator.toString());
                    TextComponent teleport = new TextComponent(Utils.translate("&a&lTELEPORT TO EMERALD GENERATOR&f"));
                    teleport.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/bedwars teleportHologram "
                            + Utils.convertingString(emeraldGenerator.getLocation().getLocation())));
                    teleport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils.translate("&aClick me to teleport")).create()));
                    p.spigot().sendMessage(teleport);
                }
            }

            return;
        }

        if ("reload".equalsIgnoreCase(args[0])) {

            BedWarsPlugin.getInstance().reloadConfig();
            Lang.reloadConfig();
            ManagerHandler.getGameManager().getGames().forEach(g -> g.getArenaOptions().reloadConfig());
            GUIStructure.reloadConfig();
            p.sendMessage(Utils.translate("&aThe config.yml has been reloaded."));

            return;
        }

        if ("reloadGames".equalsIgnoreCase(args[0])) {

            ManagerHandler.getGameManager().getGames().forEach(g -> g.getMatch().endGame("RESTART"));
            ManagerHandler.getGameManager().getGames().clear();
            ManagerHandler.getGameManager().loadGames();
            p.sendMessage(Utils.translate("&aThe games has been reloaded."));

            return;
        }

        if ("saveArena".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " saveArena <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find the arena " + args[1]));
                return;
            }

            if (BedWarsPlugin.getInstance().isFAWE()) {

                CuboidSelection selection = game.getGameCuboid();
                Location pos1 = selection.getPositionOne();
                Location pos2 = selection.getPositionTwo();
                FAWEManager.saveSchematic(pos1.getWorld(), pos1, pos2, game.getName());

                p.sendMessage(Utils.translate("&aSchematic has been saved for arena " + args[1]));
                return;
            }

            ManagerHandler.getWorldManager().saveWorld(args[1]);
            p.sendMessage(Utils.translate("&aWorld has been saved for arena " + args[1]));

            return;
        }

        if ("delete".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " delete <arenaName>");
                return;
            }

            String arenaName = args[1];
            Game game = ManagerHandler.getGameManager().getGame(arenaName);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find any arena with the name " + arenaName));
                return;
            }

            if (game.getMatch().getMatchState() != MatchState.IN_WAITING) {
                p.sendMessage(Utils.translate("&cCouldn't delete the arena. There is a match ongoing in this arena."));
                return;
            }

            ManagerHandler.getWorldManager().deleteArenaWorld(arenaName);
            ManagerHandler.getGameManager().game.set("Games." + arenaName, null);
            ManagerHandler.getGameManager().saveGameFile();
            ManagerHandler.getGameManager().getGames().remove(game);
            p.sendMessage(Utils.translate("&a⇨ The arena " + arenaName + " has been deleted."));
            return;
        }

        if ("create".equalsIgnoreCase(args[0])) {

            if (ManagerHandler.getGameManager().getPlayerMatch().containsKey(p.getUniqueId())) {
                p.sendMessage(Utils.translate("&cYou can't create an arena because you're in a game."));
                return;
            }

            if (args.length < 5) {
                p.sendMessage(this.insfArgs + " create <arenaName> <arenaType> <playersPerTeam> <minimumTeamsToStart>");
                return;
            }

//			TODO: Verific daca path-urile nu sunt null

            if (ManagerHandler.getGameManager().containsGameName(args[1])) {
                p.sendMessage(this.prefix + "There is already an arena with this name.");
                return;
            }

            if (!Utils.isInteger(args[3])) {
                p.sendMessage(this.prefix + " Argument three &d( " + args[3] + ") must be a number.");
                return;
            }

            if (!Utils.isInteger(args[4])) {
                p.sendMessage(this.prefix + " Argument four &d( " + args[4] + ") must be a number.");
                return;
            }

//			String scoreboardPath = "DEFAULT";
//			String upgradePath = "DEFAULT";
//			String shopPath = "DEFAULT";

//			if (args.length >= 6) {
//
//				if (ManagerHandler.getScoreboardManager().getConfig().get("ScoreboardPath." + args[5]) == null) {
//					p.sendMessage(Utils.translate("&cThere is not an scoreboard path with name " + args[5]));
//					return;
//				}
//
//				scoreboardPath = args[5];
//			}
//
//			if (args.length >= 7) {
//
//				String path = args[6];
//
//				p.sendMessage(ManagerHandler.getGameManager().getUpgradePath().toString());
//
//				if (!ManagerHandler.getGameManager().getUpgradePath().containsKey(path)) {
//					p.sendMessage(Utils.translate("&cThere is not an upgrade path with the name " + path));
//					return;
//				}
//
//				upgradePath = path;
//			}
//
//			if (args.length >= 8) {
//				String path = args[7];
//
//				p.sendMessage(ManagerHandler.getGameManager().getShopPath().toString());
//
//				if (!ManagerHandler.getGameManager().getShopPath().containsKey(path)) {
//					p.sendMessage(Utils.translate("&cThere is not an shop path with the name " + path));
//					return;
//				}
//
//				shopPath = path;
//			}

            if (args[1].equals(Bukkit.getWorlds().get(0).getName())) {
                p.sendMessage(Utils.translate(
                        "&cYou can't create an arena in the main world named " + Bukkit.getWorlds().get(0).getName()));
                return;
            }

            if ("world_end".equals(args[1])) {
                p.sendMessage(Utils.translate("&cYou can't create an arena in the end"));
                return;
            }

            if ("world_nether".equals(args[1])) {
                p.sendMessage(Utils.translate("&cYou can't create an arena in nether"));
                return;
            }

            ManagerHandler.getWorldManager().createWorld(args[1], new WorldCallback() {

                @Override
                public void onComplete(World world, String[] message) {
                    p.teleport(world.getSpawnLocation().add(0, 1, 0));
                    p.setGameMode(GameMode.CREATIVE);
                }

                @Override
                public void onError(String[] message) {

                    p.sendMessage(message);

                }
            });

            GameSetup gameSetup = new GameSetup(p, args[1], args[2], Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]));
//			gameSetup.setScoreboardPath(scoreboardPath);
//			gameSetup.setUpgradePath(upgradePath);
//			gameSetup.setShopPath(shopPath);

//			if (args.length >= 6) {
//				String scPath = 
//				
//			}

//			gameSetup.sendAvailableCommands();
            ManagerHandler.getGameManager().getGameSetup().put(p, gameSetup);
            p.sendMessage(Utils.translate("&eSelect the arena bounds using the axe."));
            ItemStack item = new ItemBuilder(XMaterial.WOODEN_AXE.parseMaterial())
                    .setLore("&eUsed to select the bounds of arena", "&eRight click to set second position",
                            "&eLeft click to set first position")
                    .setDisplayName("&aArena selector").build();
            p.getInventory().addItem(item);

            return;
        }

        if ("setScoreboardPath".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " setScoreboardPath <arenaName> <scoreboardPath>");
                return;
            }

            String arenaName = args[1];
            Game game = ManagerHandler.getGameManager().getGame(arenaName);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find any arena with the name " + arenaName));
                return;
            }

            if (ManagerHandler.getScoreboardManager().getConfig().get("ScoreboardPath." + args[2]) == null) {
                p.sendMessage(Utils.translate("&cThere is not a scoreboard path with the name " + args[2]));
                return;
            }

            game.setScoreboardPath(args[2]);
            ManagerHandler.getGameManager().game.set("Games." + arenaName + ".ScoreboardPath", args[2]);
            ManagerHandler.getGameManager().saveGameFile();
            p.sendMessage(Utils.translate("&a⇨ Arena's scoreboard path has been set to " + args[2]));

            return;
        }

        if ("setOptionsPath".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " setOptionsPath <arenaName> <optionsPath>");
                return;
            }

            String arenaName = args[1];
            Game game = ManagerHandler.getGameManager().getGame(arenaName);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find any arena with the name " + arenaName));
                return;
            }

            if (ManagerHandler.getScoreboardManager().getConfig().get("ScoreboardPath." + args[2]) == null) {
                p.sendMessage(Utils.translate("&cThere is not a scoreboard path with the name " + args[2]));
                return;
            }

            game.setScoreboardPath(args[2]);
            ManagerHandler.getGameManager().game.set("Games." + arenaName + ".ScoreboardPath", args[2]);
            ManagerHandler.getGameManager().saveGameFile();
            p.sendMessage(Utils.translate("&a⇨ Arena's scoreboard path has been set to " + args[2]));

            return;
        }

        if ("teleportToArena".equalsIgnoreCase(args[0])) {

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

            if (gameSetup == null) {
                p.sendMessage(this.prefix + "You must create/edit an arena first.");
                return;
            }

            World world = Bukkit.getWorld(gameSetup.getName());

            if (world == null) {
                p.sendMessage(this.prefix + "Couldn't find the arena's world.");
                return;
            }

            p.teleport(gameSetup.getInWaiting());
            p.setAllowFlight(true);
            p.setFlying(true);

            return;
        }

        if ("setMinimumTeamsToStart".equalsIgnoreCase(args[0])) {

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

            if (gameSetup == null) {
                p.sendMessage(this.prefix + "You must use the command to edit an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setMinimumTeamsToStart <amount>");
                return;
            }

            if (!Utils.isInteger(args[1])) {
                p.sendMessage(this.prefix + "Argument one (" + args[1] + ") must be a number.");
                return;
            }

            int minimumTeams = Integer.parseInt(args[1]);
            gameSetup.setMinimumTeams(minimumTeams);
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setUpgradePath".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " setUpgradePath <arenaName> <upgradePath>");
                return;
            }

            String arenaName = args[1];
            Game game = ManagerHandler.getGameManager().getGame(arenaName);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find any arena with the name " + arenaName));
                return;
            }

            if (!ManagerHandler.getGameManager().getUpgradePath().containsKey(args[2])) {
                p.sendMessage(Utils.translate("&cThere is not an upgrade path with the name " + args[2]));
                return;
            }

            game.setUpgradePathName(args[2]);
            ManagerHandler.getGameManager().game.set("Games." + arenaName + ".UpgradePath", args[2]);
            ManagerHandler.getGameManager().saveGameFile();
            p.sendMessage(Utils.translate("&a⇨ Arena's upgrade path has been set to " + args[2]));

            return;
        }

        if ("setShopPath".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " setShopPath <arenaName> <shopPath>");
                return;
            }

            String arenaName = args[1];
            Game game = ManagerHandler.getGameManager().getGame(arenaName);

            if (game == null) {
                p.sendMessage(Utils.translate("&cCouldn't find any arena with the name " + arenaName));
                return;
            }

            if (!ManagerHandler.getGameManager().getShopPath().containsKey(args[2])) {
                p.sendMessage(Utils.translate("&cThere is not a shop path with the name " + args[2]));
                return;
            }

            game.setShopPathName(args[2]);
            ManagerHandler.getGameManager().game.set("Games." + arenaName + ".ShopPath", args[2]);
            ManagerHandler.getGameManager().saveGameFile();
            p.sendMessage(Utils.translate("&a⇨ Arena's shop path has been set to " + args[2]));

            return;
        }

        if ("generateScoreboardPath".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " generateScoreboardPath <arenaName>");
                return;
            }

            Game game = ManagerHandler.getGameManager().getGame(args[1]);

            if (game == null) {
                p.sendMessage(Lang.GAME_NULL.getString());
                return;
            }

            ManagerHandler.getScoreboardManager().createPathScoreboard(game);
            p.sendMessage(Utils.translate(
                    "&e>> The scoreboard path has been successfully created. Use the arena edit gui to change the scoreboard path for arena " + args[1]
            ));

            return;
        }

        if ("getWand".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            ItemStack item = new ItemBuilder(XMaterial.WOODEN_AXE.parseMaterial())
                    .setLore("&eUsed to select the bounds of arena", "&eRight click to set second position",
                            "&eLeft click to set first position")
                    .setDisplayName("&aArena selector").build();
            p.getInventory().addItem(item);
            return;
        }

        if ("createTeam".equalsIgnoreCase(args[0])) {

            if (args.length < 3) {
                p.sendMessage(this.insfArgs + " createTeam <teamName> <color>");
                return;
            }

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            List<String> teamColor = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "LIGHT-BLUE", "YELLOW", "LIME", "PINK",
                    "GRAY", "LIGHT-GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK", "AQUA");
            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

            if (gameSetup.containsTeam(args[1])) {
                p.sendMessage(this.prefix + "There is already created a team with name " + args[1]);
                return;
            }

            if (!teamColor.contains(args[2].toUpperCase())) {
                StringBuilder builder = new StringBuilder();
                teamColor.forEach(color -> builder.append(color).append(" , "));
                p.sendMessage(this.prefix + "Available colors: " + builder);
                return;
            }

            p.sendMessage(this.prefix + "Team " + args[1] + " has been created.");
            gameSetup.getTeams().add(new TeamSetup(args[1], args[2].toUpperCase()));
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("nextStep".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.addStep();
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setWaitingLocation".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.setInWaiting(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setSpectateLocation".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.setSpectateLocation(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("addDiamondGenerator".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getDiamondGenerator().add(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("addEmeraldGenerator".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getEmeraldGenerator().add(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamSpawn".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamSpawn <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setSpawnLocation(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamGoldGenerator".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamGoldGenerator <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setGoldGenerator(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamEmeraldGenerator".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamEmeraldGenerator <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setEmeraldGenerator(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamIronGenerator".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamIronGenerator <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setIronGenerator(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamShop".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamShop <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setShop(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamUpgrade".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamUpgrade <teamName>");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);
            gameSetup.getTeamSetup(args[1]).setUpgrade(p.getLocation());
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("setTeamBed".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            if (args.length < 2) {
                p.sendMessage(this.insfArgs + " setTeamBed <teamName>");
                return;
            }

            Location location = p.getLocation();
            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

            BlockFace face = ManagerHandler.getVersionManager().getVersionWrapper().getBedFace(location);

            if (face == null) {
                p.sendMessage(Utils.translate("&c⇨ You must stay on a bed."));
                return;
            }

            Location headBed = ManagerHandler.getVersionManager().getVersionWrapper().getBedHead(location);

            gameSetup.getTeamSetup(args[1]).setBedLocation(headBed);
            gameSetup.getTeamSetup(args[1]).setBedFace(face);
            gameSetup.sendAvailableCommands();

            return;
        }

        if ("finish".equalsIgnoreCase(args[0])) {

            if (!ManagerHandler.getGameManager().getGameSetup().containsKey(p)) {
                p.sendMessage(this.prefix + "You must create an arena first.");
                return;
            }

            GameSetup gameSetup = ManagerHandler.getGameManager().getGameSetup().get(p);

            if (gameSetup.getTeamsReady() < 2) {
                p.sendMessage(this.prefix + "You must create and setup at least two teams.");
                return;
            }

            gameSetup.finish();
            ManagerHandler.getGameManager().getGameSetup().remove(p);

            return;
        }

        p.sendMessage(Utils.translate("&cCouldn't find any argument for this command."));

    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("bw");
    }

}
