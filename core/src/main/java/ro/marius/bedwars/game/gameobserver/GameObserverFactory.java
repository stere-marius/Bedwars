package ro.marius.bedwars.game.gameobserver;

import ro.marius.bedwars.game.Game;

import java.util.List;

public class GameObserverFactory {

    public static void registerObserver(Game game, GameObserver gameObserver){

        List<GameObserver> gameObserverList = game.getGameObservers();

    }

}
