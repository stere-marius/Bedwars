package ro.marius.bedwars.manager.type;

import org.bukkit.Location;
import ro.marius.bedwars.game.Game;

public final class FAWEManager {

//    private static Map<Game, BlockVector> centerLocation = new HashMap<>();
//    private static Map<Game, Schematic> gameSchematic = new HashMap<>();

//    @SuppressWarnings("deprecation")
    public static boolean saveSchematic(org.bukkit.World bukkitWorld, Location pos1, Location pos2, String arenaName) {

//        World world = new BukkitWorld(bukkitWorld);
//
//        BlockVector3 min = BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ())
//                .getMinimum(BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()));
//        BlockVector3 max = BlockVector3.at(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ())
//                .getMaximum(BlockVector3.at(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()));
//
//        CuboidRegion region = new CuboidRegion(world, min, max);
//        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
//
//        clipboard.setOrigin(region.getCenter().toBlockPoint());
//
//        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
//
//        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
//                region.getMinimumPoint());
//        forwardExtentCopy.setCopyingEntities(false);
//        Operations.complete(forwardExtentCopy);
//
//        File file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "schematics",
//                arenaName + ".schematic");
//
//        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
//            writer.write(clipboard);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }

        return true;
    }

//    public static BlockVector3 getCenter(Game game) {
//
//        BlockVector3 centerVector = centerLocation.get(game);
//
//        if (centerVector == null) {
//
//            CuboidSelection cuboid = game.getGameCuboid();
//
//            Location pos1 = cuboid.getPositionOne();
//            int x1 = pos1.getBlockX();
//            int y1 = pos1.getBlockY();
//            int z1 = pos1.getBlockZ();
//
//            Location pos2 = cuboid.getPositionTwo();
//            int x2 = pos2.getBlockX();
//            int y2 = pos2.getBlockY();
//            int z2 = pos2.getBlockZ();
//
//            BlockVector3 min = BlockVector3.at(x1, y1, z1).getMinimum(BlockVector3.at(x2, y2, z2));
//            BlockVector3 max = BlockVector3.at(x1, y1, z1).getMaximum(BlockVector3.at(x2, y2, z2));
//
//            BlockVector3 center = min.add(max).divide(2);
//
//            centerLocation.put(game, center);
//
//            return center;
//
//        }
//
//        return centerVector;
//    }

//    public static Schematic getSchematic(Game game) throws IOException {
//
//        Schematic schematic = gameSchematic.get(game);
//
//        if (schematic == null) {
//
//            File file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "schematics",
//                    game.getName() + ".schematic");
//
//            Schematic schem = FaweAPI.load(file);
//            gameSchematic.put(game, schem);
//
//            return schem;
//        }
//
//        return schematic;
//    }


    public static boolean loadSchematic(Game game) {
//
//        File file = new File(BedWarsPlugin.getInstance().getDataFolder() + File.separator + "schematics",
//                game.getName() + ".schematic");
//
//        if (!file.exists()) {
//            return false;
//        }
//
//        Schematic schem = null;
//
//        try {
//            schem = FaweAPI.load(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        gameSchematic.put(game, schem);
//
        return false;
//
    }

    public static void pasteSchematic(org.bukkit.World world, Game game) {

//        BlockVector3 centerVector = getCenter(game);
//
//        Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit")),
//                new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try {
//                            Schematic schem = getSchematic(game);
//                            schem.paste(new BukkitWorld(world), centerVector, false, true, null);
//
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//
//                });
    }

}
