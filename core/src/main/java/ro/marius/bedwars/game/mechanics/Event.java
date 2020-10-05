package ro.marius.bedwars.game.mechanics;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.generator.DiamondGenerator;
import ro.marius.bedwars.generator.EmeraldGenerator;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

public class Event {

    private EventType eventType = EventType.DIAMOND_II;
    private BukkitTask task;
    private AMatch match;
    private int seconds = 360;
    private String eventDisplay;

    public Event(AMatch match) {
        this.match = match;
        this.eventDisplay = match.getGame().getArenaOptions().getString("EventDisplay.DiamondUpgrade")
                .replace("<level>", "II");
    }

    public void reset() {
        this.eventType = EventType.DIAMOND_II;
        this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.DiamondUpgrade")
                .replace("<level>", "II");
        this.seconds = 360;
    }

    public void startTask() {

        this.task = new BukkitRunnable() {

            @Override
            public void run() {
                Event.this.seconds--;

                if (Event.this.seconds <= 0) {
                    Event.this.doMethodNextEvent();
                    Event.this.seconds = (Event.this.eventType == EventType.TIME_LEFT) ? 600 : 360;
                }

            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 20, 20);
    }

    public String getDisplay() {

        return this.eventDisplay.replace("<eventTime>", this.seconds + "");
    }

    private void doMethodNextEvent() {

        // TODO: Lista de evenimente

//        public Event(String message,String eventDisplay)

//        public Event(String message)

//        public Event(String eventDisplay)

//        public Event()

//        public void performEvent()
//        if(message != null)

        if (this.eventType == EventType.DIAMOND_II) {
            this.match.sendMessage(this.match.getGame().getArenaOptions().getString("DiamondGenerator.Tier.II.Message"));
            this.match.getDiamondGenerators().forEach(DiamondGenerator::upgradeTier);
            this.eventType = EventType.EMERALD_II;
            this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.EmeraldUpgrade")
                    .replace("<level>", "II");
            return;
        }

        if (this.eventType == EventType.EMERALD_II) {
            this.match.sendMessage(this.match.getGame().getArenaOptions().getString("EmeraldGenerator.Tier.II.Message"));
            this.match.getEmeraldGenerators().forEach(EmeraldGenerator::upgradeTier);
            this.eventType = EventType.DIAMOND_III;
            this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.DiamondUpgrade")
                    .replace("<level>", "III");
            return;
        }

        if (this.eventType == EventType.DIAMOND_III) {
            this.match.sendMessage(this.match.getGame().getArenaOptions().getString("DiamondGenerator.Tier.III.Message"));
            this.match.getDiamondGenerators().forEach(DiamondGenerator::upgradeTier);
            this.eventType = EventType.EMERALD_III;
            this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.EmeraldUpgrade")
                    .replace("<level>", "III");
            return;
        }

        if (this.eventType == EventType.EMERALD_III) {
            this.match.sendMessage(this.match.getGame().getArenaOptions().getString("EmeraldGenerator.Tier.III.Message"));
            this.match.getEmeraldGenerators().forEach(EmeraldGenerator::upgradeTier);
            this.eventType = EventType.BED_GONE;
            this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.BedGone");
            return;
        }

        if (this.eventType == EventType.BED_GONE) {

            this.match.getRejoinMap().clear();

            Iterator<Entry<UUID, Team>> it = this.match.getPlayerTeam().entrySet().iterator();

            while (it.hasNext()) {

                Entry<UUID, Team> entry = it.next();
                Team team = entry.getValue();

                match.destroyBed(team);
                team.setBedBroken(true);

                if (!team.getPlayers().isEmpty()) {
                    continue;
                }

                this.match.sendMessage(Lang.TEAM_ELIMINATED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                        .replace("<team>", team.getName()));
                this.match.getEliminatedTeams().add(team);
                this.match.isRequiredEnding();

                it.remove();

            }

            FileConfiguration config = this.match.getGame().getArenaOptions().getConfig();
            boolean send = config.getBoolean("AllBedsDestroyed.Enabled");
            String title = config.getString("AllBedsDestroyed.Title");
            String subTitle = config.getString("AllBedsDestroyed.SubTitle");
            int fadeIn = config.getInt("AllBedsDestroyed.FadeIn");
            int stay = config.getInt("AllBedsDestroyed.Stay");
            int fadeOut = config.getInt("AllBedsDestroyed.FadeOut");

            for (Player p : this.match.getPlayers()) {
                ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(p, fadeIn, stay, fadeOut, title, subTitle,
                        send, true);
                Utils.sendSoundBedBroken(p);
            }

            this.eventType = EventType.TIME_LEFT;
            this.eventDisplay = this.match.getGame().getArenaOptions().getString("EventDisplay.TimeLeft");
            return;
        }

        if (this.eventType == EventType.TIME_LEFT) {
            this.match.endGame("TIME_LEFT");
            this.task.cancel();
        }

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

    public int getSeconds() {
        return this.seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

}
