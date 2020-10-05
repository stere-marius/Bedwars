package ro.marius.bedwars.party;

import org.bukkit.entity.Player;

import java.util.Set;

public interface PartyHandler {

    boolean hasParty(Player player);

    Player getLeader(Player partyPlayer);

    Set<Player> getMembers(Player partyPlayer);

    void leave(Player partyPlayer);

    void addMember(Player partyOwner, Player playerToAdd);

    void kickMember(Player partyOwner, Player playerToKick);

    void disband(Player partyPlayer);

    void sendMessage(Player partyPlayer, String message);

}
