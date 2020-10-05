package ro.marius.bedwars.game.gameobserver;

import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.game.mechanics.NPCArena;
import ro.marius.bedwars.manager.ManagerHandler;

import java.util.List;

public class NPCObserver implements GameObserver {

    private final Game game;

    public NPCObserver(Game game) {
        this.game = game;
    }

    @Override
    public void update() {
        String arenaType = game.getArenaType();
        List<NPCArena> list = ManagerHandler.getNPCManager().getNpc().get(arenaType);

        if (list == null || list.isEmpty()) {
            return;
        }

        int players = ManagerHandler.getGameManager().getPlayersPlaying(arenaType);
        list.forEach(s -> s.update(players));
    }
}
