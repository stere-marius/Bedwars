package ro.marius.bedwars.team.upgrade;

import ro.marius.bedwars.team.upgrade.enemy.EnemyTrapEffect;
import ro.marius.bedwars.upgradeconfiguration.UpgradeTier;

public class QueuedTrap {

    private UpgradeTier tier;
    private EnemyTrapEffect effect;

    public QueuedTrap(UpgradeTier tier, EnemyTrapEffect effect) {
        this.tier = tier;
        this.effect = effect;
    }


    public UpgradeTier getTier() {
        return this.tier;
    }

    public EnemyTrapEffect getEffect() {
        return this.effect;
    }
}
