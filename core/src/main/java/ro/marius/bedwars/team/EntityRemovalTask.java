package ro.marius.bedwars.team;

import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public abstract class EntityRemovalTask extends BukkitRunnable {

    private int seconds;
    private LivingEntity livingEntity;
    private Team team;

    // animation information
    private String customName;
    private Map<Double, String> healthAnimation;
    private double lastHealth;
    private String currentHealthDisplay;

    public EntityRemovalTask(LivingEntity livingEntity, String customName, Map<Double, String> healthAnimation, Team team, int seconds) {
        this.livingEntity = livingEntity;
        this.seconds = seconds;
        this.team = team;
        this.customName = customName;
        this.healthAnimation = healthAnimation;
    }


    @Override
    public void run() {

        if (--seconds == 0) {
            livingEntity.remove();
            onComplete();
            this.cancel();
            return;
        }

        if (!livingEntity.isValid()) {
            cancel();
            onComplete();
            return;
        }

        if (lastHealth != livingEntity.getHealth() / 2) {
            lastHealth = livingEntity.getHealth() / 2;
            updateHealthDisplay();
        }

        livingEntity.setCustomName(customName
                .replace("<healthDisplay>", currentHealthDisplay)
                .replace("<timeLeft>", seconds + "")
                .replace("<teamName>", team.getName())
                .replace("<teamColor>", team.getTeamColor().getChatColor())
                .replace("<healthNumber>", livingEntity.getHealth() / 2 + ""));
    }

    public void updateHealthDisplay() {

        String healthDisplay = healthAnimation.get(lastHealth);

        if (healthDisplay == null)
            return;

        currentHealthDisplay = healthDisplay;
    }


    public abstract void onComplete();
}
