package ro.marius.bedwars.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ro.marius.bedwars.manager.ManagerHandler;
import ro.marius.bedwars.menu.extra.JoinInventory;

public class JoinNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractJoinNpcEvent e){
        if (!e.getAction().equalsIgnoreCase("INTERACT_AT")) return;

        int npcID = e.getNpcID();
        String arenaType = ManagerHandler.getNPCManager().getNpcIdArenaType().get(npcID);

        if(arenaType == null) return;

        JoinInventory joinInventory = new JoinInventory(arenaType);
        e.getPlayer().openInventory(joinInventory.getInventory());
    }

}
