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
        ItemStack itemStack = new ItemStack(Material.AIR);

        VersionWrapper version = ManagerHandler.getVersionManager().getVersionWrapper();
        String text = this.team.getLetter() + this.team.getName();
        String playerName = this.player.getName();

        for (Player gamePlayer : this.match.getPlayers()) {

            version.sendPacketEquipment(this.player, gamePlayer, itemStack, 1);
            version.sendPacketEquipment(this.player, gamePlayer, itemStack, 2);
            version.sendPacketEquipment(this.player, gamePlayer, itemStack, 3);
            version.sendPacketEquipment(this.player, gamePlayer, itemStack, 4);
            Scoreboard sc = ManagerHandler.getScoreboardManager().scoreboard.get(gamePlayer.getUniqueId()).getScoreboard();

            if (sc == null) {
                continue;
            }

            org.bukkit.scoreboard.Team iTeam = sc.getTeam(text + "I");

            if (iTeam == null) {
                continue;
            }

            if (iTeam.getEntries().contains(playerName)) {
                continue;
            }

            iTeam.addEntry(playerName);

        }

    }

    public void undoInvisibility() {

        VersionWrapper version = ManagerHandler.getVersionManager().getVersionWrapper();
        String text = this.team.getLetter() + this.team.getName();
        String playerName = this.player.getName();

        for (Player gamePlayer : this.match.getPlayers()) {

            version.sendPacketEquipment(this.player, gamePlayer, this.helmet, 4);
            version.sendPacketEquipment(this.player, gamePlayer, this.chestplate, 3);
            version.sendPacketEquipment(this.player, gamePlayer, this.leggings, 2);
            version.sendPacketEquipment(this.player, gamePlayer, this.boots, 1);

            Scoreboard sc = ManagerHandler.getScoreboardManager().scoreboard.get(gamePlayer.getUniqueId()).getScoreboard();

            if (sc == null) {
                continue;
            }

            org.bukkit.scoreboard.Team iTeam = sc.getTeam(text + "I");

            if ((iTeam != null) && iTeam.getEntries().contains(playerName)) {
                iTeam.removeEntry(playerName);
            }

            org.bukkit.scoreboard.Team nTeam = sc.getTeam(text);

            if ((nTeam != null) && !nTeam.getEntries().contains(playerName)) {
                nTeam.addEntry(playerName);
            }
        }

        PlayerInventory pInv = this.player.getInventory();

        pInv.setHelmet(this.helmet);
        pInv.setChestplate(this.chestplate);
        pInv.setLeggings(this.leggings);
        pInv.setBoots(this.boots);

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
