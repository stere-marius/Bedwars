package ro.marius.bedwars.game.gameobserver;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

public class SignObserver implements GameObserver {

    private final Game game;

    public SignObserver(Game game) {
        this.game = game;
    }


    @Override
    public void update() {
        String firstLine = Utils.translate(game.getArenaOptions().getString("ArenaSign.FirstLine"));
        String secondLine = Utils.translate(game.getArenaOptions().getString("ArenaSign.SecondLine"));
        String thirdLine = Utils.translate(game.getArenaOptions().getString("ArenaSign.ThirdLine"));
        String fourthLine = Utils.translate(game.getArenaOptions().getString("ArenaSign.FourthLine"));
        AMatch match = game.getMatch();
        int size = match.getPlayers().size();
        String matchStateDisplay = game.getMod();

        for (Location loc : game.getGameSigns()) {
            BlockState state = loc.getBlock().getState();

            if (state instanceof Sign) {

                Sign s = (Sign) state;

                s.setLine(0, firstLine.replace("<arenaName>", game.getName()).replace("<inGame>", size + "")
                        .replace("<maxPlayers>", game.getMaxPlayers() + "").replace("<mode>", matchStateDisplay));
                s.setLine(1, secondLine.replace("<arenaName>", game.getName()).replace("<inGame>", size + "")
                        .replace("<maxPlayers>", game.getMaxPlayers() + "").replace("<mode>", matchStateDisplay));
                s.setLine(2, thirdLine.replace("<arenaName>", game.getName()).replace("<inGame>", size + "")
                        .replace("<maxPlayers>", game.getMaxPlayers() + "").replace("<mode>", matchStateDisplay));
                s.setLine(3, fourthLine.replace("<arenaName>", game.getName()).replace("<inGame>", size + "")
                        .replace("<maxPlayers>", game.getMaxPlayers() + "").replace("<mode>", matchStateDisplay));
                s.update(true);

            }
        }
    }
}
