package ro.marius.bedwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ro.marius.bedwars.game.GameEdit;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.extra.arenaedit.GameEditInventory;
import ro.marius.bedwars.utils.XMaterial;
import ro.marius.bedwars.utils.itembuilder.ItemBuilder;

public class GameEditListener implements Listener {

    public static final ItemStack EDIT_GAME_ITEM = new ItemBuilder(XMaterial.CHEST.parseMaterial())
            .setDisplayName("&e&lClick to open edit inventory")
            .build();

    @EventHandler
    public void onInteractEditInventory(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        GameEdit gameEdit = ManagerHandler.getGameManager().getGameEdit().get(p.getUniqueId());

        if (gameEdit == null)
            return;
        if (e.getItem() == null)
            return;
        if (!e.getItem().isSimilar(EDIT_GAME_ITEM))
            return;


        GameEditInventory gameEditInventory = new GameEditInventory(gameEdit);
        p.openInventory(gameEditInventory.getInventory());
    }

//    @EventHandler
//    public void onAbandonConversation(ConversationAbandonedEvent e) {
//
//        System.out.println("ConversationAbandonedEvent");
//        System.out.println(e.getContext().getPlugin());
//        e.getContext().getForWhom().sendRawMessage(Utils.translate("&e&lThe conversation listener timed out."));
//
//        if (!(e.getContext().getPlugin() instanceof BedWarsPlugin))
//            return;
//
//        e.getContext().getForWhom().sendRawMessage(Utils.translate("&e&lThe conversation listener timed out."));
//    }

}
