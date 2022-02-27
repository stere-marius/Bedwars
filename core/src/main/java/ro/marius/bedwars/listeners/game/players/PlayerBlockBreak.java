package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.api.BedwarsGameBreakBlockEvent;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchData;
import ro.marius.bedwars.match.MatchState;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;

public class PlayerBlockBreak implements Listener {


    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();
        Block b = e.getBlock();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());

        if (match == null) {

            Location loc = b.getLocation();
            Game game = ManagerHandler.getGameManager().getGame(loc.getWorld().getName());

            if (game == null) {
                return;
            }

            boolean hasMetadata = b.hasMetadata("TeamBed");
            boolean isInside = game.getGameCuboid().isInsideCuboidSelection(loc);
            boolean isNotOP = !p.isOp();
            boolean isGame = game.getMatch().getMatchState() != MatchState.WAITING;

            e.setCancelled(hasMetadata && isInside && isNotOP && isGame);

            return;
        }

        if (match.getMatchState() != MatchState.IN_GAME) {
            p.sendMessage(Lang.DESTROY_NOT_PLACED_BLOCK.getString());
            e.setCancelled(true);
            return;
        }

        if (match.getSpectators().contains(p)) {
            e.setCancelled(true);
            return;
        }

        Team pTeam = match.getPlayerTeam().get(p.getUniqueId());

        if (pTeam == null) {
            return;
        }

        if (b.hasMetadata("TeamBed")) {

            Team team = match.getTeamAlive(b.getMetadata("TeamBed").get(0).asString());

            if (!match.getPlayerTeam().containsValue(team)) {
                match.destroyBed(team);
                e.setCancelled(true);
                return;
            }

            if (team.getPlayers().contains(p)) {

                if (XMaterial.isNewVersion() && p.getLocation().subtract(0, 0.5, 0).getBlock().getType().name().contains("BED")) {
                    p.teleport(p.getLocation().clone().add(0, 0.5, 0));
                }

                p.sendMessage(Lang.DESTROY_YOUR_BED.getString());
                e.setCancelled(true);
                return;
            }

            ArenaOptions arenaOptions = match.getGame().getArenaOptions();
            MatchData pData = match.getMatchData(p);
            e.setCancelled(true);
            match.destroyBed(team);
            match.getPlacedBeds().remove(team);
            team.setBedBroken(true);
            pData.addBedBroken();
            match.sendMessage(Lang.BED_DESTROYED.getString().replace("<teamColor>", team.getTeamColor().getChatColor())
                    .replace("<team>", team.getName()).replace("<player>", p.getName())
                    .replace("<playerTeamColor>", pTeam.getTeamColor().getChatColor())
                    .replace("<playerTeam>", pTeam.getName()));
            match.getPlayers().forEach(Utils::sendSoundBedBroken);
            boolean send = arenaOptions.getBoolean("BedDestroyedTitle.Enabled");
            String title = arenaOptions.getString("BedDestroyedTitle.Title");
            String subTitle = arenaOptions.getString("BedDestroyedTitle.SubTitle");
            int fadeIn = arenaOptions.getInt("BedDestroyedTitle.FadeIn");
            int stay = arenaOptions.getInt("BedDestroyedTitle.Stay");
            int fadeOut = arenaOptions.getInt("BedDestroyedTitle.FadeOut");
            team.getPlayers().forEach(pl -> ManagerHandler.getVersionManager().getVersionWrapper().sendTitle(pl, fadeIn, stay,
                    fadeOut, title, subTitle, send, true));
            match.getSpectators().forEach(Utils::sendSoundBedBroken);
            match.checkEmptyTeam(team);
            arenaOptions.performCommands("BedBreakCommands", p);

            return;
        }

        if (!match.getPlacedBlocks().contains(b)) {
            e.setCancelled(true);
            p.sendMessage(Lang.DESTROY_NOT_PLACED_BLOCK.getString());
            return;
        }

        Location location = b.getLocation();
        BedwarsGameBreakBlockEvent event = new BedwarsGameBreakBlockEvent(b, p, pTeam);
        Bukkit.getPluginManager().callEvent(event);
        @SuppressWarnings("deprecation")
        Item item = location.getWorld().dropItem(location, new ItemStack(b.getType(), 1, b.getData()));
        b.setType(Material.AIR);
        e.setCancelled(true);
        match.getMatchEntity().add(item);
        match.getPlacedBlocks().remove(b);

    }

}
