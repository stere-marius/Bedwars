package ro.marius.bedwars.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum Lang {

    NO_PERMISSION("&cYou don't have permission."),
    GAME_NULL("&cThis game doesn't exist."),
    COULD_NOT_FIND_MATCH("&cCould not find a match."),
    GAME_FULL("&cThis arena is full."),
    ALREADY_IN_GAME("&cYou are already in a game."),
    GAME_IS_STARTED("&cThis arena is in game."),
    GAME_STARTING_IN("&eThe game is starting in <seconds> seconds."),
    START_CANCELLED("&cWe don't have enough players!Start cancelled."),
    PLAYER_JOINED_IN_GAME("&7<player>&e has joined (&b<inGame>&e/&b<max>&e)!"),
    PLAYER_LEFT_IN_GAME("&7<player>&e has quit!"),
    NOT_IN_GAME("&cYou are not in any game."),
    MEMBER_LEAVE("&cThe other member from your team left the game."),
    TEAM_ELIMINATED("&f&lTEAM ELIMINATED > <teamColor><team> &chas been eliminated."),
    TEAM_FULL("&e&l>> The team <team> is full."),
    PLAYER_DISCONNECTED("<teamColor><player> &ehas disconnected."),
    PLAYER_REJOIN("<teamColor><player> &ehas reconnected."),
    CANT_REJOIN("&cYou can't rejoin in this arena."),
    GAME_FORCE_START("&aThe arena <arenaName> has been forced started."),
    STOPPED_FORCE_START("&aThe arena <arenaName> has been stopped from force start"),
    GAME_FORCED_STOPPED("&aThe arena <arenaName> has been stopped."),
    FORCE_START_COMMAND_USAGE("&cUse command: &a/bedwars forceStart <arenaName>"),
    FORCE_STOP_COMMAND_USAGE("&cUse command: &a/bedwars forceStop <arenaName>"),
    PARTY_CHAT_COMMAND_USAGE("&cUse command: &a/party chat <message> to send a message"),
    PARTY_INVITE_COMMAND_USAGE("&cCommand usage: /party invite <player>"),
    PARTY_ACCEPT_COMMAND_USAGE("&cCommand usage: /party accept <player>"),
    PARTY_KICK_COMMAND_USAGE("&cCommand usage: /party kick <player>"),
    PARTY_CHAT_FORMAT("&a(Party chat) <player> : <message>"),
    PLAYER_JOINED_IN_PARTY("&a<player> joined in party."),
    PARTY_JOIN("&aYou've joined in <player> party."),
    INVITE_YOURSELF_PARTY("&cYou can't invite yourself."),
    ONLY_LEADER_CAN_INVITE("&cOnly party leader can invite players."),
    PLAYER_IN_PARTY("&cThe player <player> is already in a party."),
    PARTY_REQUEST_SENT("&aParty invite sent to <player>"),
    PARTY_INVITE_EXPIRED("&cYour party invite timed out."),
    CANT_ACCEPT_YOURSELF("&cYou can't accept yourself a party invite"),
    NO_PARTY_REQUEST("&cYou don't have any party request"),
    PARTY_REQUEST_RECEIVED("&aParty request from <player>. Click here to accept it."),
    NOT_IN_PARTY("&cYou are not in any party."),
    PARTY_LEAVE("&eYou left from <player>'s party."),
    PARTY_DISBAND_BY_LEADER("&eThe party has been disbanded by the leader."),
    PARTY_DISBAND_BY_SIZE("&eThe party has been disbanded due to size."),
    PLAYER_LEFT_FROM_PARTY("&e<player> left the party."),
    PARTY_KICK_YOURSELF("&cYou can't kick yourself."),
    ONLY_LEADER_CAN_KICK("&cOnly the leader can kick players from party."),
    NOT_IN_YOUR_PARTY("&e<player> is not in your party."),
    PLAYER_KICKED_FROM_PARTY("&eThe player <player> has been kicked from party."),
    PARTY_KICK("&eYou have been kicked from party by <player>."),
    ONLY_LEADER_CAN_DISBAND("&cOnly the party leader can disband the party."),
    PLAYER_KILLED(
            "<playerTeamColor><player> was killed by <killerTeamColor><killer> <isFinalKill>!"),
    PLAYER_KNOCKED_VOID(
            "<playerTeamColor><player> was knocked into void by <damagerTeamColor><damager> <isFinalKill>!"),
    PLAYER_FALL_IN_VOID("<playerTeamColor><player> &7fell into the void."),
    PLAYER_DIED("<playerTeamColor><player> died."),
    PLAYER_KILLED_BY_TNT("<playerTeamColor><player> has been killed by <killerTeamColor><killer> TNT <isFinalKill>!"),
    PLAYER_KILLED_BY_ARROW(
            "<playerTeamColor><player> has been killed by <killerTeamColor><killer> arrow <isFinalKill>!"),
    PLAYER_KILLED_BY_FIREBALL(
            "<playerTeamColor><player> has been killed by <killerTeamColor><killer> fireball <isFinalKill>!"),
    PLAYER_KILLED_BY_GOLEM("<playerTeamColor><player> has been killed by <golemTeamColor><golemTeam> golem."),
    PLAYER_KILLED_BY_SILVERFISH(
            "<playerTeamColor><player> has been killed by <silverfishTeamColor><silverfishTeam> silverfish."),
    PLAYER_KILLED_HIMSELF("<playerTeamColor><player> killed himself <isFinalKill>!"),
    PLAYER_FELL_HIGH_PLACE("<playerTeamColor><player> fell from a high place.<isFinalKill>"),
    FINAL_KILL_DISPLAY("&b&lFINAL KILL"),
    BED_DESTROYED("<teamColor><team> bed &7has been destroyed by <playerTeamColor><player>."),
    DESTROY_YOUR_BED("&cYou can't destroy your bed."),
    DESTROY_NOT_PLACED_BLOCK("&cYou can only destroy placed blocks."),
    IRON_GOLEM_LIMIT("&cYou have more golem than the limit."),
    SILVERFISH_LIMIT("&cYou have more silverfish than the limit."),
    PLAYER_OFFLINE("&cError!This player is offline."),
    JOIN_COMMAND_USAGE("&eUse command: &a/bedwars join <arenaName> to join in arena."),
    SKIN_DOESNT_EXIST("&eSkin name <skinName> doesn't exist. Choose one from:&a <availableSkins>."),
    NO_PERMISSION_FOR_SKIN("&cYou don't have permission for skin <skinName>."),
    YOUR_SKIN_SET("&aYour shop keeper skin has been set to <skinName>."),
    PLAYER_SKIN_SET("&aPlayer <playerName> shop keeper skin has been set to <skinName>."),
    GAMETYPE_NULL("&cThis game type doesn't exist."),
    PLAYER_NOT_IN_GAME("&cPlayer <playerName> is not in game."),
    CANT_FIND_ANY_ARENA("&cNo arena found."),
    TELEPORTED_TO("&aYou were teleported to &7<player>."),
    SHOUT_MISSING_ARGUMENTS("&cMissing arguments! Usage: /shout <message>"),
    DELAY_LEAVE("&cYou have to wait 3 seconds before leave."),
    OUTSIDE_OF_ARENA("&cYou're outside of arena."),
    SELECTED_KIT("&eYou've selected the kit <kitDisplayName>."),
    DIAMOND_TWO_UPGRADE("&bDiamond Generators &ehave been upgraded to Tier &cII"),
    DIAMOND_THREE_UPGRADE("&bDiamond Generators &ehave been upgraded to Tier &cII"),
    EMERALD_TWO_UPGRADE("&2Emerald Generators &ehave been upgraded to Tier &cII"),
    EMERALD_THREE_UPGRADE("&2Emerald Generators &ehave been upgraded to Tier &cIII"),
    CLICK_TO_PURCHASE("&eClick to purchase"),
    UPGRADE_BOUGHT("&e<player> has bought the upgrade &a<upgradeName>"),
    PLAYER_UPGRADE_BOUGHT("&eYou bought the upgrade &a<upgradeName>"),
    ALREADY_BOUGHT_THE_UPGRADE("&cYou have already bought this upgrade."),
    NOT_ENOUGH_RESOURCES("&cYou don't have enough resources."),
    SNEAK_CLICK_QUICK_BUY_ADD("&bSneak Click to add to Quick Buy"),
    //	SNEAK_CLICK_QUICK_BUY_REMOVE("&bSneak Click to remove from Quick Buy"),
    ADDING_ITEM_TO_QUICK_SLOT("&eAdding item to Quick Slot!"),
    CLICK_TO_REPLACE("&eClick to replace!"),
    CLICK_TO_SET("&eClick to set!"),
    BEDWARS_CONSOLE_CHAT("&e<arenaName> : <message>"),
    END_MESSAGE(Arrays.asList("", "<winnerTeamColor><winnerTeam> has won the game!",
            "<center>&a&l---------------------------------------------", "", "<center>&f&lBedWars", "",
            "<center><winnerTeamColor><winnerTeam> &7 - &7<winner>", "", "",
            "<center>&e&l1st Killer&7 - <topOneName> - &7<topOneKills>",
            "<center>&6&l2nd Killer&7 - <topTwoName> - &7<topTwoKills>",
            "<center>&c&l3rd Killer&7 - <topThreeName> - &7<topThreeKills>", "",
            "<center>&a&l---------------------------------------------")),
    START_MESSAGE(Arrays.asList("", "<center>&a&l---------------------------------------------", "<center>&f&lBed Wars",
            "", "<center>&e&lProtect your bed and destroy the enemy beds.",
            "<center>&e&lUpgrade yourself and your team by collecting",
            "<center>&e&lIron, Gold, Emerald and Diamond from generators", "<center>&e&lto access powerful upgrades.",
            "", "<center>&a&l---------------------------------------------")),
    BEDWARS_HELP_ADMINS(Arrays.asList("&e-------------------------------------------", "&e⇨Bedwars Commands", "",
            "&e⇨ /bedwars join <arenaName> to join in an arena", "&e⇨ /bedwars leave to leave from arena",
            "&e⇨ /bedwars setLobby to set the lobby where the players get teleported when an arena ends",
            "&e⇨ /bedwars holograms to see stats holograms",
            "&e⇨ /bedwars clone <existingArenaName> <newArenaName> to clone an arena",
            "&e⇨ /bedwars delete <arenaName> to delete an arena",
            "&e⇨ /bedwars create <arenaName> <arenaType> <playersPerTeam> <minimumTeamsToStart> to create an arena",
            "&e⇨ /bedwars setShopPath <arenaName> <shopPathName> to set the shop path for arena",
            "&e⇨ /bedwars setUpgradePath <arenaName> <upgradePathName> to set the upgrade path for arena")),
    BEDWARS_HELP_PLAYERS(Arrays.asList("&e-------------------------------------------", "&e⇨Bedwars Commands", "",
            "&e⇨ /bedwars join <arenaName> to join in an arena", "&e⇨ /bedwars leave to leave from arena")),
    PARTY_HELP_PLAYERS(Arrays.asList("&a&l---------------------------------------------",
            "&a-> /party invite <username>", "&a-> /party accept <player>", "&a-> /party list",
            "&a-> /party kick <player>", "&a-> /party disband", "&a&l---------------------------------------------")),

    ;

    private static YamlConfiguration LANG;
    private Object message;

    Lang(Object message) {
        this.message = message;

    }

    public static void setFile(YamlConfiguration lANG2) {
        LANG = lANG2;
    }

    public static void loadLang(BedWarsPlugin plugin) {

        File lang = new File(plugin.getDataFolder(), "lang.yml");
        Logger log = Bukkit.getLogger();
        if (!lang.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                lang.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
                log.severe("[Bedwars] Couldn't create language file.");
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (Lang item : Lang.values()) {
            if (conf.getString(item.getKey()) == null) {
                conf.set(item.getKey(), item.getObject());
            }
        }
        Lang.setFile(conf);

        try {
            conf.save(lang);
        } catch (IOException e) {

            log.log(Level.WARNING, "Bedwars: Bedwars to save lang.yml.");
            e.printStackTrace();
        }

    }

    public static void reloadConfig() {
        LANG = YamlConfiguration.loadConfiguration(new File(BedWarsPlugin.getInstance().getDataFolder(), "lang.yml"));
    }

    public List<String> getList() {

        return LANG.getStringList(this.getKey());

    }

    public void sendMessage(Player p) {

        Object obj = LANG.get(this.getKey());

        if (obj instanceof List) {

            List<String> list = LANG.getStringList(this.getKey());

            for (String s : list) {
                p.sendMessage(Utils.translate(s));
            }

        }

    }

    public String getString() {

        String value = LANG.getString(this.getKey());
        if (value == null) {
            Bukkit.getLogger().info("Missing lang data: " + this.getKey());
            value = "";
        }

        return Utils.translate(value);
    }

    private String getKey() {
        return this.name().toLowerCase().replace("_", "-");
    }

    public Object getObject() {
        return this.message;
    }

}
