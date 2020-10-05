package ro.marius.bedwars.manager.type;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchData;
import ro.marius.bedwars.scoreboard.ScoreboardAPI;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.TeamColor;
import ro.marius.bedwars.utils.StringUtils;
import ro.marius.bedwars.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("deprecation")
public class ScoreboardManager {

    public File scoreboardFile = new File(BedWarsPlugin.getInstance().getDataFolder(), "scoreboard.yml");
    public YamlConfiguration scoreboardConfig = YamlConfiguration.loadConfiguration(this.scoreboardFile);
    public Map<UUID, ScoreboardAPI> scoreboard = new HashMap<>();

    public ScoreboardManager() {
        this.generateConfig();
    }

    public void generateConfig() {

        this.getConfig().addDefault("YouDisplay", " &7(You)");
        this.getConfig().addDefault("StartingDisplay", "&fStarting in &a<time>");
        this.getConfig().addDefault("SearchingDisplay", "&fSearching players...");
        List<String> lobby = new ArrayList<>();
        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Waiting-Enabled", Boolean.TRUE);
        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Waiting-Title", "&e BED WARS");
        lobby.add("");
        lobby.add("&fMap: &a<mapName>");
        lobby.add("&fPlayers: &a<inGame>/<max>");
        lobby.add("");
        lobby.add("<statusDisplay>");
        lobby.add(" ");
        lobby.add("&fServer: &aBedWars122G");
        lobby.add("");
        lobby.add("&ewww.spigotmc.org");
        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Waiting.Lines", lobby);

        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Game-Enabled", true);
        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Game-Title", "&e BED WARS");
        List<String> game = new ArrayList<>();
        game.add("");
        game.add("<nextEvent>:");
        game.add("&a<time>");
        game.add("");
        game.add("&c&lR &fRed: <isAliveRed>");
        game.add("&9&lB &fBlue: <isAliveBlue>");
        game.add("");
        game.add("&ewww.spigotmc.org");
        this.getConfig().addDefault("ScoreboardPath.DEFAULT.Game.Lines", game);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void setScoreboreboardLobby(Player p) {

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        YamlConfiguration config = this.getConfig();
        Game game = match.getGame();
        String scoreboardPath = game.getScoreboardPath();
        boolean isEnabled = config.getBoolean("ScoreboardPath." + scoreboardPath + ".Waiting-Enabled");
        // Plugin plugin =
        // Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        if (!isEnabled) {
            return;
        }

        if (this.scoreboard.containsKey(p.getUniqueId())) {
            this.scoreboard.get(p.getUniqueId()).getTask().cancel();
        }

        UUID uuid = p.getUniqueId();
        ScoreboardAPI sc = new ScoreboardAPI(uuid,
                config.getString("ScoreboardPath." + scoreboardPath + ".Waiting-Title"), "bedwars-waiting");
        List<String> list = config.getStringList("ScoreboardPath." + scoreboardPath + ".Waiting.Lines");
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                sc.clear();
                String startingDisplay = match.isStarting() ? ScoreboardManager.this.getConfig().getString("StartingDisplay")
                        : ScoreboardManager.this.getConfig().getString("SearchingDisplay");

                for (String string : list) {

                    Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

                    if ((plugin != null) && plugin.isEnabled()) {
                        string = PlaceholderAPI.setPlaceholders(p, string);
                    }

                    sc.addLine(string.replace("<mapName>", game.getName())
                            .replace("<inGame>", match.getPlayers().size() + "")
                            .replace("<max>", game.getMaxPlayers() + "").replace("<min>", game.getMaxPlayers() + "")
                            .replace("<statusDisplay>",
                                    Utils.translate(startingDisplay.replace("<time>", match.getStartingTime() + "")))
                            .replace("<time>", match.getStartingTime() + ""));
                }

                sc.updateScoreboard(p);

            }
        }.runTaskTimerAsynchronously(BedWarsPlugin.getInstance(), 0, 20);
        sc.setTask(task);
        this.scoreboard.put(p.getUniqueId(), sc);

    }

    public void setScoreboardGame(Player p, boolean rejoin) {

        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {
            return;
        }

        YamlConfiguration config = this.getConfig();
        Game game = match.getGame();
        boolean isEnabled = config.getBoolean("ScoreboardPath." + game.getScoreboardPath() + ".Game-Enabled");

        if (!isEnabled) {
            return;
        }

        if (this.getConfig().get("ScoreboardPath." + game.getScoreboardPath() + ".Game.Lines") == null) {
            return;
        }

        if (this.scoreboard.containsKey(p.getUniqueId())) {
            this.scoreboard.get(p.getUniqueId()).getTask().cancel();
        }

        if (!match.getPlayerTeam().containsKey(p.getUniqueId())) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());
        String you = Utils.translate(this.getConfig().getString("YouDisplay"));
        ScoreboardAPI sc = new ScoreboardAPI(p.getUniqueId(),
                this.getConfig().getString("ScoreboardPath." + game.getScoreboardPath() + ".Game-Title"), "bedwars-game");
        Scoreboard scoreboard = sc.getScoreboard();
        List<String> list = this.getConfig().getStringList("ScoreboardPath." + game.getScoreboardPath() + ".Game.Lines");
        this.registerHealthBar(scoreboard, match);
        Set<Team> teams = new HashSet<>(match.getPlayerTeam().values());
        boolean tabTeam = game.getArenaOptions().getBoolean("TablistTeams");

        for (Team teamObj : teams) {

            String teamName = teamObj.getName();
            TeamColor colorObj = teamObj.getTeamColor();
            String letter = teamObj.getLetter();
            String chatColor = colorObj.getChatColor();
            char firstLetter = teamName.charAt(0);
            org.bukkit.scoreboard.Team t = scoreboard.registerNewTeam(letter + teamName);
            org.bukkit.scoreboard.Team inv = scoreboard.registerNewTeam(letter + teamName + "I");

            inv.setPrefix(Utils.translate(chatColor + firstLetter + " "));
            // inv.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
            inv.setNameTagVisibility(NameTagVisibility.NEVER);
            t.setPrefix(Utils.translate(chatColor + firstLetter + " "));

            if (tabTeam) {

                for (Player player : teamObj.getPlayers()) {
                    t.addEntry(player.getName());
                }

            }

            sc.getTeams().add(letter + team.getName());

        }

//		final Pattern pattern = Pattern.compile("<isAlive(.*?)>");

        int month = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int minTeams = game.getMinTeamsToStart();
        String mapName = game.getName();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                sc.clear();
                for (String string : list) {

                    if ((plugin != null) && plugin.isEnabled()) {
                        string = PlaceholderAPI.setPlaceholders(p, string);
                    }

//					Matcher matcher = pattern.matcher(string);
//					
//					while (matcher.find()) {
//					   String teamName = matcher.group(1);
//					   string = string.replace("<isAlive" + teamName + ">", match.isAliveTeam(teamObj) + (team.getName().equals(teamName) ? you : ""));
//					}

                    for (Team teamObj : game.getTeams()) {
                        String teamName = teamObj.getName();
                        string = string.replace("<isAlive" + teamName + ">",
                                match.isAliveTeam(teamObj) + (team.getName().equals(teamName) ? you : ""));
                    }

                    //					if(match.getMatchState() != MatchState.IN_GAME) {
//						this.cancel();
//						return;
//					}

                    MatchData data = match.getMatchData(p);

                    sc.addLine(Utils.translate(string).replace("<mapName>", mapName)
                            .replace("<inGame>", match.getPlayers().size() + "")
                            .replace("<max>", game.getMaxPlayers() + "").replace("<min>", minTeams + "")
                            .replace("<nextEvent>", match.getEvent().getDisplay())
                            .replace("<time>", StringUtils.formatIntoHHMMSS(match.getEvent().getSeconds()))
                            .replace("<day>", Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "")
                            .replace("<month>", month + "").replace("<year>", year + "")
                            .replace("<kills>", data.getKills() + "").replace("<deaths>", data.getDeaths() + "")
                            .replace("<finalKills>", data.getFinalKills() + "")
                            .replace("<bedsBroken>", data.getBedBroken() + ""));

                }

                sc.updateScoreboard(p);

            }
        }.runTaskTimerAsynchronously(BedWarsPlugin.getInstance(), 0, 15);

        sc.setTask(task);
        p.setScoreboard(sc.getScoreboard());

        if (!rejoin) {
            p.setHealth(p.getHealth() - 0.001);
        } else {
            match.getPlayers().forEach(player -> player.setHealth(player.getHealth() - 0.001));
        }

        this.scoreboard.put(p.getUniqueId(), sc);

    }

    public void createPathScoreboard(Game game) {

        String path = game.getScoreboardPath();

        if (this.getConfig().get("ScoreboardPath." + path + ".Game.Lines") == null) {

            this.getConfig().addDefault("ScoreboardPath." + path + ".Game-Enabled", true);
            this.getConfig().addDefault("ScoreboardPath." + path + ".Game-Title", "&e BED WARS");

            List<String> list = new ArrayList<String>();
            list.add(" ");
            list.add("<nextEvent>:");
            list.add("&a<time>");
            list.add(" ");

            for (int i = 0; i < game.getTeams().size(); i++) {
                Team team = game.getTeams().get(i);
                list.add(team.getTeamColor().getUntranslatedChatColor() + "&l" + team.getName().toUpperCase().charAt(0)
                        + " &f" + team.getName() + ": <isAlive" + team.getName() + ">");
            }

            list.add(" ");
            list.add("&ewww.spigotmc.org");

            this.getConfig().addDefault("ScoreboardPath." + path + ".Game-Enabled", Boolean.TRUE);
            this.getConfig().addDefault("ScoreboardPath." + path + ".Game-Title", "&e BED WARS");
            this.getConfig().addDefault("ScoreboardPath." + path + ".Game.Lines", list);

        }

        if (this.getConfig().get("ScoreboardPath." + path + ".Waiting.Lines") == null) {

            List<String> lobby = new ArrayList<String>();
            this.getConfig().addDefault("ScoreboardPath." + path + ".Waiting-Enabled", Boolean.TRUE);
            this.getConfig().addDefault("ScoreboardPath." + path + ".Waiting-Title", "&e BED WARS");

            lobby.add("");
            lobby.add("&fMap: &a<mapName>");
            lobby.add("&fPlayers: &a<inGame>/<max>");
            lobby.add("");
            lobby.add("<statusDisplay>");
            lobby.add(" ");
            lobby.add("&fServer: &aBedWars122G");
            lobby.add("");
            lobby.add("&ewww.spigotmc.org");

            this.getConfig().addDefault("ScoreboardPath." + path + ".Waiting.Lines", lobby);

        }

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

    }

    public void toggleScoreboard(Player p) {

        ScoreboardAPI sc = this.scoreboard.get(p.getUniqueId());

        if (sc == null) {
            return;
        }

        sc.unregisterObjective("health");
        sc.getTask().cancel();
        sc.toggleScoreboard();
        sc.clearTeams();
        this.scoreboard.remove(p.getUniqueId());

    }

    public void registerHealthBar(Scoreboard scoreboard, AMatch match) {

        if (!match.getGame().getArenaOptions().getBoolean("HealthBar.Enabled")) {
            return;
        }

        Objective o = scoreboard.registerNewObjective("health", "health");
        o.setDisplayName(Utils.translate(match.getGame().getArenaOptions().getString("HealthBar.Display")));
        o.setDisplaySlot(DisplaySlot.BELOW_NAME);

    }

    public void saveConfig() {
        try {
            this.scoreboardConfig.save(this.scoreboardFile);
        } catch (IOException ignored) {

        }
    }

    public YamlConfiguration getConfig() {
        return this.scoreboardConfig;
    }

}
