package ro.marius.bedwars.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

public class RespawnTask {

    private Player player;
    private int time = 5;
    private AMatch match;

    private boolean send;
    private String title;
    private String subTitle;
    private int fadeIn;
    private int fadeOut;

    private BukkitTask task;

    public RespawnTask(Player player, AMatch match) {
        this.player = player;
        this.match = match;

        ArenaOptions arenaOptions = this.match.getGame().getArenaOptions();

        this.send = arenaOptions.getPathBoolean("RespawnTitle.Enabled");
        this.title = Utils.defaultNullValue(arenaOptions.getPathString("RespawnTitle.Title"), "");
        this.subTitle = Utils.defaultNullValue(arenaOptions.getPathString("RespawnTitle.SubTitle"), "");
        this.fadeIn = arenaOptions.getPathInt("RespawnTitle.FadeIn");
        this.fadeOut = arenaOptions.getPathInt("RespawnTitle.FadeOut");
    }

    public void startTask() {

        BukkitRunnable task = new BukkitRunnable() {

            @Override
            public void run() {
                RespawnTask.this.setTime(RespawnTask.this.getTime() - 1);

                if (Bukkit.getPlayer(RespawnTask.this.player.getName()) == null) {
                    this.cancel();
                    return;
                }

                if (!RespawnTask.this.match.getPlayerTeam().containsKey(RespawnTask.this.player.getUniqueId())) {
                    this.cancel();
                    return;
                }


                if (RespawnTask.this.time == 4) {
                    ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(RespawnTask.this.player, RespawnTask.this.fadeIn, 20 * 4, RespawnTask.this.fadeOut,
                            RespawnTask.this.title.replace("<seconds>", RespawnTask.this.time + ""), RespawnTask.this.subTitle.replace("<seconds>", RespawnTask.this.time + ""), RespawnTask.this.send,
                            true);
                }

                ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(RespawnTask.this.player, RespawnTask.this.fadeIn, 20, RespawnTask.this.fadeOut, null,
                        RespawnTask.this.subTitle.replace("<seconds>", RespawnTask.this.time + ""), RespawnTask.this.send, false);

                if (RespawnTask.this.getTime() <= 0) {
                    RespawnTask.this.respawn();
                    this.cancel();
                }

            }
        };

        this.task = task.runTaskTimer(BedWarsPlugin.getInstance(), 0, 20);

    }

    @SuppressWarnings("deprecation")
    public void respawn() {
        ArenaOptions arenaOptions = this.match.getGame().getArenaOptions();
        boolean send = arenaOptions.getBoolean("RespawnedTitle.Enabled");
        String title = Utils.translate(arenaOptions.getString("RespawnedTitle.Title"));
        String subTitle = Utils.translate(arenaOptions.getString("RespawnedTitle.SubTitle"));
        int fadeIn = arenaOptions.getInt("RespawnedTitle.FadeIn");
        int stay = arenaOptions.getInt("RespawnedTitle.Stay");
        int fadeOut = arenaOptions.getInt("RespawnedTitle.FadeOut");
        ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(this.player, fadeIn, stay, fadeOut, title, subTitle, send,
                true);
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
        Utils.resetPlayer(this.player, true, true);
        Team team = this.match.getPlayerTeam().get(this.player.getUniqueId());
        this.player.teleport(team.getSpawnLocation().getLocation());
        this.player.setFallDistance(0.0F);
        team.giveRespawnKit(this.match, this.player);
        this.match.getMatchData(this.player).addDeath();

        for (Player matchPlayer : this.match.getPlayers()) {
            ManagerHandler.getVersionManager().getVersionWrapper().showPlayer(matchPlayer, this.player, BedWarsPlugin.getInstance());
        }

        this.player.spigot().setCollidesWithEntities(true);
        this.match.getSpectators().remove(this.player);
    }

    public void cancelTask() {

        if (this.task == null) {
            return;
        }

        this.task.cancel();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }

    public boolean isSend() {
        return this.send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    public BukkitTask getTask() {
        return this.task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }
}
