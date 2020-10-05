package ro.marius.bedwars.scoreboard;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardAPI {

    private final List<ScoreboardText> list;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private String tag;
    private int lastSentCount;
    private final UUID player;
    private BukkitTask task;
    private final List<String> teams;

    public ScoreboardAPI(UUID player, String title, String objectiveName) {
        this.list = new ArrayList<>();
        this.lastSentCount = -1;
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = getRegisteredScoreboardObjective(Utils.translate(title), objectiveName);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.setTitle(title);
        this.teams = new ArrayList<>();
    }

    private Objective getRegisteredScoreboardObjective(String title, String objectiveName) {
        try {
            return scoreboard.registerNewObjective(objectiveName, "dummy", title);
        } catch (NoSuchMethodError ex) {
            return scoreboard.registerNewObjective(objectiveName, "dummy");
        }
    }

    public void addLine(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        ScoreboardText scText;
        if (text.length() <= 16) {
            scText = new ScoreboardText(text, "");
        } else {
            String first = text.substring(0, 16);
            String second = text.substring(16);
            if (first.endsWith("§")) {
                first = first.substring(0, first.length() - 1);
                second = '§' + second;
            }
            String lastColors = ChatColor.getLastColors(first);
            second = lastColors + second;
            scText = new ScoreboardText(first, StringUtils.left(second, 16));
        }
        this.list.add(scText);
    }

    public void removeLine(int index) {
        String name = this.getNameForIndex(index);
        this.scoreboard.resetScores(name);
        Team team = this.getOrCreateTeam(
                String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + index, index);
        team.unregister();
    }

    public void updateScoreboard(Player player) {
        player.setScoreboard(this.scoreboard);
        for (int i = 0; i < this.list.size(); ++i) {
            Team team = this.getOrCreateTeam(
                    String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + i, i);
            ScoreboardText str = this.list.get(this.list.size() - i - 1);
            team.setPrefix(str.getLeft());
            team.setSuffix(str.getRight());
            this.objective.getScore(this.getNameForIndex(i)).setScore(i + 1);
        }
        if (this.lastSentCount != -1) {
            for (int j = this.list.size(), k = 0; k < (this.lastSentCount - j); ++k) {
                this.removeLine(j + k);
            }
        }
        this.lastSentCount = this.list.size();
    }

    public Team getOrCreateTeam(String team, int i) {
        Team value = this.scoreboard.getTeam(team);
        if (value == null) {
            value = this.scoreboard.registerNewTeam(team);
            value.addEntry(this.getNameForIndex(i));
        }
        return value;
    }

    public void clearTeams() {

        if (this.scoreboard.getTeams().isEmpty()) {
            return;
        }

        for (Team team : this.scoreboard.getTeams()) {

            this.scoreboard.getTeam(team.getName()).unregister();
        }
    }

    public void removeFromTeam(Player p, ro.marius.bedwars.team.Team team) {

        Team teamObj = this.scoreboard.getTeam(team.getLetter() + team.getName());

        if (teamObj == null) {
            return;
        }
        if (!teamObj.getEntries().contains(p.getName())) {
            return;
        }

        teamObj.removeEntry(p.getName());

        if (teamObj.getEntries().isEmpty()) {
            teamObj.unregister();
        }

    }

    public void setTitle(String title) {
        title = ChatColor.translateAlternateColorCodes('&', title);
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        this.objective.setDisplayName(title);
    }

    public void unregisterTeam(String teamName) {
        if (this.scoreboard.getTeam(teamName) == null) {
            return;
        }
        this.scoreboard.getTeam(teamName).unregister();
    }

    public void unregisterObjective(String objective) {
        if (this.scoreboard.getObjective(objective) == null) {
            return;
        }
        this.scoreboard.getObjective(objective).unregister();
    }

    public String getNameForIndex(int index) {
        return String.valueOf(String.valueOf(ChatColor.values()[index].toString())) + ChatColor.RESET;
    }

    public void clear() {
        this.list.clear();
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.player);
    }

    public void toggleScoreboard() {
        this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public List<String> getTeams() {
        return this.teams;
    }

    private static class ScoreboardText {
        private final String left;
        private final String right;

        public ScoreboardText(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return this.left;
        }

        public String getRight() {
            return this.right;
        }
    }

}
