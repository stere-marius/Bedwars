package ro.marius.bedwars.listeners.game.players;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import ro.marius.bedwars.BedWarsPlugin;
import ro.marius.bedwars.configuration.Lang;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.team.Team;
import ro.marius.bedwars.utils.ServerVersion;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.XMaterial;

public class PlayerSpawnEntity implements Listener {

    private final Material SNOWBALL = XMaterial.SNOWBALL.parseMaterial();
    private final Material GHAST_SPAWN_EGG = XMaterial.GHAST_SPAWN_EGG.parseMaterial();

    @EventHandler
    public void onInteractIcefish(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        Action action = e.getAction();

        if (match == null) {
            return;
        }
        if (action == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (action == Action.LEFT_CLICK_AIR) {
            return;
        }
        if (action == Action.PHYSICAL) {
            return;
        }

        ItemStack item = e.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() != this.SNOWBALL) {
            return;
        }

        // todo

        Team team = match.getPlayerTeam().get(p.getUniqueId());

        if (team.getSilverFish().size() >= match.getGame().getArenaOptions().getInt("IceFishLimit")) {
            p.sendMessage(Lang.SILVERFISH_LIMIT.getString());
            e.setCancelled(true);
            return;
        }

        Snowball snowball = p.launchProjectile(Snowball.class);
        snowball.setMetadata("Icefish", new FixedMetadataValue(BedWarsPlugin.getInstance(), team));
        snowball.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        Utils.removeItemInHand(p);
        e.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        AMatch match = ManagerHandler.getGameManager().getPlayerMatch().get(p.getUniqueId());
        Action action = e.getAction();

        if (match == null) {
            return;
        }
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = e.getItem();

        if (item == null) {
            return;
        }

        if (item.getType() != this.GHAST_SPAWN_EGG) {
            return;
        }

        Team team = match.getPlayerTeam().get(p.getUniqueId());
        e.setCancelled(true);

        if (team.getGolems().size() >= match.getGame().getArenaOptions().getInt("IronGolemLimit")) {
            p.sendMessage(Lang.IRON_GOLEM_LIMIT.getString());
            return;
        }

        Block b = e.getClickedBlock();

        String customName = match.getGame().getArenaOptions().getString("IronGolemName")
                .replace("<teamName>", team.getName()).replace("<teamColor>", team.getTeamColor().getChatColor());
        IronGolem golem = ManagerHandler.getVersionManager().getVersionWrapper().spawnGolem(b.getLocation().add(0, 1, 0));
        golem.setCustomName(customName);
        golem.setMetadata("Team", new FixedMetadataValue(BedWarsPlugin.getInstance(), team));
        golem.setMetadata("Match", new FixedMetadataValue(BedWarsPlugin.getInstance(), match));
        team.getGolems().add(golem);

        int amount = e.getItem().getAmount();

        if (amount > 1) {
            e.getItem().setAmount(amount - 1);
            return;
        }

        boolean isVersionHigh = ManagerHandler.getVersionManager().getServerVersion().getID() >= ServerVersion.v1_9_R1
                .getID();

        if (!isVersionHigh) {
            p.setItemInHand(null);
            return;
        }


        if (p.getInventory().getItemInMainHand().isSimilar(e.getItem())) {
            p.getInventory().setItemInMainHand(null);
            return;
        }

        if (p.getInventory().getItemInOffHand().isSimilar(e.getItem())) {
            p.getInventory().setItemInOffHand(null);
        }

    }

}
