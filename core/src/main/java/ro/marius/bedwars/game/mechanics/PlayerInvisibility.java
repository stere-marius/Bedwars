package ro.marius.bedwars.game.mechanics;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.VersionWrapper;
import ro.marius.bedwars.team.Team;

public class PlayerInvisibility {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private BukkitTask task;
    private AMatch match;
    private Team team;
    private Player player;

    public PlayerInvisibility(AMatch match, Player player) {

        this.helmet = player.getInventory().getHelmet();
        this.chestplate = player.getInventory().getChestplate();
        this.leggings = player.getInventory().getLeggings();
        this.boots = player.getInventory().getBoots();
        this.match = match;
        this.team = match.getPlayerTeam().get(player.getUniqueId());
        this.player = player;
    }

    public void putInvisibility() {
        ManagerHandler.getVersionManager().getVersionWrapper().sendHideEquipmentPacket(player, this.match.getPlayers());
        hidePlayerNameTag();
    }


    public void undoInvisibility() {
        ManagerHandler.getVersionManager().getVersionWrapper().sendShowEquipmentPacket(player, this.match.getPlayers());
        showPlayerNameTag();
    }

    public void showPlayerNameTag() {
        String scoreboardTeamName = getScoreboardTeamName();
        String playerName = this.player.getName();

        for (Player gamePlayer : this.match.getPlayers()) {

            Scoreboard sc = ManagerHandler.getScoreboardManager().scoreboard.get(gamePlayer.getUniqueId()).getScoreboard();

            if (sc == null) {
                continue;
            }

            org.bukkit.scoreboard.Team iTeam = sc.getTeam(scoreboardTeamName + "I");

            if ((iTeam != null) && iTeam.getEntries().contains(playerName)) {
                iTeam.removeEntry(playerName);
            }

            org.bukkit.scoreboard.Team nTeam = sc.getTeam(scoreboardTeamName);

            if ((nTeam != null) && !nTeam.getEntries().contains(playerName)) {
                nTeam.addEntry(playerName);
            }
        }
    }

    public void hidePlayerNameTag() {
        String scoreboardTeamName = getScoreboardTeamName();
        String playerName = this.player.getName();
        for (Player gamePlayer : this.match.getPlayers()) {

            Scoreboard sc = ManagerHandler.getScoreboardManager().scoreboard.get(gamePlayer.getUniqueId()).getScoreboard();

            if (sc == null) {
                continue;
            }

            org.bukkit.scoreboard.Team iTeam = sc.getTeam(scoreboardTeamName + "I");

            if (iTeam == null) {
                continue;
            }

            if (iTeam.getEntries().contains(playerName)) {
                continue;
            }

            iTeam.addEntry(playerName);

        }
    }

    public String getScoreboardTeamName(){
        return this.team.getLetter() + this.team.getName();
    }

    public void runTaskRemove() {

        this.task = new BukkitRunnable() {

            @Override
            public void run() {

                PlayerInvisibility.this.undoInvisibility();
                PlayerInvisibility.this.match.getInvisibility().remove(PlayerInvisibility.this.player);

            }
        }.runTaskLaterAsynchronously(BedWarsPlugin.getInstance(), 20 * 45);

    }

    public void cancelTask() {

        if (this.task == null) {
            return;
        }

        this.task.cancel();
    }


    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
