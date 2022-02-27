package ro.marius.bedwars.game.gameobserver;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.socketclient.ClientSocket;

import java.util.*;

public class SocketObserver implements GameObserver {

    private final Game game;

    public SocketObserver(Game game) {
        this.game = game;
    }

    @Override
    public void update() {

        AMatch match = game.getMatch();
        ClientSocket clientSocket = ManagerHandler.getSocketManager().getSocket();

        Gson gson = new Gson();
        Map<String, Object> gsonMap = new HashMap<>();
        gsonMap.put("ServerIP", Bukkit.getServer().getIp());
        gsonMap.put("ServerPort", Bukkit.getServer().getPort());
        gsonMap.put("GameName", game.getName());
        gsonMap.put("ArenaType", game.getArenaType());
        gsonMap.put("PlayersPerTeam", game.getPlayersPerTeam());
        gsonMap.put("MatchState", match.getMatchState().name());
        gsonMap.put("MatchPlayers", match.getPlayers().size());
        gsonMap.put("MaxPlayers", match.getGame().getMaxPlayers());
        Set<String> rejoinList = new HashSet<>();
        match.getRejoinMap().keySet().forEach(playerUUID -> rejoinList.add(playerUUID.toString()));
        gsonMap.put("RejoinUUID", rejoinList);
        Set<String> spectatorsList = new HashSet<>();
        match.getSpectators().forEach(playerUUID -> spectatorsList.add(playerUUID.toString()));
        gsonMap.put("SpectatorUUID", spectatorsList);

        String jsonObject = gson.toJson(gsonMap);

        if (clientSocket == null) return;

        clientSocket.sendMessage(jsonObject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketObserver that = (SocketObserver) o;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }
}
