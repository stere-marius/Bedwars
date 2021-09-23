package ro.marius.bedwars.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInteractJoinNpcEvent extends Event {

    private final Player player;
    private final String action;
    private final int entityID;
    private final int npcID;
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PlayerInteractJoinNpcEvent(Player player, String action, int entityID, int npcID) {
        this.player = player;
        this.action = action;
        this.entityID = entityID;
        this.npcID = npcID;
    }

    public Player getPlayer() {
        return player;
    }

    public String getAction() {
        return action;
    }

    public int getEntityID() {
        return entityID;
    }

    public int getNpcID() {
        return npcID;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
