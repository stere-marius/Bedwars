package ro.marius.bedwars.game.arenareset;

public class ArenaResetFactory {

    public static ArenaReset getInstance(String resetType) {

        // TODO: Creez un path in config.yml numit ArenaResetType

        if (resetType.equalsIgnoreCase("WORLD_ADAPTER"))
            return new WorldReset();

        if (resetType.equalsIgnoreCase("ASYNC_WORLD_ADAPTER"))
            return new AsyncWorldReset();

        if (resetType.equalsIgnoreCase("FAWE_RESET"))
            return new FAWEReset();

        throw new NullPointerException("Could not find the resetArena type of type " + resetType);
    }


}
