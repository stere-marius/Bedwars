package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.mechanics.Cause;
import ro.marius.bedwars.game.mechanics.PlayerDamageCause;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchData;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

public class PlayerDeathGame implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player player = e.getEntity();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(player.getUniqueId());

        if (match == null) {
            return;
        }

        ArenaOptions arenaOptions = match.getGame().getArenaOptions();

        Team pTeam = match.getPlayerTeam().get(player.getUniqueId());
        boolean containsDamage = match.getDamageCause().containsKey(player);
        player.setHealth(20.0D);

        if (!containsDamage) {
            match.sendMessage(
                    Lang.PLAYER_DIED.getString().replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                            .replace("<playerTeam>", pTeam.getName()).replace("<player>", player.getName()));
            match.doMethodForBedBroken(player);
            e.setDeathMessage(null);
            e.setDroppedExp(0);
            e.getDrops().clear();
            return;
        }

        Player playerKiller = (player.getKiller() != null) ? player.getKiller()
                : match.getDamageCause().get(player).getDamager();

        if (playerKiller == null) {
            match.sendMessage(
                    Lang.PLAYER_DIED.getString().replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                            .replace("<playerTeam>", pTeam.getName()).replace("<player>", player.getName()));
            match.doMethodForBedBroken(player);
            e.setDeathMessage(null);
            e.setDroppedExp(0);
            e.getDrops().clear();
            return;
        }

        Team kTeam = match.getPlayerTeam().get(playerKiller.getUniqueId());
        MatchData kMatchData = match.getMatchData(playerKiller);

        PlayerDamageCause playerDamageCause = match.getDamageCause().get(player);
        int damageSeconds = playerDamageCause.getSeconds();
        Cause damageCause = playerDamageCause.getDamageCause();

        if ((damageCause == Cause.ARCHER) && (damageSeconds <= 1)) {
            Player killer = playerDamageCause.getDamager();
            kMatchData.addKill();
            kMatchData.addFinalKill(pTeam.isBedBroken());

            for (ItemStack itemStack : e.getDrops()) {

                Material type = itemStack.getType();

                if (!type.name().contains("INGOT") || (type != Material.DIAMOND) || (type != Material.EMERALD)) {
                    continue;
                }

                Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
                match.getMatchEntity().add(item);
            }

            match.sendMessage(Lang.PLAYER_KILLED_BY_ARROW.getString()
                    .replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                    .replace("<playerTeam>", pTeam.getName()).replace("<player>", player.getName())
                    .replace("<killerTeamColor>", kTeam.getTeamColor().getChatColor())
                    .replace("<killerTeam>", kTeam.getName()).replace("<killer>", killer.getName())
                    .replace("<isFinalKill>", pTeam.isBedBroken() ? Lang.FINAL_KILL_DISPLAY.getString() : ""));
            arenaOptions.performCommands("BedBreakCommands", killer, pTeam.isBedBroken());

        } else if ((damageCause == Cause.FIREBALL) && (damageSeconds <= 2)) {
            Player killer = playerDamageCause.getDamager();
            kMatchData.addKill();
            kMatchData.addFinalKill(pTeam.isBedBroken());

            for (ItemStack itemStack : e.getDrops()) {

                Material type = itemStack.getType();

                if (!type.name().contains("INGOT") || (type != Material.DIAMOND) || (type != Material.EMERALD)) {
                    continue;
                }

                Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
                match.getMatchEntity().add(item);
            }

            match.sendMessage(Lang.PLAYER_KILLED_BY_FIREBALL.getString()
                    .replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                    .replace("<playerTeam>", pTeam.getName()).replace("<player>", player.getName())
                    .replace("<killerTeamColor>", kTeam.getTeamColor().getChatColor())
                    .replace("<killerTeam>", kTeam.getName()).replace("<killer>", killer.getName())
                    .replace("<isFinalKill>", pTeam.isBedBroken() ? Lang.FINAL_KILL_DISPLAY.getString() : ""));
            arenaOptions.performCommands("FinalKillCommands", killer, pTeam.isBedBroken());

        } else if (kTeam != null) {

            Player p = e.getEntity();
            Utils.addAvailableItems(p, playerKiller);
            kMatchData.addKill();
            match.sendMessage(Lang.PLAYER_KILLED.getString().replace("<killer>", playerKiller.getName())
                    .replace("<killerTeamColor>", kTeam.getTeamColor().getChatColor())
                    .replace("<killerTeam>", kTeam.getName())
                    .replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                    .replace("<playerTeam>", pTeam.getName()).replace("<player>", p.getName())
                    .replace("<isFinalKill>", pTeam.isBedBroken() ? Lang.FINAL_KILL_DISPLAY.getString() : ""));
            kMatchData.addFinalKill(pTeam.isBedBroken());
            arenaOptions.performCommands("FinalKillCommands", playerKiller, pTeam.isBedBroken());

        } else {

            match.sendMessage(
                    Lang.PLAYER_DIED.getString().replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                            .replace("<playerTeam>", pTeam.getName()).replace("<player>", player.getName()));
        }

        match.doMethodForBedBroken(player);
        e.setDeathMessage(null);
        e.setDroppedExp(0);
        e.getDrops().clear();
    }

}
