package ro.marius.bedwars.party.parties_api;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ro.marius.bedwars.party.PartyHandler;

import java.util.Set;
import java.util.stream.Collectors;

public class PartiesApiPartyHandler implements PartyHandler {

    private final PartiesAPI partiesAPI = Parties.getApi();

    @Override
    public boolean hasParty(Player player) {
        return partiesAPI.getPartyPlayer(player.getUniqueId()).isInParty();
    }

    @Override
    public Player getLeader(Player player) {
        PartyPlayer partyPlayer = partiesAPI.getPartyPlayer(player.getUniqueId());

        if(partyPlayer == null) return null;

        Party party = partiesAPI.getParty(partyPlayer.getPartyId());
        Bukkit.broadcastMessage("The leader is " + Bukkit.getPlayer(party.getLeader()).getName());
        return Bukkit.getPlayer(party.getLeader());
    }

    @Override
    public Set<Player> getMembers(Player player) {
        PartyPlayer partyPlayer = partiesAPI.getPartyPlayer(player.getUniqueId());
        Party party = partiesAPI.getParty(partyPlayer.getPartyId());
        Set<Player> players = party
                .getMembers()
                .stream()
                .map(Bukkit::getPlayer)
                .collect(Collectors.toSet());
        Bukkit.broadcastMessage("Party's members");
        players.forEach(p -> Bukkit.broadcastMessage(p.getName()));
        return players;
    }

    @Override
    public void leave(Player player) {

    }

    @Override
    public void addMember(Player partyOwner, Player playerToAdd) {

    }

    @Override
    public void kickMember(Player partyOwner, Player playerToKick) {

    }

    @Override
    public void disband(Player player) {

    }

    @Override
    public void sendMessage(Player player, String message) {

    }
}
