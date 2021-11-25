package ro.marius.bedwars.party.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class BedwarsParty {

    public final Map<UUID, String> members = new LinkedHashMap<>();

    public BedwarsParty(Player owner) {
        this.members.put(owner.getUniqueId(), "owner");
    }

    public Player getOwner() {

        for (Map.Entry<UUID, String> entry : members.entrySet()) {

            if (!"owner".equals(entry.getValue()))
                continue;

            return Bukkit.getPlayer(entry.getKey());
        }

        return null;
    }

    public Set<Player> getPlayerMembers() {

        Set<Player> playerMembers = new HashSet<>();
        members.keySet().forEach(uuid -> playerMembers.add(Bukkit.getPlayer(uuid)));

        return playerMembers;
    }


    public boolean isOwner(Player player) {
        return members.get(player.getUniqueId()) != null && members.get(player.getUniqueId()).equals("owner");
    }

    public Map<UUID, String> getMembers() {
        return members;
    }

    public void sendMessage(String message) {
        members.keySet().forEach(player -> Bukkit.getPlayer(player).sendMessage(message));
    }
}
