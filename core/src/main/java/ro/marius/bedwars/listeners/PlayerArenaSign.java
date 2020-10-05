package ro.marius.bedwars.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.ArenaOptions;
import ro.marius.bedwars.game.Game;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.manager.type.GameManager;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.utils.Utils;

import java.util.Iterator;
import java.util.List;

public class PlayerArenaSign implements Listener {

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e) {
        String[] l = e.getLines();
        Sign s = (Sign) e.getBlock().getState();

        if (l.length < 2) {
            return;
        }
        if (!"Bedwars".equalsIgnoreCase(l[0].replaceAll("\\s", ""))) {
            return;
        }

        Game game = ManagerHandler.getGameManager().getGame(l[1].replaceAll("\\s", ""));

        if (game == null) {
            e.getPlayer().sendMessage(Utils.translate("&cThe arena " + l[1] + " does not exist."));
            return;
        }

        AMatch match = game.getMatch();
        int players = match.getPlayers().size();
        int maxPlayers = game.getMaxPlayers();
        String name = game.getName();
        String mod = game.getMod();
        ArenaOptions arenaOptions = game.getArenaOptions();

        new BukkitRunnable() {

            @Override
            public void run() {
                s.setLine(0,
                        Utils.translate(arenaOptions.getString("ArenaSign.FirstLine")).replace("<arenaName>", name)
                                .replace("<inGame>", players + "").replace("<maxPlayers>", maxPlayers + "")
                                .replace("<mode>", mod));
                s.setLine(1,
                        Utils.translate(arenaOptions.getString("ArenaSign.SecondLine")).replace("<arenaName>", name)
                                .replace("<inGame>", players + "").replace("<maxPlayers>", maxPlayers + "")
                                .replace("<mode>", mod));
                s.setLine(2,
                        Utils.translate(arenaOptions.getString("ArenaSign.ThirdLine")).replace("<arenaName>", name)
                                .replace("<inGame>", players + "").replace("<maxPlayers>", maxPlayers + "")
                                .replace("<mode>", mod));
                s.setLine(3,
                        Utils.translate(arenaOptions.getString("ArenaSign.FourthLine")).replace("<arenaName>", name)
                                .replace("<inGame>", players + "").replace("<maxPlayers>", maxPlayers + "")
                                .replace("<mode>", mod));
                s.update(true);

            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 1);

        if (!game.getGameSigns().contains(s.getLocation())) {
            game.getGameSigns().add(s.getLocation());
            List<String> signLocation = ManagerHandler.getGameManager().game.getStringList("Signs." + game.getName());
            signLocation.add(Utils.convertingString(s.getLocation()));
            ManagerHandler.getGameManager().game.set("Signs." + game.getName(), signLocation);
            ManagerHandler.getGameManager().saveGameFile();
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractAtArenaSigns(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (e.getClickedBlock() == null) {
            return;
        }
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) e.getClickedBlock().getState();
        Location signLocation = sign.getLocation();
        Player p = e.getPlayer();

        List<Game> games = ManagerHandler.getGameManager().getGames();

        for (Game game : games) {

            if (!game.getGameSigns().contains(signLocation)) {
                continue;
            }

            game.getMatch().addPlayer(p);
            break;

        }

    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {

        Block b = e.getBlock();
        Player p = e.getPlayer();

        if (!(b.getState() instanceof Sign)) {
            return;
        }

        if (!p.isOp() || !p.hasPermission("bedwars.admin")) {
            return;
        }

        if (e.isCancelled()) {
            return;
        }

        GameManager gameManager = ManagerHandler.getGameManager();

        for (Iterator<Game> it = gameManager.getGames().iterator(); it.hasNext(); ) {
            Game game = it.next();
            if (game.getGameSigns().contains(b.getLocation())) {
                game.getGameSigns().remove(b.getLocation());
                List<String> signLocation = gameManager.game.getStringList("Signs." + game.getName());
                signLocation.remove(Utils.convertingString(b.getLocation()));
                gameManager.game.set("Signs." + game.getName(), signLocation);
                gameManager.saveGameFile();
            }
        }
    }

}
