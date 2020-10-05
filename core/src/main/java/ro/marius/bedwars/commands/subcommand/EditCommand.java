package ro.marius.bedwars.commands.subcommand;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ro.marius.bedwars.ISubCommand;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.game.mechanics.GameLocation;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.extra.arenaedit.GameEditInventory;
import ro.marius.bedwars.shopconfiguration.ShopPath;
import ro.marius.bedwars.upgradeconfiguration.UpgradePath;
import ro.marius.bedwars.utils.Utils;

public class EditCommand implements ISubCommand {

    private final String insufficientArguments = Utils.translate("&e⇨ Insufficient arguments: /bedwars arenaEdit");

//    private Team team = null;

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player p = (Player) sender;

//        if(args.length == 1){
//
//        }


        GameEdit gameEdit = ManagerHandler.getGameManager().getGameEdit().get(p.getUniqueId());

        if (gameEdit == null) {
            p.sendMessage(Utils.translate(
                    "&7>> You have to choose an arena before editing it. Use the command /bedwars arenaEdit <arenaName>"));
            return;
        }

        if (args.length == 1) {
            GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
            p.openInventory(gameEditInventory.getInventory());
            return;
        }

//        if ("teamEdit".equalsIgnoreCase(args[1])) {
//            Location playerLocation = p.getLocation();
//            GameLocation gameLocation = new GameLocation(Utils.convertingString(playerLocation), playerLocation, playerLocation.getWorld().getName());
//            if (team == null)
//                team = new Team("TestName", "T", "RED", BlockFace.EAST, gameLocation, gameLocation, gameLocation, gameLocation, gameLocation, gameLocation, gameLocation);
//            Inventory inventory = new TeamEditInventory(team).getInventory();
//            p.openInventory(inventory);
//            return;
//        }


        // TODO: Set arena offline

//        if ("setPlayersPerTeam".equalsIgnoreCase(args[1])) {
//
//            if (args.length < 3) {
//                p.sendMessage(insufficientArguments + " setPlayersPerTeam <number>");
//                return;
//            }
//
//            if (!Utils.isInteger(args[2])) {
//                p.sendMessage(Utils.translate("&7>> The argument two must be a number . You typed " + args[2]));
//                return;
//            }
//
//            int nr = Utils.getInteger(args[2]);
//            gameEdit.setPlayersPerTeam(nr);
//            p.sendMessage(Utils.translate("&e>> The number of players per team has been changed to " + nr));
//            return;
//        }

        if ("setWaitingLobby".equalsIgnoreCase(args[1])) {

            Location playerLocation = p.getLocation();
            gameEdit.getGame().setWaitingLocation(new GameLocation(Utils.convertingString(playerLocation), playerLocation, playerLocation.getWorld().getName()));
            p.sendMessage(Utils.translate("&e>> The waiting location has been changed to " + Utils.convertingString(playerLocation)));

            return;
        }

        if ("setPositionOne".equalsIgnoreCase(args[1])) {

            Location playerLocation = p.getLocation();
            gameEdit.getGame().getGameCuboid().setPositionOne(playerLocation);
            p.sendMessage(Utils.translate("&e>> The position one location has been changed to " + Utils.convertingString(playerLocation)));
            return;
        }

        if ("setPositionTwo".equalsIgnoreCase(args[1])) {

            Location playerLocation = p.getLocation();
            gameEdit.getGame().getGameCuboid().setPositionTwo(playerLocation);
            p.sendMessage(Utils.translate("&e>> The position two location has been changed to " + Utils.convertingString(playerLocation)));
            return;
        }

        if ("setName".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(insufficientArguments + " setName <name>");
                return;
            }

            String newName = args[2];
            gameEdit.getGame().setName(newName);
            p.sendMessage(Utils.translate("&e>> Arena's name has been changed to " + newName));
            return;
        }

        if ("setArenaType".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(insufficientArguments + " setArenaType <arenaType>");
                return;
            }

            String arenaType = args[2];
            gameEdit.getGame().setArenaType(arenaType);
            p.sendMessage(Utils.translate("&e>> Arena's type has been changed to " + arenaType));
            return;
        }

        if ("setUpgradePath".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(insufficientArguments + " setUpgradePath <upgradePath>");
                return;
            }

            String upgradePathName = args[2];
            UpgradePath upgradePath = ManagerHandler.getGameManager().getUpgradePath().get(upgradePathName);

            if (upgradePath == null) {
                p.sendMessage(Utils.translate("&c>> Could not find the upgrade path with the name " + upgradePathName));
                p.sendMessage(Utils.translate("&c>> If you have added it, you might want to restart the server."));
                return;
            }

            gameEdit.getGame().setUpgradePath(upgradePath);
            gameEdit.getGame().setUpgradePathName(upgradePathName);
            p.sendMessage(Utils.translate("&e>> The upgrade path name has been changed to " + upgradePathName));
            return;
        }

        if ("setShopPath".equalsIgnoreCase(args[1])) {

            if (args.length < 3) {
                p.sendMessage(insufficientArguments + " setShopPath <upgradePath>");
                return;
            }

            String shopPathName = args[2];
            ShopPath shopPath = ManagerHandler.getGameManager().getShopPath().get(shopPathName);

            if (shopPath == null) {
                p.sendMessage(Utils.translate("&c>> Could not find the shop path with the name " + shopPathName));
                p.sendMessage(Utils.translate("&c>> If you have added it, you might want to restart the server."));
                return;
            }

            gameEdit.getGame().setShopPath(shopPath);
            gameEdit.getGame().setShopPathName(shopPathName);
            p.sendMessage(Utils.translate("&e>> The shop path name has been changed to " + shopPathName));
            return;
        }

        GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
        p.openInventory(gameEditInventory.getInventory());


        // TODO Check args
        // TODO Check team name for every argument
        // TODO GUI for adding diamond generator, right click on item to delete, left click to add, mid click to teleport


//        String teamName = args[2];
//        Team team = gameEdit.getGame().getMatch().getTeamAlive(teamName);
//
//        if (team == null) {
//            p.sendMessage(Utils.translate("&cThe team " + args[2] + " does not exist."));
//            return;
//        }
//
//        if ("setTeamName".equalsIgnoreCase(args[1])) {
//
//            if (args[3].isEmpty()) {
//                p.sendMessage(Utils.translate("&c>> The argument three must not be empty."));
//                return;
//            }
//
//            team.setName(args[3]);
//            p.sendMessage(Utils.translate("&a>> Name of team " + args[2] + " has been changed to " + args[3]));
//            return;
//        }
//
//        if ("setTeamColor".equalsIgnoreCase(args[1])) {
//
//            List<String> teamColor = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "LIGHT-BLUE", "YELLOW", "LIME", "PINK",
//                    "GRAY", "LIGHT-GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK", "AQUA");
//
//            if (!teamColor.contains(args[3].toUpperCase())) {
//                p.sendMessage(Utils.translate("&e>> Could not find the color " + args[3]));
//                p.sendMessage(Utils.translate("&e>> Available colors: " + String.join(" , ", teamColor)));
//                return;
//            }
//
//            team.setColorName(args[3]);
//            team.generateKitItems();
//            p.sendMessage(Utils.translate("&a>> Color of team " + args[2] + " has been changed to " + args[3]));
//            return;
//        }
//
//        Set<String> teamArgs = Sets.newHashSet("setTeamSpawn", "setTeamIronGenerator",
//                "setTeamGoldGenerator", "setTeamEmeraldGenerator",
//                "setTeamShop", "setTeamUpgrade",
//                "setTeamBed");
//        Optional<String> argumentFound = teamArgs.stream().filter(arg -> arg.equalsIgnoreCase(args[1])).findFirst();
//
//        if (!argumentFound.isPresent()) {
//            p.sendMessage("&c>> Parameter " + args[1] + " not found. Use one of these: " + String.join(", ", teamArgs));
//            return;
//        }
//
//        String argument = argumentFound.get();
//
////        if (args.length < 3) {
////            p.sendMessage(INSUFFICIENT_ARGS + " arenaEdit " + argument + " <teamName>");
////            return;
////        }
//
//        Location playerLocation = p.getLocation();
//        GameLocation gameLocation = new GameLocation(Utils.convertingString(playerLocation), playerLocation, playerLocation.getWorld().getName());
//        String locationString = "&d(" + playerLocation.getWorld().getName() + "," + playerLocation.getBlockX() + "," + playerLocation.getBlockY() + "," + playerLocation.getBlockZ() + ")";
//
//        switch (argument) {
//            case "setTeamSpawn":
//                team.setSpawnLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's spawn location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamBed":
//                BlockFace face = ManagerHandler.getVersionManager().getVersionWrapper().getBedFace(playerLocation);
//
//                if (face == null) {
//                    p.sendMessage(Utils.translate("&c⇨ You must stay on a bed."));
//                    return;
//                }
//
//                Location headBed = ManagerHandler.getVersionManager().getVersionWrapper().getBedHead(playerLocation);
//                team.setBedLocation(new GameLocation(Utils.convertingString(headBed), headBed, headBed.getWorld().getName()));
//                team.setBedFace(face);
//                p.sendMessage(Utils.translate("&a>> Team's bed location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamIronGenerator":
//                team.setIronGenerator(gameLocation);
//                team.getIronFloorGenerator().setLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's iron generator location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamGoldGenerator":
//                team.setGoldGenerator(gameLocation);
//                team.getIronFloorGenerator().setLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's gold generator location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamEmeraldGenerator":
//                team.setEmeraldGenerator(gameLocation);
//                team.getEmeraldFloorGenerator().setLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's emerald generator location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamShop":
//                team.setShopLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's shop location has been set to " + locationString + "&a ."));
//                break;
//            case "setTeamUpgrade":
//                team.setUpgradeLocation(gameLocation);
//                p.sendMessage(Utils.translate("&a>> Team's upgrade location has been set to " + locationString + "&a ."));
//                break;
//            default:
//                p.sendMessage(Utils.translate("&c>> Could not find the argument " + args[1]));
//                break;
//        }


    }

}
