package ro.marius.bedwars.menu.extra;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.match.AMatch;
import ro.marius.bedwars.match.MatchSpectator;
import ro.marius.bedwars.menu.ExtraInventory;
import ro.marius.bedwars.utils.Utils;
import ro.marius.bedwars.utils.itembuilder.SkullBuilder;

public class SpectatorTeleport extends ExtraInventory {

    private MatchSpectator matchSpectator;
    private AMatch match;

    public SpectatorTeleport(MatchSpectator matchSpectator, AMatch match) {
        this.matchSpectator = matchSpectator;
        this.match = match;
    }

    @Override
    public Inventory getInventory() {

        int size = (int) Math.ceil(this.match.getPlayers().size() / 9.0);

        if (size == 0) {
            size = 1;
        }
        if (size > 5) {
            size = 6;
        }

        Inventory inv = Bukkit.createInventory(this, size * 9);

        int i = 0;

        for (Player player : this.match.getPlayers()) {
            inv.setItem(i, new SkullBuilder().withOwner(player.getUniqueId(), ManagerHandler.getVersionManager().getVersionWrapper())
                    .setDisplayName("&aTeleport to " + player.getName()).build());
            i++;
        }

        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if ((item == null) || (item.getType() == Material.AIR)) {
            return;
        }
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        Player target = ManagerHandler.getVersionManager().getOwningPlayer(skullMeta);

        p.teleport(target);
        p.sendMessage(Utils.translate("&aYou have been teleported to " + target.getName()));
        this.matchSpectator.startTeleport(target);

    }

    public AMatch getMatch() {
        return this.match;
    }

    public void setMatch(AMatch match) {
        this.match = match;
    }
}
