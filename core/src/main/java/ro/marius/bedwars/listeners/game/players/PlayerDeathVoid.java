package ro.marius.bedwars.listeners.game.players;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.mechanics.Cause;
import ro.marius.bedwars.game.mechanics.PlayerDamageCause;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchData;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;

public class PlayerDeathVoid implements Listener {


    @EventHandler
    public void onEntityDeath(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        DamageCause cause = e.getCause();

        if (match == null) {
            return;
        }
        if (cause != DamageCause.VOID) {
            return;
        }
        if (match.getMatchState() != MatchState.IN_GAME) {
            return;
        }
        if (match.getSpectators().contains(p)) {
            return;
        }

        ArenaOptions arenaOptions = match.getGame().getArenaOptions();
        // e.setCancelled(true);
        p.setHealth(0.5);

        if (match.getDamageCause().containsKey(p)) {
            PlayerDamageCause damageCause = match.getDamageCause().get(p);
            Player damager = damageCause.getDamager();

            if ((damager != null) && (damageCause.getDamageCause() == Cause.KNOCKBACK) && (damageCause.getSeconds() < 10)
                    && (match.getPlayerTeam().get(damager.getUniqueId()) != null)) {

                Team pTeam = match.getPlayerTeam().get(p.getUniqueId());
                Team dTeam = match.getPlayerTeam().get(damager.getUniqueId());
                MatchData dMatchData = match.getMatchData(damager);

                if(!match.getSpectators().contains(damager)) {
                    Utils.addAvailableItems(p, damager);
                }

                match.sendMessage(Lang.PLAYER_KNOCKED_VOID.getString()
                        .replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                        .replace("<playerTeam>", pTeam.getName())
                        .replace("<damagerTeamColor>", dTeam.getTeamColor().getChatColor())
                        .replace("<damagerTeam>", dTeam.getName()).replace("<player>", p.getName())
                        .replace("<damager>", damager.getName())
                        .replace("<isFinalKill>", pTeam.isBedBroken() ? Lang.FINAL_KILL_DISPLAY.getString() : ""));
                dMatchData.addFinalKill(pTeam.isBedBroken());
                dMatchData.addKill();
                match.doMethodForBedBroken(p);
                arenaOptions.performCommands("FinalKillCommands", damager, pTeam.isBedBroken());
                return;
            }

        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());
        match.sendMessage(
                Lang.PLAYER_FALL_IN_VOID.getString().replace("<playerTeamColor>", team.getTeamColor().getChatColor())
                        .replace("<playerTeam>", team.getName()).replace("<player>", p.getName())
                        .replace("<isFinalKill>", team.isBedBroken() ? Lang.FINAL_KILL_DISPLAY.getString() : ""));
        match.doMethodForBedBroken(p);

    }

}
