package ro.marius.bedwars.game.mechanics;

import ro.marius.bedwars.utils.CuboidSelection;

public class LobbyRemoval {

    private final CuboidSelection waitingLobbySelection;

    public LobbyRemoval(CuboidSelection waitingLobbySelection) {
        this.waitingLobbySelection = waitingLobbySelection;
    }

    public void removeWaitingLobby(){
        
    }

    public CuboidSelection getWaitingLobbySelection() {
        return waitingLobbySelection;
    }
}
