package ro.marius.bedwars.team.upgrade.team;

import org.bukkit.entity.Player;
import ro.marius.bedwars.floorgenerator.FloorGenerator;
import ro.marius.bedwars.floorgenerator.FloorGeneratorType;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.team.upgrade.IUpgrade;

public class GeneratorUpgrade implements IUpgrade {

    private FloorGeneratorType generatorType;
    private int time;
    private int amount;

    public GeneratorUpgrade(FloorGeneratorType generatorType, int time, int amount) {
        this.generatorType = generatorType;
        this.time = time;
        this.amount = amount;
    }

    @Override
    public void onActivation(AMatch match, Player p) {

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team == null) {
            return;
        }

        if (this.generatorType == FloorGeneratorType.IRON) {
            FloorGenerator ironGenerator = team.getIronFloorGenerator();
            ironGenerator.setTimeTask(this.time);
            ironGenerator.setAmount(this.amount);
            ironGenerator.cancelTask();
            ironGenerator.start();

            return;
        }

        if (this.generatorType == FloorGeneratorType.GOLD) {
            FloorGenerator goldGenerator = team.getGoldFloorGenerator();
            goldGenerator.setTimeTask(this.time);
            goldGenerator.setAmount(this.amount);
            goldGenerator.cancelTask();
            goldGenerator.start();

            return;
        }

        if (this.generatorType == FloorGeneratorType.EMERALD) {
            FloorGenerator emeraldGenerator = team.getEmeraldFloorGenerator();

            if (emeraldGenerator == null) {
                emeraldGenerator = new FloorGenerator(match, FloorGeneratorType.EMERALD, this.amount, this.time,
                        team.getEmeraldGenerator());
                team.setEmeraldFloorGenerator(emeraldGenerator);
            }

            emeraldGenerator.setTimeTask(this.time);
            emeraldGenerator.setAmount(this.amount);
            emeraldGenerator.cancelTask();
            emeraldGenerator.start();
            emeraldGenerator.setLocation(team.getEmeraldGenerator());

        }

    }

    @Override
    public void cancelTask() {

    }

    @Override
    public IUpgrade clone() {

        return new GeneratorUpgrade(this.generatorType, this.time, this.amount);
    }

}
