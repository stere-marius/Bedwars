package ro.marius.bedwars.utils;

import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;

import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class DebugUtils {

    public static String getRejoinMapToString(AMatch match) {

        StringJoiner stringJoiner = new StringJoiner(" , ");

        for (Map.Entry<UUID, Team> entry : match.getRejoinMap().entrySet()) {
            stringJoiner.add("[ " + entry.getKey() + ":" + entry.getValue() + " ]");
        }

        return stringJoiner.toString();
    }

    public static String getPlayersToString(AMatch match) {

        StringJoiner stringJoiner = new StringJoiner(",");
        match.getPlayers().forEach(p -> stringJoiner.add(p.getName()));

        return stringJoiner.toString();
    }

    public static String getTeamsToString(AMatch match) {

        StringJoiner stringJoiner = new StringJoiner(" , ");

        for (Map.Entry<UUID, Team> entry : match.getPlayerTeam().entrySet()) {
            stringJoiner.add("[ " + entry.getKey() + ":" + entry.getValue() + " ]");
        }

        return stringJoiner.toString();
    }

}
