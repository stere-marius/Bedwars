package ro.marius.bedwars.party.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.party.PartyHandler;

import java.util.*;

public class BedwarsPartyHandler implements PartyHandler {

    private Map<UUID, BedwarsParty> partyMap = new HashMap<>();

    @Override
    public boolean hasParty(Player player) {
        return partyMap.containsKey(player.getUniqueId());
    }

    @Override
    public Player getLeader(Player playerParty) {
        return partyMap.get(playerParty.getUniqueId()) == null ? null : partyMap.get(playerParty.getUniqueId()).getOwner();
    }

    @Override
    public Set<Player> getMembers(Player partyOwner) {

        BedwarsParty bedwarsParty = partyMap.get(partyOwner.getUniqueId());

        return bedwarsParty == null ? Collections.emptySet() : bedwarsParty.getPlayerMembers();
    }

    @Override
    public void leave(Player player) {
        BedwarsParty party = partyMap.get(player.getUniqueId());

        if (party == null) {
            player.sendMessage(Lang.NOT_IN_PARTY.getString());
            return;
        }


        if (party.isOwner(player)) {
            disband(player);
            return;
        }

        player.sendMessage(Lang.PARTY_LEAVE.getString().replace("<player>", party.getOwner().getName()));
        party.getMembers().remove(player.getUniqueId());
        party.sendMessage(Lang.PLAYER_LEFT_FROM_PARTY.getString().replace("<player>", player.getName()));
        partyMap.remove(player.getUniqueId());

        if (party.getMembers().size() > 1) {
            return;
        }

        disband(party.getOwner());
    }

    @Override
    public void addMember(Player partyOwner, Player playerToAdd) {

        BedwarsParty party = this.partyMap.get(partyOwner.getUniqueId());

        if (party == null) {
            party = new BedwarsParty(partyOwner);
            partyMap.put(partyOwner.getUniqueId(), party);
        }

        if (!party.isOwner(partyOwner)) {
            partyOwner.sendMessage(Lang.ONLY_LEADER_CAN_INVITE.getString());
            return;
        }

        if (partyMap.get(playerToAdd.getUniqueId()) != null) {
            partyOwner.sendMessage(Lang.PLAYER_IN_PARTY.getString().replace("<player>", playerToAdd.getName()));
            return;
        }

        party.getMembers().put(playerToAdd.getUniqueId(), "MEMBER");
        party.getMembers().keySet().forEach(uuid ->
                Bukkit.getPlayer(uuid).sendMessage(Lang.PLAYER_JOINED_IN_PARTY.getString().replace("<player>", playerToAdd.getName())));
        this.partyMap.put(playerToAdd.getUniqueId(), party);
    }

    @Override
    public void kickMember(Player partyOwner, Player playerToKick) {

        BedwarsParty party = this.partyMap.get(partyOwner.getUniqueId());

        if (party == null) {
            partyOwner.sendMessage(Lang.NOT_IN_PARTY.getString());
            return;
        }

        if (!party.isOwner(partyOwner)) {
            partyOwner.sendMessage(Lang.ONLY_LEADER_CAN_KICK.getString());
            return;
        }

        if (!party.getMembers().containsKey(playerToKick.getUniqueId())) {
            partyOwner.sendMessage(Lang.NOT_IN_YOUR_PARTY.getString().replace("<player>", playerToKick.getName()));
            return;
        }

        party.getMembers().remove(playerToKick.getUniqueId());
        partyMap.remove(playerToKick.getUniqueId());
        party.sendMessage(Lang.PLAYER_KICKED_FROM_PARTY.getString().replace("<player>", playerToKick.getName()));
        playerToKick.sendMessage(
                Lang.PARTY_KICK.getString().replace("<player>", partyOwner.getName()));

    }

    @Override
    public void disband(Player partyOwner) {

        BedwarsParty party = this.partyMap.get(partyOwner.getUniqueId());

        if (party == null) {
            partyOwner.sendMessage(Lang.NOT_IN_PARTY.getString());
            return;
        }

        if (!party.isOwner(partyOwner)) {
            partyOwner.sendMessage(Lang.ONLY_LEADER_CAN_DISBAND.getString());
            return;
        }

        party.sendMessage(Lang.PARTY_DISBAND_BY_LEADER.getString().replace("<player>", partyOwner.getName()));
        party.getMembers().keySet().forEach(partyMap::remove);
        party.getMembers().clear();
    }

    @Override
    public void sendMessage(Player playerParty, String message) {
        BedwarsParty party = this.partyMap.get(playerParty.getUniqueId());

        if (party == null)
            return;

        party.sendMessage(message);
    }
}
