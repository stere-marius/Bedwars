package ro.marius.bedwars.configuration;

import java.util.ArrayList;
import java.util.List;

public class LevelConfiguration {

    private int nextLevelXP;
    private List<String> consoleCommands, playerCommands = new ArrayList<>();

    public int getNextLevelXP() {
        return this.nextLevelXP;
    }

    public void setNextLevelXP(int nextLevelXP) {
        this.nextLevelXP = nextLevelXP;
    }

    public List<String> getConsoleCommands() {
        return this.consoleCommands;
    }

    public void setConsoleCommands(List<String> consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    public List<String> getPlayerCommands() {
        return this.playerCommands;
    }

    public void setPlayerCommands(List<String> playerCommands) {
        this.playerCommands = playerCommands;
    }
}
