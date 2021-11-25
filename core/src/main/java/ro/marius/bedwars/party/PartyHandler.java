package ro.marius.bedwars.party;

import org.bukkit.entity.Player;

import java.util.Set;

public interface PartyHandler {

    boolean hasParty(Player player);

    Player getLeader(Player player);

    Set<Player> getMembers(Player player);

    void leave(Player player);

    void addMember(Player partyOwner, Player playerToAdd);

    void kickMember(Player partyOwner, Player playerToKick);

    void disband(Player player);

    void sendMessage(Player player, String message);

}
